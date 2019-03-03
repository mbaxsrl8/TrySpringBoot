package cn.lurui.SpringBootTest;

import cn.lurui.SpringBootTest.Controller.TVSeriesController;
import cn.lurui.SpringBootTest.Dao.TvCharacterDao;
import cn.lurui.SpringBootTest.Dao.TvSeriesDao;
import cn.lurui.SpringBootTest.Pojo.TvCharacter;
import cn.lurui.SpringBootTest.Pojo.TvSeries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * 测试web控制层和业务逻辑层，mock数据访问层的类，以避免数据库内数据差异造成的测试上的困难
 * 和TvSeriesServiceTests相比，类上多了@AutoConfigureMockMvc注解，这是初始化一个mvc环境用于测试
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

public class AppTests {

    @MockBean
    TvSeriesDao tvSeriesDao;

    @MockBean
    TvCharacterDao tvCharacterDao;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TVSeriesController tvSeriesController;

    @Test
    public void contextLoads() {
        //这个方法体内容为空，是为了测试是否有bean没有被装载进来。如果没有，程序时不能执行到这一步的。
        //有了具体测试方法后，这个测试方法也就不再需要了
    }

    @Test
    public void testGetAll() throws Exception{
        List<TvSeries> list = new ArrayList<>();
        TvSeries tvSeries = new TvSeries();
        tvSeries.setName("POI");
        list.add(tvSeries);
        //这些桩模块的加载可参考TvSeriesServiceTest中的例子
        Mockito.when(tvSeriesDao.getAll()).thenReturn(list);

        //下面这个是相当于在启动项目后，执行 GET /tvseries，被测模块是web控制层，因为web控制层会调用业务逻辑层，
        // 所以业务逻辑层也会被测试
        //业务逻辑层调用了被mock出来的数据访问层桩模块。
        //如果想仅仅测试web控制层，（例如业务逻辑层尚未编码完毕），可以mock一个业务逻辑层的桩模块
        mockMvc.perform(MockMvcRequestBuilders.get("/tvseries")).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("POI")));
        //上面这几句和字面意思一致，期望状态是200，返回值包含POI三个字面，桩模块返回的一个电视剧名字是POI，如果测试正确是包含这三个字母的。
    }

    @Test
    public void testAddSeries() throws Exception {
        BitSet bitSet = new BitSet(1);
        bitSet.set(0,false);

        //下面的两个doAnswer方法用来验证插入到数据中的参数是否和我们传入进去的相等
        //bitSet验证桩模块是否被执行过
        Mockito.doAnswer((Answer<Object>) invocation -> {
            Object[] args = invocation.getArguments();
            TvSeries tvSeries = (TvSeries) args[0];
            Assert.assertEquals(tvSeries.getName(),"可爱的湖南人");
            tvSeries.setId(118);
            bitSet.set(0,true);
            return null;
        }).when(tvSeriesDao).insert(Mockito.any(TvSeries.class));

        Mockito.doAnswer((Answer<Object>) invocation -> {
            Object[] args = invocation.getArguments();
            TvCharacter tvCharacter = (TvCharacter) args[0];
            //应该是json中传递过来的剧中角色名字
            Assert.assertEquals(tvCharacter.getName(),"CaiYishu");
            Assert.assertEquals(118, tvCharacter.getTvSeriesId());
            bitSet.set(0,true);
            return null;
        }).when(tvCharacterDao).insert(Mockito.any(TvCharacter.class));

        String jsonData = "{\"name\":\"可爱的湖南人\",\"seasonCount\":1,\"originalRelease\":\"1996-01-18\"," +
                "\"tvCharacters\":[{\"id\":1,\"name\":\"CaiYishu\"}]}";

        //模拟一个MVC环境，用POST方法传入一个JSON消息，将结果打印出来并验证状态是否为200
        this.mockMvc.perform(MockMvcRequestBuilders.post("/tvseries").contentType(MediaType.APPLICATION_JSON).
                content(jsonData)).andDo
                (MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assert.assertTrue(bitSet.get(0));
    }

    @Test
    public void testFileUpload() throws Exception{
        String fileFolder = "/target/files";
        File folder = new File(fileFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 下面这句可以设置bean里面通过@Value获得的数据
        ReflectionTestUtils.setField(tvSeriesController,"uploadFolder",folder.
                getAbsolutePath());

        //用来获取资源
        InputStream inputStream = getClass().getResourceAsStream("/testfileupload.jpg");
        if(inputStream == null) {
            throw new RuntimeException("需要先在src/test/resources目录下放置一张jpg文件，名为testfileupload.jpg然后运行测试");
        }

        //模拟一个文件上传的请求
        MockMultipartFile imgFile = new MockMultipartFile("photo","/testfileupload.jpg","image/jpeg",IOUtils.toByteArray(inputStream) );

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/tvseries/1/photos")
        .file(imgFile)).andExpect(MockMvcResultMatchers.status().isOk());

        //解析返回的JSON
        ObjectMapper objectMapper = new ObjectMapper();//Jackson框架
        Map<String,Object> map = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),new TypeReference<Map<String,Object>>(){});

        String fileName = (String) map.get("photo");
        File f2 = new File(folder,fileName);

        //返回的文件名，应该已经保存在fileFolder文件夹下
        Assert.assertTrue(f2.exists());
    }
}

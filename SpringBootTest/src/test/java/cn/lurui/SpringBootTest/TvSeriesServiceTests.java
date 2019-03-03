package cn.lurui.SpringBootTest;

import cn.lurui.SpringBootTest.Dao.TvSeriesDao;
import cn.lurui.SpringBootTest.Pojo.TvSeries;
import cn.lurui.SpringBootTest.Service.TvSeriesService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TvSeriesServiceTests {
    /**
     * 实现一个单元测试用的桩模块
     * MockBean注解可给当前的spring context装载一个假的bean上去替代原有的同名bean
     * mock了Dao层的bean后，数据访问层就被接管了，从而实现测试不受具体数据库内数据值影响的结果
     */
    @MockBean
    TvSeriesDao tvSeriesDao;

    @Autowired
    TvSeriesService tvSeriesService;

    @Test
    public void testGetAllWithoutMockit() {
        List<TvSeries> list = tvSeriesService.getAllTvSeries();
        //使用这个测试方法时应把mockBean注释掉
        //这里的测试结果依赖连接数据库内的记录，很难写一个判断是否成功的条件，甚至无法执行
        //下面的testGetAll()方法，使用了mock出来的Dao作为桩模块，避免了这一情形
        System.out.println("The size is "+list.size());
        Assert.assertTrue(list.size()>0);
    }

    @Test
    public void testGetAll() {
        //设置一个TvSeries list
        List<TvSeries> list = new ArrayList<>();
        TvSeries tvSeries = new TvSeries();
        String name = "POI";
        tvSeries.setName(name);
        list.add(tvSeries);

        //下面这个语句时告诉mock出来的tvSeriesDao当执行getAll方法时，返回上面创建的那个list
        Mockito.when(tvSeriesDao.getAll()).thenReturn(list);

        //测试tvSeriesService的getAll方法，获得返回值
        List<TvSeries> res = tvSeriesService.getAllTvSeries();

        //判断返回值和最初的list是否相同
        Assert.assertEquals(list.size(), res.size());
        Assert.assertEquals(name,res.get(0).getName());
    }

    @Test
    public void testGetOne() {
        //根据不同的传入的参数，被mock的bean返回不同数据的例子
        String newName = "Person of Interest";
        BitSet mockExecuted = new BitSet();

        Mockito.doAnswer((Answer<Object>) invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            TvSeries bean = (TvSeries) args[0];

            Assert.assertEquals(newName,bean.getName());
            mockExecuted.set(0);
            return 1;
        }).when(tvSeriesDao).update(any(TvSeries.class));

        TvSeries tvSeries = new TvSeries();
        tvSeries.setName(newName);
        tvSeries.setId(111);

        tvSeriesService.updateTvSeries(tvSeries);
        Assert.assertTrue(mockExecuted.get(0));
    }
}

package cn.lurui.SpringBootTest.Controller;

import cn.lurui.SpringBootTest.Service.TvSeriesService;
import cn.lurui.SpringBootTest.Pojo.TvSeries;
import org.apache.commons.io.IOUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/tvseries")
public class TVSeriesController {

    private static final Log log = LogFactory.getLog(TVSeriesController.class);
    //通过@Value将外部的值动态注入到Bean中
    @Value("${SpringBootTest.uploadFolder:target/files}") String uploadFolder;


    private final TvSeriesService tvSeriesService;

    @Autowired
    public TVSeriesController(TvSeriesService tvSeriesService) {
        this.tvSeriesService = tvSeriesService;
    }

    @GetMapping
    public List<TvSeries> getAll() {
        if (log.isTraceEnabled()) {
            log.trace("getAll() is invoked");
        }
        List<TvSeries> list = tvSeriesService.getAllTvSeries();
        if (log.isTraceEnabled()) {
            log.trace("查询获得"+list.size()+"条记录");
        }
        return list;
    }


    @PostMapping
    public TvSeries insert(@RequestBody TvSeries tvSeries) {
        if (log.isTraceEnabled()) {
            log.trace("传进来的参数是" + tvSeries.toString());
        }
        tvSeriesService.addTvseries(tvSeries);
        return tvSeries;
    }

    @PostMapping(value = "/{id}/photos",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> addPhoto(@PathVariable int id, @RequestParam("photo")MultipartFile imgFile) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("接受到文件"+id+"收到文件:"+imgFile.getOriginalFilename());
        }
        //保存文件
        File folder = new File(uploadFolder);
        if(!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = imgFile.getOriginalFilename();
        assert fileName != null;
        if (fileName.endsWith(".jpg")) {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(folder,fileName));
            IOUtils.copy(imgFile.getInputStream(),fileOutputStream);
            fileOutputStream.close();

            Map<String, String> result = new HashMap<>();
            result.put("photo", fileName);
            return result;
        }else {
            throw new RuntimeException("不支持的格式，仅支持jpg格式");
        }
    }

    @PutMapping("/{id}")
    public TvSeries updateDtoById(@PathVariable int id, @RequestBody TvSeries dto) {
        if (log.isTraceEnabled()) {
            log.trace("update:" + id);
        }
        if (id==101 || id==102) {
            return createBOL();
        }else
            throw new ResourceNotFoundException();
    }

    @DeleteMapping("/{id}")
    public Map<String,String> deleteById(@PathVariable int id, HttpServletRequest request,
                                         @RequestParam(value = "deleteReason", required = false) String deleteReason){
        if (log.isTraceEnabled()) {
            log.trace("delete:" + id);
        }
        Map<String,String> res = new HashMap<>();
        if (id==101) {
            res.put("message","#101被"+request.getRemoteAddr()+"删除(原因："+deleteReason+")");
        }else if (id==102) {
            throw new RuntimeException("#102不能被删除");
        }else {
            throw new ResourceNotFoundException();
        }
        return res;
    }

    @GetMapping("/{id}") //这个跟在"/tvseries"之后
    private TvSeries getById(@PathVariable int id) {//这个注解代表从路径中获取参数
        if (log.isTraceEnabled()) {
            log.trace("get"+id);
        }
        if (id==101)
            return createCAM();
        else if (id==102)
            return createBOL();
        else
            throw new ResourceNotFoundException();
    }

    @GetMapping(value = "/{id}/icon",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getIcon(@PathVariable int id)throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("getIcon("+id+")");
        }
        String iconFile = "src/test/resources/wallhaven-671094.jpg";
        InputStream inputStream = new FileInputStream(iconFile);
        return IOUtils.toByteArray(inputStream);
    }

    private TvSeries createCAM() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,Calendar.MARCH,28);
        return new TvSeries(1,"CaiDaXianAndMe",1,calendar.getTime());
    }

    private TvSeries createBOL() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1995,Calendar.MAY,9);
        return new TvSeries(2,"TheBirthOfLuXiaoFen",23,calendar.getTime());
    }
}

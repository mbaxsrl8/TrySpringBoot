package cn.lurui.SpringBootTest.Dao;

import cn.lurui.SpringBootTest.Pojo.TvSeries;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TvSeriesDao {
    @Select("select * from tv_series")
    List<TvSeries> getAll();

    void update(TvSeries tvSeries);

    void insert(TvSeries tvSeries);
}

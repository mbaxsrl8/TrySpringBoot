package cn.lurui.SpringBootTest.Service;

import cn.lurui.SpringBootTest.Dao.TvCharacterDao;
import cn.lurui.SpringBootTest.Dao.TvSeriesDao;
import cn.lurui.SpringBootTest.Pojo.TvCharacter;
import cn.lurui.SpringBootTest.Pojo.TvSeries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TvSeriesService {

    private final Log log = LogFactory.getLog(TvSeriesService.class);

    private final TvSeriesDao tvSeriesDao;

    private final TvCharacterDao tvCharacterDao;

    @Autowired
    public TvSeriesService(TvSeriesDao tvSeriesDao, TvCharacterDao tvCharacterDao) {
        this.tvSeriesDao = tvSeriesDao;
        this.tvCharacterDao = tvCharacterDao;
    }

    public List<TvSeries> getAllTvSeries() {
        return tvSeriesDao.getAll();
    }

    public TvSeries updateTvSeries(TvSeries tvSeries) {
        if(log.isTraceEnabled()) {
            log.trace("updateTvSeries started for " + tvSeries);
        }
        tvSeriesDao.update(tvSeries);
        return tvSeries;
    }

    public TvSeries addTvseries(TvSeries tvSeries) {
        if (log.isTraceEnabled()) {
            log.trace("add tvSeries started for "+tvSeries);
        }

        tvSeriesDao.insert(tvSeries);
        if (tvSeries.getId()==null) {
            throw new RuntimeException("cannot get primary key!");
        }
        if (tvSeries.getTvCharacters()!=null) {
            int tvSeriesId = tvSeries.getId();
            for (TvCharacter tvCharacter:tvSeries.getTvCharacters()) {
                tvCharacter.setTvSeriesId(tvSeriesId);
                tvCharacterDao.insert(tvCharacter);
            }
        }

        return tvSeries;
    }
}

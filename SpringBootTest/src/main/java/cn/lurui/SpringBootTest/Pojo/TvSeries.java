package cn.lurui.SpringBootTest.Pojo;


import java.util.Date;
import java.util.List;

public class TvSeries {

    private Integer id;
    private String name;
    private int seasonCount;
    private Date originalRelease;
    private List<TvCharacter> tvCharacters;

    public TvSeries() { }

    public TvSeries(Integer id, String name, int seasonCount, Date originalRelease) {
        this.id = id;
        this.name = name;
        this.seasonCount = seasonCount;
        this.originalRelease = originalRelease;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }

    public Date getOriginalRelease() {
        return originalRelease;
    }

    public void setOriginalRelease(Date originalRelease) {
        this.originalRelease = originalRelease;
    }

    public List<TvCharacter> getTvCharacters() {
        return tvCharacters;
    }

    public void setTvCharacters(List<TvCharacter> tvCharacters) {
        this.tvCharacters = tvCharacters;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{id=" + id + ";name=" + name + "}";
    }
}

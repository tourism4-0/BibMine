package si.fri.turizem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "aid", nullable = false)
    private String aid;

    @Basic
    @Column(name = "json")
    private byte[] json;

    @Basic
    @Column(name = "xml")
    private byte[] xml;


    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonIgnoreProperties("article")
    private Collection<ArticleQuery> articleQueries;



    public Article(Integer id) {
        this.id = id;
    }

    public Article() {

    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", aid='" + aid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return getId().equals(article.getId()) &&
                getAid().equals(article.getAid()) &&
                Arrays.equals(getJson(), article.getJson()) &&
                Arrays.equals(getXml(), article.getXml()) &&
                getArticleQueries().equals(article.getArticleQueries());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getAid(), getArticleQueries());
        result = 31 * result + Arrays.hashCode(getJson());
        result = 31 * result + Arrays.hashCode(getXml());
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public byte[] getJson() {
        return json;
    }

    public void setJson(byte[] json) {
        this.json = json;
    }

    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
    }

    public Collection<ArticleQuery> getArticleQueries() {
        return articleQueries;
    }

    public void setArticleQueries(Collection<ArticleQuery> articleQueries) {
        this.articleQueries = articleQueries;
    }
}

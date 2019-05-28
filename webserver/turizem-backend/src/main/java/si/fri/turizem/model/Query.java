package si.fri.turizem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Query implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "query")
    private String query;

    @OneToMany(mappedBy = "query", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    @JsonIgnoreProperties("query")
    private Collection<ArticleQuery> articleQueries;

    @Override
    public String toString() {
        return "Query{" +
                "id=" + id +
                ", query='" + query + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query1 = (Query) o;
        return getId().equals(query1.getId()) &&
                Objects.equals(getQuery(), query1.getQuery()) &&
                Objects.equals(getArticleQueries(), query1.getArticleQueries());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getQuery(), getArticleQueries());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Collection<ArticleQuery> getArticleQueries() {
        return articleQueries;
    }

    public void setArticleQueries(Collection<ArticleQuery> articleQueries) {
        this.articleQueries = articleQueries;
    }
}

package si.fri.turizem.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "article_query")
@NamedQueries({
        @NamedQuery(
                name = "ArticleQuery.findArticleQuery",
                query = "SELECT aq " +
                        "FROM ArticleQuery aq WHERE aq.query.id = :queryId AND aq.article.id = :articleId"
        )
})
@IdClass(ArticleQueryPK.class)

public class ArticleQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "article_id",nullable = false,insertable = false,updatable = false)
    private int idArticle;

    @Id
    @Column(name = "query_id",nullable = false,updatable = false,insertable = false)
    private int idQuery;

    @ManyToOne()
    @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false, insertable = false,updatable = false)
    private Article article;

    @ManyToOne()
    @JoinColumn(name = "query_id", referencedColumnName = "id", nullable = false, insertable = false,updatable = false)
    private Query query;

    @Override
    public String toString() {
        return "ArticleQuery{" +
                "idArticle=" + idArticle +
                ", idQuery=" + idQuery +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleQuery that = (ArticleQuery) o;
        return getIdArticle() == that.getIdArticle() &&
                getIdQuery() == that.getIdQuery() &&
                Objects.equals(getArticle(), that.getArticle()) &&
                Objects.equals(getQuery(), that.getQuery());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdArticle(), getIdQuery(), getArticle(), getQuery());
    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    public int getIdQuery() {
        return idQuery;
    }

    public void setIdQuery(int idQuery) {
        this.idQuery = idQuery;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }
}

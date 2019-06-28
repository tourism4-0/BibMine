package si.fri.bibmine.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ArticleQueryPK implements Serializable {

    @Id
    @Column(name = "article_id", nullable = false)
    private int idArticle;

    @Id
    @Column(name = "query_id", nullable = false)
    private int idQuery;

    @Override
    public String toString() {
        return "ArticleQueryPK{" +
                "idArticle=" + idArticle +
                ", idQuery=" + idQuery +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleQueryPK that = (ArticleQueryPK) o;
        return getIdArticle() == that.getIdArticle() &&
                getIdQuery() == that.getIdQuery();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdArticle(), getIdQuery());
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
}

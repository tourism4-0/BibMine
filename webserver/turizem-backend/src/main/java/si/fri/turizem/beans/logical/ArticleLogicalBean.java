package si.fri.turizem.beans.logical;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import si.fri.turizem.beans.entity.ArticleEntityBean;
import si.fri.turizem.models.Article;
import si.fri.turizem.models.Query;
import si.fri.turizem.util.ScopusClientUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

import static si.fri.turizem.util.ScopusClientUtil.getArticle;
import static si.fri.turizem.util.ScopusClientUtil.getArticleFullText;

@RequestScoped
public class ArticleLogicalBean {
    private static final Logger LOG = LogManager.getLogger(ArticleLogicalBean.class.getName());

    @Inject
    private ArticleEntityBean articleEntityBean;

    /**
     * Prepare entity for persisting in DB
     *
     * @param q
     */
    public void persistArticles(String q) {

        JSONArray articles = ScopusClientUtil.getArticlesList(q);

        for (int i = 0; i < articles.length(); i++) {
            LOG.info("LOGICAL BEAN");
            Article article = new Article();
            String dataXml = getArticle(articles.getJSONObject(i).getString("prism:url"));
            article.setXml(dataXml.getBytes(StandardCharsets.UTF_8));
            article.setJson(convertXmlToJson(dataXml, articles.getJSONObject(i).getString("dc:identifier").substring(10)).getBytes(StandardCharsets.UTF_8));
            article.setAid(articles.getJSONObject(i).getString("dc:identifier").substring(10));

            Query query = new Query();
            query.setQuery(q);

            articleEntityBean.persistArticle(article, query);
        }
    }

    public String getArticleFull(String aid) {
        Article article = articleEntityBean.getArticle(aid);
        String dataString = new String(article.getJson(), StandardCharsets.UTF_8);
        JSONObject fullText = new JSONObject(dataString);

        if(fullText.getJSONArray("content").length() != 0)
            return fullText.getJSONArray("content").toString();
        else
            return null;
    }

    /**
     * Convert article XML data to JSON
     * Article full text is also added to JSON
     *
     * @param xml
     * @param aid
     * @return
     */
    public String convertXmlToJson(String xml, String aid) {
        LOG.info("****************************************  convertXmlToJson  ****************************************");

        try {
            JSONObject xmlJSONObj = XML.toJSONObject(xml);

            // remove unnecessary fields
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns:ce");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").getJSONObject("item").remove("xmlns:ce");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").getJSONObject("item").remove("ait:process-info");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").getJSONObject("item").getJSONObject("bibrecord").getJSONObject("item-info").remove("ait:process-info");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns:xocs");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns:xsi");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns:ait");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns:cto");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns:prism");
            xmlJSONObj.getJSONObject("abstracts-retrieval-response").remove("xmlns:dc");

            String contentString = getArticleFullText(aid);

            if (contentString.isEmpty()) {
                contentString = "{  \"full-text-retrieval-response\": {\n" +
                        "    \"originalText\": {\n" +
                        "      \"xocs:doc\": {\n" +
                        "        \"xocs:serial-item\": {\n" +
                        "          \"article\": {\n" +
                        "            \"body\": {\n" +
                        "              \"ce:sections\": {\n" +
                        "                \"ce:section\": [{}]\n" +
                        "              }\n" +
                        "            }\n" +
                        "          }\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            }

            try {
                JSONObject content = new JSONObject(contentString);

                content = content.getJSONObject("full-text-retrieval-response").getJSONObject("originalText").getJSONObject("xocs:doc").getJSONObject("xocs:serial-item").
                        getJSONObject("article").getJSONObject("body").getJSONObject("ce:sections");

                xmlJSONObj.append("content", content.getJSONArray("ce:section"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return xmlJSONObj.toString();
        } catch (JSONException e) {
            return e.toString();
        }
    }

}

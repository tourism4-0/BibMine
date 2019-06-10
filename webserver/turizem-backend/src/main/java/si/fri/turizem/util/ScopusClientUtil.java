package si.fri.turizem.util;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ScopusClientUtil {
    private static final Logger LOG = LogManager.getLogger(ScopusClientUtil.class.getName());

    /*Scopus environment variables*/
    private static final String SCOPUS_KEY = "";

    /*Scopus settings*/
    private static String scopusAPIKey = "ad751272c5fa07ea01537f133b200940";
    private static String scopusURL = "https://api.elsevier.com/content/";

    static {
        initialize();
    }

    private static void initialize() {
        LOG.trace("Util {} initialized", ScopusClientUtil.class.getName());

        Map<String, String> env = System.getenv();

        if (env.containsKey(SCOPUS_KEY)) {
            scopusAPIKey = env.get(SCOPUS_KEY);
        }
    }

    /**
     * Get list of articles satisfying the query
     *
     * @param q
     * @return
     * @throws NoSuchFieldException
     */
    public static JSONArray getArticlesList(String q) {
        LOG.info("**********************************************  getArticleList  **********************************************");

        String cursor = "*";
        String queryUrl;

        JSONArray articles = new JSONArray();

        //       while(cursor != null) {
        for (int test = 0; test < 1; test++) { // Note: For LOOP ONLY FOR DEV. To be removed in final version.
            int subtract = 0;
            queryUrl = scopusURL + "search/scopus?query=" + q + "&apiKey=" + scopusAPIKey + "&cursor=" + cursor + "&count=200";

            try {
                URIBuilder builder = new URIBuilder(queryUrl);
                URI uri = builder.build();

                HttpGet getRequest = new HttpGet(uri);
                HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);

                int status = response.getStatusLine().getStatusCode();

                LOG.info("Server status {}: GET list of articles by keyword", status);

                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();

                    LOG.info(entity.getContent().toString());

                    if (entity != null) {
                        JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                        JSONObject searchResults = jsonObject.getJSONObject("search-results");

                        int searchTotal = Integer.valueOf(searchResults.getString("opensearch:totalResults")) - subtract;
                        LOG.info("TOTAL AMOUNT OF RESULTS LEFT: " + searchTotal);

                        if (!searchResults.getJSONObject("cursor").getString("@next").equals(cursor))
                            cursor = searchResults.getJSONObject("cursor").getString("@next").replace("+", "%2B").replace("/", "%2F");
                        else
                            cursor = null;

                        if (searchResults.has("entry")) {
                            JSONArray searchEntries = searchResults.getJSONArray("entry");

                            for (int i = 0; i < searchEntries.length(); i++) {
                                articles.put(searchEntries.getJSONObject(i));
                            }
                        } else
                            break;
                    }
                } else {
                    String msg = "Remote server '" + getRequest.getURI() + "' has responded with status " + status + ".";
                    LOG.error(msg);
                    throw new InternalServerErrorException(msg);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            subtract = subtract + 200;
        }

        return articles;
    }

    /**
     * Get Article XML metadata
     *
     * @param url
     * @return
     */
    public static String getArticle(String url) {
        LOG.info("************************************************  getArticle  ************************************************");

        String queryUrl = url + "?apiKey=" + scopusAPIKey;
        String data = "";

        try {
            URIBuilder builder = new URIBuilder(queryUrl);
            URI uri = builder.build();

            HttpGet getRequest = new HttpGet(uri);

            HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);

            int status = response.getStatusLine().getStatusCode();

            LOG.info("Server status {} :GET article request", status);

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    data = EntityUtils.toString(entity);
                    return data;
                }
            } else if (status == 404) {
                LOG.info("Server status {} :GET Full article request", status);
                return data;
            } else {
                String msg = "Remote server '" + getRequest.getURI() + "' has responded with status " + status + ".";
                LOG.error(msg);
                throw new InternalServerErrorException(msg);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Get Scopus Article Full text
     * the article is retrieved as xml
     *
     * @param aid
     * @return
     */
    public static String getArticleFullText(String aid) {
        LOG.info("********************************************  getArticleFullText  ********************************************");
        try {
            String queryUrl = scopusURL + "article/scopus_id/" + aid + "?apiKey=" + scopusAPIKey + "&httpAccept=text%2Fxml";
            String articleText;

            URIBuilder builder = new URIBuilder(queryUrl);
            URI uri = builder.build();

            HttpGet getRequest = new HttpGet(uri);
            HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                LOG.info("Server status {} :GET Full article text request", status);

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    JSONObject content = XML.toJSONObject(EntityUtils.toString(entity));
                    articleText = content.toString();

                    return articleText;
                }
            } else if (status == 404) {
                LOG.info("Server status {} :GET Full article text request", status);
                return "";
            } else {
                String msg = "Remote server '" + getRequest.getURI() + "' has responded with status " + status + ".";
                throw new InternalServerErrorException(msg);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Helper method
     * Convert article XML data to JSON
     * Article full text is also added to JSON
     *
     * @param xml
     * @param aid
     * @return
     */
    public static String convertXmlToJson(String xml, String aid) {
        LOG.info("*********************************************  convertXmlToJson  *********************************************");

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
            LOG.info("XML to JSON conversion status: SUCCESS.");
            return xmlJSONObj.toString();
        } catch (JSONException e) {
            return e.toString();
        }
    }


}

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
import org.json.JSONObject;

import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class ScopusClientUtil {
    private static final Logger LOG = LogManager.getLogger(ScopusClientUtil.class.getName());

    /*Scopus environment variables*/
    private static final String SCOPUS_KEY="";

    /*Scopus settings*/
    private static String scopusAPIKey = "ad751272c5fa07ea01537f133b200940";
    private static String scopusURL = "https://api.elsevier.com/content/";

    static {
        initialize();
    }

    private static void initialize(){
        LOG.trace("Util {} initialized", ScopusClientUtil.class.getName());

        Map<String, String> env = System.getenv();

        if (env.containsKey(SCOPUS_KEY)){
            scopusAPIKey = env.get(SCOPUS_KEY);
        }
    }

    public static List<Object> getArticlesList(String query){

        String queryUrl = scopusURL + "search/scopus?query=" + query + "&apiKey=" + scopusAPIKey;
        LOG.info(queryUrl);
        JSONArray articles = new JSONArray();

        try {
            URIBuilder builder = new URIBuilder(queryUrl);
            URI uri = builder.build();

            HttpGet getRequest = new HttpGet(uri);
            HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);

            int status = response.getStatusLine().getStatusCode();

            LOG.info("Server status {}: GET list of articles by keyword", status);

            if(status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                LOG.info(entity.getContent().toString());

                if(entity != null) {
                    JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                    JSONObject parent = jsonObject.getJSONObject("search-results");

                    articles = parent.getJSONArray("entry");

                    LOG.info(parent.toString());
                    LOG.info(articles.toString());
                }
            }else{
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

        for (int i=0; i < articles.length(); i++){
            getArticle(articles.getJSONObject(20).getString("dc:identifier").substring(10));
            getArticleAbstract(articles.getJSONObject(i).getString("dc:identifier").substring(10));
        }

        return articles.toList();
    }

    public static String getArticleAbstract(String scopusId){
        String queryUrl = scopusURL + "abstract/scopus_id/" + scopusId + "?apiKey=" + scopusAPIKey;
        String articleAbstract = "";
        try {
            URIBuilder builder = new URIBuilder(queryUrl);
            URI uri = builder.build();

            HttpGet getRequest = new HttpGet(uri);
            getRequest.setHeader("Accept", "application/json");

            HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);

            int status = response.getStatusLine().getStatusCode();

            LOG.info("Server status {}: GET article's abstract", status);

            if(status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if(entity != null) {
                    JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                    articleAbstract = jsonObject.getJSONObject("abstracts-retrieval-response").getJSONObject("coredata").getString("dc:description");
                }
            }else{
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
        return articleAbstract;
    }

    public static String getArticle(String scopusId){

        String queryUrl = scopusURL + "article/scopus_id/" + scopusId + "?apiKey=" + scopusAPIKey + "&httpAccept=text%2Fxml";

        String text = "";

        try {
            URIBuilder builder = new URIBuilder(queryUrl);
            URI uri = builder.build();

            HttpGet getRequest = new HttpGet(uri);

            HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);

            int status = response.getStatusLine().getStatusCode();

            LOG.info("Server status {} :GET Full article text request", status);

            if(status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if(entity != null) {

                    text = EntityUtils.toString(entity);
                    LOG.info("ARTICLE TEXT " + text);
                    //ToDo: persist in DB

                    return text;
                }
            }else if(status == 404){
                LOG.info("Server status {} :GET Full article text request", status);
                return null;
            }
            else{
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
        return text;
    }
}

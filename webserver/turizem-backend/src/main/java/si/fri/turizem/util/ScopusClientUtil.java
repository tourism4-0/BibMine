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
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import si.fri.turizem.models.parse.Affiliations;
import si.fri.turizem.models.parse.Article;
import si.fri.turizem.models.parse.Authors;

import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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

        while(cursor != null) {
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
    public static Article convertXmlToJson(String xml, String aid) {
        LOG.info("*********************************************  convertXmlToJson  *********************************************");

        Document doc = Jsoup.parse(xml);
        Article article = new Article();

        // get article fulltext
        article.setContent(getArticleFullText(aid));

        // map relevant fields
        String publicationTitle = (doc.select("prism|title").isEmpty()) ? "" : doc.select("prism|title").first().text();
        String publicationAbstract = (doc.select("ce|para").isEmpty()) ? "" : doc.select("ce|para").first().text();
        String publicationName = (doc.select("prism|publicationname").isEmpty()) ? "" : doc.select("prism|publicationname").first().text();
        String publicationType = (doc.select("prism|aggregationtype").isEmpty()) ? "" : doc.select("prism|aggregationtype").first().text();
        String publicationSubType = (doc.select("subtypedescription").isEmpty()) ? "" : doc.select("subtypedescription").first().text();
        String publicationVolume = (doc.select("prism|volume").isEmpty()) ? "" : doc.select("prism|volume").first().text();
        String publicationIssue = (doc.select("prism|issueidentifier").isEmpty()) ? "" : doc.select("prism|issueidentifier").first().text();
        String publicationPageRange = (doc.select("prism|pageRange").isEmpty()) ? "" : doc.select("prism|pageRange").first().text();
        String publicationDate = (doc.select("prism|coverdate").isEmpty()) ? "" : doc.select("prism|coverdate").first().text();
        String publicationEid = (doc.select("eid").isEmpty()) ? "" : doc.select("eid").first().text();
        String publicationDoi = (doc.select("ce|doi").isEmpty()) ? "" : doc.select("ce|doi").first().text();
        String publicationOpenAccess = (doc.select("openaccess").isEmpty()) ? "" : doc.select("openaccess").first().text();
        String publicationScopusIdentifier = (doc.select("dc|identifier").isEmpty()) ? "" : doc.select("dc|identifier").first().text();
        String publicationFundingAcronym = (doc.select("fund-acr").isEmpty()) ? "" : doc.select("fund-acr").first().text();
        String publicationFundingAgencyID = (doc.select("fund-no").isEmpty()) ? "" : doc.select("fund-no").first().text();
        String publicationFundingAgency = (doc.select("fund-sponsor").isEmpty()) ? "" : doc.select("fund-sponsor").first().text();
        String publicationCitedBy = (doc.select("citedby-count").isEmpty()) ? "" : doc.select("citedby-count").first().text();

        article.setTitle(publicationTitle);
        article.setPublicationAbstact(publicationAbstract);
        article.setPublicationName(publicationName);
        article.setType(publicationType);
        article.setSubtype(publicationSubType);
        article.setVolume(publicationVolume);
        article.setIssue(publicationIssue);
        article.setPageRange(publicationPageRange);
        article.setDate(publicationDate);
        article.setEid(publicationEid);
        article.setDoi(publicationDoi);
        article.setUrl("https://doi.org/" + article.getDoi());
        article.setOpenAccess(publicationOpenAccess);
        article.setScopusIdentifier(publicationScopusIdentifier);
        article.setFundingAcronym(publicationFundingAcronym);
        article.setFundingAgencyID(publicationFundingAgencyID);
        article.setFundingAgency(publicationFundingAgency);
        article.setCitedBy(publicationCitedBy);

        article.setKeywords("");
        Elements keywords = doc.select("author-keyword");
        for (Element authkeyword : keywords) {
            String keyword = authkeyword.text() + ", ";
            article.setKeywords(article.getKeywords().concat(keyword));
        }

        article.setIndexTerms("");
        Elements indexes = doc.select("mainterm");
        for (Element indexTerm : indexes) {
            String index = indexTerm.text() + ", ";
            article.setIndexTerms(article.getIndexTerms().concat(index));
        }

        article.setReferences("");
        Elements references = doc.select("ref-fulltext");
        for (Element reference : references) {
            String ref = reference.text() + "\n ";
            article.setReferences(article.getReferences().concat(ref));
        }

        article.setSubjectAreas("");
        Elements subjectAreas = doc.select("subject-areas");
        for (Element subjectArea : subjectAreas) {
            String subject = subjectArea.text() + ", ";
            article.setSubjectAreas(article.getSubjectAreas().concat(subject));
        }

        Elements authors = doc.select("authors").first().children();
        List<Authors> authorsList = new ArrayList<>();
        for (Element author : authors) {
            Authors authorParse = new Authors();
            List<Affiliations> affiliationParses = new ArrayList<>();

            String firstName = (author.select("ce|given-name").isEmpty()) ? "" : author.select("ce|given-name").first().text();
            String lastName = (author.select("ce|surname").isEmpty()) ? "" : author.select("ce|surname").first().text();
            String indexedName = (author.select("ce|indexed-name").isEmpty()) ? "" : author.select("ce|indexed-name").first().text();
            String authorUrl = (author.select("author-url").isEmpty()) ? "" : author.select("author-url").first().text();
            String auid = (author.select("author").isEmpty()) ? "" : author.select("author").first().attr("auid");

            authorParse.setFirstName(firstName);
            authorParse.setLastName(lastName);
            authorParse.setIndexedName(indexedName);
            authorParse.setAuthorUrl(authorUrl);
            authorParse.setAuid(auid);

            authorsList.add(authorParse);

            Elements authorAffiliations = author.select("affiliation");
            for (Element authorAffiliation : authorAffiliations) {
                Affiliations affiliationParse = new Affiliations();

                String afid = (authorAffiliation.select("affiliation").isEmpty()) ? "" : authorAffiliation.select("affiliation").first().attr("id");
                String name = "";
                String city = "";
                String country = "";

                String cssQuery = "affiliation[id$=" + afid + "]";

                Elements affiliations = doc.select("abstracts-retrieval-response > affiliation").select(cssQuery);
                for (Element affiliation : affiliations) {
                    name = (affiliation.select("affilname").isEmpty()) ? "" : affiliation.select("affilname").first().text();
                    city = (affiliation.select("affiliation-city").isEmpty()) ? "" : affiliation.select("affiliation-city").first().text();
                    country = (affiliation.select("affiliation-country").isEmpty()) ? "" : affiliation.select("affiliation-country").first().text();
                }

                affiliationParse.setAfid(afid);
                affiliationParse.setName(name);
                affiliationParse.setCity(city);
                affiliationParse.setCountry(country);

                affiliationParses.add(affiliationParse);
            }
            authorParse.setAffiliations(affiliationParses);
            article.setAuthors(authorsList);
        }
        return article;
    }
}

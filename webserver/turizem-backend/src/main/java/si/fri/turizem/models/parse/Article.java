package si.fri.turizem.models.parse;

import java.util.List;

public class Article {

    String title;
    String publicationName;
    String type;
    String subtype;
    String publicationAbstact;
    String volume;
    String issue;
    String pageRange;
    String date;
    String eid;
    String doi;
    String content;
    String url;
    String keywords;
    String indexTerms;
    String openAccess;
    String references;
    String subjectAreas;
    String citedBy;
    String fundingAcronym;
    String fundingAgencyID;
    String fundingAgency;
    String scopusIdentifier;

    List<Authors> authors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationName() {
        return publicationName;
    }

    public void setPublicationName(String publicationName) {
        this.publicationName = publicationName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getPublicationAbstact() {
        return publicationAbstact;
    }

    public void setPublicationAbstact(String publicationAbstact) {
        this.publicationAbstact = publicationAbstact;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getPageRange() {
        return pageRange;
    }

    public void setPageRange(String pageRange) {
        this.pageRange = pageRange;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getIndexTerms() {
        return indexTerms;
    }

    public void setIndexTerms(String indexTerms) {
        this.indexTerms = indexTerms;
    }

    public String getOpenAccess() {
        return openAccess;
    }

    public void setOpenAccess(String openAccess) {
        this.openAccess = openAccess;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(String subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    public String getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(String citedBy) {
        this.citedBy = citedBy;
    }

    public String getFundingAcronym() {
        return fundingAcronym;
    }

    public void setFundingAcronym(String fundingAcronym) {
        this.fundingAcronym = fundingAcronym;
    }

    public String getFundingAgencyID() {
        return fundingAgencyID;
    }

    public void setFundingAgencyID(String fundingAgencyID) {
        this.fundingAgencyID = fundingAgencyID;
    }

    public String getFundingAgency() {
        return fundingAgency;
    }

    public void setFundingAgency(String fundingAgency) {
        this.fundingAgency = fundingAgency;
    }

    public String getScopusIdentifier() {
        return scopusIdentifier;
    }

    public void setScopusIdentifier(String scopusIdentifier) {
        this.scopusIdentifier = scopusIdentifier;
    }

    public List<Authors> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Authors> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", publicationName='" + publicationName + '\'' +
                ", type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", publicationAbstact='" + publicationAbstact + '\'' +
                ", volume='" + volume + '\'' +
                ", issue='" + issue + '\'' +
                ", pageRange='" + pageRange + '\'' +
                ", date='" + date + '\'' +
                ", eid='" + eid + '\'' +
                ", doi='" + doi + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", keywords='" + keywords + '\'' +
                ", indexTerms='" + indexTerms + '\'' +
                ", openAccess='" + openAccess + '\'' +
                ", references='" + references + '\'' +
                ", subjectAreas='" + subjectAreas + '\'' +
                ", citedBy='" + citedBy + '\'' +
                ", fundingAcronym='" + fundingAcronym + '\'' +
                ", fundingAgencyID='" + fundingAgencyID + '\'' +
                ", fundingAgency='" + fundingAgency + '\'' +
                ", scopusIdentifier='" + scopusIdentifier + '\'' +
                ", authors=" + authors +
                '}';
    }
}

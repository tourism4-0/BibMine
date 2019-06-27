package si.fri.turizem.models.parse;

import java.util.List;

public class Authors {

    String firstName;
    String lastName;
    String indexedName;
    String authorUrl;
    String auid;

    List<Affiliations> affiliations;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public void setIndexedName(String indexedName) {
        this.indexedName = indexedName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getAuid() {
        return auid;
    }

    public void setAuid(String auid) {
        this.auid = auid;
    }

    public List<Affiliations> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<Affiliations> affiliations) {
        this.affiliations = affiliations;
    }

    @Override
    public String toString() {
        return "Authors{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", indexedName='" + indexedName + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", auid='" + auid + '\'' +
                ", affiliations=" + affiliations +
                '}';
    }
}

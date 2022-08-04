package model;

import java.util.ArrayList;

public class AppProperties {

    ArrayList<Organization> orgs = new ArrayList<>();

    UserProperties userProperties = null;

    public ArrayList<Organization> getOrgs() {
        return orgs;
    }

    public ArrayList<String> getOrgNames() {
        ArrayList<String> retArr = new ArrayList<>();
        for (Organization o : orgs) {
            retArr.add(o.getLogin());
        }
        return retArr;
    }
    public void setOrgs(ArrayList<Organization> orgs) {
        this.orgs = orgs;
    }

    public UserProperties getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(UserProperties userProperties) {
        this.userProperties = userProperties;
    }
}

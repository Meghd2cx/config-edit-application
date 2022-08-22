package model;

import java.util.ArrayList;

/**
 * The AppProperties class stores application data such as User Properties and Organizations associated with the logged in User. It should be used as a Singleton type class,
 * only one instance of the class should be used for any given application.
 *
 * @author Meghnath Dey
 * */
public class AppProperties {

    /**
     * List of organizations associated with logged in user
     * */
    ArrayList<Organization> orgs = new ArrayList<>();

    /**
     * Properties of the logged-in user as well as OAuth information specific to the user.
     * */
    UserProperties userProperties = null;


    public ArrayList<Organization> getOrgs() {
        return orgs;
    }

    public Organization getOrg(String login) {
        int ind = getOrgNames().indexOf(login);
        return orgs.get(ind);
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
package model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The Organization class acts as a representation of a GitHub repository. Also contains all associated repositories of the organization.
 *
 * @author Meghnath Dey
 * */
public class Organization {

    private String login;
    private int id;
    private String url;
    private String repos_url;
    private String events_url;
    private String hooks_url;
    private String issues_url;
    private String members_url;
    private String public_members_url;
    private String avatar_url;
    private String description;
    private ArrayList<Repository> repos = new ArrayList<>();

    public Organization(String login) {
        this.login = login;
    }

    public Repository getRepo (String name) {
        int ind = getRepoNames().indexOf(name);
        System.out.println("Selected Repo: " + name);
        return repos.get(ind);
    }
    public boolean addRepo(Repository repository) {
        return repos.add(repository);
    }

    private ArrayList<String> getRepoNames() {
        ArrayList<String> retArr = new ArrayList<>();
        for (Repository r: repos) {
            retArr.add(r.getName());
        }
        return retArr;
    }

    public ArrayList<Repository> getRepos() {return this.repos;}

    public void setRepos(ArrayList<Repository> repos) {
        this.repos = repos;
    }
    public Repository remove(int index) {
        return repos.remove(index);
    }

    public boolean remove(Object o) {
        return repos.remove(o);
    }

    public void clear() {
        repos.clear();
    }

    public boolean addAll(Collection<? extends Repository> c) {
        return repos.addAll(c);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }

    public String getEvents_url() {
        return events_url;
    }

    public void setEvents_url(String events_url) {
        this.events_url = events_url;
    }

    public String getHooks_url() {
        return hooks_url;
    }

    public void setHooks_url(String hooks_url) {
        this.hooks_url = hooks_url;
    }

    public String getIssues_url() {
        return issues_url;
    }

    public void setIssues_url(String issues_url) {
        this.issues_url = issues_url;
    }

    public String getMembers_url() {
        return members_url;
    }

    public void setMembers_url(String members_url) {
        this.members_url = members_url;
    }

    public String getPublic_members_url() {
        return public_members_url;
    }

    public void setPublic_members_url(String public_members_url) {
        this.public_members_url = public_members_url;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

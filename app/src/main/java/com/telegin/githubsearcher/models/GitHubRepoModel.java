package com.telegin.githubsearcher.models;

import java.net.URL;

/**
 * Created by sergeytelegin on 18/10/2017.
 */

public class GitHubRepoModel {
    public String avatarUrl;
    public String name;
    public String description;
    public URL url;

    public GitHubRepoModel(String avatarUrl, String name, String description, URL url) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    @Override
    public String toString() {
        return "GitHubRepoModel{" +
                "avatarUrl='" + avatarUrl + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

package com.telegin.githubsearcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity implements ILoginListener {

    GitHub github;
    LoginDialog loginDialog;

    @ViewById
    Button githubLoginButton;

    @ViewById
    Button anonymouslyLoginButton;

    @Click
    public void githubLoginButton() {
        loginDialog = LoginDialog.getInstance(this);
        loginDialog.show(getFragmentManager(), LoginDialog.class.getName());
    }

    @Click
    public void anonymouslyLoginButton() {
        try {
            this.github = GitHub.connectAnonymously();
            Intent intentActivity = new Intent(this, FeedActivity_.class);
            intentActivity.putExtra("isAnonymously", true);
            startActivity(intentActivity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelLoginDialog() {
        loginDialog.dismiss();
    }

    @Override
    public void onConfirmLoginDialog(String username, String password) {
        loginDialog.dismiss();
        Intent intentActivity = new Intent(this, FeedActivity_.class);
        intentActivity.putExtra("isAnonymously", false);
        intentActivity.putExtra("username", username);
        intentActivity.putExtra("password", password);
        startActivity(intentActivity);
    }
}

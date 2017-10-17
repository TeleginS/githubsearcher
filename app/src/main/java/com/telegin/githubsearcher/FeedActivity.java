package com.telegin.githubsearcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.telegin.githubsearcher.adapters.ImageAdapter;
import com.telegin.githubsearcher.listeners.AnimateFirstDisplayListener;
import com.telegin.githubsearcher.models.GitHubRepoModel;
import com.telegin.githubsearcher.widgets.ProgressWidget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sergeytelegin
 */
@EActivity(R.layout.activity_feed)
public class FeedActivity extends AppCompatActivity implements ILoginListener {

    private static int PAGE_SIZE = 30;

    @ViewById
    SearchView searchView;

    @ViewById
    ListView listView;

    @ViewById
    ProgressWidget progressWidget;

    @ViewById
    Toolbar appBar;

    LoginDialog loginDialog;
    String username;
    String password;
    boolean isAnonymously;
    GitHub gitHub;
    ImageAdapter imageAdapter;
    List<GitHubRepoModel> gitHubRepoModelList = new LinkedList<>();
    String searchText;
    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    ScheduledFuture<?> searchTask;
    PagedIterator<GHRepository> result;
    boolean isLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        try {
            isAnonymously = intent.getBooleanExtra("isAnonymously", true);
            if (!isAnonymously) {
                username = intent.getStringExtra("username");
                password = intent.getStringExtra("password");
                gitHub = GitHub.connectUsingPassword(username, password);
            } else {
                gitHub = GitHub.connectAnonymously();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    public void afterViews() {
        appBar.setTitle(R.string.app_name);
        appBar.inflateMenu(R.menu.main_menu);
        if (isAnonymously) {
            appBar.getMenu().findItem(R.id.buttonExit).setVisible(false);
        } else {
            appBar.getMenu().findItem(R.id.buttonSignin).setVisible(false);
        }
        appBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.buttonSignin:
                        runLogIn();
                        appBar.getMenu().findItem(R.id.buttonSignin).setVisible(false);
                        appBar.getMenu().findItem(R.id.buttonExit).setVisible(true);
                        break;
                    case R.id.buttonExit:
                        exit();
                        break;
                }

                return true;
            }
        });
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
                if (totalItemCount > 0) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if (lastInScreen >= totalItemCount - 5 && !isLoading) {
                        isLoading = true;
                        getNextPage();
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(gitHubRepoModelList.get(i).url)));
                startActivity(browserIntent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                gitHubRepoModelList.clear();
                if (searchTask != null)
                    searchTask.cancel(true);

                searchTask = executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProgressBar();
                            GHRepositorySearchBuilder search = gitHub.searchRepositories().q(searchText);
                            result = search.list().withPageSize(PAGE_SIZE).iterator();

                            List<GHRepository> res = result.nextPage();
                            for (GHRepository ghRepository : res) {
                                GitHubRepoModel gitHubRepoModel = new GitHubRepoModel(ghRepository.getOwner().getAvatarUrl(), ghRepository.getName(), ghRepository.getDescription(), ghRepository.getHtmlUrl());
                                gitHubRepoModelList.add(gitHubRepoModel);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageAdapter = new ImageAdapter(getApplicationContext(), gitHubRepoModelList);
                                    listView.setAdapter(imageAdapter);
                                    imageAdapter.notifyDataSetChanged();
                                    listView.invalidate();
                                }
                            });
                            isLoading = false;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            stopProgressBar();
                        }
                    }
                }, 1, TimeUnit.SECONDS);
                return true;
            }
        });
    }

    public void getNextPage() {
        final List<GitHubRepoModel> gitHubRepoModelListTemp = new LinkedList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runProgressBar();
                    List<GHRepository> res = result.nextPage();
                    for (GHRepository ghRepository : res) {
                        GitHubRepoModel gitHubRepoModel = new GitHubRepoModel(ghRepository.getOwner().getAvatarUrl(), ghRepository.getName(), ghRepository.getDescription(), ghRepository.getHtmlUrl());
                        gitHubRepoModelListTemp.add(gitHubRepoModel);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gitHubRepoModelList.addAll(gitHubRepoModelListTemp);
                            imageAdapter.notifyDataSetChanged();
                        }
                    });
                    isLoading = false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    stopProgressBar();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimateFirstDisplayListener.displayedImages.clear();
    }

    private void runProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressWidget.show();
            }
        });
    }

    private void stopProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressWidget.hide();
            }
        });
    }

    @Override
    public void onCancelLoginDialog() {
        loginDialog.dismiss();
    }

    @Override
    public void onConfirmLoginDialog(String username, String password) {
        loginDialog.dismiss();
        isAnonymously = false;
        this.username = username;
        this.password = password;
        try {
            gitHub = GitHub.connectUsingPassword(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runLogIn() {
        loginDialog = LoginDialog.getInstance(this);
        loginDialog.show(getFragmentManager(), LoginDialog.class.getName());
    }

    public void exit() {
        Intent intentActivity = new Intent(this, LoginActivity_.class);
        startActivity(intentActivity);
    }
}

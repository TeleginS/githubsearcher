package com.telegin.githubsearcher;

/**
 * Created by sergeytelegin on 13/10/2017.
 */

public interface ILoginListener {
    void onCancelLoginDialog();
    void onConfirmLoginDialog(String username, String password);
}

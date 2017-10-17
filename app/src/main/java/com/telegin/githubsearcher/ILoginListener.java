package com.telegin.githubsearcher;

/**
 * Created by sergeytelegin
 */
interface ILoginListener {
    void onCancelLoginDialog();
    void onConfirmLoginDialog(String username, String password);
}

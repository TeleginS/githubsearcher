package com.telegin.githubsearcher;

import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.EditText;

import com.telegin.githubsearcher.widgets.ProgressWidget;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.kohsuke.github.GitHub;

/**
 * Created by sergeytelegin
 */
@EFragment(R.layout.login_dialog)
public class LoginDialog extends DialogFragment {

    @ViewById
    ProgressWidget progressWidget;

    @ViewById
    EditText usernameEditText;

    @ViewById
    EditText passwordEditText;

    ILoginListener listener;

    public static LoginDialog_ getInstance(ILoginListener listener) {
        LoginDialog_ loginDialog = new LoginDialog_();
        loginDialog.listener = listener;

        return loginDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Click
    public void onDialogPressedFirstButton() {
        listener.onCancelLoginDialog();
    }

    @Click
    public void onDialogPressedSecondButton() {
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runProgressBar();
                        GitHub github = GitHub.connectUsingPassword(username, password);
                        if (github.isCredentialValid()) {
                            listener.onConfirmLoginDialog(username, password);
                        } else {
                            hideProgressBarWithErrors();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runProgressBar(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressWidget.show();
            }
        });
    }

    private void hideProgressBarWithErrors(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                usernameEditText.setError("Incorrect username or password.");
                progressWidget.hide();
            }
        });
    }
}

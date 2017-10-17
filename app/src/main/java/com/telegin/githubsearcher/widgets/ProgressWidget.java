package com.telegin.githubsearcher.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.telegin.githubsearcher.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by sergeytelegin
 */
@EViewGroup(R.layout.progress_widget_layout)
public class ProgressWidget extends RelativeLayout {

    @ViewById
    ProgressBar progressBar;

    public ProgressWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        hide();
    }

    public void show() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (getVisibility() != View.GONE) {
            setVisibility(View.GONE);
        }
    }

}

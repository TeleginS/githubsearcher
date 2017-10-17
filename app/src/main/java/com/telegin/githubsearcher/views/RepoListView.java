package com.telegin.githubsearcher.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by sergeytelegin
 */
public class RepoListView extends ExpandableListView {

    protected BeforeExpandGroupListener beforeExpandGroupListener;
    protected BeforeCollapseGroupListener beforeCollapseGroupListener;

    public RepoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean expandGroup(int i) {
        if (beforeExpandGroupListener != null) {
            beforeExpandGroupListener.beforeGroupExpand(i);
        }
        return super.expandGroup(i);
    }

    @Override
    public boolean collapseGroup(int i) {
        if (beforeCollapseGroupListener != null) {
            beforeCollapseGroupListener.beforeGroupCollapse(i);
        }
        return super.collapseGroup(i);
    }

    public void setBeforeExpandGroupListener(BeforeExpandGroupListener beforeExpandGroupListener) {
        this.beforeExpandGroupListener = beforeExpandGroupListener;
    }

    public void setBeforeCollapseGroupListener(BeforeCollapseGroupListener beforeCollapseGroupListener) {
        this.beforeCollapseGroupListener = beforeCollapseGroupListener;
    }

    public static interface BeforeExpandGroupListener {
        void beforeGroupExpand(int i);
    }

    public static interface BeforeCollapseGroupListener {
        void beforeGroupCollapse(int i);
    }
}

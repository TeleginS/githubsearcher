package com.telegin.githubsearcher.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.List;

/**
 * Created by sergeytelegin
 */
@EBean
public class GitHubRepoListAdapter<T> extends BaseExpandableListAdapter {
    public IBindView groupView;
    public IBindView childView;

    private List<T> group;

    @Override
    public int getGroupCount() {
        return (group != null) ? group.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupIndex) {
        return 1;
    }

    @Override
    public Object getGroup(int position) {
        return group.get(position);
    }

    @Override
    public Object getChild(int groupIndex, int childPosititon) {
        return getGroup(groupIndex);
    }

    @Override
    public long getGroupId(int groupIndex) {
        return groupIndex;
    }

    @Override
    public long getChildId(int groupIndex, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupIndex, boolean isExpanded, View view, ViewGroup viewGroup) {
        IBindView currentGroupView;
        if (view == null) {
            currentGroupView = groupView.copy();
        } else {
            currentGroupView = (IBindView) view;
        }
        currentGroupView.bind(getGroup(groupIndex), groupIndex);
        return (View) currentGroupView;
    }

    @Override
    public View getChildView(int groupIndex,
                             int childPosition,
                             boolean isLastChild,
                             View view,
                             ViewGroup viewGroup) {
        IBindView currentChildView;
        if (view == null) {
            currentChildView = childView.copy();
        } else {
            currentChildView = (IBindView) view;
        }
        currentChildView.bind(getChild(groupIndex, childPosition), childPosition);
        return (View) currentChildView;
    }

    @Override
    public boolean isChildSelectable(int groupIndex, int childPosition) {
        return true;
    }

    @UiThread
    public void setGroup(List<T> group) {
        if (group != null) {
            this.group = group;
            refresh();
        }
    }

    public List<T> getJobs(){
        return group;
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}

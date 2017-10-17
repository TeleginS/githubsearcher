package com.telegin.githubsearcher.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.telegin.githubsearcher.R;
import com.telegin.githubsearcher.listeners.AnimateFirstDisplayListener;
import com.telegin.githubsearcher.models.GitHubRepoModel;
import com.telegin.githubsearcher.views.RepoView;

import java.util.List;

/**
 * Created by sergeytelegin
 */
public class ImageAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    List<GitHubRepoModel> res;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private DisplayImageOptions options;

    public ImageAdapter(Context context, List<GitHubRepoModel> res) {
        this.res = res;
        inflater = LayoutInflater.from(context);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }

    @Override
    public int getCount() {
        if (res == null) {
            return 0;
        }
        return res.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final RepoView holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.repo_item_layout, parent, false);
            holder = new RepoView();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.description = (TextView) view.findViewById(R.id.description);
            holder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            holder = (RepoView) view.getTag();
        }

        holder.name.setText(res.get(position).name);
        holder.description.setText(res.get(position).description);

        ImageLoader.getInstance().displayImage(res.get(position).avatarUrl, holder.image, options, animateFirstListener);

        return view;
    }
}
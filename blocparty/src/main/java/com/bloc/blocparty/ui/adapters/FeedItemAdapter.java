package com.bloc.blocparty.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;

import java.util.ArrayList;
import java.util.List;

public class FeedItemAdapter extends ArrayAdapter<FeedItem> {

    private Context mContext;
    private ArrayList<FeedItem> mFeedItems;

    public FeedItemAdapter(Context context, List<FeedItem> objects) {
        super(context, R.layout.feed_item_adapter, objects);

        this.mContext = context;
        this.mFeedItems = (ArrayList<FeedItem>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.textLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView textLabel;
        TextView timeLabel;
    }
}

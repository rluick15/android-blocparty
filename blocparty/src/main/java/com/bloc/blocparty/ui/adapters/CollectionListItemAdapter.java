package com.bloc.blocparty.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bloc.blocparty.R;
import com.bloc.blocparty.objects.Collection;

import java.util.ArrayList;
import java.util.List;

public class CollectionListItemAdapter extends ArrayAdapter<Collection> {

    private Context mContext;
    private ArrayList<Collection> mCollections;

    public CollectionListItemAdapter(Context context, List<Collection> objects) {
        super(context, R.layout.collection_list_item_adapter, objects);

        this.mContext = context;
        this.mCollections = (ArrayList<Collection>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        Collection collection = mCollections.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.feed_item_adapter, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private static class ViewHolder {

    }
}

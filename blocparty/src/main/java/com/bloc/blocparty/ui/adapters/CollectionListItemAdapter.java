package com.bloc.blocparty.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
            holder.title = (TextView) convertView.findViewById(R.id.collectionNameField);
            holder.userCount = (TextView) convertView.findViewById(R.id.userCountField);
            holder.image1 = (ImageView) convertView.findViewById(R.id.image1);
            holder.image2 = (ImageView) convertView.findViewById(R.id.image2);
            holder.image3 = (ImageView) convertView.findViewById(R.id.image3);
            holder.image4 = (ImageView) convertView.findViewById(R.id.image4);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(collection.getName());
        holder.userCount.setText(String.valueOf(collection.getUserCount()) + " users");

        Bitmap[] images = collection.getImages();
        if(images[0] != null) {
            holder.image1.setImageBitmap(images[0]);
        }
        if(images[1] != null) {
            holder.image2.setImageBitmap(images[1]);
        }

        if(images[2] != null) {
            holder.image3.setImageBitmap(images[2]);
        }

        if(images[3] != null) {
            holder.image4.setImageBitmap(images[3]);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView userCount;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        ImageView image4;
    }
}

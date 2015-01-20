package com.bloc.blocparty.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.feed_item_adapter, null);
            holder = new ViewHolder();
            holder.feedImage = (ImageView) convertView.findViewById(R.id.feedImage);
            holder.profPicture = (ImageView) convertView.findViewById(R.id.profileImage);
            holder.name = (TextView) convertView.findViewById(R.id.nameField);
            holder.message = (TextView) convertView.findViewById(R.id.descField);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        new ImageLoadTask(mFeedItems.get(position).getImageUrl(), holder.feedImage).execute();
        new ImageLoadTask(mFeedItems.get(position).getProfilePictureUrl(), holder.profPicture).execute();
        Log.e("URL", mFeedItems.get(position).getProfilePictureUrl());
        holder.name.setText(mFeedItems.get(position).getName());
        holder.message.setText(mFeedItems.get(position).getMessage());

        return convertView;
    }

    private static class ViewHolder {
        ImageView feedImage;
        ImageView profPicture;
        TextView name;
        TextView message;
        ImageButton favoriteButton;
        ImageButton threeDots;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView picture;

        public ImageLoadTask(String url, ImageView picField) {
            this.url = url;
            this.picture = picField;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap myBitmap = null;
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("ERRIR", String.valueOf(myBitmap));
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            picture.setImageBitmap(bitmap);
        }

    }
}

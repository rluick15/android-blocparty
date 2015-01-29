package com.bloc.blocparty.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloc.blocparty.BlocPartyApplication;
import com.bloc.blocparty.R;
import com.bloc.blocparty.objects.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.collection_list_item_adapter, null);

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
        String[] images = collection.getImages();
        if(images[0] != null) {
            new ImageLoadTask(images[0], holder.image1).execute();
        }
        if(images[1] != null) {
            new ImageLoadTask(images[1], holder.image2).execute();
        }

        if(images[2] != null) {
            new ImageLoadTask(images[2], holder.image3).execute();
        }

        if(images[3] != null) {
            new ImageLoadTask(images[3], holder.image4).execute();
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

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView image;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.image = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap myBitmap = null;
            try {
                URL imageUrl = new URL(url);
                HttpGet httpRequest = new HttpGet(imageUrl.toString());
                DefaultHttpClient httpclient = BlocPartyApplication.getHttpInstance();
                HttpResponse response = httpclient.execute(httpRequest);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                myBitmap = BitmapFactory.decodeStream(bufHttpEntity.getContent());
                httpRequest.abort();
            } catch (Exception e) {
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            image.setImageBitmap(bitmap);
        }
    }
}

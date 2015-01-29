package com.bloc.blocparty.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.bloc.blocparty.BlocPartyApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;
import java.util.ArrayList;

/**
 * This class represents a Collection object.
 */
public class Collection {

    private String mName;
    private int mUserCount = 0;
    private ArrayList<FeedItem> mFeedPosts;
    private Bitmap[] mImages;
    private ArrayList<String> mUserNames;

    public Collection(String name) {
        this.mName = name;
        mImages = new Bitmap[4];
        mUserNames = new ArrayList<>();
        mFeedPosts = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public int getUserCount() {
        return mUserCount;
    }

    public ArrayList<FeedItem> getFeedPosts() {
        return mFeedPosts;
    }

    public void addPost(FeedItem post) {
        mFeedPosts.add(post);

        if(!mUserNames.contains(post.getName())) {
            mUserNames.add(post.getName());
            Log.e("NAMES", String.valueOf(mUserNames));
            mUserCount++;
        }

        if(mUserCount <= 4) {
            new ImageLoadTask(post.getProfilePictureUrl()).execute();
        }
    }

    public Bitmap[] getImages() {
        return mImages;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;

        public ImageLoadTask(String url) {
            this.url = url;
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
                e.printStackTrace();
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            mImages[mUserCount - 1] = bitmap;
        }

    }
}

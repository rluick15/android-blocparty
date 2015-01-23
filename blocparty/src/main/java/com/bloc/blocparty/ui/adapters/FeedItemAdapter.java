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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bloc.blocparty.BlocPartyApplication;
import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.instagram.InstagramRequest;
import com.bloc.blocparty.utils.Constants;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedItemAdapter extends ArrayAdapter<FeedItem> {

    private Context mContext;
    private ArrayList<FeedItem> mFeedItems;
    private ListView mListView;

    public FeedItemAdapter(Context context, List<FeedItem> objects) {
        super(context, R.layout.feed_item_adapter, objects);

        this.mContext = context;
        this.mFeedItems = (ArrayList<FeedItem>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        mListView = (ListView) parent;

        FeedItem feedItem = mFeedItems.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.feed_item_adapter, null);
            holder = new ViewHolder();
            holder.feedImage = (ImageView) convertView.findViewById(R.id.feedImage);
            holder.profPicture = (ImageView) convertView.findViewById(R.id.profileImage);
            holder.name = (TextView) convertView.findViewById(R.id.nameField);
            holder.message = (TextView) convertView.findViewById(R.id.descField);
            holder.favoriteButton = (ImageButton) convertView.findViewById(R.id.likeButton);
            holder.progressBarMain = (ProgressBar) convertView.findViewById(R.id.progressBarMain);
            holder.progressBarProf = (ProgressBar) convertView.findViewById(R.id.progressBarProf);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        new ImageLoadTask(mContext, feedItem.getImageUrl(), holder.feedImage,
                holder.progressBarMain, holder.progressBarProf).execute();
        new ImageLoadTask(mContext, feedItem.getProfilePictureUrl(), holder.profPicture,
                holder.progressBarMain, holder.progressBarProf).execute();
        holder.name.setText(feedItem.getName());
        holder.message.setText(feedItem.getMessage());

        if(feedItem.getNetworkName().equals(Constants.FACEBOOK)) {
            facebookAdapter(feedItem, holder);
        }
        else if(feedItem.getNetworkName().equals(Constants.INSTAGRAM)) {
            instagramAdapter(feedItem, holder);
        }

        return convertView;
    }

    private void instagramAdapter(FeedItem feedItem, ViewHolder holder) {
        if (feedItem.getFavorited() == true) {
            holder.favoriteButton.setImageDrawable(
                    mContext.getResources().getDrawable(R.drawable.ic_intagram_heart));
            heartButton(feedItem, holder.favoriteButton, true);
        }
        else if(feedItem.getFavorited() == false) {
            holder.favoriteButton.setImageDrawable(
                    mContext.getResources().getDrawable(R.drawable.ic_instagram_unheart));
            heartButton(feedItem, holder.favoriteButton, false);
        }
    }

    private void facebookAdapter(final FeedItem feedItem, final ViewHolder holder) {
        if (feedItem.getFavorited()) {
            holder.favoriteButton.setImageDrawable(
                    mContext.getResources().getDrawable(R.drawable.ic_facebook_like_icon));
            likeButton(feedItem, holder.favoriteButton,
                    HttpMethod.DELETE, mContext.getString(R.string.post_unliked));
        }
        else if(!feedItem.getFavorited()) {
            holder.favoriteButton.setImageDrawable(
                    mContext.getResources().getDrawable(R.drawable.ic_facebook_unliked_icon));
            likeButton(feedItem, holder.favoriteButton,
                    HttpMethod.POST, mContext.getString(R.string.post_liked));
        }
    }

    private void likeButton(final FeedItem feedItem, final ImageButton favButton,
                            final HttpMethod method, final String toast) {
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Request(Session.getActiveSession(),
                        feedItem.getPostId() + "/likes", null, method, new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
                        feedItem.setFavorited(!feedItem.getFavorited());
                        updateView(feedItem);
                    }
                }).executeAsync();
            }
        });
    }

    private void heartButton(final FeedItem feedItem, final ImageButton favButton, final Boolean liked) {
        final String postId = feedItem.getPostId();

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int responseCode = 0;
                InstagramRequest request = new InstagramRequest(mContext);
                if(!liked) {
                    request.likePost(postId, feedItem);
                }
                else if(liked){
                    request.unlikePost(postId, feedItem);
                }

                //Todo: only on callback
                updateView(feedItem);
            }
        });
    }

    private void updateView(FeedItem feedItem) {
        int position = getPosition(feedItem);
        int start = mListView.getFirstVisiblePosition();
        View view = mListView.getChildAt(position - start);
        mListView.getAdapter().getView(position, view, mListView);
    }

    private static class ViewHolder {
        ImageView feedImage;
        ImageView profPicture;
        TextView name;
        TextView message;
        ImageButton favoriteButton;
        ImageButton threeDots;
        ProgressBar progressBarMain;
        ProgressBar progressBarProf;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private ProgressBar mainPb;
        private ProgressBar profPb;
        private String url;
        private ImageView picture;
        private Context context;

        public ImageLoadTask(Context context, String url, ImageView picField,
                             ProgressBar progressBarMain, ProgressBar progressBarProf) {
            this.context = context;
            this.url = url;
            this.picture = picField;
            this.mainPb = progressBarMain;
            this.profPb = progressBarProf;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainPb.setVisibility(View.VISIBLE);
            profPb.setVisibility(View.VISIBLE);
            picture.setImageDrawable(null);
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
            Log.e("ERRIR", String.valueOf(myBitmap));
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            mainPb.setVisibility(View.INVISIBLE);
            profPb.setVisibility(View.INVISIBLE);

            picture.setImageBitmap(bitmap);
        }

    }
}

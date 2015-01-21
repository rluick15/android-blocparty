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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bloc.blocparty.BlocPartyApplication;
import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
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

    public FeedItemAdapter(Context context, List<FeedItem> objects) {
        super(context, R.layout.feed_item_adapter, objects);

        this.mContext = context;
        this.mFeedItems = (ArrayList<FeedItem>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

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

        return convertView;
    }

    private void facebookAdapter(final FeedItem feedItem, ViewHolder holder) {
        if (feedItem.favorited() == true) {
            holder.favoriteButton.setImageDrawable(
                    mContext.getResources().getDrawable(R.drawable.ic_facebook_like_icon));
        }
        else if(feedItem.favorited() == false) {
            holder.favoriteButton.setImageDrawable(
                    mContext.getResources().getDrawable(R.drawable.ic_facebook_unliked_icon));
            holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Request(Session.getActiveSession(),
                            feedItem.getPostId() + "/likes", null, HttpMethod.POST, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            Toast.makeText(mContext, "Post Liked!", Toast.LENGTH_SHORT).show();
                            //Todo:doesnt work?? or does it??
                        }
                    }).executeAsync();
                }
            });
        }
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

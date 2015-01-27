package com.bloc.blocparty.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.activities.BlocParty;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;
import com.bloc.blocparty.utils.Constants;
import com.twitter.sdk.android.Twitter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This class handles sending the request to the Twitter server
 */
public class TwitterRequest {

    private Context mContext;
    private String mAuthToken;
    private String mAuthSecret;
    private twitter4j.Twitter mTwitter;

    public TwitterRequest(Context context) {
        this.mContext = context;
        if(Twitter.getSessionManager().getActiveSession() != null) {
            mAuthToken = Twitter.getSessionManager().getActiveSession().getAuthToken().token;
            mAuthSecret = Twitter.getSessionManager().getActiveSession().getAuthToken().secret;
            setTwitter();
        }
    }

    private void setTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(Constants.TWITTER_KEY)
                .setOAuthConsumerSecret(Constants.TWITTER_SECRET)
                .setOAuthAccessToken(mAuthToken)
                .setOAuthAccessTokenSecret(mAuthSecret);
        Configuration configuration =  builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        mTwitter = factory.getInstance();
    }

    private twitter4j.Twitter getTwitter() {
        return mTwitter;
    }

    public Boolean isLoggedIn() {
        return mAuthToken != null;
    }

    public void feedRequest() {
        if(mAuthToken != null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    twitter4j.Twitter twitter = getTwitter();
                    List<Status> statuses = new ArrayList<>();
                    try {
                        statuses = twitter.getHomeTimeline(new Paging(1, 100));
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }

                    final List<Status> finalStatuses = statuses;

                    ((BlocParty) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = 0;
                            List<Status> photoTweets = new ArrayList<>();

                            //get the last 10 photo tweets
                            for (Status status : finalStatuses) {
                                for(MediaEntity entity : status.getMediaEntities()) {
                                    if((entity.getType().equals("photo")) && i < 10) {
                                        photoTweets.add(status);
                                        i++;
                                    }
                                }
                            }

                            for(Status status : photoTweets) {
                                String postId = String.valueOf(status.getId());
                                String profUrl = status.getUser().getProfileImageURL();
                                String name = status.getUser().getName();
                                String message = status.getText();
                                Boolean liked = status.isFavorited();

                                String imageUrl = null;
                                for (MediaEntity entity : status.getMediaEntities()) {
                                    imageUrl = entity.getMediaURL();
                                }

                                FeedItem feedItem = new FeedItem(postId, imageUrl, profUrl,
                                        name, message, liked, Constants.TWITTER);
                                ((BlocParty) mContext).createFeedItem(feedItem);
                            }
                        }
                    });
                }
            }.start();
        }
    }

    public void favoriteTweet(final String postId, final FeedItem feedItem, final FeedItemAdapter feedItemAdapter) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    twitter4j.Twitter twitter = getTwitter();
                    twitter.createFavorite(Long.parseLong(postId));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                ((BlocParty) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedItem.setFavorited(true);
                        Toast.makeText(mContext, mContext.getString(R.string.post_liked),
                                Toast.LENGTH_SHORT).show();
                        feedItemAdapter.updateView(feedItem);
                    }
                });
            }
        }.start();
    }

    public void unfavoriteTweet(final String postId, final FeedItem feedItem, final FeedItemAdapter feedItemAdapter) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    twitter4j.Twitter twitter = getTwitter();
                    twitter.destroyFavorite(Long.parseLong(postId));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                ((BlocParty) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedItem.setFavorited(false);
                        Toast.makeText(mContext, mContext.getString(R.string.post_unliked),
                                Toast.LENGTH_SHORT).show();
                        feedItemAdapter.updateView(feedItem);
                    }
                });
            }
        }.start();
    }

    public void uploadImage(final Bitmap image, final String message) {
        final Boolean[] error = new Boolean[1];
        error[0] = false;

        new Thread() {
            @Override
            public void run() {
                super.run();

                twitter4j.Twitter twitter = getTwitter();

                File imageFile = bitmapToFile(image);

                try {
                    StatusUpdate imageStatus = new StatusUpdate(message);
                    imageStatus.setMedia(imageFile);
                    twitter.updateStatus(imageStatus);
                } catch (TwitterException e) {
                    error[0] = true;
                }

                ((BlocParty) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(error[0]) {
                            Toast.makeText(mContext, mContext.getString(R.string.error_request),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, mContext.getString(R.string.toast_image_uploaded),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        }.start();
    }

    private File bitmapToFile(Bitmap image) {
        File f = null;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int fileNum = prefs.getInt(Constants.SAVE_FILE, 0);

            f = new File(mContext.getCacheDir(),
                    mContext.getString(R.string.app_name) + String.valueOf(fileNum));
            f.createNewFile();

            fileNum++;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Constants.SAVE_FILE, fileNum);
            editor.apply();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();
            bos.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.close();
        } catch (IOException ignored) {}

        return f;
    }
}

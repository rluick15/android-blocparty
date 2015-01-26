package com.bloc.blocparty.twitter;

import android.content.Context;

import com.bloc.blocparty.ui.activities.BlocParty;
import com.bloc.blocparty.utils.Constants;
import com.twitter.sdk.android.Twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This class handles sending the request to the Twitter server
 */
public class TwitterRequest {

    private String mAccessToken;
    private Context mContext;
    private String mAuthToken;
    private String mAuthSecret;

    public TwitterRequest(Context context) {
        this.mContext = context;
        mAuthToken = Twitter.getSessionManager().getActiveSession().getAuthToken().token;
        mAuthSecret = Twitter.getSessionManager().getActiveSession().getAuthToken().secret;
    }

    public String getAccessToken() {
        return mAuthToken;
    }

    public void feedRequest() {
        if(mAuthToken != null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(Constants.TWITTER_KEY)
                        .setOAuthConsumerSecret(Constants.TWITTER_SECRET)
                        .setOAuthAccessToken(mAuthToken)
                        .setOAuthAccessTokenSecret(mAuthSecret);
                    Configuration configuration = builder.build();

                    TwitterFactory factory = new TwitterFactory(configuration);
                    twitter4j.Twitter twitter = factory.getInstance();
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
                                for(MediaEntity entity : status.getMediaEntities()) {
                                }
                            }
                        }
                    });
                }
            }.start();
        }
    }

    public String getResponse() {
        InputStream inputStream = null;
        try {
            String urlString = "https://api.twitter.com/1.1/statuses/home_timeline.json";
            URL url = new URL(urlString);
            inputStream = url.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return streamToString(inputStream);
    }

    /**
     * Method that returns String from the InputStream given by p_is
     * @param stream The given InputStream
     * @return The String from the InputStream
     */
    public static String streamToString(InputStream stream) {
        StringBuffer outString = null;
        try {
            BufferedReader reader;
            outString = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(stream));
            String mReader = reader.readLine();
            while (mReader != null) {
                outString.append(mReader);
                mReader = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outString.toString();
    }
}

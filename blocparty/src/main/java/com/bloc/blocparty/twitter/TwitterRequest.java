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

/**
 * This class handles sending the request to the Twitter server
 */
public class TwitterRequest {

    private String mAccessToken;
    private Context mContext;
    private String mAuthToken;

    public TwitterRequest(Context context) {
        this.mContext = context;
        mAuthToken = Twitter.getSessionManager().getActiveSession().getAuthToken().token;
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

                    ((BlocParty) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }.start();
        }
    }

    public String getResponse(String endpoint) {
        InputStream inputStream = null;
        try {
            String urlString = Constants.INSTAGRAM_API_URL
                    + endpoint + mAccessToken;
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

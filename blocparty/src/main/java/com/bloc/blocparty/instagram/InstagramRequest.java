package com.bloc.blocparty.instagram;

import android.content.Context;

import com.bloc.blocparty.ui.activities.BlocParty;
import com.bloc.blocparty.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class handles sending the request to the Instagram server
 */
public class InstagramRequest {

    private String mAccessToken;
    private Context mContext;

    public InstagramRequest(Context context) {
        this.mContext = context;

        InstagramSession iSession = new InstagramSession(context);
        mAccessToken = iSession.getAccessToken();
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void feedRequest() {
        if(mAccessToken != null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    final String response = getResponse(Constants.INSTAGRAM_FEED_ENDPOINT);

                    ((BlocParty) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                                JSONArray array = jsonObject.getJSONArray(Constants.DATA);

                                for(int i = 0; i < array.length(); i++) {
                                    JSONObject object = (JSONObject) array.get(i);
                                    JSONObject user = object.getJSONObject(Constants.USER);

                                    String postId = object.getString(Constants.ID);
                                    String imageUrl = object.getJSONObject(Constants.IMAGES)
                                            .getJSONObject(Constants.STANDARD_RESOLUTION)
                                            .getString(Constants.URL);
                                    String name = user.getString(Constants.FULL_NAME);
                                    String profUrl = user.getString(Constants.PROFILE_PICTURE);
                                    String message = object.getJSONObject(Constants.CAPTION)
                                            .getString(Constants.TEXT);
                                    Boolean liked = object.getBoolean(Constants.USER_HAS_LIKED);

                                    ((BlocParty) mContext).createFeedItem(postId, imageUrl, profUrl, name,
                                            message, liked, Constants.INSTAGRAM);
                                }
                            } catch (JSONException ignored) {}
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

    public String getLikeResponse(String endpoint) {
        InputStream inputStream = null;
        try {
            String urlString = "curl -X DELETE " + Constants.INSTAGRAM_API_URL
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

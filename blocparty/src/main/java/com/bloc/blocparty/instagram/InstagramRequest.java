package com.bloc.blocparty.instagram;

import android.content.Context;
import android.widget.Toast;

import com.bloc.blocparty.objects.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.activities.BlocParty;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;
import com.bloc.blocparty.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

                                for (int i = 0; i < array.length(); i++) {
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

                                    FeedItem feedItem = new FeedItem(postId, imageUrl, profUrl,
                                            name, message, liked, Constants.INSTAGRAM);
                                    ((BlocParty) mContext).createFeedItem(feedItem);
                                }
                            } catch (JSONException ignored) {
                            }
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

    public void likePost(final FeedItem feedItem, final FeedItemAdapter feedItemAdapter) {
        final int[] responseCode = new int[1];

        new Thread()  {
            @Override
            public void run() {
                super.run();

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://api.instagram.com/v1/media/" +
                        feedItem.getPostId() + "/likes");
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("access_token", mAccessToken));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpclient.execute(httppost);
                    responseCode[0] = response.getStatusLine().getStatusCode();

                } catch (UnsupportedEncodingException | ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ((BlocParty) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseCode[0] == 200) {
                            feedItem.setFavorited(true);
                            Toast.makeText(mContext, mContext.getString(R.string.post_liked),
                                    Toast.LENGTH_SHORT).show();
                            feedItemAdapter.updateView(feedItem);
                        }
                        else {
                            Toast.makeText(mContext, mContext.getString(R.string.error_request),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }.start();
    }

    public void unlikePost(final FeedItem feedItem, final FeedItemAdapter feedItemAdapter) {
        final int[] responseCode = new int[1];

        new Thread()  {
            @Override
            public void run() {
                super.run();

                HttpClient httpclient = new DefaultHttpClient();
                HttpDelete httpDelete = new HttpDelete("https://api.instagram.com/v1/media/" +
                        feedItem.getPostId() + "/likes?access_token=" + mAccessToken);

                try {
                    HttpResponse response = httpclient.execute(httpDelete);
                    responseCode[0] = response.getStatusLine().getStatusCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ((BlocParty) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseCode[0] == 200) {
                            feedItem.setFavorited(false);
                            Toast.makeText(mContext, mContext.getString(R.string.post_unliked),
                                    Toast.LENGTH_SHORT).show();
                            feedItemAdapter.updateView(feedItem);
                        }
                        else {
                            Toast.makeText(mContext, mContext.getString(R.string.error_request),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }.start();
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

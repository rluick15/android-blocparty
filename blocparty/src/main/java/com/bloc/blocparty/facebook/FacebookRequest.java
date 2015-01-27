package com.bloc.blocparty.facebook;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.activities.BlocParty;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;
import com.bloc.blocparty.utils.Constants;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rich on 1/22/2015.
 */
public class FacebookRequest {

    private String mCurrentUserId;
    private Context mContext;
    private Session mSession;

    public FacebookRequest(Context context) {
        this.mContext = context;
        getCurrentFacebookUser();
        mSession = Session.getActiveSession();
    }

    public Boolean isLoggedIn() {
        if(Session.getActiveSession().isOpened()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This method sends a data request to the facebook api server and retrieves current users id
     */
    private void getCurrentFacebookUser() {
        final Session session = Session.getActiveSession();
        if (isLoggedIn()) {
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            mCurrentUserId = user.getId();
                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        }
    }

    public String getUserId() {
        return mCurrentUserId;
    }

    /**
     * This method sends a data request to the facebook api server and retrieves the feed data.
     * It then creates a feed object and puts it into an array to be fed into the adapter
     */
    public void getFeedData() {
        if(isLoggedIn()) {
            Bundle params = new Bundle();
            params.putString(Constants.ACCESS_TOKEN, Session.getActiveSession().getAccessToken());
            params.putString(Constants.LIMIT, Constants.LIMIT_QUERY);
            params.putString(Constants.FILTER, Constants.FILTER_QUERY);

            new Request(Session.getActiveSession(),
                    Constants.FACEBOOK_REQUEST_URL,
                    params,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {
                            GraphObject graphObject = response.getGraphObject();
                            if (graphObject != null) {
                                JSONObject jsonObject = graphObject.getInnerJSONObject();

                                try {
                                    JSONArray array = jsonObject.getJSONArray(Constants.DATA);

                                    for (int i = 0; i < array.length(); i++) {
                                        Boolean liked = false;
                                        JSONObject object = (JSONObject) array.get(i);
                                        JSONObject from = object.getJSONObject(Constants.FROM);

                                        String postId = object.getString(Constants.ID);
                                        String name = from.getString(Constants.NAME);
                                        String userId = from.getString(Constants.ID);
                                        String pictureId = object.getString(Constants.PICTURE_ID);
                                        String message = object.getString(Constants.MESSAGE);

                                        FeedItem feedItem = new FeedItem(postId, pictureId, userId,
                                                name, message, liked, Constants.FACEBOOK);
                                        ((BlocParty) mContext).createFeedItem(feedItem);
                                    }
                                }
                                catch (JSONException ignored) {}
                            }
                        }
                    }
            ).executeAsync();
        }
    }

    public void likeRequest(final FeedItem feedItem, final FeedItemAdapter feedItemAdapter) {
        new Request(mSession, feedItem.getPostId() + "/likes", null, HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Toast.makeText(mContext, mContext.getString(R.string.post_liked), Toast.LENGTH_SHORT).show();
                feedItem.setFavorited(true);
                feedItemAdapter.updateView(feedItem);
            }
        }).executeAsync();
    }

    public void unlikeRequest(final FeedItem feedItem, final FeedItemAdapter feedItemAdapter) {
        new Request(mSession, feedItem.getPostId() + "/likes", null, HttpMethod.DELETE, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Toast.makeText(mContext, mContext.getString(R.string.post_unliked), Toast.LENGTH_SHORT).show();
                feedItem.setFavorited(false);
                feedItemAdapter.updateView(feedItem);
            }
        }).executeAsync();
    }

    public void isLiked(String postId, final FeedItem feedItem, final ImageButton button, final FeedItemAdapter feedItemAdapter) {
        if(isLoggedIn()) {
            new Request(Session.getActiveSession(), "/" + postId, null, HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            GraphObject graphObject = response.getGraphObject();
                            if (graphObject != null) {
                                JSONObject jsonObject = graphObject.getInnerJSONObject();
                                try {
                                    JSONObject likes = jsonObject.getJSONObject(Constants.LIKES);
                                    JSONArray likesArray = likes.getJSONArray(Constants.DATA);
                                    for(int i = 0; i < likesArray.length(); i++) {
                                        JSONObject object = (JSONObject) likesArray.get(i);
                                        String id = object.getString(Constants.ID);
                                        if(id.equals(mCurrentUserId)) {
                                            feedItem.setFavorited(true);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            feedItemAdapter.facebookAdapter(feedItem, button);
                        }
                    }).executeAsync();

        }
    }

    public void uploadPhoto(Bitmap image, String message) {
        Request request = Request.newUploadPhotoRequest(mSession, image,
            new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    if(response.getError() != null) {
                        Toast.makeText(mContext, mContext.getString(R.string.error_request),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(mContext, mContext.getString(R.string.toast_image_uploaded),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        });

        Bundle params = request.getParameters();
        params.putString(Constants.MESSAGE, message);
        request.executeAsync();
    }
}

package com.bloc.blocparty.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.instagram.InstagramRequest;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;
import com.bloc.blocparty.utils.Constants;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;


public class BlocParty extends Activity {

    private ArrayList<FeedItem> mFeedItems;
    private ListView mFeedList;
    private FeedItemAdapter mAdapter;
    private String mCurrentUserId;
    private String mInstagramAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloc_party);

        mFeedList = (ListView) findViewById(R.id.feedList);
        mFeedItems = new ArrayList<>();

        mAdapter = new FeedItemAdapter(BlocParty.this, mFeedItems);
        mFeedList.setAdapter(mAdapter);

        //getCurrentFacebookUser();
        getFacebookData();
        getInstagramData();
    }

    /**
     * This method sends a data request to the facebook api server and retrieves current users id
     */
//    public void getCurrentFacebookUser() {
//        final Session session = Session.getActiveSession();
//        if (session != null && session.isOpened()) {
//            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
//                @Override
//                public void onCompleted(GraphUser user, Response response) {
//                    // If the response is successful
//                    if (session == Session.getActiveSession()) {
//                        if (user != null) {
//                            mCurrentUserId = user.getId();
//                        }
//                    }
//                }
//            });
//            Request.executeBatchAsync(request);
//        }
//    }

    /**
     * This method sends a data request to the facebook api server and retrieves the feed data.
     * It then creates a feed object and puts it into an array to be fed into the adapter
     */
    private void getFacebookData() {
        Bundle params = new Bundle();
        params.putString(Constants.ACCESS_TOKEN, Session.getActiveSession().getAccessToken());
        params.putString(Constants.LIMIT, Constants.LIMIT_QUERY);
        params.putString(Constants.FILTER, Constants.FILTER_QUERY);

        if(Session.getActiveSession().isOpened()) {
            new Request(Session.getActiveSession(),
                    Constants.FACEBOOK_REQUEST_URL,
                    params,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {
                            Boolean liked = false;
                            GraphObject graphObject = response.getGraphObject();
                            if (graphObject != null) {
                                JSONObject jsonObject = graphObject.getInnerJSONObject();

                                try {
                                    JSONArray array = jsonObject.getJSONArray(Constants.DATA);

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = (JSONObject) array.get(i);
                                        JSONObject from = object.getJSONObject(Constants.FROM);

                                        String postId = object.getString(Constants.ID);
                                        String name = from.getString(Constants.NAME);
                                        String userId = from.getString(Constants.ID);
                                        String pictureId = object.getString(Constants.PICTURE_ID);
                                        String message = object.getString(Constants.MESSAGE);

                                        //get likes info
//                                        JSONObject likes = object.getJSONObject(Constants.LIKES);
//                                        JSONArray likesArray = likes.getJSONArray(Constants.DATA);
//                                        for(int j = 0; j < likesArray.length(); j++) {
//                                            JSONObject likesObject = (JSONObject) likesArray.get(i);
//                                            if(likesObject.getString(Constants.ID).equals(mCurrentUserId)) {
//                                                liked = true;
//                                            }
//                                        }

                                        createFeedItem(postId, pictureId, userId, name,
                                                message, liked, Constants.FACEBOOK);
                                    }
                                }
                                catch (JSONException ignored) {}
                            }
                        }
                    }
            ).executeAsync();
        }
    }

    private void getInstagramData() {
        final InstagramRequest request = new InstagramRequest(BlocParty.this);
        mInstagramAT = request.getAccessToken();

        if(mInstagramAT != null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    final String response = request.getResponse(Constants.INSTAGRAM_FEED_ENDPOINT);

                    runOnUiThread(new Runnable() {
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

                                    createFeedItem(postId, imageUrl, profUrl, name,
                                            message, liked, Constants.INSTAGRAM);
                                }

                            } catch (JSONException ignored) {}
                        }
                    });
                }
            }.start();
        }
    }

    private void createFeedItem(String postId, String pictureId, String userPicId, String name,
                                String message, Boolean liked, String network) {
        FeedItem feedItem = new FeedItem(postId, pictureId, userPicId,
                name, message, liked, network);
        mFeedItems.add(feedItem);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bloc_party, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

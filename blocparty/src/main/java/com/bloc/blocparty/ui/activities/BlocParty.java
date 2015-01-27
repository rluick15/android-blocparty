package com.bloc.blocparty.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.facebook.FacebookRequest;
import com.bloc.blocparty.instagram.InstagramRequest;
import com.bloc.blocparty.twitter.TwitterRequest;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;
import com.bloc.blocparty.utils.ConnectionDetector;
import com.bloc.blocparty.utils.gestureImageView.GestureImageView;
import com.facebook.Session;

import java.util.ArrayList;


public class BlocParty extends Activity {

    private ArrayList<FeedItem> mFeedItems;
    private ListView mFeedList;
    private FeedItemAdapter mAdapter;
    private GestureImageView mFullScreenImage;
    private RelativeLayout mFullScreenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloc_party);

        setupFullScreenMode();

        ConnectionDetector detector = new ConnectionDetector(this);
        if(!detector.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.toast_no_internet), Toast.LENGTH_LONG).show();
        }

        mFeedList = (ListView) findViewById(R.id.feedList);
        mFeedItems = new ArrayList<>();

        mAdapter = new FeedItemAdapter(BlocParty.this, mFeedItems);
        mFeedList.setAdapter(mAdapter);

        FacebookRequest fbRequest = new FacebookRequest(this);
        fbRequest.getFeedData();

        InstagramRequest iRequest = new InstagramRequest(this);
        iRequest.feedRequest();

        TwitterRequest tRequest = new TwitterRequest(this);
        tRequest.feedRequest();
    }

    private void setupFullScreenMode() {
        mFullScreenLayout = (RelativeLayout) findViewById(R.id.fullScreenLayout);
        ImageView quitFullScreen = (ImageView) findViewById(R.id.quitFullScreen);
        quitFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActionBar().show();
                mFullScreenLayout.setVisibility(View.GONE);
                mFeedList.setVisibility(View.VISIBLE);
                mFullScreenLayout.removeView(mFullScreenImage);
            }
        });
    }

    /*
     * If the full screen layout is opened go back to regular layout on back button instead
     * of exiting the app
     */
    @Override
    public void onBackPressed() {
        if(mFullScreenLayout.getVisibility() == View.VISIBLE){
            getActionBar().show();
            mFullScreenLayout.setVisibility(View.GONE);
            mFeedList.setVisibility(View.VISIBLE);
            mFullScreenLayout.removeView(mFullScreenImage);
        }
        else {
            super.onBackPressed();
        }
    }

    public void createFeedItem(FeedItem feedItem) {
        mFeedItems.add(feedItem);
        mAdapter.notifyDataSetChanged();
    }

    public void fullScreenImage(Bitmap bitmap) {
        mFullScreenLayout.setVisibility(View.VISIBLE);
        mFeedList.setVisibility(View.GONE);
        getActionBar().hide();

        //create a new GestureImageView and add it to the full screen layout
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mFullScreenImage = new GestureImageView(this);
        mFullScreenImage.setImageBitmap(bitmap);
        mFullScreenImage.setLayoutParams(params);

        ViewGroup layout = (ViewGroup) findViewById(R.id.fullScreenLayout);
        layout.addView(mFullScreenImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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
        if (id == R.id.action_camera) {
            
        }
        else if(id == R.id.action_filter) {

        }
        return super.onOptionsItemSelected(item);
    }
}

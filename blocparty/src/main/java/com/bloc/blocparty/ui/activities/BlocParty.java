package com.bloc.blocparty.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.facebook.FacebookRequest;
import com.bloc.blocparty.instagram.InstagramRequest;
import com.bloc.blocparty.twitter.TwitterRequest;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;
import com.bloc.blocparty.utils.ConnectionDetector;
import com.facebook.Session;

import java.util.ArrayList;


public class BlocParty extends Activity {

    private ArrayList<FeedItem> mFeedItems;
    private ListView mFeedList;
    private FeedItemAdapter mAdapter;
    private ImageView mFullScreenImage;
    private ImageView mQuitFullScreen;
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
        mFullScreenImage = (ImageView) findViewById(R.id.fullview);
        mQuitFullScreen = (ImageView) findViewById(R.id.quitFullScreen);
        mQuitFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFullScreenLayout.setVisibility(View.GONE);
                mFullScreenImage.setImageBitmap(null);
            }
        });
    }

    public void createFeedItem(FeedItem feedItem) {
        mFeedItems.add(feedItem);
        mAdapter.notifyDataSetChanged();
    }

    public void fullScreenImage(Bitmap bitmap) {
        mFullScreenLayout.setVisibility(View.VISIBLE);
        mFullScreenImage.setImageBitmap(bitmap);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

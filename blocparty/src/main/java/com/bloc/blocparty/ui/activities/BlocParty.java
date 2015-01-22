package com.bloc.blocparty.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bloc.blocparty.FeedItem.FeedItem;
import com.bloc.blocparty.R;
import com.bloc.blocparty.facebook.FacebookRequest;
import com.bloc.blocparty.instagram.InstagramRequest;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;

import java.util.ArrayList;


public class BlocParty extends Activity {

    private ArrayList<FeedItem> mFeedItems;
    private ListView mFeedList;
    private FeedItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloc_party);

        mFeedList = (ListView) findViewById(R.id.feedList);
        mFeedItems = new ArrayList<>();

        mAdapter = new FeedItemAdapter(BlocParty.this, mFeedItems);
        mFeedList.setAdapter(mAdapter);

        FacebookRequest fbRequest = new FacebookRequest(this);
        fbRequest.getFeedData();

        InstagramRequest iRequest = new InstagramRequest(BlocParty.this);
        iRequest.feedRequest();
    }

    public void createFeedItem(String postId, String pictureId, String userPicId, String name,
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

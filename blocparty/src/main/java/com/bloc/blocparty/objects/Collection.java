package com.bloc.blocparty.objects;

import java.util.ArrayList;

/**
 * This class represents a Collection object.
 */
public class Collection {

    private String mName;
    private int mUserCount = 0;
    private ArrayList<FeedItem> mFeedPosts;

    public Collection(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public int getUserCount() {
        return mUserCount;
    }

    public ArrayList<FeedItem> getFeedPosts() {
        return mFeedPosts;
    }

    public void addPost(FeedItem post) {
        mFeedPosts.add(post);

        //mUserCount++;
    }
}

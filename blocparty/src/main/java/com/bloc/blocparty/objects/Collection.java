package com.bloc.blocparty.objects;

import java.util.ArrayList;

/**
 * This class represents a Collection object.
 */
public class Collection {

    private String mName;
    private int mUserCount = 0;
    private ArrayList<FeedItem> mFeedPosts;
    private String[] mImages;
    private ArrayList<String> mUserNames;
    private ArrayList<String> mPostIds;

    public Collection(String name) {
        this.mName = name;
        mImages = new String[4];
        mUserNames = new ArrayList<>();
        mFeedPosts = new ArrayList<>();
        mPostIds = new ArrayList<>();
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

        if(!mPostIds.contains(post.getPostId())) {
            mPostIds.add(post.getPostId());

            if (!mUserNames.contains(post.getName())) {
                mUserNames.add(post.getName());
                mUserCount++;

                if (mUserCount <= 4) {
                    mImages[mUserCount - 1] = post.getProfilePictureUrl();
                }
            }
        }
    }

    public Boolean containsPostId(String postId) {
        return mPostIds.contains(postId);
    }

    public String[] getImages() {
        return mImages;
    }


}

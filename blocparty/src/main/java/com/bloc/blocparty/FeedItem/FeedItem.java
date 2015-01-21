package com.bloc.blocparty.FeedItem;

/**
 * This is a class to help standardize the information from the different api's into one feed
 * item object
 */
public class FeedItem {

    private String postId;
    private String imageUrl;
    private String profilePictureUrl;
    private String name;
    private String message;
    private Boolean favorited;
    private String networkName; //facebook, twitter, or instagram

    public FeedItem(String postId, String pictureId, String userId, String name, String message,
                    Boolean favorited, String networkName) {

        this.postId = postId;
        this.imageUrl = "https://graph.facebook.com/" + pictureId + "/picture";
        this.profilePictureUrl = "http://graph.facebook.com/" + userId + "/picture";
        this.name = name;
        this.message = message;
        this.networkName = networkName;
        this.favorited = favorited;
    }

    public String getPostId() {
        return postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getNetworkName() {
        return networkName;
    }

    public Boolean favorited() {return favorited;}
}

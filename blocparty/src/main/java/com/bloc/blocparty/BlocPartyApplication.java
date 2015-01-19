package com.bloc.blocparty;

import android.app.Application;

import org.brickred.socialauth.android.SocialAuthAdapter;

/**
 * Application class
 */
public class BlocPartyApplication extends Application {

    // SocialAuth Component
    private static SocialAuthAdapter socialAuthAdpater;

    public static SocialAuthAdapter getSocialAuthAdapter() {
        return socialAuthAdpater;
    }

    public void setSocialAuthAdapter(SocialAuthAdapter socialAuthAdapter) {
        this.socialAuthAdpater = socialAuthAdapter;
    }
}

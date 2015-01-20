package com.bloc.blocparty;

import android.app.Application;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Application class
 */
public class BlocPartyApplication extends Application {

    private static DefaultHttpClient httpClientInstance;

    /**
     * This method defines the Http client as a singleton and allows it to be retrieved
     */
    public static DefaultHttpClient getHttpInstance(){
        if(httpClientInstance == null){
            httpClientInstance = new DefaultHttpClient();
        }
        return httpClientInstance;
    }

}

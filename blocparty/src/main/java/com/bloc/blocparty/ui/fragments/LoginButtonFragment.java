package com.bloc.blocparty.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.activities.OnBoardActivity;
import com.bloc.blocparty.utils.Constants;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginButtonFragment extends Fragment {

    private LoginButton authButton;
    private TwitterLoginButton tLoginButton;
    private UiLifecycleHelper uiHelper;
    private Context mContext;

    public LoginButtonFragment() {} // Required empty public constructor

    public LoginButtonFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_buttons, container, false);

        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_photos", "read_stream", "user_status"));

        tLoginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        tLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                ((OnBoardActivity) mContext).nextFragment();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(mContext, mContext.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data);
        tLoginButton.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
        }
        else if (state.isClosed()) {
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
            if(session != null && session.isOpened()){
                ((OnBoardActivity) mContext).nextFragment();
            }
            else if(state.isClosed() || session == null) {
                Toast.makeText(mContext, mContext.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    /*
     * This method selects the built in facebook login button
     */
    public void clickButton(String network) {
        if(network.equals(Constants.FACEBOOK)) {
            authButton.performClick();
        }
        else if(network.equals(Constants.TWITTER)){
            tLoginButton.performClick();
        }
    }
}

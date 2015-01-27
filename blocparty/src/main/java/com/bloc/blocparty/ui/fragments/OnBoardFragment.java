package com.bloc.blocparty.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bloc.blocparty.R;
import com.bloc.blocparty.instagram.InstagramApp;
import com.bloc.blocparty.ui.activities.OnBoardActivity;
import com.bloc.blocparty.utils.Constants;
import com.twitter.sdk.android.Twitter;

/**
 * This fragment allows the user to link one or more of their accounts during on boarding
 */
public class OnBoardFragment extends Fragment {

    private int mNetworkId;
    private Button mSignInButton;
    private Context mContext;
    private InstagramApp mApp;

    public OnBoardFragment() {} // Required empty public constructor

    public OnBoardFragment(Context context, int networkId) {
        this.mContext = context;
        this.mNetworkId = networkId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_on_board, container, false);

        mSignInButton = (Button) rootView.findViewById(R.id.selectButton);
        Button dismissButton = (Button) rootView.findViewById(R.id.dismissButton);
        TextView title = (TextView) rootView.findViewById(R.id.networkTitle);
        TextView message = (TextView) rootView.findViewById(R.id.networkMessage);
        ImageView icon = (ImageView) rootView.findViewById(R.id.imageIcon);

        Log.e("WTF", String.valueOf(mNetworkId));
        if(mNetworkId == 0) {
            mSignInButton.setTextColor(getResources().getColor(R.color.facebook_blue));
            title.setText(getString(R.string.onboard_title_facebook));
            message.setText(getString(R.string.onboard_message_facebook));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.facebook));
            facebookOnBoard();
        }
        else if(mNetworkId == 1) {
            mSignInButton.setTextColor(getResources().getColor(R.color.twitter_blue));
            title.setText(getString(R.string.onboard_title_twitter));
            message.setText(getString(R.string.onboard_message_twitter));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.twitter));
            twitterOnBoard();
        }
        else if(mNetworkId == 2) {
            mSignInButton.setTextColor(getResources().getColor(R.color.instagram_blue));
            title.setText(getString(R.string.onboard_title_instagram));
            message.setText(getString(R.string.onboard_message_instagram));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.instagram));
            instagramOnBoard();
        }

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetworkId++;
                ((OnBoardActivity) mContext).loadOnBoardFrag(mNetworkId);
            }
        });

        return rootView;
    }

    private void instagramOnBoard() {
        mApp = new InstagramApp(mContext, Constants.CLIENT_ID, Constants.CLIENT_SECRET,
                Constants.CALLBACK_URL);
        mApp.setListener(listener);
        if(mApp.hasAccessToken()) {
            nextFragment();
        }
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mApp.hasAccessToken()) {
                    mApp.authorize();
                }
            }
        });
    }

    private void twitterOnBoard() {
        if(Twitter.getSessionManager().getActiveSession() != null) {
            nextFragment();
        }
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnBoardActivity) mContext).clickLoginButton(Constants.TWITTER);
            }
        });
    }

    private void facebookOnBoard() {
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnBoardActivity) mContext).clickLoginButton(Constants.FACEBOOK);
            }
        });
    }

    public void nextFragment() {
        mNetworkId++;
        ((OnBoardActivity) mContext).loadOnBoardFrag(mNetworkId);
    }

    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {
        @Override
        public void onSuccess() {
            nextFragment();
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(mContext, mContext.getString(R.string.connection_failed), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnBoardingInteractionListener {
        public void loadOnBoardFrag(int networkId);
    }
}

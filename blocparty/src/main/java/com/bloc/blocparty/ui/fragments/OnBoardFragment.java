package com.bloc.blocparty.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.activities.OnBoardActivity;

/**
 * This fragment allows the user to link one or more of their accounts during on boarding
 */
public class OnBoardFragment extends Fragment {

    private int mNetworkId;
    private Button mSignInButton;
    private Context mContext;

    public OnBoardFragment() {} // Required empty public constructor

    public OnBoardFragment(Context context, int networkId) {
        this.mContext = context;
        this.mNetworkId = networkId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_on_board, container, false);

        mSignInButton = (Button) rootView.findViewById(R.id.selectButton);
        Button dismissButton = (Button) rootView.findViewById(R.id.dismissButton);
        TextView title = (TextView) rootView.findViewById(R.id.networkTitle);
        TextView message = (TextView) rootView.findViewById(R.id.networkMessage);
        ImageView icon = (ImageView) rootView.findViewById(R.id.imageIcon);

        if(mNetworkId == 0) {
            mSignInButton.setTextColor(getResources().getColor(R.color.facebook_blue));
            title.setText(getString(R.string.onboard_title_facebook));
            message.setText(getString(R.string.onboard_message_facebook));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_facebook_icon));
            facebookOnBoard();
        }
        else if(mNetworkId == 1) {
            mSignInButton.setTextColor(getResources().getColor(R.color.twitter_blue));
            title.setText(getString(R.string.onboard_title_twitter));
            message.setText(getString(R.string.onboard_message_twitter));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter_icon));
            twitterOnBoard();
        }
        else if(mNetworkId == 2) {
            mSignInButton.setTextColor(getResources().getColor(R.color.instagram_blue));
            title.setText(getString(R.string.onboard_title_instagram));
            message.setText(getString(R.string.onboard_message_instagram));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_instagram_icon));
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

    }

    private void twitterOnBoard() {

    }

    private void facebookOnBoard() {

    }

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

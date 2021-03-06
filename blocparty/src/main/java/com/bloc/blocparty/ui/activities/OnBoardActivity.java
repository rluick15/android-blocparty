package com.bloc.blocparty.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.fragments.LoginButtonFragment;
import com.bloc.blocparty.ui.fragments.OnBoardFragment;
import com.bloc.blocparty.utils.Constants;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class OnBoardActivity extends FragmentActivity implements OnBoardFragment.OnBoardingInteractionListener {

    private LoginButtonFragment loginButtonFrag;
    private OnBoardFragment obFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide the action bar
        setContentView(R.layout.activity_on_board);

        checkIfAlreadyOnBoarded();
        loadOnBoardFrag(Constants.NETWORK_ID);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loginButtonFrag = new LoginButtonFragment(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, loginButtonFrag)
                    .commit();
        }
        else {
            // Or set the fragment from restored state info
            loginButtonFrag = (LoginButtonFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(loginButtonFrag != null) {
            loginButtonFrag.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
             * This mehtod checks if the user has gone trough the onboarding process and if so, launches
             * the main activity
             */
    private void checkIfAlreadyOnBoarded() {
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, BlocParty.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }
    }

    /*
     * This mehtod loads the appropriate on boarding fragment as the user clicks through them and
     * after they are all done, it loads the main activity
     *
     * @params: networkId The int id of which network is up for sign in
     */
    public void loadOnBoardFrag(int networkId) {
        if(networkId == 3) {
            Toast.makeText(this, getString(R.string.toast_onboard_complete), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, BlocParty.class);
            startActivity(intent);
            finish();
        }
        else {
            obFrag = new OnBoardFragment(this, networkId);
            getFragmentManager().beginTransaction().replace(R.id.onBoardFrag, obFrag).commit();
        }
    }

    /*
     * This method is called when the user selects to login in to facebook or twitter. It clicks the
     * built in facebook/twitter login button in the fragment
     */
    public void clickLoginButton(String network) {
       loginButtonFrag.clickButton(network);
    }

    public void nextFragment() {
        obFrag.nextFragment();
    }
}

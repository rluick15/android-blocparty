package com.bloc.blocparty.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.fragments.OnBoardFragment;
import com.bloc.blocparty.utils.Constants;

public class OnBoardActivity extends FragmentActivity implements OnBoardFragment.OnBoardingInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide the action bar
        setContentView(R.layout.activity_on_board);

        //checkIfAlreadyOnBoarded();
        loadOnBoardFrag(Constants.NETWORK_ID);
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
            Toast.makeText(this, "On-Boarding Complete!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, BlocParty.class);
            startActivity(intent);
            finish();
        }
        else {
            OnBoardFragment obFrag = new OnBoardFragment(this, networkId);
            getFragmentManager().beginTransaction().replace(R.id.onBoardFrag, obFrag).commit();
        }
    }
}

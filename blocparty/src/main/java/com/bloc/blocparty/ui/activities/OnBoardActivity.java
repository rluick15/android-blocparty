package com.bloc.blocparty.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.bloc.blocparty.R;
import com.bloc.blocparty.ui.fragments.OnBoardFragment;

public class OnBoardActivity extends FragmentActivity implements OnBoardFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide the action bar
        setContentView(R.layout.activity_on_board);

        //checkIfAlreadyOnBoarded();

        OnBoardFragment obFrag = new OnBoardFragment();
        getFragmentManager().beginTransaction().replace(R.id.onBoardFrag, obFrag).commit();
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

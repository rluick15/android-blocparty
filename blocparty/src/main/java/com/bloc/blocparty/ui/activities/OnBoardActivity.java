package com.bloc.blocparty.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.bloc.blocparty.R;

public class OnBoardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide the action bar
        setContentView(R.layout.activity_on_board);

        checkIfAlreadyOnBoarded();
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
}

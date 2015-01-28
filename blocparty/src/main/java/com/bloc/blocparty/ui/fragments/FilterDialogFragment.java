package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;

import com.bloc.blocparty.R;
import com.bloc.blocparty.objects.Collection;
import com.bloc.blocparty.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This fragment handles filtering the
 */
public class FilterDialogFragment extends DialogFragment {

    private Context mContext;
    private ListView mCollectionList;

    public FilterDialogFragment() {} // Required empty public constructor

    public FilterDialogFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //get Collection Array
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(Constants.PREFS, 0);
        String json = sharedPrefs.getString(Constants.COLLECTION_ARRAY, null);
        Type type = new TypeToken<ArrayList<Collection>>(){}.getType();
        ArrayList<Collection> collections = new Gson().fromJson(json, type);

        mCollectionList = (ListView) view.findViewById(R.id.collectionList);
        mCollectionList.setEmptyView(view.findViewById(android.R.id.empty));

        ImageButton addCollection = (ImageButton) view.findViewById(R.id.addCollection);
        addCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}

package com.bloc.blocparty.ui.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bloc.blocparty.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddToCollectionFragment extends Fragment {

    private Context mContext;

    public AddToCollectionFragment() {} // Required empty public constructor

    public AddToCollectionFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
        
        return view;
    }


}

package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bloc.blocparty.R;

/**
 * This fragment handles filtering the
 */
public class FilterDialogFragment extends DialogFragment {

    private Context mContext;

    public FilterDialogFragment() {} // Required empty public constructor

    public FilterDialogFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
        return view;
    }
}

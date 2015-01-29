package com.bloc.blocparty.ui.fragments;


import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bloc.blocparty.R;
import com.bloc.blocparty.objects.Collection;
import com.bloc.blocparty.objects.FeedItem;
import com.bloc.blocparty.ui.adapters.CollectionListItemAdapter;
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
    private Collection mCollection;
    private Button mSelectCollectionButton;

    public FilterDialogFragment() {} // Required empty public constructor

    public FilterDialogFragment(Context context) {
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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
        dialogTitle.setText(getString(R.string.title_choose_collection));

        //get Collection Array
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(Constants.PREFS, 0);
        String json = sharedPrefs.getString(Constants.COLLECTION_ARRAY, null);
        Type type = new TypeToken<ArrayList<Collection>>(){}.getType();
        final ArrayList<Collection> collections = new Gson().fromJson(json, type);

        mCollectionList = (ListView) view.findViewById(R.id.collectionList);
        mCollectionList.setEmptyView(view.findViewById(android.R.id.empty));

        if(collections != null) {
            CollectionListItemAdapter adapter = new CollectionListItemAdapter(mContext, collections);
            mCollectionList.setAdapter(adapter);
        }

        ImageButton addCollection = (ImageButton) view.findViewById(R.id.addCollection);
        addCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCollectionDialogFragment dialogFragment
                        = new CreateCollectionDialogFragment(FilterDialogFragment.this, mContext, collections);
                dialogFragment.show(getFragmentManager(), "dialog");
            }
        });

        mSelectCollectionButton = (Button) view.findViewById(R.id.selectCollectionButton);
        mSelectCollectionButton.setText(getString(R.string.button_select_collection));
        mSelectCollectionButton.setEnabled(false);

        mCollectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCollection = (Collection) parent.getItemAtPosition(position);
                mSelectCollectionButton.setEnabled(true);
            }
        });

        mSelectCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mCollection.getName();
                ArrayList<FeedItem> feedItems = mCollection.getFeedPosts();
            }
        });

        return view;
    }
}

package com.bloc.blocparty.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.bloc.blocparty.BlocPartyApplication;
import com.bloc.blocparty.R;
import com.bloc.blocparty.facebook.FacebookRequest;
import com.bloc.blocparty.instagram.InstagramRequest;
import com.bloc.blocparty.objects.FeedItem;
import com.bloc.blocparty.twitter.TwitterRequest;
import com.bloc.blocparty.ui.adapters.FeedItemAdapter;
import com.bloc.blocparty.ui.fragments.FilterDialogFragment;
import com.bloc.blocparty.ui.fragments.UploadPhotoDialogFragment;
import com.bloc.blocparty.utils.ConnectionDetector;
import com.bloc.blocparty.utils.Constants;
import com.bloc.blocparty.utils.gestureImageView.GestureImageView;
import com.facebook.Session;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;
import java.util.ArrayList;


public class BlocParty extends Activity {

    private ArrayList<FeedItem> mFeedItems;
    private ListView mFeedList;
    private FeedItemAdapter mAdapter;
    private GestureImageView mFullScreenImage;
    private RelativeLayout mFullScreenLayout;
    private LinearLayout mFilterHeader;
    private ImageView mExitFilter;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private TextView mCollectionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloc_party);

        setupFullScreenMode();
        setupHeaderViews();

        ConnectionDetector detector = new ConnectionDetector(this);
        if(!detector.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.toast_no_internet), Toast.LENGTH_LONG).show();
        }

        mFeedList = (ListView) findViewById(R.id.feedList);
        mFeedItems = new ArrayList<>();

        setAdapter();

        FacebookRequest fbRequest = new FacebookRequest(this);
        fbRequest.getFeedData();

        InstagramRequest iRequest = new InstagramRequest(this);
        iRequest.feedRequest();

        TwitterRequest tRequest = new TwitterRequest(this);
        tRequest.feedRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get(Constants.DATA);
            UploadPhotoDialogFragment fragment = new UploadPhotoDialogFragment(this, imageBitmap);
            fragment.show(getFragmentManager(), "dialog");
        }
    }

    /*
     * If the full screen layout is opened go back to regular layout on back button instead
     * of exiting the app
     */
    @Override
    public void onBackPressed() {
        if(mFullScreenLayout.getVisibility() == View.VISIBLE){
            getActionBar().show();
            mFullScreenLayout.setVisibility(View.GONE);
            mFeedList.setVisibility(View.VISIBLE);
            mFullScreenLayout.removeView(mFullScreenImage);
        }
        else {
            super.onBackPressed();
        }
    }

    /*
     * This method sets the custom adapter for the photo feed
     */
    private void setAdapter() {
        mAdapter = new FeedItemAdapter(BlocParty.this, mFeedItems);
        mFeedList.setAdapter(mAdapter);
    }

    /*
     * This method sets the header views for when a filter is set. It sets the name of the filter
     * as well as the exit button and the collage of images of users contained in the filter group
     */
    private void setupHeaderViews() {
        mFilterHeader = (LinearLayout) findViewById(R.id.filterHeader);
        mCollectionTitle = (TextView) findViewById(R.id.collectionName);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);
        mExitFilter = (ImageView) findViewById(R.id.exitButton);
        mExitFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterHeader.setVisibility(View.GONE);
                image1.setImageBitmap(null);
                image2.setImageBitmap(null);
                image3.setImageBitmap(null);
                image4.setImageBitmap(null);
                setAdapter();
            }
        });
    }

    /*
     * This method is called each time a new feed item needs to be created in a twitter, facebook,
     * or instagram request. It adds it to the ArrayList being displated in the adapter and then
     * updates the adapter
     *
     * @param feedItem the feedItem object to be added to the adapter
     */
    public void createFeedItem(FeedItem feedItem) {
        mFeedItems.add(feedItem);
        mAdapter.notifyDataSetChanged();
    }

    /*
     * This method sets the full screen image layout for when the user clicks on an image to
     * display it in full screen.
     */
    private void setupFullScreenMode() {
        mFullScreenLayout = (RelativeLayout) findViewById(R.id.fullScreenLayout);
        ImageView quitFullScreen = (ImageView) findViewById(R.id.quitFullScreen);
        quitFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActionBar().show();
                mFullScreenLayout.setVisibility(View.GONE);
                mFeedList.setVisibility(View.VISIBLE);
                mFullScreenLayout.removeView(mFullScreenImage);
            }
        });
    }

    /*
     * This method is called when a user clicks on an image to make it full screen. It populates the
     * previously created full screen views with the correct image
     *
     * @param bitmap the image being put into full screen mode
     */
    public void fullScreenImage(Bitmap bitmap) {
        mFullScreenLayout.setVisibility(View.VISIBLE);
        mFeedList.setVisibility(View.GONE);
        getActionBar().hide();

        //create a new GestureImageView and add it to the full screen layout
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mFullScreenImage = new GestureImageView(this);
        mFullScreenImage.setImageBitmap(bitmap);
        mFullScreenImage.setLayoutParams(params);

        ViewGroup layout = (ViewGroup) findViewById(R.id.fullScreenLayout);
        layout.addView(mFullScreenImage);
    }

    /*
     * This method takes the filter paramters and resets the adapter to show feed items only
     * from the selected collection
     *
     * param:name the name of the collection
     * param:feeditems the arraylist containing the feeditems to disply
     * param:images a string array containing the urls of the images of users in the collection
     */
    public void filterAdapter(String name, ArrayList<FeedItem> feedItems, String[] images) {
        mFilterHeader.setVisibility(View.VISIBLE);

        mCollectionTitle.setText(name);

        if(images[0] != null) {
            new ImageLoadTask(images[0], image1).execute();
        }
        if(images[1] != null) {
            new ImageLoadTask(images[1], image2).execute();
        }

        if(images[2] != null) {
            new ImageLoadTask(images[2], image3).execute();
        }

        if(images[3] != null) {
            new ImageLoadTask(images[3], image4).execute();
        }

        FeedItemAdapter adapter = new FeedItemAdapter(BlocParty.this, feedItems);
        mFeedList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bloc_party, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_camera) {
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
                Toast.makeText(this, getString(R.string.toast_no_camera), Toast.LENGTH_SHORT).show();
            }
            else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, Constants.REQUEST_IMAGE_CAPTURE);
                }
            }
        }
        else if(id == R.id.action_filter) {
            FilterDialogFragment fragment = new FilterDialogFragment(this);
            fragment.show(getFragmentManager(), "dialog");
        }
        return super.onOptionsItemSelected(item);
    }


    /*
     * This AsyncTask takes an image url and downloads the image into Bitmap form using an HTTPGet
     * request. It then sets the image to the appropriate ImageView
     *
     * @param url the web url of the image
     * @param imageView the imageView the bitmap will be set to
     */
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView image;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.image = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap myBitmap = null;
            try {
                URL imageUrl = new URL(url);
                HttpGet httpRequest = new HttpGet(imageUrl.toString());
                DefaultHttpClient httpclient = BlocPartyApplication.getHttpInstance();
                HttpResponse response = httpclient.execute(httpRequest);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                myBitmap = BitmapFactory.decodeStream(bufHttpEntity.getContent());
                httpRequest.abort();
            } catch (Exception ignored) {}
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            image.setImageBitmap(bitmap);
        }
    }
}

package com.example.developerslife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ImageView mMainLoadView;
    private TextView mtextView;

    Button mButtonPrev;
    Button mButtonNext;

    ArrayList<Parcelable> mCache = new ArrayList<Parcelable>();
    int mPosInCache = 1;
    boolean mIsFirstLoad = true;

    //prevent user from very fast tapping
    boolean mIsCanGoOn = true;

    boolean mIsPrevEnabled = false;
    boolean mIsError = false;
    boolean mIsLoading = false;

    public void updateImg(String pmImgUrl) {
        Glide
                .with(this)
                .load(pmImgUrl)
                .thumbnail(Glide.with(this).load(R.drawable.loader_placeholder))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mImageView);
    }

    public void loadingScreen(boolean pmIsLoading) {
        if (pmIsLoading) {
            mIsLoading = true;
            mImageView.setVisibility(View.GONE);
            mtextView.setVisibility(View.GONE);
            mMainLoadView.setVisibility(View.VISIBLE);

        } else {
            mIsLoading = false;
            mMainLoadView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            mtextView.setVisibility(View.VISIBLE);
        }
    }

    public void updatePost(CachedItem pmLastItem) {

        TextView textView = (TextView) findViewById(R.id.textView);

        if (pmLastItem!=null) {
            textView.setText(pmLastItem.getDescription());
            updateImg(pmLastItem.getImgUrl());
        } else {
            mIsCanGoOn = false;
            String pmUrl = getResources().getString(R.string.api_url);
            RequestQueue queue = Volley.newRequestQueue(this);

            // Request a string response from the provided URL.
            StringRequest lvStringRequest = new StringRequest(Request.Method.GET, pmUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String pmResponse) {
                            loadingScreen(false);
                            JSONObject lvJson = null;
                            try {
                                lvJson = new JSONObject(pmResponse);
                                String lvGifURL = lvJson.getString("gifURL");
                                String lvDescription = lvJson.getString("description");

                                textView.setText(lvDescription);
                                mCache.add(new CachedItem(lvGifURL, lvDescription));
                                mIsCanGoOn = true;
                                updateImg(lvGifURL);
                                mIsFirstLoad = false;
                            } catch (JSONException pmE) {
                                pmE.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError pmError) {
                    loadingScreen(false);
                    onError();
                    mIsFirstLoad = false;
                }
            });

            queue.add(lvStringRequest);
            loadingScreen(true);
        }
    }

    @Override
    protected void onCreate(Bundle pmSavedInstanceState) {
        super.onCreate(pmSavedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonPrev = (Button) findViewById(R.id.buttonPrev);
        mButtonNext = (Button) findViewById(R.id.buttonNext);
        mImageView = findViewById(R.id.imageView);
        mtextView = findViewById(R.id.textView);
        mMainLoadView = findViewById(R.id.mainLoadView);

        mMainLoadView.setVisibility(View.GONE);
        mButtonPrev.setEnabled(false);
        mIsPrevEnabled = false;

        if (pmSavedInstanceState == null) {
            updatePost(null);
        } else {
            boolean lvIsError = pmSavedInstanceState.getBoolean("isError");
            boolean lvIsLoading = pmSavedInstanceState.getBoolean("isLoading");
            if (!lvIsError && !lvIsLoading) {
                int lvRestoredPosInCache = pmSavedInstanceState.getInt("mPosInCache");
                ArrayList<Parcelable> mCacheRestored = mCache = pmSavedInstanceState.getParcelableArrayList("mCache");
                CachedItem lvRestredCachedItem = (CachedItem)mCacheRestored.get(lvRestoredPosInCache - 1);
                updatePost(lvRestredCachedItem);
            } else {
                setContentView(R.layout.activity_main_error);
            }
        }
    }

    public void onClickPrev(View pmView) {
        mPosInCache -= 1;
        updatePost((CachedItem) mCache.get(mPosInCache - 1));
        if (mPosInCache == 1) {
            mButtonPrev.setEnabled(false);
            mIsPrevEnabled = false;
        }
    }

    public void onClickNext(View pmView) {
        // if user taps too fast to load new post - stop him
        if (mIsCanGoOn) {
            if (mPosInCache == mCache.size() || mIsFirstLoad) {
                updatePost(null);
                mPosInCache += 1;
            } else {
                mPosInCache += 1;
                updatePost((CachedItem) mCache.get(mPosInCache - 1));
            }
        }

        if (mPosInCache == 2) {
            mButtonPrev.setEnabled(true);
            mIsPrevEnabled = true;
        }
    }

    public void onError() {
        setContentView(R.layout.activity_main_error);
        mIsError = true;
    }
    public void onRetry(View pmView) {
        mIsError = false;
        setContentView(R.layout.activity_main);
        mButtonPrev = (Button) findViewById(R.id.buttonPrev);
        mButtonNext = (Button) findViewById(R.id.buttonNext);
        mImageView = findViewById(R.id.imageView);
        updatePost(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("mCache", mCache);
        savedInstanceState.putInt("mPosInCache", mPosInCache);
        savedInstanceState.putBoolean("mIsFirstLoad", mIsFirstLoad);
        savedInstanceState.putBoolean("mIsCanGoOn", mIsCanGoOn);
        savedInstanceState.putBoolean("isPrevEnabled", mIsPrevEnabled);
        savedInstanceState.putBoolean("isError", mIsError);
        savedInstanceState.putBoolean("isLoading", mIsLoading);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCache = savedInstanceState.getParcelableArrayList("mCache");
        mPosInCache = savedInstanceState.getInt("mPosInCache");
        mIsFirstLoad = savedInstanceState.getBoolean("mIsFirstLoad");
        mIsCanGoOn = savedInstanceState.getBoolean("mIsCanGoOn");
        mIsPrevEnabled = savedInstanceState.getBoolean("isPrevEnabled");
        mIsError = savedInstanceState.getBoolean("isError");
        mIsLoading = savedInstanceState.getBoolean("isLoading");

        mButtonPrev.setEnabled(mIsPrevEnabled);
    }
}
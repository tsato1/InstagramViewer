package com.takahidesato.popularphotos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private final static String CLIENT_ID= "";

    private ArrayList<PhotoItem> mPhotoItemArrayList;
    private PhotoItemAdapter mPhotoItemAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mPhotoItemArrayList = new ArrayList<>();
        mPhotoItemAdapter = new PhotoItemAdapter(this, mPhotoItemArrayList);
        ListView listView = (ListView) findViewById(R.id.lsv_photos);
        listView.setAdapter(mPhotoItemAdapter);

        fetchPopularPhotos();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });

    }

    private void fetchPopularPhotos() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray photos = null;
                try {
                    JSONArray photosJArray = response.getJSONArray("data");
                    for (int i = 0; i < photosJArray.length(); i++) {
                        JSONObject photoJObj = photosJArray.getJSONObject(i);
                        PhotoItem item = new PhotoItem();
                        item.type = photoJObj.getString("type");
                        item.username = photoJObj.getJSONObject("user").getString("username");
                        item.userPhotoUrl = photoJObj.getJSONObject("user").getString("profile_picture");
                        item.caption = photoJObj.getJSONObject("caption").getString("text");
                        item.imageUrl = photoJObj.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        item.videoUrl = item.type.equals("video")? photoJObj.getJSONObject("videos").getJSONObject("standard_resolution").getString("url"):null;
                        item.imageHeight = photoJObj.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        item.likesCount = photoJObj.getJSONObject("likes").getInt("count");
                        item.createdTime = photoJObj.getInt("created_time");
                        mPhotoItemArrayList.add(item);

                        if (item.videoUrl != null) Log.d("videourl", item.videoUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPhotoItemAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(MainActivity.class.getSimpleName(), "AsyncHttp Failed");
            }
        });
    }
}

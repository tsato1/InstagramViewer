package com.takahidesato.popularphotos;

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
                Log.d("test", response.toString());

                JSONArray photos = null;
                try {
                    JSONArray photosJArray = response.getJSONArray("data");
                    for (int i = 0; i < photosJArray.length(); i++) {
                        JSONObject photoJObj = photosJArray.getJSONObject(i);
                        PhotoItem item = new PhotoItem();
                        item.username = photoJObj.getJSONObject("user").getString("username");
                        item.caption = photoJObj.getJSONObject("caption").getString("text");
                        item.imageUrl = photoJObj.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        item.imageHeight = photoJObj.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        item.likesCount = photoJObj.getJSONObject("likes").getInt("count");
                        item.createdTime = photoJObj.getJSONObject("caption").getInt("created_time");
                        mPhotoItemArrayList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPhotoItemAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(MainActivity.class.getSimpleName(), "AsyncHttp Failed");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

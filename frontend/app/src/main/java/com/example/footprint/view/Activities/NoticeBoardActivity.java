package com.example.footprint.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.footprint.R;
import com.example.footprint.model.Post;
import com.example.footprint.model.PostList;
import com.example.footprint.net.RestAPI;
import com.example.footprint.view.Fragment.NoticeBoardBlueFragment;
import com.example.footprint.view.Fragment.NoticeBoardRedFragment;
import com.example.footprint.view.Fragment.NoticeBoardYellowFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NoticeBoardActivity extends AppCompatActivity {

    private Button btnNoticeRed, btnNoticeYellow, btnNoticeBlue;
    private ArrayList<PostList> posts, postTypeB;
    public PostList postTypeA, postTypeC;
    public String typeA;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    NoticeBoardRedFragment noticeBoardRedFragment;
    NoticeBoardYellowFragment noticeBoardYellowFragment;
    NoticeBoardBlueFragment noticeBoardBlueFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);


        Intent intent = getIntent();
        classificationType(intent.getExtras().getDouble("lat"), intent.getExtras().getDouble("lng"));



//
//
//        if(posts != null) {
//              for (int i = 0; i < posts.size(); i++) {
//                    if (posts.get(i).getType().equals("0")) {
//                        postTypeA = posts.get(i);
//                        Log.d("test", "test");
//                    } else if (posts.get(i).getType().equals("1")) {
//                        postTypeB.add(posts.get(i));
//                    } else if (posts.get(i).getType().equals("2")) {
//                        postTypeC = posts.get(i);
//                    }
//                }
//        }

        btnNoticeRed = (Button) findViewById(R.id.btn_notice_red);
        btnNoticeYellow = (Button) findViewById(R.id.btn_notice_yellow);
        btnNoticeBlue = (Button) findViewById(R.id.btn_notice_blue);

        btnNoticeRed.setOnClickListener(new BtnOnClickListener());
        btnNoticeYellow.setOnClickListener(new BtnOnClickListener());
        btnNoticeBlue.setOnClickListener(new BtnOnClickListener());

        noticeBoardRedFragment = new NoticeBoardRedFragment();
        noticeBoardYellowFragment = new NoticeBoardYellowFragment();
        noticeBoardBlueFragment = new NoticeBoardBlueFragment();


//      Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
//      setSupportActionBar(tb);



    }

    private void classificationType(double lat, double lng) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", Double.toString(lat));
            jsonObject.put("longitude", Double.toString(lng));
        } catch (JSONException e) {

        }
        Log.d("why", "i don't know");

        RestAPI.post("/post/list", jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<PostList> postLists;
                try {
                    Gson gson = new Gson();
                    Log.d("testResponse", "" + response);
                    posts = gson.fromJson(response.toString(), new TypeToken<ArrayList<PostList>>() {}.getType());
                    setType(posts);

                } catch (Exception e) {
                    posts = null;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("testResponse", "Fail");
            }
        });

    }

    private void setType(ArrayList<PostList> posts){
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getType().equals("0")) {
                postTypeA = posts.get(i);
                typeA = postTypeA.getPostId();
                Log.d("test", "test");
            } else if (posts.get(i).getType().equals("1")) {
                postTypeB.add(posts.get(i));
            } else if (posts.get(i).getType().equals("2")) {
                postTypeC = posts.get(i);
            }
        }
        setFragment(0);
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_notice_red:
                    setFragment(0);
                    break;
                case R.id.btn_notice_yellow:
                    setFragment(1);
                    break;
                case R.id.btn_notice_blue:
                    setFragment(2);
                    break;
            }
        }
    }

    private void setFragment(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n) {
            case 0:
                fragmentTransaction.replace(R.id.fragment, noticeBoardRedFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.fragment, noticeBoardYellowFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.fragment, noticeBoardBlueFragment);
                fragmentTransaction.commit();
                break;
        }

    }
}

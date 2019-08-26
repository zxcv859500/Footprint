package com.example.footprint.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.footprint.R;
import com.example.footprint.model.Comment;
import com.example.footprint.model.PostList;
import com.example.footprint.model.Token;
import com.example.footprint.net.JwtDecoder;
import com.example.footprint.net.RestAPI;
import com.example.footprint.view.Fragment.NoticeBoardBlueFragment;
import com.example.footprint.view.Fragment.NoticeBoardRedFragment;
import com.example.footprint.view.Fragment.NoticeBoardYellowFragment;
import com.example.footprint.view.Fragment.NoticeBoardYellowListFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeBoardActivity extends AppCompatActivity {

    private Button btnNoticeRed, btnNoticeYellow, btnNoticeBlue;
    private ArrayList<PostList> posts;
    public ArrayList<PostList>  postTypeB = new ArrayList<PostList>();
    private PostList postTypeA, postTypeC,postTypeB_tmp;
    public String typeA, typeC, typeB;
    private ArrayList<Comment> comments;
    private String previlage;

    public double lat, lng;
    public String thoroughfare;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    NoticeBoardRedFragment noticeBoardRedFragment;
    NoticeBoardYellowListFragment noticeBoardYellowListFragment;
    NoticeBoardBlueFragment noticeBoardBlueFragment;
    NoticeBoardYellowFragment noticeBoardYellowFragment;

    public ArrayList<Comment> getComments() {
        return comments;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);


        Intent intent = getIntent();
        lat = intent.getExtras().getDouble("lat");
        lng = intent.getExtras().getDouble("lng");
        classificationType(lat, lng);


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

        btnNoticeYellow.setEnabled(false);
        btnNoticeBlue.setEnabled(false);

        btnNoticeRed.setOnClickListener(new BtnOnClickListener());
        btnNoticeYellow.setOnClickListener(new BtnOnClickListener());
        btnNoticeBlue.setOnClickListener(new BtnOnClickListener());

        noticeBoardRedFragment = new NoticeBoardRedFragment();
        noticeBoardYellowListFragment = new NoticeBoardYellowListFragment();
        noticeBoardBlueFragment = new NoticeBoardBlueFragment();
        noticeBoardYellowFragment = new NoticeBoardYellowFragment();


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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;
                ArrayList<PostList> postLists;
                try {
                    array = response.getJSONArray("posts");
                    Gson gson = new Gson();
                    Log.d("testResponse", "" + array);

                    posts = gson.fromJson(array.toString(), new TypeToken<ArrayList<PostList>>() {}.getType());
                    Log.d("testPosts",""+posts);

                    setType(posts);
                    thoroughfare = response.getString("road");
                    Log.e("thoroughfare", thoroughfare);

                } catch (JSONException e) {
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
        Log.d("test_",posts.size()+"");
        int tmp = posts.size();
        for (int i = 0; i < tmp; i++) {
            final int index;
            index = i;
            Log.d("test_roof","for"+index);
            if (posts.get(i).getType().equals("0")) {
                postTypeA = posts.get(i);
                typeA = postTypeA.getPostId();
                Log.d("test", "test");
            } else if (posts.get(index).getType().equals("1")) {
                Log.d("test_type", "testB");
                postTypeB_tmp = posts.get(index);
                postTypeB.add(postTypeB_tmp);
            } else if (posts.get(index).getType().equals("2")) {
                postTypeC = posts.get(i);
                typeC = postTypeC.getPostId();
                btnNoticeBlue.setEnabled(true);
                Log.d("test", typeC);
            }
        }
        setFragment(0);
        Log.d("test_", "typeA");
        if(postTypeB_tmp != null){
            btnNoticeYellow.setEnabled(true);
        }


//        RestAPI.get("/comment/" + typeA, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Gson gson = new Gson();
//                try {
//                    comments = gson.fromJson(response.getJSONArray("result").toString(), new TypeToken<ArrayList<Comment>>() {
//                    }.getType());
//                    Log.d("test_", comments.get(1).getNickname());
//                    setFragment(0);
//                } catch (JSONException e) {
//
//                }
//
//            }
//        });


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

    public void setFragment(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n) {
            case 0:
                fragmentTransaction.replace(R.id.fragment, noticeBoardRedFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.fragment, noticeBoardYellowListFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.fragment, noticeBoardBlueFragment);
                fragmentTransaction.commit();
                break;
            case 3:
                fragmentTransaction.replace(R.id.fragment, noticeBoardYellowFragment);
                fragmentTransaction.commit();
                break;
        }

    }

    public void refresh(){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(noticeBoardRedFragment);
        fragmentTransaction.attach(noticeBoardRedFragment);
        fragmentTransaction.commit();

//        noticeBoardRedFragment.getCommentAdapter().notifyDataSetChanged();
        Log.d("test_","why?");

    }
    public void refreshBlue(){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(noticeBoardBlueFragment);
        fragmentTransaction.attach(noticeBoardBlueFragment);
        fragmentTransaction.commit();

    }

    public void refreshYellow(){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(noticeBoardYellowFragment);
        fragmentTransaction.attach(noticeBoardYellowFragment);
        fragmentTransaction.commit();
    }

    public void setTypeB(String postNum){
        typeB = postNum;
    }



    public void dlePost(String postNum){
        Log.d("test_del","start");
        RestAPI.get("/post/"+postNum+"/delete",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("test_del",response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("test_del",responseString);
            }
        });
        Log.d("test_del","finhttp");
        Intent intent = new Intent(NoticeBoardActivity.this, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void addLike(String postNum){

        RestAPI.get("/post/"+postNum+"/like",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });

    }
    public void subLike(String postNum){

        RestAPI.get("/post/"+postNum+"/like/cancel",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });

    }

    public Boolean decoderJwt(){
        Token token = Token.getTokenObject();
        String tmp;
        tmp = token.getTokenKey();
        try {
            JSONObject jsonObject = new JSONObject(JwtDecoder.decode(tmp));
            previlage = jsonObject.get("previlage").toString();
        } catch (JSONException e) {
            previlage = "99";
            e.printStackTrace();
        }



        Log.d("testToekn",JwtDecoder.decode(tmp));
        Log.d("testToekn",previlage);

        if(previlage.equals("0")){
            return true;
        }else {
            return false;
        }
    }

}

package com.example.footprint.view.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.footprint.R;
import com.example.footprint.adapter.CommentAdapter;
import com.example.footprint.model.Comment;
import com.example.footprint.model.Post;
import com.example.footprint.model.TimeParse;
import com.example.footprint.net.RestAPI;
import com.example.footprint.view.Activities.NoticeBoardActivity;
import com.example.footprint.view.Activities.PostActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeBoardYellowFragment extends Fragment {

    private View view;

    private View header;

    private ListView lvNoticeBlue;

    private TextView tvNickName;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvMainText;
    private ImageView ivImage;
    private Button btnDel;
    private Button btnLove;
    private Button btnNext;
    private TextView tvLove;



    private CommentAdapter commentAdapter;
    private Button btComment;
    private EditText etComment;

    private String postNum;
    private Post post;
    private ArrayList<Comment> comments;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice_board_yellow, container, false);

        lvNoticeBlue = (ListView) view.findViewById(R.id.lv_notice_yellow);

        header = getLayoutInflater().inflate(R.layout.header_notice_red, null, false);
        tvNickName = (TextView) header.findViewById(R.id.tv_nick_name);
        tvTitle = (TextView) header.findViewById(R.id.tv_title);
        ivImage = (ImageView) header.findViewById(R.id.iv_image);
        tvDate = (TextView) header.findViewById(R.id.tv_date);
        tvMainText = (TextView) header.findViewById(R.id.tv_main_text);
        btnLove = (Button) header.findViewById(R.id.btn_love);
        etComment = (EditText) view.findViewById(R.id.et_comment_yellow);
        btComment = (Button) view.findViewById(R.id.bt_comment_yellow);
        btnDel = (Button) header.findViewById(R.id.btn_del);
        btnNext = header.findViewById(R.id.btn_next);
        tvLove = (TextView) header.findViewById(R.id.tv_love);


        comments = new ArrayList<Comment>();


        commentAdapter = new CommentAdapter(getActivity(), comments){
            @Override
            public void setNotifyOnChange(boolean notifyOnChange) {
                ((NoticeBoardActivity)getActivity()).refreshBlue();
                super.setNotifyOnChange(notifyOnChange);
            }
        };
        lvNoticeBlue.addHeaderView(header);
        lvNoticeBlue.setAdapter(commentAdapter);

        btComment.setOnClickListener(new BtnOnClickListener());
        btnDel.setOnClickListener(new BtnOnClickListener());
        btnLove.setOnClickListener(new BtnOnClickListener());
        btnNext.setOnClickListener(new BtnOnClickListener());

        postNum = ((NoticeBoardActivity) getActivity()).typeB;


        RestAPI.get("/post/" + postNum, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                post = gson.fromJson(response.toString(), Post.class);
                Picasso.with(getActivity()).load("http://203.254.143.185:3000/api/picture/" + post.getPictureId()).into(ivImage);
                tvTitle.setText(post.getTitle());
                tvNickName.setText(post.getAuthor());
                tvDate.setText(TimeParse.getTime(post.getDate()));
                String tmp = "좋아요 "+(post.getLike()) +"개";
                tvLove.setText(tmp);
                tvDate.setText(post.getDate());
                tvMainText.setText(post.getContent());

                Log.d("testResponse", "" + response);

            }
        });

        getComment();

        return view;
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_comment_yellow:
                    Log.d("test_comment","onClick");
                    writeComment();
                    break;
                case R.id.btn_del:
                    Log.d("test_del","del Comment");
                    ((NoticeBoardActivity)getActivity()).dlePost(postNum);
                    Log.d("test_del","finDel");
                    break;
                case R.id.btn_love:

                    if (post.getLikeFlag().equals("false")){
                        post.setLikeFlag("true");
                        ((NoticeBoardActivity)getActivity()).addLike(postNum);
                        ((NoticeBoardActivity)getActivity()).refreshYellow();
                    }
                    else{
                        post.setLikeFlag("false");
                        ((NoticeBoardActivity)getActivity()).subLike(postNum);
                        ((NoticeBoardActivity)getActivity()).refreshYellow();

                    }
                    break;
                case R.id.btn_next:
                    Intent postIntent = new Intent((NoticeBoardActivity)getActivity(), PostActivity.class);
                    postIntent.putExtra("type", 2);
                    postIntent.putExtra("lat", ((NoticeBoardActivity)getActivity()).lat);
                    postIntent.putExtra("lng", ((NoticeBoardActivity)getActivity()).lng);
                    postIntent.putExtra("thoroughfare", ((NoticeBoardActivity)getActivity()).thoroughfare);
                    startActivity(postIntent);
                    break;
            }

        }
    }


    private void writeComment(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", etComment.getText().toString());
        } catch (JSONException e) {

        }
        etComment.setText("");
        RestAPI.post("/comment/" + postNum + "/write", jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("test_comment",response.toString());
                getComment();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("test_comment",responseString);
            }
        });

        ((NoticeBoardActivity)getActivity()).refreshYellow();
    }

    private void getComment() {

        RestAPI.get("/comment/" + postNum, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                try {
                    comments = gson.fromJson(response.getJSONArray("result").toString(), new TypeToken<ArrayList<Comment>>() {
                    }.getType());
                    commentAdapter.setItems(comments);

                } catch (JSONException e) {

                }

            }
        });
        Log.d("test_comment","getComment");

    }


}

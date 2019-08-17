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

import androidx.fragment.app.Fragment;

import com.example.footprint.R;
import com.example.footprint.adapter.CommentAdapter;
import com.example.footprint.model.Comment;
import com.example.footprint.model.Post;
import com.example.footprint.model.TimeParse;
import com.example.footprint.net.RestAPI;
import com.example.footprint.view.Activities.MapActivity;
import com.example.footprint.view.Activities.NoticeBoardActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class NoticeBoardRedFragment extends Fragment {

    private View view;
    private View header;

    private ListView lvNoticeRed;

    private TextView tvNickName;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvMainText;
    private ImageView ivImage;
    private Button btnDel;
    private Button btnLove;

    public CommentAdapter getCommentAdapter() {
        return commentAdapter;
    }

    private CommentAdapter commentAdapter;
    private Button btComment;
    private EditText etComment;

    private String postNum;
    private Post post;
    private ArrayList<Comment> comments;
    private Comment comment;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice_board_red, container, false);

        lvNoticeRed = (ListView) view.findViewById(R.id.lv_notice_red);

        header = getLayoutInflater().inflate(R.layout.header_notice_red, null, false);
        tvNickName = (TextView) header.findViewById(R.id.tv_nick_name);
        tvTitle = (TextView) header.findViewById(R.id.tv_title);
        ivImage = (ImageView) header.findViewById(R.id.iv_image);
        tvDate = (TextView) header.findViewById(R.id.tv_date);
        tvMainText = (TextView) header.findViewById(R.id.tv_main_text);
        btnLove = (Button) header.findViewById(R.id.btn_love);
        btComment = (Button) view.findViewById(R.id.bt_comment);
        etComment = (EditText) view.findViewById(R.id.et_comment);
        btnDel = (Button) header.findViewById(R.id.btn_del);


        comments = new ArrayList<Comment>();

//        Comment comment1 = new Comment("test","인생자판기","2019/08/08","12");
//        Comment comment2 = new Comment("test","인생자판기","2019/08/08","12");
//        comments.add(comment1);
//        comments.add(comment2);

//        comments = ((NoticeBoardActivity)getActivity()).getComments();

        commentAdapter = new CommentAdapter(getActivity(), comments);
        lvNoticeRed.addHeaderView(header);
        lvNoticeRed.setAdapter(commentAdapter);

        btComment.setOnClickListener(new BtnOnClickListener());
        btnDel.setOnClickListener(new BtnOnClickListener());


        postNum = ((NoticeBoardActivity) getActivity()).typeA;

        Log.d("test_", postNum);

        RestAPI.get("/post/" + postNum, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                post = gson.fromJson(response.toString(), Post.class);
                Picasso.with(getActivity()).load("http://203.254.143.185:3000/api/picture/" + post.getPictureId()).into(ivImage);
                tvTitle.setText(post.getTitle());
                tvNickName.setText(post.getAuthor());
                tvDate.setText(TimeParse.getTime(post.getDate()));

                Log.d("test_time",TimeParse.getTime(post.getDate()));

                tvMainText.setText(post.getContent());

                Log.d("test_Response", "" + response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("test_", responseString);
            }
        });

        getComment();

        return view;
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_comment:
                    Log.d("test_comment","onClick");
                    writeComment();
                    break;
                case R.id.btn_del:
                    Log.d("test_del","del Comment");
                    ((NoticeBoardActivity)getActivity()).dlePost(postNum);
                    Log.d("test_del","finDel");
                    break;
            }

        }
    }

    private void writeComment(){
        Comment comment1 = new Comment("test", "인생자판기", "2019/08/08", "12");
        Comment comment2 = new Comment("test", "인생자판기", "2019/08/08", "12");
        comments.add(comment1);
        comments.add(comment2);
        getComment();
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
    }


    private void dlePost(){
        RestAPI.get("/post/"+postNum+"/delete",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("test_del",response.toString());
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("test_del",responseString);
            }
        });
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

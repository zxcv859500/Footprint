package com.example.footprint.view.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.footprint.R;
import com.example.footprint.adapter.CommentAdapter;
import com.example.footprint.model.Comment;
import com.example.footprint.model.Post;
import com.example.footprint.model.PostList;
import com.example.footprint.net.RestAPI;
import com.example.footprint.view.Activities.NoticeBoardActivity;
import com.example.footprint.view.MainActivity;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeBoardRedFragment extends Fragment {

    private View view;
    private View header;

    private ListView lvNoticeRed;

    private TextView tvNickName;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvMainText;
    private ImageView ivImage;
    private Button btnLove;
    private CommentAdapter commentAdapter;

    private String postNum;
    private Post post;
    private ArrayList<Comment> comments;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice_board_red,container,false);

        lvNoticeRed = (ListView) view.findViewById(R.id.lv_notice_red);

        header = getLayoutInflater().inflate(R.layout.header_notice_red,null,false);
        tvNickName = (TextView) header.findViewById(R.id.tv_nick_name);
        tvTitle = (TextView) header.findViewById(R.id.tv_title);
        ivImage = (ImageView) header.findViewById(R.id.iv_image);
        tvDate = (TextView) header.findViewById(R.id.tv_date);
        tvMainText = (TextView) header.findViewById(R.id.tv_main_text);
        btnLove = (Button) header.findViewById(R.id.btn_love);

        comments = new ArrayList<Comment>();


//        Comment comment = new Comment("test","인생자판기","2019/08/08","12");
//        comments.add(comment);

        commentAdapter = new CommentAdapter(getActivity(),comments);
        lvNoticeRed.addHeaderView(header);
        lvNoticeRed.setAdapter(commentAdapter);



        postNum = ((NoticeBoardActivity)getActivity()).typeA;


        RestAPI.get("/post/" + postNum,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                post = gson.fromJson(response.toString(), Post.class);
                Picasso.with(getActivity()).load("http://203.254.143.185:3000/api/picture/"+post.getPictureId()).into(ivImage);
                tvTitle.setText(post.getTitle());
                tvNickName.setText(post.getAuthor());
                tvDate.setText(post.getDate());
                tvMainText.setText(post.getContent());

                Log.d("testResponse",""+response);

            }
        });

        return view;
    }
}

package com.example.footprint.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.footprint.R;
import com.example.footprint.model.Comment;
import com.example.footprint.model.TimeParse;
import com.example.footprint.net.RestAPI;
import com.example.footprint.view.Activities.NoticeBoardActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    private ArrayList<Comment> comments;

    private static class ViewHolder {
        public TextView tvNickNameComment;
        public TextView tvMainTextComment;
        public TextView tvDateComment;
        public TextView tvLoveComment;
        public Button btnDelComment;
        public Button btnLover;
    }

    public CommentAdapter(Context context,ArrayList<Comment> commentArrayList){
        super(context,0,commentArrayList);
        this.comments = commentArrayList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Comment comment = getItem(position);

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_notice_red,parent,false);
            viewHolder.tvNickNameComment = (TextView) convertView.findViewById(R.id.tv_nick_name_comment);
            viewHolder.tvMainTextComment = (TextView) convertView.findViewById(R.id.tv_main_text_comment);
            viewHolder.tvDateComment = (TextView) convertView.findViewById(R.id.tv_date_comment);
            viewHolder.btnLover = (Button) convertView.findViewById(R.id.btn_love_comment );
            viewHolder.btnDelComment = (Button) convertView.findViewById(R.id.btn_del_comment);
            viewHolder.tvLoveComment = (TextView) convertView.findViewById(R.id.tv_love_comment);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNickNameComment.setText(comment.getNickname());
        viewHolder.tvMainTextComment.setText(comment.getMaintext());
        viewHolder.tvDateComment.setText(TimeParse.getTime(comment.getDate()));
        String tmp = "좋아요 "+ comment.getLike()+"개";
        viewHolder.tvLoveComment.setText(tmp);
        viewHolder.btnDelComment.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("test_comment_del","onClick");
                delComment(comment.getCommentId());
            }
        });
        viewHolder.btnLover.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("test_bool",comment.getLikeFlag());
                if(comment.getLikeFlag().equals("true")){
                    comment.setLikeFlag("false");
                    RestAPI.get("/comment/"+comment.getCommentId()+"/like/cancel",new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("test_del",response.toString());

                            setNotifyOnChange(true);


                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("test_del",responseString);

                        }
                    });

                }else if(comment.getLikeFlag().equals("false")){
                    comment.setLikeFlag("true");
                    RestAPI.get("/comment/"+comment.getCommentId()+"/like",new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            setNotifyOnChange(true);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {


                        }
                    });

                }
            }


        });

        return convertView;
    }






    private void delComment(String id){
        RestAPI.get("/comment/"+id+"/delete",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setNotifyOnChange(true);
                Log.d("test_del",response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("test_del",responseString);
            }
        });


    }

    public void setItems (ArrayList<Comment> comments){
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();

    }

}

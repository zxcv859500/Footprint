package com.example.footprint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.footprint.R;
import com.example.footprint.model.Comment;
import com.example.footprint.model.TimeParse;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private ArrayList<Comment> comments;

    private static class ViewHolder {
        public TextView tvNickNameComment;
        public TextView tvMainTextComment;
        public TextView tvDateComment;
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
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNickNameComment.setText(comment.getNickname());
        viewHolder.tvMainTextComment.setText(comment.getMaintext());
        viewHolder.tvDateComment.setText(TimeParse.getTime(comment.getDate()));
        viewHolder.btnLover.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 좋아요 서버 업로드필요
            }
        });

        return convertView;
    }

    public void setItems (ArrayList<Comment> comments){
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }
}

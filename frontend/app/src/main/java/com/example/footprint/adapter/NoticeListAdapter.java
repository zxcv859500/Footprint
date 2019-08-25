package com.example.footprint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footprint.R;
import com.example.footprint.model.PostList;
import com.example.footprint.model.TimeParse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeListAdapter extends ArrayAdapter<PostList> {
    private Context context;

    private static class ViewHolder{
        public TextView tvDate;
        public TextView tvTitle;
        public ImageView ivList;
    }

    public NoticeListAdapter(Context context, ArrayList<PostList> commentArrayList){
        super(context,0,commentArrayList);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PostList postList = getItem(position);

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_notice_yellow,parent,false);

            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_date_list);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title_list);
            viewHolder.ivList = (ImageView) convertView.findViewById(R.id.iv_list);
            convertView.setTag(viewHolder);
        }else{
         viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load("http://203.254.143.185:3000/api/picture/" + postList.getPicturedId())
                .error(R.drawable.ic_launcher_background)
                .fit().into(viewHolder.ivList);
        viewHolder.tvTitle.setText(postList.getTitle());
        viewHolder.tvDate.setText(TimeParse.getTime(postList.getDate()));

        return convertView;
    }

}

package com.example.footprint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.footprint.R;
import com.example.footprint.model.PostList;
import com.example.footprint.model.TimeParse;

import java.util.ArrayList;

public class NoticeListAdapter extends ArrayAdapter<PostList> {

    private static class ViewHolder{
        public TextView tvDate;
        public TextView tvTitle;
    }

    public NoticeListAdapter(Context context, ArrayList<PostList> commentArrayList){
        super(context,0,commentArrayList);

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
            convertView.setTag(viewHolder);
        }else{
         viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(postList.getTitle());
        viewHolder.tvDate.setText(TimeParse.getTime(postList.getDate()));

        return convertView;
    }

}

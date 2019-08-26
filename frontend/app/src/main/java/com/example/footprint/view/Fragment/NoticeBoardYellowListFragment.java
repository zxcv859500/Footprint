package com.example.footprint.view.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.footprint.R;
import com.example.footprint.adapter.NoticeListAdapter;
import com.example.footprint.model.PostList;
import com.example.footprint.view.Activities.NoticeBoardActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NoticeBoardYellowListFragment extends Fragment {

    private View view;

    private ListView lvNoticeYellowList;

    private ArrayList<PostList> postLists;
    private NoticeListAdapter noticeListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice_board_yellow_list,container,false);

        lvNoticeYellowList = (ListView) view.findViewById(R.id.lv_notice_yellow_list);

        postLists = new ArrayList<PostList>();

        postLists.addAll(((NoticeBoardActivity)getActivity()).postTypeB);

        Collections.sort(postLists , new Comparator<PostList>() {
            @Override
            public int compare(PostList postList, PostList t1) {
                return postList.getDate().compareTo(t1.getDate());
            }
        });

        Collections.reverse(postLists);

        Log.d("test",postLists.get(0).getPostId());

        noticeListAdapter = new NoticeListAdapter(getActivity(), postLists);

        lvNoticeYellowList.setAdapter(noticeListAdapter);

        setOnItemClickListener();

        return view;


    }

    public void setOnItemClickListener(){
        lvNoticeYellowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ((NoticeBoardActivity)getActivity()).setFragment(3);
                ((NoticeBoardActivity)getActivity()).setTypeB(postLists.get(position).getPostId());
            }
        });

    }

}

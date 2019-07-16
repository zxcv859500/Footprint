package com.example.footprint.view.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.footprint.R;

public class NoticeBoardRedFragment extends Fragment {

    private View view;
    private View header;

    private ListView lvNoticeRed;

    private TextView tvNickName;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvMainText;
    private Button btnLove;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice_board_red,container,false);

        lvNoticeRed = (ListView) view.findViewById(R.id.lv_notice_red);

        header = getLayoutInflater().inflate(R.layout.header_notice_red,null,false);
        tvNickName = (TextView) header.findViewById(R.id.tv_nick_name);
        tvTitle = (TextView) header.findViewById(R.id.tv_title);
        tvDate = (TextView) header.findViewById(R.id.tv_date);
        tvMainText = (TextView) header.findViewById(R.id.tv_main_text);
        btnLove = (Button) header.findViewById(R.id.btn_love);

        lvNoticeRed.addHeaderView(header);

        return view;
    }
}

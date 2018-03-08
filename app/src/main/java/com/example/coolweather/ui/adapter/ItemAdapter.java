package com.example.coolweather.ui.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.coolweather.R;

import java.util.ArrayList;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class ItemAdapter extends BaseAdapter {


    private  FragmentActivity mContext;
    private  ArrayList<String> mData=new ArrayList<>();

    public ItemAdapter(FragmentActivity activity, ArrayList<String> dataList) {
        this.mContext=activity;
        this.mData=dataList;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_data, null);
        TextView tv_data = view.findViewById(R.id.Tv_data);
        tv_data.setText(mData.get(position));
        return view;
    }

}


package com.performance.slidedeletelistviewdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gracker on 2015/11/23.
 */
public class ListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> arrayList;

    ListAdapter(Context context, ArrayList arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
            viewHolder.textView = (TextView) view.findViewById(R.id.listview_item_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textView.setText(arrayList.get(position));
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void updateDataSet(int position) {
        Log.i("Gracker", "update position =" + position);
        arrayList.remove(position);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        private TextView textView;
    }
}

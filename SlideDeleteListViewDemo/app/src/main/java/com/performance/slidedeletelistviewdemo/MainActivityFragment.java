package com.performance.slidedeletelistviewdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.performance.slidedeletelistview.SlideDeleteListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public static int ITEM_COUNT = 10;

    private SlideDeleteListView listView;
    private ListAdapter mListAdapter;
    private ArrayList<String> list;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (SlideDeleteListView) viewRoot.findViewById(R.id.list_view);

        list = new ArrayList<>(ITEM_COUNT);
        for (int i = 0; i < ITEM_COUNT; i++) {
            list.add(i, "this is a deletable item , position = " + i);
        }

        mListAdapter = new ListAdapter(getActivity(), list);

        listView.setAdapter(mListAdapter);
        listView.setRemoveListener(new SlideDeleteListView.RemoveListener() {
            @Override
            public void removeItem(SlideDeleteListView.RemoveDirection direction, int position) {
                Toast.makeText(getContext(), "Item " + position + " has deleted",
                        Toast.LENGTH_SHORT).show();
                mListAdapter.updateDataSet(position - listView.getHeaderViewsCount());
            }
        });
        listView.addFooterView(inflater.inflate(R.layout.footer_layout, container));
        listView.addHeaderView(inflater.inflate(R.layout.header_layout, container));
        listView.addHeaderView(inflater.inflate(R.layout.header_layout, container));

        return viewRoot;
    }
}

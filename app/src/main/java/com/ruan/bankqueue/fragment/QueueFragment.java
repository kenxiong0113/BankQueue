package com.ruan.bankqueue.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruan.bankqueue.R;

/**
 * @author by ruan on 2017/12/16.
 */

public class QueueFragment extends Fragment {
    View view;
    static String ARG = "arg";
    public QueueFragment() {
        super();
    }
    public static QueueFragment newInstance(String param) {
        QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue, container, false);

        return view;
    }
}

package com.ruan.bankqueue.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruan.bankqueue.R;

/**
 * @author by ruan
 */

public class SubscribeFragment extends Fragment {

    View view;
    static String ARG = "arg";
    public SubscribeFragment() {
        super();
    }

    public static SubscribeFragment newInstance(String param) {
        SubscribeFragment fragment = new SubscribeFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        return view;
    }
}

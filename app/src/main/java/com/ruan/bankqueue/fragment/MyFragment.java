package com.ruan.bankqueue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;

/**
 * @author by ruan
 */

public class MyFragment extends Fragment {

    View view;
    static String ARG = "arg";
    Unbinder unbinder;

    public MyFragment() {
        super();
    }

    public static MyFragment newInstance(String param) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     *
     * @param view
     */
    @OnClick({})
    public void onClick(View view){
        switch (view.getId()){

            default:
                break;

        }
    }
}

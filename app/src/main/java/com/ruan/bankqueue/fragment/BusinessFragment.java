package com.ruan.bankqueue.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.adapter.MyViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author by ruan
 */

public class BusinessFragment extends Fragment {
    View view;
    private final static String ARG = "arg";
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;

    public BusinessFragment() {
        super();
    }

    public static BusinessFragment newInstance(String param) {
        BusinessFragment fragment = new BusinessFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_business, container, false);
        unbinder = ButterKnife.bind(this, view);
        initWidget();
        return view;
    }

    private void initWidget() {
        setupViewPager(viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("附进ATM"));
        tabLayout.addTab(tabLayout.newTab().setText("银行排队"));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewpager) {
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(AtmFragment.newInstance("1"), "附进ATM");
        adapter.addFragment(QueueFragment.newInstance("2"), "银行排队");
        viewpager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

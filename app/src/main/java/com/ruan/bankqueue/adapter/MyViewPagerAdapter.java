package com.ruan.bankqueue.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by ruan on 2017/12/16
 */

public  class MyViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragment=new ArrayList<Fragment>();
    private final List<String> mFragmentTitle=new ArrayList<String>();

    public void addFragment(Fragment  fragment,String title){
        mFragment.add(fragment);
        mFragmentTitle.add(title);
    }
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitle.get(position);
    }
}

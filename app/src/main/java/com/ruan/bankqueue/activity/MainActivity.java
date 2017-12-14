package com.ruan.bankqueue.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.fragment.BusinessFragment;
import com.ruan.bankqueue.fragment.MyFragment;
import com.ruan.bankqueue.fragment.SubscribeFragment;

/**
 * @author by ruan
 */
public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    BottomNavigationView navigation;
    BusinessFragment businessFragment;
    MyFragment myFragment;
    SubscribeFragment subscribeFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_business:

                    return true;
                case R.id.navigation_subscribe:

                    return true;
                case R.id.navigation_my:

                    return true;
                default:
                    break;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initFragmentManager();
    }

    /**
     *  fragment管理器
     */

    private void initFragmentManager() {
        // 实例化 fragment
        frameLayout = (FrameLayout)findViewById(R.id.content);
        businessFragment = new BusinessFragment();
        subscribeFragment = new SubscribeFragment();
        myFragment=new MyFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, businessFragment)
                .add(R.id.content, subscribeFragment)
                .add(R.id.content, myFragment)
                .hide(subscribeFragment)
                .hide(myFragment)
                .commit();
    }

}

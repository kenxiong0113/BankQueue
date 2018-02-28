package com.ruan.bankqueue.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.adapter.MySubscribeAdapter;
import com.ruan.bankqueue.javabean.Reservation;
import com.ruan.bankqueue.javabean.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author by ruan 2018/1/28
 *         我的预约
 */
public class MySubscribeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_yy)
    RecyclerView rvYy;
    @BindView(R.id.srl_yy)
    SwipeRefreshLayout srlYy;
    private User user;
    MySubscribeAdapter adapter;
    private List<Reservation> reservations = new ArrayList<Reservation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_subscrebe;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        user = BmobUser.getCurrentUser(User.class);
        initToolbar();
        setRecyclerView();
        initData();
    }

    private void initToolbar() {
        setTitle("我的预约");
        setTopLeftButton(R.drawable.ic_return, new OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    private void setRecyclerView() {
        srlYy.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvYy.setLayoutManager(manager);
        adapter = new MySubscribeAdapter(reservations);
        rvYy.setAdapter(adapter);
    }

    private void initData() {
        BmobQuery<Reservation> query = new BmobQuery<Reservation>();
        query.addWhereEqualTo("state", true);
        query.addWhereEqualTo("phone", user.getUsername());
        query.findObjects(new FindListener<Reservation>() {
            @Override
            public void done(List<Reservation> list, BmobException e) {
                if (e == null) {
                    for (Reservation reservation : list) {
                        Reservation re = new Reservation(reservation.getTime(), reservation.getBankName());
                        reservations.add(re);
                        adapter.notifyDataSetChanged();
                    }
                    srlYy.setRefreshing(false);
                } else {
                    Log.e("MySubscribeActivity", e.getMessage() + e.getErrorCode());
                    Toast.makeText(MySubscribeActivity.this, e.getMessage() + e.getErrorCode(),
                            Toast.LENGTH_SHORT).show();
                    srlYy.setRefreshing(false);

                }
            }
        });
    }

    /**
     * 重写下拉刷新监听，这里请求网络数据
     */
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reservations.clear();
                initData();
                Log.e("BlacklistUserActivity", "下拉");
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
}

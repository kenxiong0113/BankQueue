package com.ruan.bankqueue.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.javabean.Reservation;
import com.ruan.bankqueue.javabean.User;
import com.ruan.bankqueue.picker.DateTimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author by ruan 2018/1/26
 *         预约活动界面
 */
public class SubscribeActivity extends BaseActivity {

    @BindView(R.id.btn_yy)
    Button btnYY;
    String year, month, day, hour, minutes;
    @BindView(R.id.tv_yy)
    TextView tvYy;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    String bankName;
    User user;
    String yyObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_subscribe;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        user = BmobUser.getCurrentUser(User.class);
        bankName = getIntent().getStringExtra("bankName");
        initToolbar();
        getServerTime();
        setButtonListener();
        initData();
    }

    private void initToolbar() {
        setTitle(bankName);
        setTopLeftButton(R.drawable.ic_return, new OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }
    private void initData(){
        BmobQuery<Reservation> query = new BmobQuery<Reservation>();
        query.addWhereEqualTo("phone",BmobUser.getCurrentUser().getUsername());
        query.addWhereEqualTo("bankName",bankName);
        query.addWhereEqualTo("state",true);
        query.order("-createdAt");
        query.findObjects(new FindListener<Reservation>() {
            @Override
            public void done(List<Reservation> list, BmobException e) {
                if (e == null ){
                    if (list.size() == 0){
                        tvYy.setText("未预约");
                    }else {
                        tvYy.setText(list.get(0).getTime());
                        btnYY.setText("重新预约");
                    }
                }else {

                }
            }
        });

    }

    private void getServerTime() {
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long time, BmobException e) {
                if (e == null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String times = formatter.format(new Date(time * 1000L));
                    try {
                        Date date = formatter.parse(times);
                        long timestampServerTime = date.getTime();
                        year = times.substring(1, 4);
                        month = times.substring(6, 7);
                        day = times.substring(8, 10);
                        Log.e("QueueActivity", "当前服务器时间为:" + times);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Toast.makeText(SubscribeActivity.this, "获取当前服务器时间失败:" +
                            e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                    Log.e("bmob", "获取服务器时间失败:" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    private void setButtonListener() {
        btnYY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePicker picker = new DateTimePicker(SubscribeActivity.this, DateTimePicker.HOUR_24);
                picker.setDateRangeStart(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day) + 1);
                picker.setDateRangeEnd(2025, 12, 31);
                picker.setTimeRangeStart(9, 0);
                picker.setTimeRangeEnd(18, 59);
                picker.setTopLineColor(0x99FF0000);
                picker.setLabelTextColor(0xFFFF0000);
                picker.setDividerColor(0xFFFF0000);
                picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
                    @Override
                    public void onDateTimePicked(final String year, final String month, final String day,
                                                 final String hour, final String minute) {
                        Reservation reservation = new Reservation();

                        reservation.setPhone(user.getUsername());
                        reservation.setBankName(getIntent().getStringExtra("bankName"));
                        reservation.setTime("20"+year + "-" + month + "-" + day + " " + hour + ":" + minute);
                        reservation.setUserId(user);
                        reservation.setState(true);
                        reservation.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    tvYy.setText("20"+year + "-" + month + "-" + day + " " + hour + ":" + minute);
                                    btnYY.setText("重新预约");
                                }else {
                                    Log.d("SubscribeActivity3", e.getMessage() + e.getErrorCode());
                                    Toast.makeText(SubscribeActivity.this, e.getMessage() + e.getErrorCode(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                picker.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });
    }

    private void query(){
        BmobQuery<Reservation> query = new BmobQuery<Reservation>();
        query.addWhereEqualTo("phone",user.getUsername());
        query.addWhereEqualTo("bankName",bankName);
        query.addWhereEqualTo("state",true);
        query.findObjects(new FindListener<Reservation>() {
            @Override
            public void done(List<Reservation> list, BmobException e) {
                if (e == null){
                    if (list.size() != 0){
                     yyObject = list.get(0).getObjectId();
                        Reservation reservation = new Reservation();
                        reservation.setState(false);
                        reservation.update(yyObject, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null){
                                    tvYy.setText("未预约");
                                    btnYY.setText("预约业务");
                                }else {
                                    Log.e("SubscribeActivity1", e.getMessage() + e.getErrorCode());
                                    Toast.makeText(SubscribeActivity.this, e.getMessage() + e.getErrorCode(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    Log.e("SubscribeActivity2", e.getMessage() + e.getErrorCode());
                    Toast.makeText(SubscribeActivity.this, e.getMessage() + e.getErrorCode(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}

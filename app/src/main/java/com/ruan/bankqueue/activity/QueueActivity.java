package com.ruan.bankqueue.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.javabean.Bank;
import com.ruan.bankqueue.javabean.HeadCount;
import com.ruan.bankqueue.javabean.Queue;
import com.ruan.bankqueue.javabean.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author by ruan 用户银行排队界面
 */
public class QueueActivity extends BaseActivity {
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.queue_num)
    TextView queueNum;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.img_yes)
    ImageView imgYes;
    @BindView(R.id.rl_yes)
    RelativeLayout rlYes;
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.rl_no)
    RelativeLayout rlNo;
    String bankName;
    @BindView(R.id.progress)
    ProgressBar progress;
    User user;
    String queueObjectId;
    String countObjectId;
    private int sum;
    private  String times;
    /**
     * 服务器时间转换成的时间戳
     */
    private long timestampServerTime;
    /**
     * 用户最后一次取消排队的时间转换成的时间戳
     */
    private long ts;
    /**
     * 银行排队的总人数
     */
    private Integer count;
    private Integer code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_queue;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initWidget();
        user = BmobUser.getCurrentUser(User.class);
        getOpenWindows();
        getServerTime();
        //定时刷新，一分钟刷新
        handler.postDelayed(run,1000*60);
    }

    private void initWidget() {
        bankName = getIntent().getStringExtra("bankName");
        setTitle(bankName);
        setTopLeftButton(R.drawable.ic_return, new OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 查询获取该银行的开放窗口
     */
    private void getOpenWindows() {
        BmobQuery<Bank> query = new BmobQuery<Bank>();
        query.addWhereEqualTo("bankName", bankName);
        query.addWhereEqualTo("state", true);
        query.findObjects(new FindListener<Bank>() {
            @Override
            public void done(List<Bank> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(QueueActivity.this);
                        dialog.setIcon(R.drawable.ic_no_windows);
                        dialog.setTitle("非常抱歉！");
                        dialog.setMessage(bankName + "窗口未开放");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        dialog.show();
                    } else {
                        // TODO: 2018/1/13 窗口开放，允许排队的逻辑
                        progress.setVisibility(View.VISIBLE);
                        individualQueueStatus();
                    }
                } else {
                    Log.e("QueueActivity", e.getErrorCode() + e.getMessage());
                }

            }
        });
    }
    @OnClick({R.id.img_yes,R.id.img_cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_yes:

                getLastTime();
                break;
            case R.id.img_cancel:
                Queue queue1 = new Queue();
                queue1.setObjectId(queueObjectId);
                queue1.remove("user");
                queue1.setState(false);
                queue1.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            queueNum.setText("请排队");
                            rlYes.setVisibility(View.VISIBLE);
                            rlNo.setVisibility(View.GONE);
                        }else {
                            Toast.makeText(QueueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("QueueActivity", "e.getErrorCode():" + e.getErrorCode());
                        }
                    }
                });

                break;
            default:
                break;
        }
    }

    /**
     * 用户是否有排队记录
     */
    private void individualQueueStatus(){
        BmobQuery<Queue> queueBmobQuery = new BmobQuery<Queue>();
        queueBmobQuery.addWhereEqualTo("user",user.getObjectId());
        queueBmobQuery.order("-createdAt");
        queueBmobQuery.findObjects(new FindListener<Queue>() {
            @Override
            public void done(List<Queue> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){
                        //无排队记录
                        rlYes.setVisibility(View.VISIBLE);
                        rlNo.setVisibility(View.GONE);
                    }else {
//                        有排队记录
                        if (list.get(0).getBankName().equals(bankName)) {
                            rlNo.setVisibility(View.VISIBLE);
                            rlYes.setVisibility(View.GONE);
                            queueObjectId = list.get(0).getObjectId();
                            //获取到用户的排号码
                            code = list.get(0).getLineCode();
                            if (code < 10) {
                                queueNum.setText("00" + String.valueOf(code));
                            } else if (code < 100) {
                                queueNum.setText("0" + String.valueOf(code));
                            } else {
                                queueNum.setText(String.valueOf(code));
                            }
                            front();
                        } else {
                            //在别行有排队记录时，弹出不可取消对话框
                            AlertDialog.Builder dialog = new AlertDialog.Builder(QueueActivity.this);
                            dialog.setIcon(R.drawable.ic_no_windows);
                            dialog.setTitle("非常抱歉！");
                            dialog.setMessage("您在"+bankName+"有排队记录,请先到该行取消排队");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            dialog.show();
                        }
                    }
                }else {
                    Log.e("QueueActivity", e.getErrorCode() + e.getMessage());
                }
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 查询银行累计排队的总人数
     */

    private void bankQueueCount(){
        BmobQuery<HeadCount> query = new BmobQuery<HeadCount>();
        query.addWhereEqualTo("bankName",bankName);
        //查询最新的数据
        query.order("-createdAt");
        query.findObjects(new FindListener<HeadCount>() {
            @Override
            public void done(List<HeadCount> list, BmobException e) {
                if (e == null){
                    //该银行暂时没有人排队
                        count = list.get(0).getCount();
                        count = count + 1;
                        countObjectId = list.get(0).getObjectId();
                        Queue queue = new Queue();
                        queue.setLineCode(count);
                        queue.setBankName(bankName);
                        queue.setUser(user);
                        queue.setLineUpRecord(user);
                        queue.setState(true);
                        queue.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    HeadCount headcount = new HeadCount();
                                    headcount.setCount(count);
                                    headcount.setObjectId(countObjectId);
                                    headcount.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                individualQueueStatus();
                                            } else {
                                                Toast.makeText(QueueActivity.this, "排队失败" + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("QueueActivity.save", e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                }else{
                    Log.e("QueueActivity", e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    /**
     * 定时查询当前排队人数 ，并更新界面
     */
    Handler handler = new Handler();
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            this.update();
            handler.postDelayed(this,1000*60);
        }
        void update(){
            front();
            getServerTime();
        }
    };

    /**
     * 查询所选银行目前排在用户前面的还有多少人在排队
     */
    private void front(){
        Log.e("QueueActivity", "code:" + code);
        BmobQuery<Queue> query = new BmobQuery<Queue>();
        query.addWhereEqualTo("bankName",bankName);
        query.addWhereLessThan("lineCode", code);
        query.addWhereEqualTo("state",true);
        query.findObjects(new FindListener<Queue>() {
            @Override
            public void done(List<Queue> list, BmobException e) {
                if (e == null){
                    sum = list.size();
                    num.setText(String.valueOf(sum));
                    Log.e("QueueActivity", "sum:" + sum);
                }else{
                    Log.e("QueueActivity", e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    /**
     * 获取用户最后一次点击取消排队的时间
     */
    private void getLastTime(){
        BmobQuery<Queue> bmobQuery = new BmobQuery<Queue>();
        bmobQuery.addWhereEqualTo("lineUpRecord",user.getObjectId());
        bmobQuery.addWhereEqualTo("bankName",bankName);
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(new FindListener<Queue>() {
            @Override
            public void done(List<Queue> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){
                        //查询所选银行累计排队的总人数，确定排号码
                        bankQueueCount();
                    }else {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String updateTime = list.get(0).getUpdatedAt();
                        try {
                            Date date = formatter.parse(updateTime);
                            ts = date.getTime();
                            long mistiming = ((timestampServerTime - ts)/(1000*60));
                            if ( mistiming > 5){
                                //查询所选银行累计排队的总人数，确定排号码
                                bankQueueCount();
                            }else {
                                Toast.makeText(QueueActivity.this, "操作频繁，请"+mistiming+"分钟后重试",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }finally {

                        }
                    }
                }else {
                    Log.e("QueueActivity", "排队"+e.getMessage() + e.getErrorCode());
                    Toast.makeText(QueueActivity.this, e.getMessage()+e.getErrorCode(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getServerTime(){
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long time,BmobException e) {
                if(e==null){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                     times = formatter.format(new Date(time*1000L));
                    try {
                        Date date = formatter.parse(times);
                         timestampServerTime = date.getTime();
                        Log.e("QueueActivity","当前服务器时间为:" + times);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }else{
                    Log.e("bmob","获取服务器时间失败:" + e.getMessage()+e.getErrorCode());
                }
            }

        });
    }

}

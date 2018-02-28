package com.ruan.bankqueue.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.activity.SubscribeActivity;
import com.ruan.bankqueue.javabean.Reservation;
import java.util.List;


/**
 * @author by ruan on 2018/1/26.
 * 我的预约界面，选项适配器
 */

public class MySubscribeAdapter extends RecyclerView.Adapter<MySubscribeAdapter.ViewHolder>{
    private List<Reservation> list;
    private Activity  activity;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvBank;
        LinearLayout llYY;

        public ViewHolder(View view){
            super(view);
            tvTime = (TextView)view.findViewById(R.id.tv_time);
            tvBank = (TextView)view.findViewById(R.id.tv_bank);
            llYY = (LinearLayout)view.findViewById(R.id.ll_yy);
        }
    }

    public MySubscribeAdapter(List<Reservation> list){
        this.list = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yy,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Reservation my = list.get(position);
        holder.tvTime.setText("预约时间：\t\t"+my.getTime());
        holder.tvBank.setText("预约地点：\t\t"+my.getBankName());
        holder.llYY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(v.getContext(), SubscribeActivity.class);
                intent.putExtra("bankName",list.get(position).getBankName());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

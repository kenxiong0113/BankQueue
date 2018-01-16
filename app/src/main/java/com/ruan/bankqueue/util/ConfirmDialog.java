package com.ruan.bankqueue.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.ruan.bankqueue.R;

/**
 * @author  by ruan on 2018/1/12.
 */
public class ConfirmDialog extends Dialog {
    private Context context;
    private String title;
    private String massage;
    private String confirmButtonText;
    private String cancelButtonText;
    private String navigationButtonText;
    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {
        /**
         * 确定按钮
         */
         void doConfirm();
        /**
         * 取消按钮
         */
         void doCancel();

        /**
         * 导航按钮
         */
         void doNavigation();
    }

    public ConfirmDialog(Context context, String title,String massage,
                         String confirmButtonText, String cancelButtonText,
                        String navigationButtonText) {
        super(context,R.style.MyDialog);
        this.context = context;
        this.title = title;
        this.massage = massage;
        this.confirmButtonText = confirmButtonText;
        this.cancelButtonText = cancelButtonText;
        this.navigationButtonText = navigationButtonText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_view, null);
        setContentView(view);

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvMassage = (TextView)view.findViewById(R.id.massage);
        Button tvConfirm = (Button) view.findViewById(R.id.confirm);
        Button tvCancel = (Button) view.findViewById(R.id.cancel);
        Button tvNavigation = (Button)view.findViewById(R.id.navigation);

        tvTitle.setText(title);
        tvMassage.setText(massage);
        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cancelButtonText);
        tvNavigation.setText(navigationButtonText);

        tvConfirm.setOnClickListener(new ClickListener());
        tvCancel.setOnClickListener(new ClickListener());
        tvNavigation.setOnClickListener(new ClickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 获取屏幕宽、高用
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        // 高度设置为屏幕的0.6
        lp.width = (int) (d.widthPixels * 0.8);
//        lp.height = (int)(d.heightPixels * 0.3);
        dialogWindow.setAttributes(lp);
    }

    public void setClickListener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.confirm:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.cancel:
                    clickListenerInterface.doCancel();
                    break;
                case R.id.navigation:
                    clickListenerInterface.doNavigation();
                default:
                    break;
            }
        }

    }
}
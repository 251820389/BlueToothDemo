package com.nationz.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nationz.nk_bluetooth.R;
import com.nationz.payinterface.OnClickInterface;

/**
 * Created by LEO on 2016/7/21.
 */
public class DialogUtil {
    int position = 0;

    private void showDialog(Context context, int resource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("其他支付");
        builder.setCancelable(false);
        final String[] bankList = context.getResources().getStringArray(resource);
        // 设置一个单项选择下拉框
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合 第二个参数代表索引，指定默认哪一个单选框被勾选上，0表示默认会被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */

        builder.setSingleChoiceItems(bankList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private boolean isCancel = true;

    public void pwdDialog(final Context mContext, final String amount, final OnClickInterface onClick) {
        final Dialog dialog = new Dialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.pwd_input_layout, null);
        dialog.setTitle("交易金额：" + amount);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        final EditText pwdET = (EditText) dialog.findViewById(R.id.pwdInputView);
        Button pwd_btn = (Button) dialog.findViewById(R.id.pwd_btn);

        pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwdET.getText().toString().length() < 6) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.error_pwd_len));
                        }
                    });
                } else {
                    isCancel = false;
                    dialog.dismiss();
                    onClick.onClickListener();
                }
            }
        });
        dialog.show();
        //cancel input password
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
                if (isCancel) {
                    onClick.onDismissListener();
                }
            }
        });
    }
}

package com.zg.chatgpt.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zg.chatgpt.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    protected void addText(String userId, long timestamp, String msg) {
//        String msgs = msgListTV.getText().toString();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
//        String now = sdf.format(new Date(timestamp));
//        msgs += "\n" + userId + "   " + now + '\n';
//        msgs += msg + "\n";
//        msgListTV.setText(msgs);
//    }

    public void toast(String msg) {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_toast, null);
        TextView tv_msg = (TextView) view.findViewById(R.id.toast_text);
        tv_msg.setText(msg);
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 20);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    protected String randomId(int n) {
        String id = "";
        for (int i = 0; i < n; ++i) {
            id += Math.abs(new Random().nextInt() % 10);
        }
        setTitle("我的ID:" + id);
        return id;
    }

    interface OnInputCB {
        void onInput(String content);
    }

    public void showInput(String title, OnInputCB cb) {
        final EditText inputServer = new EditText(this);
        inputServer.setText("chat_gpt_001");
        inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setView(inputServer);
        builder.setPositiveButton("确定", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = inputServer.getText().toString();
                        if (content != null && !content.isEmpty()) {
                            cb.onInput(content);
                            alertDialog.dismiss();
                        } else {
                            toast("输入不能为空");
                            return;
                        }
                    }
                });
    }

}
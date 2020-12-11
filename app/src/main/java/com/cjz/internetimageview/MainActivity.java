package com.cjz.internetimageview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    protected static final int CHANGE_UI = 1;
    private static final int ERROR = 2;

    private EditText mEtUrl;
    private LinearLayout mLlText;
    private ImageView mIvPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtUrl = findViewById(R.id.et_url);
        mLlText = findViewById(R.id.ll_text);
        mIvPic = findViewById(R.id.iv_pic);
    }

    public void click(View view) {
        final String path = mEtUrl.getText().toString();
        if (TextUtils.isEmpty(path)){
            Toast.makeText(this, "图片路径不能为空", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(){

                private HttpURLConnection conn;
                private Bitmap bitmap;

                @Override
                public void run() {
                    try {
                        URL url = new URL(path);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(5000);
                        int code = conn.getResponseCode();
                        Log.d("MainActivity", "code:" + code);
                        if (code == 200){
                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);
                            Message message = new Message();
                            message.what = CHANGE_UI;
                            message.obj = bitmap;
                            handler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = ERROR;
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.what = ERROR;
                        handler.sendMessage(message);
                    }
                }
            }.start();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHANGE_UI){
                Bitmap bitmap = (Bitmap) msg.obj;
                mIvPic.setImageBitmap(bitmap);
            } else if (msg.what == ERROR){
                Toast.makeText(MainActivity.this, "显示图片错误", Toast.LENGTH_SHORT).show();
            }
        }
    };
}

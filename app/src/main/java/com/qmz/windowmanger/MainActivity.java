package com.qmz.windowmanger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn;
    private View window;
    private Button btnClose;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private LinkedList<View> ViewList = new LinkedList<>();
    private Map<View, WindowManager.LayoutParams> layoutParamsMap = new HashMap<>();
    private float currentX;
    private float currentY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onClick(View v) {
        window = View.inflate(MainActivity.this, R.layout.window, null);
        btnClose = window.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = ViewList.indexOf(v.getParent());
                View removeView = ViewList.remove(index);
                windowManager.removeView(removeView);
                layoutParamsMap.remove(removeView);
            }
        });

        window.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    System.out.println("=====onKey=======" + event.getAction());
                    View removeView = ViewList.removeLast();
                    windowManager.removeView(removeView);
                    layoutParamsMap.remove(removeView);
                }
                return false;
            }
        });

        window.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentX = event.getRawX();
                        currentY = event.getRawY();

                        Log.d(TAG, "onTouch: currentX=====" + currentX);
                        // Log.d(TAG, "onTouch: currentY====="+currentY);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        Log.d(TAG, "onTouch: moveX=====" + moveX);
                        float moveY = event.getRawY();

                        float offsetX = moveX - currentX;
                        float offsetY = moveY - currentY;
                        currentX = moveX;
                        currentY = moveY;
                        layoutParamsMap.get(v).x = (int) (layoutParamsMap.get(v).x + offsetX);
                        layoutParamsMap.get(v).y = (int) (layoutParamsMap.get(v).y + offsetY);
                        windowManager.updateViewLayout(v, layoutParamsMap.get(v));


                        break;

                }


                return false;
            }
        });
        ViewList.add(window);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = 300;
        layoutParams.height = 300;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(WINDOW_SERVICE);
        
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            } else {
                windowManager.addView(window, layoutParams);
                layoutParamsMap.put(window, layoutParams);
            }
        }

    }

    private static final String TAG = "MainActivity";

}

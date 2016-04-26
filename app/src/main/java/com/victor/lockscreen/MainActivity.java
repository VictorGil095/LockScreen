package com.victor.lockscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //Тут будут часы
    private TextView mTextView;
    //картинка, при перетаскивании которой lockscreen разблокируется
    private ImageView mImageView;
    private EditText mEditText;
    private int x, y;
    private int windowHeight;
    private int windowWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image_v);
        mTextView = (TextView) findViewById(R.id.text_v);
        mEditText = (EditText) findViewById(R.id.edit_t);
        try {
            startService(new Intent(this, MyService.class));
            MyListener stateListener = new MyListener();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);
            //узнаем размеры экрана, чтобы полностью охватить его LockScreen-ом
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            windowHeight = displaymetrics.heightPixels;
            windowWidth = displaymetrics.widthPixels;
            //слушатель перемещения движения картинки
            mImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            int x_cord = (int) event.getRawX();
                            int y_cord = (int) event.getRawY();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new ViewGroup.MarginLayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                            lp.setMargins(x_cord, y_cord, 0, 0);
                            mImageView.setLayoutParams(lp);
                            //при достаточном свайпе картинки экран разблокируется, но сервис будет продолжать работать и отслеживать состояние экрана
                            if (((x_cord) >= windowWidth / 2) || (y_cord) >= windowHeight / 2) {
                                finish();
                            }
                            break;
                        case MotionEvent.ACTION_DOWN:
                            x = (int) event.getRawX();
                            y = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            mImageView.setLayoutParams(params);
                    }
                    return true;
                }
            });
            //узнаем настоящее время
            Calendar calendar = Calendar.getInstance();
            //выводим его в TextView
            mTextView.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //чтобы сервис не работал в фоновом режиме, переопределено нажатие кнопки "назад"
        stopService(new Intent(this, MyService.class));
    }

    //слушатель ивентов
    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(final int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            //отслеживание ввода символов
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //при введении в EditText слово "hello" экран разблокируется
                    if (s.toString().equalsIgnoreCase("hello")) {
                        finish();
                    } else if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                        finish();
                    }
                }
            };
            mEditText.addTextChangedListener(watcher);
        }
    }
}
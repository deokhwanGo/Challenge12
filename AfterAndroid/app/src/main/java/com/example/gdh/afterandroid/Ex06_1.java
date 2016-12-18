package com.example.gdh.afterandroid;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InterruptedIOException;

public class Ex06_1 extends AppCompatActivity {
    ProgressBar bar;
    TextView textView01;
    boolean isRunning = false;

    ProgressHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex06_1);

        bar = (ProgressBar) findViewById(R.id.progress);
        textView01 = (TextView) findViewById(R.id.textView01);

        handler = new ProgressHandler();

    }

    public void onStart() {
        super.onStart();

        bar.setProgress(0);
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 20 && isRunning; i++) {
                        Thread.sleep(1000);

                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    }
                } catch (Exception ex) {
                    Log.e("MainActivity", "Exception in processing message.", ex);
                }
            }
        });

        isRunning = true;
        thread1.start();
    }

    public void onStop() {
        super.onStop();

        isRunning = false;
    }


    public class ProgressHandler extends Handler {

        public void handleMessage(Message msg) {

           bar.incrementProgressBy(5);

            if (bar.getProgress() == bar.getMax()) {
                textView01.setText("Done");
            } else {
                textView01.setText("Working ..." + bar.getProgress());
            }

        }

    }

}

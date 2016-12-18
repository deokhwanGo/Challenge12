package com.example.gdh.afterandroid;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Ex06_3 extends Activity {

    EditText mainedit;
    EditText threadedit;

    MainHandler mainHandler;
    ProcessThread thread1;

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex06_3);

        mainedit = (EditText)findViewById(R.id.mainedit);
        threadedit = (EditText)findViewById(R.id.threadedit);

        mainHandler = new MainHandler();
        thread1 = new ProcessThread();

        btn =(Button)findViewById(R.id.startbtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inStr = mainedit.getText().toString();
                Message msgToSend = Message.obtain();
                msgToSend.obj =inStr;

                thread1.handler.sendMessage(msgToSend);
            }
        });

        thread1.start();
    }

    class ProcessThread extends Thread {

        ProcessHandler handler;

        public ProcessThread(){
            handler = new ProcessHandler();
        }

        @Override
        public void run() {
            Looper.prepare();
            Looper.loop();
        }
    }

    class ProcessHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Message resultMsg =Message.obtain();
            resultMsg.obj = msg.obj+"Mike!!!";

             mainHandler.sendMessage(resultMsg);

        }
    }

    public class MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            threadedit.setText(str);
        }
    }
}

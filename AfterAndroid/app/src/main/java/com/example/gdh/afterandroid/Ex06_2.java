package com.example.gdh.afterandroid;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Ex06_2 extends AppCompatActivity {

    TextView textView01;
    Button button01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex06_2);

        textView01 = (TextView)findViewById(R.id.textView01);
        button01 = (Button)findViewById(R.id.button01);

        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request();
            }
        });
    }

    private void request(){
        String title = "원격요청";
        String message = "데이터를 요청하시겠습니까?";
        String titleButtonYes = "예";
        String titleButtonNo = "아니요";

        AlertDialog dialog =makeRequestDialog(title,message,titleButtonYes,titleButtonNo);
        dialog.show();

        textView01.setText("원격 데이터 요청 중");
    }

    private AlertDialog makeRequestDialog(CharSequence title,CharSequence message, CharSequence titleButtonYes, CharSequence titleButtonNo){

        AlertDialog.Builder requestDialog = new AlertDialog.Builder(this);
        requestDialog.setTitle(title);
        requestDialog.setMessage(message);
        requestDialog.setPositiveButton(titleButtonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RequestHandler handler = new RequestHandler();
                handler.sendEmptyMessageDelayed(0,20);

            }
        });

        requestDialog.setNegativeButton(titleButtonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return requestDialog.show();
    }

    public class RequestHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            for (int k = 0; k <10; k++){
                try {
                    Thread.sleep(1000);
                }catch (Exception ex){

                }

            }
            textView01.setText("원격 데이터 요청 완료");
        }
    }
}

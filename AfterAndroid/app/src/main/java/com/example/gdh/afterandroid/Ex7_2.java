package com.example.gdh.afterandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Ex7_2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex7_2);

        Button socketbtn = (Button)findViewById(R.id.socketbtn);
        final EditText ipedit = (EditText)findViewById(R.id.ipedit);

        socketbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addr = ipedit.getText().toString().trim();

                ConnectThread thread = new ConnectThread(addr);
                thread.start();
            }
        });

    }

    class ConnectThread extends Thread{

        String hostname;

        public ConnectThread(String addr){
            hostname = addr;
        }

        @Override
        public void run() {
            try {
                int port = 11001;

                Socket socket = new Socket(hostname,port);

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject("Hellow AndroidTown on Android");
                outputStream.flush();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                String obj = (String) inputStream.readObject();

                Log.d("Ex7_2","서버에서 받은 메시지 : "+obj);
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

package com.example.gdh.afterandroid;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class Ex7_3 extends AppCompatActivity {

    public static String defaultUrl = "http://m.naver.com";
    Handler handler = new Handler();
    TextView txtMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex7_3);

        final EditText editText = (EditText)findViewById(R.id.input01);
        Button button = (Button)findViewById(R.id.requestbtn);
        txtMsg = (TextView)findViewById(R.id.txtMsg);
        ScrollView scrollView =(ScrollView)findViewById(R.id.scroll);
        editText.setText(defaultUrl);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlStr = editText.getText().toString();
                ConnectThread thread = new ConnectThread(urlStr);
                thread.start();
            }
        });


    }

    class ConnectThread extends Thread{
        String urlStr;

        public ConnectThread(String inStr){
            urlStr = inStr;
        }

        @Override
        public void run() {
            try {
                final String output = request(urlStr);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtMsg.setText(output);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private String request(String urlStr){
            StringBuilder output = new StringBuilder();
            try{
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if (conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    int resCode = conn.getResponseCode();
                    if (resCode == HttpURLConnection.HTTP_OK){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while (true){
                            line = reader.readLine();
                            if (line == null){
                                break;
                            }
                            output.append(line+"\n");
                        }
                        Log.d("Ex7_3",line);
                        reader.close();
                        conn.disconnect();
                    }else if (resCode == HttpURLConnection.HTTP_NOT_FOUND){
                        Log.d("실패","실패");
                    }else if(resCode == HttpURLConnection.HTTP_MOVED_TEMP){
                        Log.d("Ex7_3","302");
                        URLConnection com = url.openConnection();
                        InputStream is = openConnectionCheckRedirects(com);
                        is.close();
                    }
                }
            }catch (Exception e){
                Log.e("Ex7_3","Exception",e);
                e.printStackTrace();
            }
            return output.toString();
        }
        public InputStream openConnectionCheckRedirects(URLConnection c) throws IOException
        {
            boolean redir;
            int redirects = 0;
            InputStream in = null;
            do
            {
                if (c instanceof HttpURLConnection)
                {
                    ((HttpURLConnection) c).setInstanceFollowRedirects(false);
                }
                in = c.getInputStream();
                redir = false;
                if (c instanceof HttpURLConnection)
                {
                    HttpURLConnection http = (HttpURLConnection) c;
                    int stat = http.getResponseCode();
                    if (stat >= 300 && stat <= 307 && stat != 306 &&
                            stat != HttpURLConnection.HTTP_NOT_MODIFIED)
                    {
                        URL base = http.getURL();
                        String loc = http.getHeaderField("Location");
                        URL target = null;
                        if (loc != null)
                        {
                            target = new URL(base, loc);
                        }
                        http.disconnect();
                        if (target == null || !(target.getProtocol().equals("http")
                                || target.getProtocol().equals("https"))
                                || redirects >= 5)
                        {
                            throw new SecurityException("illegal URL redirect");
                        }
                        redir = true;
                        c = target.openConnection();
                        redirects++;
                    }
                }
            }
            while (redir);
            return in;
        }


    }

}

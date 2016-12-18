package com.example.gdh.afterandroid;

import android.app.ActionBar;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class Ex06_5_1 extends AppCompatActivity {

    Handler handler = new Handler();
    ImageSwitcher switcher;
    Button startbtn;
    Button stopbtn;
    ImageThread thread;
    boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex06_5_1);

        startbtn = (Button)findViewById(R.id.startbtn);
        stopbtn = (Button)findViewById(R.id.stopbtn);
        switcher = (ImageSwitcher)findViewById(R.id.switcher);
        switcher.setVisibility(View.INVISIBLE);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackgroundColor(0xFF000000);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimation();
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAnimation();
            }
        });
    }


    private void startAnimation() {
        switcher.setVisibility(View.VISIBLE);

        thread = new ImageThread();
        thread.start();
    }

    private void stopAnimation() {
        running = false;
        try {
            thread.join();
        } catch(InterruptedException ex) { }

        switcher.setVisibility(View.INVISIBLE);
    }

    class ImageThread extends Thread {
        int duration = 250;
        final int imageId[] = { R.drawable.emo_im_crying,
                R.drawable.emo_im_happy,
                R.drawable.emo_im_laughing,
                R.drawable.emo_im_surprised };
        int currentIndex = 0;

        public void run() {
            running = true;
            while (running) {
                synchronized (this) {
                    handler.post(new Runnable() {
                        public void run() {
                            switcher.setImageResource(imageId[currentIndex]);
                        }
                    });

                    currentIndex++;
                    if (currentIndex == imageId.length) {
                        currentIndex = 0;
                    }

                    try {
                        Thread.sleep(duration);
                    } catch (InterruptedException ex) { }
                }
            }
        }
    }
}

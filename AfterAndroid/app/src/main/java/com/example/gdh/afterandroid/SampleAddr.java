package com.example.gdh.afterandroid;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SampleAddr extends AppCompatActivity {

    TextView textView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_addr);

        Button btn = (Button) findViewById(R.id.btn);
        textView = (TextView)findViewById(R.id.textv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_OK)

        {

            Cursor cursor = getContentResolver().query(data.getData(),

                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,

                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);

            cursor.moveToFirst();

          String name = cursor.getString(0);        //0은 이름을 얻어옵니다.

           String number = cursor.getString(1);   //1은 번호를 받아옵니다.
            textView.setText(name+"/"+number);
            cursor.close();


        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

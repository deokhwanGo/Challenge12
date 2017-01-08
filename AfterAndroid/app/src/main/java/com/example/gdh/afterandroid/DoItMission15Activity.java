package com.example.gdh.afterandroid;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class DoItMission15Activity extends Activity {
    public static final String TAG = "DoItMission15Activity";

    public static int spacing = -30;

    ImageView bookImage;
    TextView bookName;
    TextView bookAuthor;
    TextView bookPublisher;
    TextView bookDate;
    TextView bookPrice;

    public static int defaultSelection = 1;

    BookDatabase database;

    ImageAdapter coverImageAdapter;

    int recordCount;
    int[] recordIds;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_it_mission15);



        CoverFlow coverflow = (CoverFlow) findViewById(R.id.coverflow);
        coverImageAdapter = new ImageAdapter(this);
        coverflow.setAdapter(coverImageAdapter);

        coverflow.setSpacing(spacing);
        coverflow.setSelection(defaultSelection, true);
        coverflow.setAnimationDuration(3000);

        coverflow.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Item selected : " + position, Toast.LENGTH_SHORT).show();

                selectItemFromDatabase(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        bookImage = (ImageView) findViewById(R.id.bookImage);
        bookName = (TextView) findViewById(R.id.bookName);
        bookAuthor = (TextView) findViewById(R.id.bookAuthor);
        bookPublisher = (TextView) findViewById(R.id.bookPublisher);
        bookDate = (TextView) findViewById(R.id.bookDate);
        bookPrice = (TextView) findViewById(R.id.bookPrice);

        // open database
        if (database != null) {
            database.close();
            database = null;
        }

        database = BookDatabase.getInstance(this);
        boolean isOpen = database.open();
        if (isOpen) {
            Log.d(TAG, "Book database is open.");
        } else {
            Log.d(TAG, "Book database is not open.");
        }

        // load all items from the database
        loadAllItems();

        // default item


    }


    protected void onDestroy() {
        // open database
        if (database != null) {
            database.close();
            database = null;
        }

        super.onDestroy();
    }


    private void loadAllItems() {
        String SQL = "select _id, NAME, AUTHOR, PUBLISHER, RELEASE_DATE, PRICE, IMAGE_NAME from " + BookDatabase.TABLE_BOOK_INFO;
        Cursor cursor = database.rawQuery(SQL);

        recordCount = cursor.getCount();
        Log.d(TAG, "count of all items : " + recordCount);
        if (recordCount < 1) {
            return;
        }

        int curIndex = 0;
        Integer[] imageIds = new Integer[recordCount];
        recordIds = new int[recordCount];
        while(cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String name = cursor.getString(1);
            String author = cursor.getString(2);
            String publisher = cursor.getString(3);
            String releaseDate = cursor.getString(4);
            String price = cursor.getString(5);
            String imageName = cursor.getString(6);
            Log.d(TAG, "_id : " + _id + ", " + name + ", " + author + ", " + publisher + ", " + releaseDate + ", " + price + ", " + imageName);

            // get image resource id
            int imageResId = getResources().getIdentifier(imageName, "drawable", "com.example.gdh.afterandroid");
            Log.d(TAG, "Image Resource Id : " + imageResId);

            recordIds[curIndex] = _id;
            imageIds[curIndex] = new Integer(imageResId);

            curIndex++;
        }

        coverImageAdapter.setImageIds(imageIds);
        coverImageAdapter.notifyDataSetChanged();
    }


    private void selectItemFromDatabase(int position) {
        if (recordIds == null || position >= recordIds.length) {
            Log.d(TAG, "invalid position : " + position);

            return;
        }

        int _id = recordIds[position];

        String SQL = "select _id, NAME, AUTHOR, PUBLISHER, RELEASE_DATE, PRICE, IMAGE_NAME from " + BookDatabase.TABLE_BOOK_INFO + " where _id = " + _id;
        Cursor cursor = database.rawQuery(SQL);

        int count = cursor.getCount();
        Log.d(TAG, "count of selected items : " + count);
        if (count < 1) {
            return;
        }

        cursor.moveToNext();

        int id = cursor.getInt(0);
        String name = cursor.getString(1);
        String author = cursor.getString(2);
        String publisher = cursor.getString(3);
        String releaseDate = cursor.getString(4);
        String price = cursor.getString(5);
        String imageName = cursor.getString(6);
        Log.d(TAG, "id : " + id + ", " + name + ", " + author + ", " + publisher + ", " + releaseDate + ", " + price + ", " + imageName);

        // get image resource id
        int imageResId = getResources().getIdentifier(imageName, "drawable", "com.example.gdh.afterandroid");
        Log.d(TAG, "Image Resource Id : " + imageResId);


        bookImage.setImageResource(imageResId);
        bookName.setText(name);
        bookAuthor.setText(author);
        bookPublisher.setText(publisher);
        bookDate.setText(releaseDate);
        bookPrice.setText(price);
    }




    public class ImageAdapter extends BaseAdapter {
        int itemBackground;
        private Context mContext;

        private Integer[] mImageIds;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public void setImageIds(Integer[] ids) {
            mImageIds = ids;
        }

        public int getCount() {
            if (mImageIds == null) {
                return 0;
            }

            return mImageIds.length;
        }

        public Object getItem(int position) {
            if (mImageIds == null) {
                return null;
            }

            if (position < 0 || position >= mImageIds.length) {
                return null;
            }

            return mImageIds[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imgView = null;
            if (convertView == null) {
                imgView = new ImageView(mContext);
            } else {
                imgView = (ImageView) convertView;
            }

            Log.d(TAG, "image resId in getView : " + mImageIds[position]);

            imgView.setImageResource(mImageIds[position]);
            imgView.setLayoutParams(new CoverFlow.LayoutParams(160, 220));
            imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
            drawable.setAntiAlias(true);

            return imgView;
        }

        public float getScale(boolean focused, int offset) {
            return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
        }

    }

}

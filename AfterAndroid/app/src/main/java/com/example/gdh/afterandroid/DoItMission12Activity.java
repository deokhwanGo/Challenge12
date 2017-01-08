package com.example.gdh.afterandroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DoItMission12Activity extends AppCompatActivity {
	public static final String TAG = "DoItMission12Activity";

	ContentResolver resolver;
	Cursor cursor;
	int count;

	static int phoneCount;
	ArrayList<Integer> phoneIndexList = new ArrayList<Integer>();
	int recordIndex = 0;

	Cursor photoCursor;
	int photoCount;
	int photoRecordIndex = 0;



	LinearLayout personLayout;
	ImageView personImage;
	TextView personName;
	TextView personMobile;
	TextView personEmail;

	TextView personCount;

	LinearLayout photoLayout;
	ImageView photoImage;
	TextView photoName;
	TextView photoInfo;

	TextView photoCountText;


	Animation translateLeftAnim;
	Animation photoTranslateLeftAnim;

	public static boolean running = false;
	public static final int INTERVAL_ANIMATION = 5000;

	Handler handler = new Handler();

	Bitmap contactBitmap;
	Bitmap photoThumbnailBitmap;
	Bitmap photoFullBitmap;

	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	static int MY_PERMISSIONS_REQUEST_READ_CONTACTS =8619;
	static int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 8050;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        personLayout = (LinearLayout) findViewById(R.id.personLayout);

        personImage = (ImageView) findViewById(R.id.personImage);
        personName = (TextView) findViewById(R.id.personName);
        personMobile = (TextView) findViewById(R.id.personMobile);
        personEmail = (TextView) findViewById(R.id.personEmail);

        personCount = (TextView) findViewById(R.id.personCount);


        photoLayout = (LinearLayout) findViewById(R.id.photoLayout);

        photoImage = (ImageView) findViewById(R.id.photoImage);
        photoName = (TextView) findViewById(R.id.photoName);
        photoInfo = (TextView) findViewById(R.id.photoInfo);

        photoCountText = (TextView) findViewById(R.id.photoCount);



        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        ContactAnimationListener animListener = new ContactAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);

        photoTranslateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        PhotoAnimationListener photoAnimListener = new PhotoAnimationListener();
        photoTranslateLeftAnim.setAnimationListener(photoAnimListener);


        queryContacts();

        ContactAnimationThread thread = new ContactAnimationThread();
        thread.start();

		try {
			queryPhotos();
		} catch (Exception e) {
			e.printStackTrace();
		}

		PhotoAnimationThread photoThread = new PhotoAnimationThread();
        photoThread.start();

    }


    private void queryContacts() {

		resolver = getContentResolver();
		if (ContextCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.READ_CONTACTS)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_CONTACTS)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.


				cursor = resolver.query(Contacts.CONTENT_URI, null, null, null, null);
				count = cursor.getCount();


			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_CONTACTS},
						MY_PERMISSIONS_REQUEST_READ_CONTACTS);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
				cursor = resolver.query(Contacts.CONTENT_URI, null, null, null, null);
				count = cursor.getCount();
			}
		}

		cursor = resolver.query(Contacts.CONTENT_URI, null, null, null, null);
		count = cursor.getCount();
		// find only contacts with phone numbers
		phoneCount = 0;
		phoneIndexList.clear();

			while(cursor.moveToNext()) {
				String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
				if (hasPhoneNumber.equalsIgnoreCase("1")) {
					int curPosition = cursor.getPosition();
					phoneIndexList.add(curPosition);
					phoneCount++;
				}
			}

		Log.d(TAG, "Count of contacts with phone number : " + phoneCount);

		// display the first contact
		int index = phoneIndexList.get(recordIndex);
		displayContact(index);

		personCount.setText("1" + "/" + phoneCount);


    }


    private void queryPhotos() throws  Exception {
		resolver = getContentResolver();
		if (ContextCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
				photoCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
				photoCount = photoCursor.getCount();
			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}

		photoCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
		photoCount = photoCursor.getCount();
    	Log.d(TAG, "Count of photos : " + photoCount);

		// display the first contact
    	int index = 0;
		displayPhoto(index);

		photoCountText.setText("1" + "/" + photoCount);
    }



    class ContactAnimationThread extends Thread {
    	public void run() {
    		running = true;
    		while(running) {
	    		try {
	    			Thread.sleep(INTERVAL_ANIMATION);
	    		} catch(Exception ex) {
	    			ex.printStackTrace();
	    		}

	    		handler.post(new DisplayContactRunnable());
    		}
    	}
    }

    class DisplayContactRunnable implements Runnable {
    	public void run() {
    		personLayout.startAnimation(translateLeftAnim);
    	}
    }


    private class ContactAnimationListener implements AnimationListener {
		public void onAnimationEnd(Animation animation) {
			// display next contact
			recordIndex++;
			if (recordIndex >= phoneIndexList.size()) {
				recordIndex = 0;
			}

			int index = phoneIndexList.get(recordIndex);
			displayContact(index);

			personCount.setText((recordIndex+1) + "/" + phoneCount);
		}

		public void onAnimationRepeat(Animation animation) { }
		public void onAnimationStart(Animation animation) { }
    }


    class PhotoAnimationThread extends Thread {
    	public void run() {
    		running = true;
    		while(running) {
	    		try {
	    			Thread.sleep(INTERVAL_ANIMATION);
	    		} catch(Exception ex) {
	    			ex.printStackTrace();
	    		}

	    		handler.post(new DisplayPhotoRunnable());
    		}
    	}
    }

    class DisplayPhotoRunnable implements Runnable {
    	public void run() {
    		photoLayout.startAnimation(photoTranslateLeftAnim);
    	}
    }


    private class PhotoAnimationListener implements AnimationListener {
		public void onAnimationEnd(Animation animation) {
			// display next contact
			photoRecordIndex++;
			if (photoRecordIndex >= photoCount) {
				photoRecordIndex = 0;
			}

			displayPhoto(photoRecordIndex);

			photoCountText.setText((photoRecordIndex+1) + "/" + photoCount);
		}

		public void onAnimationRepeat(Animation animation) { }
		public void onAnimationStart(Animation animation) { }
    }


    private void displayContact(int index) {
    	if (cursor == null) {
    		queryContacts();
    	}

    	cursor.moveToPosition(index);

		String id = cursor.getString(cursor.getColumnIndex(Contacts._ID));
		String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));

		if (name != null) {
			personName.setText(name);
		} else {
			personName.setText("");
		}

		String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
        if (hasPhoneNumber.equalsIgnoreCase("1")) {
     	   hasPhoneNumber = "true";
        } else {
     	   hasPhoneNumber = "false" ;
        }

        String phoneNumber = null;
        if (Boolean.parseBoolean(hasPhoneNumber)) {
     	   Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
     	   while (phones.moveToNext()) {
     		   phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
     	   }
     	   phones.close();
        }

        if (phoneNumber != null) {
			personMobile.setText(phoneNumber);
		} else {
			personMobile.setText("");
		}

        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
        String email = null;
        while (emails.moveToNext()) {
     	   email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        emails.close();

        if (email != null) {
        	personEmail.setText(email);
		} else {
			personEmail.setText("");
		}

        Log.d(TAG, "Person : " + id + ", " + name + ", " + phoneNumber + ", " + email);


        // photo
        if (contactBitmap != null) {
        	contactBitmap.recycle();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        Uri dataUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(id));
       // contactBitmap = loadPhoto(dataUri, options);
        if (contactBitmap != null) {
     	   	Log.d(TAG, "Contact Photo image exists.");

     	   	personImage.setImageBitmap(contactBitmap);
        } else {
        	Log.d(TAG, "Contact Photo image is null.");

        	personImage.setImageResource(R.drawable.image02);
        }

    }

    private Bitmap loadPhoto(Uri selectedUri, BitmapFactory.Options options) {
        Uri contactUri = null;
        if (Contacts.CONTENT_ITEM_TYPE.equals(getContentResolver().getType(selectedUri))) {
            contactUri = Contacts.lookupContact(getContentResolver(), selectedUri);
        } else {

            Cursor cursor = getContentResolver().query(selectedUri, new String[] { Data.CONTACT_ID }, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    final long contactId = cursor.getLong(0);
                    contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        Cursor cursor = null;
        Bitmap bm = null;

        try {
            Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
            cursor = getContentResolver().query(photoUri, new String[] {Photo.PHOTO},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                bm = loadContactPhoto(cursor, 0, options);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }



        return bm;
    }

    public Bitmap loadContactPhoto(Cursor cursor, int bitmapColumnIndex, BitmapFactory.Options options) {
        if (cursor == null) {
            return null;
        }

        byte[] data = cursor.getBlob(bitmapColumnIndex);
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }


    private void displayPhoto(int index) {
    	if (photoCursor == null) {
    		try {
				queryPhotos();
			}catch (Exception e){
				e.printStackTrace();
			}

    	}

    if(photoCursor.moveToPosition(index)){
			String id = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media._ID));
			String dateStr = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

			long dateLong = 0L;
			String date = "";
			try {
				dateLong = Long.parseLong(dateStr);

				Date curDate = new Date();
				curDate.setTime(dateLong);

				date = format.format(curDate);
			} catch(Exception ex) {
				ex.printStackTrace();
			}


			if (date != null) {
				photoName.setText(date);
			} else {
				photoName.setText("");
			}

			Log.d(TAG, "Photo : " + id + ", " + date);


			// photo
			if (photoThumbnailBitmap != null) {
				photoThumbnailBitmap.recycle();
			}

			if (photoFullBitmap != null) {
				photoFullBitmap.recycle();
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;

			Uri uri = Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id) );
			photoThumbnailBitmap = loadThumbnailImage(uri.toString());
			if (photoThumbnailBitmap != null) {
				Log.d(TAG, "Photo Album image exists.");

				photoImage.setImageBitmap(photoThumbnailBitmap);
			} else {
				Log.d(TAG, "Photo Album image is null.");

				photoImage.setImageResource(R.drawable.emo_im_crying);
			}

			String description = null;
			photoFullBitmap = loadFullImage(uri);
			if (photoFullBitmap == null) {
				Log.d(TAG, "Photo Album full image is null.");
			} else {
				Log.d(TAG, "Photo Album full image is not null.");

				int photoWidth = photoFullBitmap.getWidth();
				int photoHeight = photoFullBitmap.getHeight();
				description = photoWidth + " X " + photoHeight;
			}

			if (description != null) {
				photoInfo.setText(description);
			} else {
				photoInfo.setText("");
			}

		}




    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		Log.d("aaa",requestCode+"/"+permissions+"/"+ grantResults);
	}

	protected Bitmap loadThumbnailImage(String url ) {
    	int originalImageId = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1, url.length()));

    	return MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), originalImageId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }

    public Bitmap loadFullImage(Uri photoUri) {
    	Cursor photoCursor = null;
    	try {
    		String[] projection = { MediaStore.Images.Media.DATA };
    		photoCursor = getContentResolver().query( photoUri, projection, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC" );
    		if ( photoCursor != null && photoCursor.getCount() == 1 ) {
    			photoCursor.moveToFirst();
    			String photoFilePath = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA));

    			return BitmapFactory.decodeFile( photoFilePath, null );
    		}
    	} finally {
    		if ( photoCursor != null ) {
    			photoCursor.close();
    		}
    	}

    	return null;
    }


	protected void onDestroy() {
		running = false;

		if (cursor != null) {
			cursor.close();
		}

		if (photoCursor != null) {
			photoCursor.close();
		}

		super.onDestroy();
	}


}
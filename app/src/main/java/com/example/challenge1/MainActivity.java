package com.example.challenge1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView captured, stored;
    private EditText id;
    private TextView numStored;
    private Button capture, view;
    private SQLiteDatabase imgDB;
    private static final int cam_capture_image = 12358;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captured = (ImageView) findViewById(R.id.disC);
        stored = (ImageView) findViewById(R.id.dispS);
        id = (EditText) findViewById(R.id.id);
        numStored= (TextView) findViewById(R.id.numStored);
        capture = (Button) findViewById(R.id.capture);
        view = (Button) findViewById(R.id.view);
        imgDB = openOrCreateDatabase("STORAGE", MODE_PRIVATE, null);
        String s = "CREATE TABLE IF NOT EXISTS imageList (" +
                "id INTEGER," +
                "image BLOB);" +
                "DROP TABLE imageList;";
        imgDB.execSQL(s);
    }

    public void captureImage(View view) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picIntent, cam_capture_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cam_capture_image && resultCode == RESULT_OK) {
            Bitmap img = (Bitmap) data.getExtras().get("data");
            captured.setImageBitmap(img);
            Cursor cr = imgDB.rawQuery("SELECT * FROM imageList;", null);
            int idr = 1;
            if (cr.moveToFirst()) {
                cr.moveToLast();
                idr = cr.getInt(cr.getColumnIndex("id")) + 1;
            }
            numStored.setText("Number of Images Stored = "+Integer.toString(idr));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 0, stream);
            ContentValues cv = new ContentValues();
            cv.put("id", idr);
            cv.put("image", stream.toByteArray());
            imgDB.insert("imageList", null, cv);
        }
    }

    public void displayImageStored(View view){
        Cursor cr=imgDB.rawQuery("SELECT * FROM imageList WHERE id = "+id.getText().toString()+";", null);
        cr.moveToLast();
        int idr = cr.getInt(cr.getColumnIndex("id"));
        byte[] imgB= cr.getBlob(cr.getColumnIndex("image"));
        Bitmap img= BitmapFactory.decodeByteArray(imgB,0,imgB.length);
        stored.setImageBitmap(img);
        id.setText(Integer.toString(idr));
    }
}

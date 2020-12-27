package com.uploadexample;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.myupload.istorage;


public class MainActivity extends AppCompatActivity {

    Button btnTakepictrue, btnget;
    private final int CAM_PIC_REQUEST = 1313;
    private String mApiKey = "1207d55d8a880040c5ee587b8fef09487043150e";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTakepictrue = findViewById(R.id.buttonTake);
        btnget = findViewById(R.id.btn_getLink);
        btnTakepictrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAM_PIC_REQUEST);
            }
        });

        btnget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //  String a = new istorage(MainActivity.this).getLink();
             //  Toast.makeText(MainActivity.this, "get link " + a, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAM_PIC_REQUEST){
            Uri uri = data.getData();
            String path = getRealPathFromURI(this,uri);
            new istorage(MainActivity.this)
                    .setLinkFile(path)
                    .setToken(mApiKey)
                    .upload();
        }
    }

    private String getRealPathFromURI(Context context, Uri uri)
    {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null,
                null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }
}
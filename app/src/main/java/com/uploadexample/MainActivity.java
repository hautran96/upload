package com.uploadexample;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.listener.onGetResults;
import com.myupload.Istorage;
import com.utils.Constant;


public class MainActivity extends AppCompatActivity implements onGetResults {

    Button btnTakepictrue, btnget;
    private Istorage istorage;
    private final int CAM_PIC_REQUEST = 1313;
    private final String mFileKey = "5fe991604147eb0021563fef";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTakepictrue = findViewById(R.id.buttonTake);
        btnget = findViewById(R.id.btn_getLink);
        String mApiKey = "05af5c31967932f4edf565ba26889ac6b75abab7";
        istorage =  new Istorage.IstorageBuilder(this, this)
                .setApiKey(mApiKey)
                .build();
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
               istorage.getLink(mFileKey);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAM_PIC_REQUEST){
            Uri uri = data.getData();
            String path = getRealPathFromURI(this,uri);
            istorage.upload(path);
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

    @Override
    public void onUpload(String mes, String key) {
        Log.i(Constant.TAG, "mes " + mes + " link " + key);
    }

    @Override
    public void onGetLink(int code, String link) {
        Log.i(Constant.TAG, "code " + code + " link " + link);
    }
}
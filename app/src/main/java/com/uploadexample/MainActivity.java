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
import com.listener.onGetLinkResults;
import com.myupload.istorage;
import com.utils.Constant;
import com.utils.HttpUtils;


public class MainActivity extends AppCompatActivity implements onGetLinkResults, HttpUtils.GetDataCompleted {

    Button btnTakepictrue, btnget;
    private final int CAM_PIC_REQUEST = 1313;
    private String mFileKey = "5fe991604147eb0021563fef";
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
                new istorage(MainActivity.this)
                       .setFileKey(mFileKey)
                       .getLink(MainActivity.this);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAM_PIC_REQUEST){
            Uri uri = data.getData();
            String path = getRealPathFromURI(this,uri);
            String mApiKey = "02e08d931ddaf543c20465b1b8b73ce3df20546d";
            new istorage(MainActivity.this)
                    .setLinkFile(path)
                    .setToken(mApiKey)
                    .upload(this);
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
    public void onSuccess(String link) {
        // return key image upload
        Log.i(Constant.TAG, "mFileKey  " + link);
    }

    @Override
    public void onCompleted(String link) {
        // return link image
        Log.i(Constant.TAG, "link " + link);
    }
}
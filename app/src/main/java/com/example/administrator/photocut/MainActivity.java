package com.example.administrator.photocut;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
public EditText setCount;
  //  public Uri imageUri;
    public CircleImageView myView;
    public Bitmap mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myView=(CircleImageView)findViewById(R.id.myView);
        setCount=(EditText)findViewById(R.id.SetCount);
        Button change=(Button)findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String sCount=setCount.getText().toString();
                    int count=Integer.parseInt(sCount);
                    myView.setCount(count);
                    myView.changeCount();
                } catch (NumberFormatException e){e.printStackTrace();}
            }
        });
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto();
                myView.setImageBitmap(mBitmap);
            }
        });
    }
//    @Override
//    public void onActivityResult(int requestCode,int resultCode,Intent intent){
//        switch (requestCode){
//            case 1:
//                if (resultCode==RESULT_OK){
//                    try {
//                        mBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
//                        Log.i("TAG",mBitmap.toString());
//                        myView.setImageBitmap(mBitmap);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//        }
//    }
//    public void changePhoto(){
//       File outputImage=new File(Environment.getExternalStorageDirectory(),"output_photo.jpg");
//        try{
//            if (outputImage.exists()){
//                outputImage.delete();
//            }
//            outputImage.createNewFile();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        imageUri = Uri.fromFile(outputImage);
//        Intent intent=new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
//        //intent.putExtra("crop",true);
//        //intent.putExtra("scale",true);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//        startActivityForResult(intent,1);
//    }
    public  void changePhoto(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String uri="file://"+Environment.getExternalStorageDirectory()+"/a.jpg";
            mBitmap=null;
            try {
                mBitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(uri)));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
      @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
          if (resultCode!=RESULT_OK) return;
          ContentResolver contentResolver=getContentResolver();
           mBitmap=null;
          if (requestCode==1){
              Uri uri=intent.getData();
              try {
                  mBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                  myView.setImageBitmap(mBitmap);
              }catch (IOException e){
                  e.printStackTrace();
              }
          }
      }
}

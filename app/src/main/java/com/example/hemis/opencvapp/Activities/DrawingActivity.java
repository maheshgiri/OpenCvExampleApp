package com.example.hemis.opencvapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import com.example.hemis.opencvapp.CustomViews.VistaDibuixar;
import com.example.hemis.opencvapp.Utils.IOUtils;


public class DrawingActivity extends Activity  {
    Bitmap sourceImage,destImage, BitmapMascara;
    VistaDibuixar vista;
    ImageButton saveBtn;
    IOUtils ioutils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AttributeSet a=null;
        VistaDibuixar vista= new VistaDibuixar(this,a);

        setContentView(vista);
        ioutils =new IOUtils();

        Intent intent2 = getIntent();
        sourceImage= ioutils.loadImageFromStorage("src.png");
        //sourceImage = (Bitmap) intent2.getParcelableExtra("SourceImage");

        System.out.println("he rebut el SourceImage" + sourceImage.getWidth());
        //System.out.println(saveToInternalSorage(sourceImage));
        //vista= (VistaDibuixar) findViewById(R.id.drawing);
        //saveBtn= (ImageButton) findViewById(R.id.new_btn);
        //saveBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }





}

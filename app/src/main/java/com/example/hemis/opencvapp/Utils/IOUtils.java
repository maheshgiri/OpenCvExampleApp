package com.example.hemis.opencvapp.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Marc on 24/12/2014.
 */
public class IOUtils {


    public IOUtils(){
    }

    public String saveToInternalSorage(Context c,Bitmap bitmapImage,String name){
        ContextWrapper cw = new ContextWrapper(c.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,name);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("saved at"+directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }


    public Bitmap loadImageFromStorage(String name)
    {
        Bitmap b=null;
        String path="/data/data/com.example.hemis.opencvapp/app_imageDir";

        try {
            File f=new File(path, name);
            b = BitmapFactory.decodeStream(new FileInputStream(f));

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        System.out.println("loaded from"+path);

        return b;
    }

    public void scanMedia(Context c,String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        c.sendBroadcast(scanFileIntent);
    }

}

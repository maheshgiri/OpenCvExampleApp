package com.example.hemis.opencvapp.Utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Marc on 13/01/2015.
 */
public class ImageUtils {

    public ImageUtils(){

    }

    public Bitmap rotate(Bitmap map){

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(), matrix, true);
    }

    public Bitmap escala(int x,int y,Bitmap b){
        Bitmap resizedBitmap;
        if (b.getHeight()>(y*0.7)) {
            float multFactor = (float) b.getHeight() / (float) b.getWidth();
            int newWidth = (int) ((y*0.7)/multFactor);
            resizedBitmap = Bitmap.createScaledBitmap(b, newWidth, (int)(y*0.7), false);
        }else{
            resizedBitmap=b;
        }

        if (resizedBitmap.getWidth()>(x*0.8)) {
            float multFactor = (float) resizedBitmap.getHeight() / (float) resizedBitmap.getWidth();
            int newWidth = (int) ((x * 0.8) / multFactor);
            resizedBitmap = Bitmap.createScaledBitmap(resizedBitmap, newWidth, (int) (x * 0.8), false);
        }
        return  resizedBitmap;
    }
}

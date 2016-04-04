package com.example.hemis.opencvapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.photo.Photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2HSV;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.CV_BLUR;
import static org.opencv.imgproc.Imgproc.CV_BLUR_NO_SCALE;
import static org.opencv.imgproc.Imgproc.CV_DIST_MASK_3;
import static org.opencv.imgproc.Imgproc.CV_GAUSSIAN;
import static org.opencv.imgproc.Imgproc.CV_POLY_APPROX_DP;
import static org.opencv.imgproc.Imgproc.CV_SHAPE_RECT;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.resize;


public class MainActivityA extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener,Camera.PictureCallback {
    protected Button captureButton;
    MyCameraView javaCameraView;
    ImageView outputImage;
    Mat output;
    Mat src_mask ;
    private Mat grayscaleImage;
    private int absoluteFaceSize;
    Bitmap outputBmp;
    Mat source;
    Mat destination;
    boolean ready=false;
    CascadeClassifier detectHumanBody;
    CameraBridgeViewBase.CvCameraViewFrame cvCameraViewFrame;
    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {


        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.d("TAG", "Loaded Successfully..");
                    ready=true;
                    //javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }


        }

    };


    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2,new Matrix(), null);
        //canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

void createImage(){

  try {
      Bitmap srcBmp=BitmapFactory.decodeResource(getResources(), R.drawable.matca);
       Mat sourceMat=new Mat();//=Imgcodecs.imread(sourcePath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
      Utils.bitmapToMat(srcBmp, sourceMat);
    //  srcBmp.recycle();
      //srcBmp=null;
      Bitmap srcBmp1=BitmapFactory.decodeResource(getResources(), R.drawable.example_image);
       Mat destMat=new Mat();
      Bitmap result=overlay(srcBmp1, srcBmp);
     // Bitmap scaledBitmap = Bitmap.createScaledBitmap(result, dstWidth, dstHeight, filter);

      srcBmp.recycle();
      srcBmp1.recycle();
      outputImage.setImageBitmap(result);
      Utils.bitmapToMat(srcBmp, destMat);
      srcBmp.recycle();
      resize(sourceMat, sourceMat, new Size(destMat.cols(), destMat.rows()), 0, 0, INTER_CUBIC);
      outputBmp = Bitmap.createBitmap(sourceMat.cols(), sourceMat.rows(), Bitmap.Config.ARGB_8888);
      Utils.matToBitmap(sourceMat, outputBmp, true);
      outputBmp.recycle();
      Rect roi = new Rect(sourceMat.cols(),sourceMat.rows(), sourceMat.cols(),sourceMat.rows());
      src_mask=new Mat(destMat.rows(),destMat.cols(),destMat.type());
      //src_mask=destMat.submat(roi);
      output=new Mat(destMat.rows(),destMat.cols(),destMat.type());

      // Imgproc.cvtColor(destination, output, Imgproc.COLOR_RGB2BGRA);

      //Utils.bitmapToMat(batt, output);
      try{
          //resize(sourceMat, sourceMat, new Size(destMat.cols(), destMat.rows()));
          /*final Mat newOut=new Mat(destMat.cols(),destMat.rows(),destMat.depth());
          new Runnable() {
              @Override
              public void run() {
                  Photo.seamlessClone(sourceMat, destMat, src_mask, new Point(60, 60), newOut, Photo.NORMAL_CLONE);
              }
          }.run();*/
          Mat outMat=new Mat();
          resize(src_mask, outMat, new Size(destMat.cols(), destMat.rows()), 0, 0, INTER_CUBIC);

          Core.addWeighted(src_mask,0.8, sourceMat, 0.2, 1, output);

      }catch (CvException e){
          Log.e("EXCPEITON",e.getMessage());
      }catch (Exception r){
          Log.e("EXCP",r.getMessage());
      }

      //
      // Imgproc.cvtColor(source, output, Imgproc.COLOR_RGB2BGRA);
      outputBmp = Bitmap.createBitmap(destMat.cols(), destMat.rows(),Bitmap.Config.ARGB_8888);

      Utils.matToBitmap(destMat, outputBmp,true);
      //outputBmp.compress(Bitmap.CompressFormat.JPEG,800)



  }catch (Exception e){
      Log.e("EXCEPTION",""+e.getMessage());
  }
  /*  // Write the image in a file (in jpeg format)
    try {
           *//* FileOutputStream fos = new FileOutputStream(mPictureFileName);
            fos.write(data);
             Log.d("DATA",""+data);
            fos.close();*//*
        Bitmap batt = BitmapFactory.decodeResource(getResources(), R.drawable.download);
        Bitmap batt1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_chat_bubble_outline_black_24dp);

         Mat src=new Mat(batt1.getHeight(), batt1.getWidth(), CvType.CV_8U);
         Mat dest=new Mat(batt.getHeight(), batt.getWidth(), CvType.CV_8U);
         Mat mat=new Mat(batt1.getHeight(), batt1.getWidth(), CvType.CV_8U);
         Point center=new Point(dest.width()/2,dest.height()/2);
// Seamlessly clone src into dst and put the results in output
        FileOutputStream out = null;
        Bitmap bmp = null;
        List<MatOfPoint> list=new ArrayList<>();
        Point poly[][]=new Point[1][7];
        poly[0][0] = new Point(4, 80);
        poly[0][1] = new Point(30, 54);
        poly[0][2] = new Point(151,63);
        poly[0][3] = new Point(254,37);
        poly[0][4] = new Point(298,90);
        poly[0][5] = new Point(272,134);
        poly[0][6] = new Point(43,122);
        list.add(new MatOfPoint(poly[0][0], poly[0][1], poly[0][2], poly[0][3], poly[0][4], poly[0][5], poly[0][6]));
        Mat binarized = new Mat();
        threshold(mat, binarized, 100, 255, THRESH_BINARY);

        Mat output=new Mat(src.rows(),src.cols(),src.depth(),Scalar.all(255));
        try {

          Photo.seamlessClone(src, dest,binarized, center, output, MIXED_CLONE);
        }catch (Exception e){
            Log.e("EW",""+e.getMessage());
        }
       bmp = Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.RGB_565);
       Utils.matToBitmap(output, bmp);
        Log.d("Bitmap", bmp.toString());
        outputImage.setImageBitmap(bmp);

        try {
            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
//Bitmap.createBitmap(output.cols(), output.rows(), Bitmap.Config.RGB_565)


            //   out = new FileOutputStream(mPictureFileName);
            //     bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            //  Log.d("LOGG","success"+mPictureFileName);

        }
        catch (CvException e){Log.d("Exception",e.getMessage());}catch (Exception e) {
            e.printStackTrace();
        }


    } catch (Exception e){
        e.printStackTrace();
    }*/

    /**
     * Working Code
     *
     */
    /*try {
      Bitmap srcBmp=BitmapFactory.decodeResource(getResources(), R.drawable.ic_chat_bubble_outline_black_24dp);
       Mat sourceMat=new Mat();//=Imgcodecs.imread(sourcePath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
      Utils.bitmapToMat(srcBmp, sourceMat);
      srcBmp.recycle();
      srcBmp=null;
      srcBmp=BitmapFactory.decodeResource(getResources(), R.drawable.example_image);
       Mat destMat=new Mat();
      Utils.bitmapToMat(srcBmp, destMat);
      srcBmp.recycle();

      Rect roi = new Rect(destMat.cols()-sourceMat.cols(),destMat.rows()-sourceMat.rows(), sourceMat.cols(),sourceMat.rows());
      src_mask=new Mat(destMat.rows(),destMat.cols(),destMat.type());
      src_mask=destMat.submat(roi);
      output=new Mat(destMat.rows(),destMat.cols(),destMat.type());

      // Imgproc.cvtColor(destination, output, Imgproc.COLOR_RGB2BGRA);

      //Utils.bitmapToMat(batt, output);
      try{
          //resize(sourceMat, sourceMat, new Size(destMat.cols(), destMat.rows()));
          /*final Mat newOut=new Mat(destMat.cols(),destMat.rows(),destMat.depth());
          new Runnable() {
              @Override
              public void run() {
                  Photo.seamlessClone(sourceMat, destMat, src_mask, new Point(60, 60), newOut, Photo.NORMAL_CLONE);
              }
          }.run();
    Mat outMat=new Mat();
    resize(src_mask, outMat, new Size(destMat.cols(), destMat.rows()),0,0,INTER_CUBIC);
    outputBmp = Bitmap.createBitmap(output.cols(), output.rows(),Bitmap.Config.ARGB_8888);
    Utils.matToBitmap(output, outputBmp, true);
    outputBmp.recycle();
    Core.addWeighted(outMat,0.3, destMat, 0.7, 0, output);

}catch (CvException e){
        Log.e("EXCPEITON",e.getMessage());
    }catch (Exception r){
        Log.e("EXCP",r.getMessage());
    }

    //
    // Imgproc.cvtColor(source, output, Imgproc.COLOR_RGB2BGRA);
    outputBmp = Bitmap.createBitmap(output.cols(), output.rows(),Bitmap.Config.ARGB_8888);

    Utils.matToBitmap(output, outputBmp,true);
    //outputBmp.compress(Bitmap.CompressFormat.JPEG,800)
    outputImage.setImageBitmap(outputBmp);


}catch (Exception e){
        Log.e("EXCEPTION",""+e.getMessage());
        }
        */
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        captureButton = (Button) findViewById(R.id.captureImage);
        javaCameraView = (MyCameraView) findViewById(R.id.cameraSurfaceView);
        outputImage=(ImageView)findViewById(R.id.image);
        javaCameraView.setVisibility(View.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setOnTouchListener(this);




        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ready) createImage();
                /*String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/saved_images");
                myDir.mkdirs();
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                Log.e("Path", "" + file.getAbsolutePath());
                javaCameraView.takePicture(file.getAbsolutePath());*/

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, baseLoaderCallback);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {


    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        return inputFrame.rgba();
        //detectHumanBody.detectMultiScale(inputFrame,);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return true;

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.e("PATH",""+data);
    }
}

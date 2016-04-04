package com.example.hemis.opencvapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.hemis.opencvapp.PoissonBlending.Blending;
import com.example.hemis.opencvapp.R;
import com.example.hemis.opencvapp.Utils.IOUtils;


public class FinalSettingsActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    Bitmap destImage1, destImage2, sourceImage, masked, mask;
    ImageView imageView2;

    IOUtils ioutils;
    SeekBar seekBar;
    CheckBox box;
    int x_min, x_max, y_min, y_max;
    int mask_width, mask_height;
    int offset_x, offset_y;
    private ProgressDialog progress;
    boolean mixed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_final_settings);

        ioutils = new IOUtils();

        masked = ioutils.loadImageFromStorage("masked.png");
        sourceImage = ioutils.loadImageFromStorage("src.png");
        destImage1 = ioutils.loadImageFromStorage("dest.png");
        destImage2 = ioutils.loadImageFromStorage("dest.png");
        x_min = 9999;
        x_max = 0;
        y_min = 9999;
        y_max = 0;
        mixed=false;
        box=(CheckBox)findViewById(R.id.mixedCheckBox);

        mask = maskCreate(sourceImage, masked);
        //mask=createMask4test();
        imageView2 = (ImageView) findViewById(R.id.ImagePos);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        imageView2.setImageBitmap(destImage1);

        imageView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (action == MotionEvent.ACTION_UP) {
                    canvi_coordenades(imageView2, destImage2, x, y);
                    coloca();
                }
                return true;
            }

        });
    }

    private void canvi_coordenades(ImageView iv, Bitmap bm, int x, int y) {
        if (x > 0 && y > 0 && x < iv.getWidth() && y < iv.getHeight()) {
            offset_x = (int) ((double) x * ((double) bm.getWidth() / (double) iv.getWidth()));
            offset_y = (int) ((double) y * ((double) bm.getHeight() / (double) iv.getHeight()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.position, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private Bitmap maskCreate(Bitmap src, Bitmap masked) {
        Bitmap mask = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                if (src.getPixel(x, y) == masked.getPixel(x, y))
                    mask.setPixel(x, y, Color.BLACK);
                else {
                    mask.setPixel(x, y, Color.WHITE);
                    if (x < x_min) x_min = x;
                    if (x > x_max) x_max = x;
                    if (y < y_min) y_min = y;
                    if (y > y_max) y_max = y;

                }
            }
        }

        mask_width = (x_max - x_min) + 3;
        mask_height = (y_max - y_min) + 3;
        Bitmap mask2 = Bitmap.createBitmap(mask_width, mask_height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < mask_width; x++) {
            for (int y = 0; y < mask_height; y++) {

                mask2.setPixel(x, y, mask.getPixel(x_min + x - 1, y_min + y - 1));

            }
        }
        return mask2;
    }

    private Bitmap createMask4test(){
        offset_x=10;
        offset_y=10;

        Bitmap mask3 = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);

        for(int i=0;i<300-1;i++){
            for(int j=0;i<300-1;j++){

                if(i>10 && i<61 && j>10 && j<21){
                    mask3.setPixel(i,j,Color.WHITE);
                }else{
                    mask3.setPixel(i,j,Color.BLACK);
                }
            }
        }

        return mask3;
    }

    public void fesBlending(View view) {
        if(box.isChecked())
            mixed=true;
        else
            mixed=false;

        AsyncBlending bb = new AsyncBlending();
        bb.execute("a");
        progress.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
        progress.setIndeterminate(true);
        progress.show();
    }

    private void coloca() {
        if ((offset_x +(int)(mask_width/2.0)) < destImage1.getWidth()-2 && (offset_x -(int)(mask_width/2.0))-2 > 0
                && (offset_y +(int)( mask_height/2.0)) < destImage1.getHeight()-2 &&(offset_y -(int)( mask_height/2.0))-2 >0){

            destImage2 = destImage1.copy(destImage1.getConfig(), true);

            offset_x+=-(int)(mask_width/2.0);
            offset_y+=-(int)( mask_height/2.0);

            for (int i = 0; i < mask.getWidth(); i++) {
                for (int j = 0; j < mask.getHeight(); j++) {
                    if (mask.getPixel(i, j) == Color.WHITE) {
                        destImage2.setPixel(i + offset_x , j + offset_y, sourceImage.getPixel(i + x_min, j + y_min));
                    }
                }
            }
            imageView2.setImageBitmap(destImage2);
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private class AsyncBlending extends AsyncTask<String, Integer, String> {
        Blending b;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(FinalSettingsActivity.this, "Processing...", "Please wait", true);


        }

        @Override
        protected String doInBackground(String... params) {
            b = new Blending(destImage1, sourceImage, mask, offset_x, offset_y, x_min, y_min, 1 + (seekBar.getProgress() / 1000)  ,mixed);
            b.principal();
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            ioutils.saveToInternalSorage(FinalSettingsActivity.this, b.sortida, "done.png");
            //imageView2.setImageBitmap(b.sortida);
            progress.dismiss();
            Intent i = new Intent(FinalSettingsActivity.this, ResultsActivity.class);
            Bundle bundle = new Bundle();

            bundle.putDouble("time", (float) b.elapsedTime);
            bundle.putInt("punts", (int) b.getNumPunts());
            bundle.putInt("iters",(int) b.getIters());
            i.putExtras(bundle);
            startActivity(i);
        }
    }
}

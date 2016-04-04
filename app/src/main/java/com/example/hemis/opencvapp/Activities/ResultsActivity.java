package com.example.hemis.opencvapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.hemis.opencvapp.R;
import com.example.hemis.opencvapp.Utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;

public class ResultsActivity extends Activity {
    Bitmap out;
    ImageView iv;
    ImageButton saveBtn;
    IOUtils ioutils;
    EditText input;
    double time;
    int punts;
    int iters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_results);

        Bundle b = getIntent().getExtras();
        time = b.getDouble("time");
        punts=b.getInt("punts");
        iters=b.getInt("iters");
        time=time/1000;
        ioutils =new IOUtils();
        out= ioutils.loadImageFromStorage("done.png");
        System.out.println("omg"+out.getWidth());
        iv=(ImageView) findViewById(R.id.outputt);
        saveBtn=(ImageButton) findViewById(R.id.imageButton);
        iv.setImageBitmap(out);
        input = new EditText(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.results, menu);
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

    public void saveButtonClicked(View view) {

        new AlertDialog.Builder(ResultsActivity.this)
                .setTitle("Save the image")
                .setMessage("Enter the file name")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();

                        String path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/" + value + ".png";
                        File file = new File(path);
                        if (file.exists()) {
                            file.delete();
                        }
                        try {
                            FileOutputStream outstream = new FileOutputStream(file);
                            out.compress(Bitmap.CompressFormat.PNG, 100, outstream);
                            outstream.flush();
                            outstream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ioutils.scanMedia(ResultsActivity.this,path);

                        Toast.makeText(getApplicationContext(), "image saved at: " + Environment.DIRECTORY_PICTURES + "/" + value + ".png",
                                Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


            }
        }).show();


    }

    public void viewStats(View view) {

        new AlertDialog.Builder(ResultsActivity.this)
                .setTitle("Statistics:")
                .setMessage("Elapsed time: "+time+"s"+"\n"+"Nº of points: "+punts+"\n"+"Nº of iterations: "+iters)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }
}

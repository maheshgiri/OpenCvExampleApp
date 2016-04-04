package com.example.hemis.opencvapp.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;


import com.example.hemis.opencvapp.R;
import com.example.hemis.opencvapp.Utils.IOUtils;
import com.example.hemis.opencvapp.Utils.ImageUtils;


public class LoadImagesActivity extends ActionBarActivity {
    String path;
    private ImageView imgView1;
    public Bitmap src,dest;
    ImageButton b1;
    IOUtils ioutils;
    ImageUtils imageutils;
    int button=0;
    LinearLayout linear;
    TableLayout table;
    TextView textView;
    int status;
    int width,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_load_images);

        imgView1= (ImageView)findViewById(R.id.destImageView);



        textView =(TextView)findViewById(R.id.msg);

        b1=(ImageButton) findViewById(R.id.b1);

        ioutils =new IOUtils();
        imageutils= new ImageUtils();
        status=0;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select the action");
        menu.add(0, v.getId(), 0, "Load from gallery");
        menu.add(0, v.getId(), 0, "Take a photo");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        try
        {
            if(item.getTitle()=="Load from gallery")
            {
                if(status==0)
                    button=1;
                else
                    button=3;
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, button);
            }
            else if(item.getTitle()=="Take a photo")
                takePhoto();

            return true;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return true;
        }
    }

    public void carregaButton(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
        unregisterForContextMenu(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            if (requestCode == 1 || requestCode == 3) {
                Uri photoUri = data.getData();
                if (photoUri != null) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    if (requestCode == 1) {
                        this.dest = BitmapFactory.decodeFile(filePath);
                        dest = imageutils.escala(width,height,dest);
                        imgView1.setImageBitmap(this.dest);
                    } else if (requestCode == 3) {
                        this.src = BitmapFactory.decodeFile(filePath);
                        src = imageutils.escala(width,height,src);
                        checkSrc();
                        imgView1.setImageBitmap(this.src);
                    }
                }
            }
        }
            if (requestCode == 2) {
                ioutils.scanMedia(this, path);
                if (status==0){
                    this.dest = BitmapFactory.decodeFile(path);
                    dest = imageutils.escala(width,height,dest);
                    imgView1.setImageBitmap(this.dest);
                }else{
                    this.src = BitmapFactory.decodeFile(path);
                    src = imageutils.escala(width,height,src);
                    checkSrc();
                    imgView1.setImageBitmap(this.src);
                }
            }
    }


    private void takePhoto()
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        final Calendar c = Calendar.getInstance();
        String new_Date= c.get(Calendar.DAY_OF_MONTH)+"-"+((c.get(Calendar.MONTH))+1)   +"-"+c.get(Calendar.YEAR) +" " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE)+ "-"+ c.get(Calendar.SECOND);
        path=String.format(Environment.getExternalStorageDirectory() +"/"+Environment.DIRECTORY_DCIM+"/Camera"+"/%s.png",new_Date);
        File photo = new File(path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
        startActivityForResult(intent, 2);
    }

    public void okBtnClicked(View view){
        if(status==0 && dest!=null){
            textView.setText("Step 2: choose a source image");
            status=1;
            imgView1.setImageDrawable(null);
        }else if(status==1 && src!=null){
            Intent intentt = new Intent(this, DrawingActivity.class);
            ioutils.saveToInternalSorage(this,dest, "dest.png");
            ioutils.saveToInternalSorage(this,src, "src.png");
            startActivity(intentt);
        }
    }


    private void checkSrc(){
        if(src.getWidth()>dest.getWidth()){
            float multFactor = (float) src.getHeight() / (float) src.getWidth();
            int newWidth = (int) (dest.getWidth()*0.95);
            src = Bitmap.createScaledBitmap(src, newWidth, (int) (newWidth*multFactor), false);
        }
    }


    public void decreaseBtnClicked(View view) {
        if(status==0){
            if (dest != null) {
                float multFactor = (float) dest.getHeight() / (float) dest.getWidth();
                int newWidth = (int) (dest.getWidth() * 0.95);
                dest = Bitmap.createScaledBitmap(dest, newWidth, (int) (newWidth * multFactor), false);
                imgView1.setImageBitmap(dest);
            }
        }else if(status==1){
            if (src != null) {
                float multFactor = (float) src.getHeight() / (float) src.getWidth();
                int newWidth = (int) (src.getWidth() * 0.95);
                src = Bitmap.createScaledBitmap(src, newWidth, (int) (newWidth * multFactor), false);
                imgView1.setImageBitmap(src);
            }
        }
    }


    public void rotateBtnClicked(View view) {
        if(status==0){
            if (dest != null) {
                dest=imageutils.rotate(dest);
                dest=imageutils.escala(width,height,dest);
                imgView1.setImageBitmap(dest);
            }
        }else if(status==1){
            if (src != null) {
                src=imageutils.rotate(src);
                src=imageutils.escala(width,height,src);
                imgView1.setImageBitmap(src);
            }
        }
    }


}
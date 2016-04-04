package com.example.hemis.opencvapp.CustomViews;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.hemis.opencvapp.Activities.FinalSettingsActivity;
import com.example.hemis.opencvapp.R;
import com.example.hemis.opencvapp.Utils.IOUtils;

public class VistaDibuixar extends View {

    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;

    public Bitmap mBitmap;
    public Bitmap Bitmap2;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private int strokeWidth;
    private IOUtils ioutils;
    Bitmap ok,cancel;
    Context c;
    int c_width,c_height;
    Paint paintText;
    Rect rectText;
    String msg;

    public VistaDibuixar(Context c,AttributeSet attrs) {
        super(c);
        this.c=c;
        this.setBackgroundColor(0x006699);
        ok=BitmapFactory.decodeResource(getResources(), R.drawable.v);
        cancel=BitmapFactory.decodeResource(getResources(), R.drawable.x);
        ok = Bitmap.createScaledBitmap(ok, ok.getWidth()/6,ok.getHeight()/6, false);
        cancel = Bitmap.createScaledBitmap(cancel, cancel.getWidth()/6,cancel.getHeight()/6, false);
        ioutils = new IOUtils();
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mBitmap= ioutils.loadImageFromStorage("src.png");
        mBitmap=mBitmap.copy(Bitmap.Config.ARGB_8888 ,true);
        msg="Step 3: select the area to be pasted";
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        strokeWidth=Long.valueOf(Math.round(Math.sqrt(mBitmap.getWidth()*mBitmap.getHeight())/10.0)).intValue();
        mPaint.setStrokeWidth(strokeWidth);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(50);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setShadowLayer(10f, 10f, 10f, Color.GRAY);

        rectText = new Rect();
        paintText.getTextBounds(msg, 0, msg.length(), rectText);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvas = new Canvas(mBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        c_width=canvas.getWidth();
        c_height=canvas.getHeight();

        canvas.drawColor(0xFFAAAAAA);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(ok,canvas.getWidth()-130,canvas.getHeight()-150,null);
        canvas.drawBitmap(cancel,canvas.getWidth()-270,canvas.getHeight()-150,null);
        canvas.drawText(msg,0, canvas.getHeight()-rectText.height(), paintText);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;


    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
        Bitmap2=mBitmap.copy(Bitmap.Config.ARGB_8888 ,true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                buttonTouched(x,y);
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    private void buttonTouched(float x,float y){
        if(x> c_width-130 && x<c_width && y<c_height && y>c_height-150){
            ioutils.saveToInternalSorage(c, Bitmap2, "masked.png");
            Intent intent3 = new Intent(c,FinalSettingsActivity.class);
            c.startActivity(intent3);
        }
        if(x> c_width-270 && x<c_width-140 && y<c_height && y>c_height-150){
            mBitmap= ioutils.loadImageFromStorage("src.png");
            mBitmap=mBitmap.copy(Bitmap.Config.ARGB_8888 ,true);
            mCanvas = new Canvas(mBitmap);
        }

    }

}
package com.example.hemis.opencvapp.PoissonBlending;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;


import java.util.ArrayList;
import java.util.Random;

public class Blending {

    private static final int MAX_ITERS = 20;
    public Bitmap src; //imatge origen d'on extraiem una zona
    public Bitmap dest; //imatge on enganxem una zona
    public Bitmap mask; //mascara que defineix la zona: Color.WHITE és la zona del src, Color.BLACK és la zona de destí
    public Bitmap sortida; //l'utilitzem per guardar els índexs dels pixels i conté el resultat final
    ArrayList<Punt> punts;
    Context c;
    int offset_x,offset_y,x_min,y_min;
    float W;
    int [][] mat;
    long lStartTime;
    long lEndTime;
    public double elapsedTime;
    Boolean mixed;



    public Blending(Bitmap dest, Bitmap src, Bitmap mask,int offset_x,int offset_y,int x_min,int y_min,float W,Boolean mixed){
        this.offset_x=offset_x;
        this.offset_y=offset_y;
        this.x_min=x_min;
        this.y_min=y_min;
        this.W=1.986f;

        //els bitmaps src, dest i mask son inmutables, per aixo creo de nous
        this.dest=dest;
        this.src=src;
        this.mask=mask;
        punts = new ArrayList<Punt>();
        this.sortida = dest.copy(dest.getConfig(),true);
        this.mixed=mixed;

    }


    /*
     s'usa per el mixed seamless blending
      */
    public void maskLoop() {

        mat= new int [mask.getWidth()] [mask.getHeight()];
        int Nums = 0;
        Punt p;
        int val;

        for (int w = 0; w < mask.getWidth(); w++) {
            for (int h = 0; h < mask.getHeight(); h++) {
                if (mask.getPixel(w, h)== Color.WHITE) {
                    val=src.getPixel(w+x_min,h+y_min);
                    p=new Punt(w+offset_x,h+offset_y,val);
                    p.setRed(Color.red(val));
                    p.setGreen(Color.green(val));
                    p.setBlue(Color.blue(val));
                    calculaVpq(p, w+x_min, h+y_min);
                    mat[w][h]= Nums;
                    punts.add(p);
                    Nums++;
                }
            }
        }

/*
        Random r= new Random();
        int nu=r.nextInt(punts.size()-1);
        while(punts.size()<50000){
            Punt pp=new Punt(punts.get(nu).getX(),punts.get(nu).getY(),0);
            pp.setRed(40);
            pp.setGreen(40);
            pp.setBlue(40);
            punts.add(pp);
        }
*/
    }


    /*
    s'usa per el mixed seamless blending
     */
    public void maskLoop2() {

        mat= new int [mask.getWidth()] [mask.getHeight()];
        int Nums = 0;
        Punt p;
        int val;

        for (int w = 0; w < mask.getWidth(); w++) {
            for (int h = 0; h < mask.getHeight(); h++) {
                if (mask.getPixel(w, h)== Color.WHITE) {
                    val=src.getPixel(w+x_min,h+y_min);
                    p=new Punt(w+offset_x,h+offset_y,val);
                    p.setRed(Color.red(val));
                    p.setGreen(Color.green(val));
                    p.setBlue(Color.blue(val));
                    calculaVpq2(p, w + x_min, h + y_min, w, h);
                    mat[w][h]= Nums;
                    punts.add(p);
                    Nums++;
                }
            }
        }
    }


    private void calculaVpq(Punt p,int x,int y){
        int qq;

        int totalVpq_R=0;
        int totalVpq_G=0;
        int totalVpq_B=0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if ((dx == 0) && (dy == 0)) continue;

                if ((dx == 0) || (dy == 0)) {

                    qq = Color.red(src.getPixel(x + dx, y + dy));   // gq red
                    totalVpq_R += Color.red(src.getPixel(x, y)) - qq;  // gp - gq

                    qq = Color.green(src.getPixel(x + dx, y + dy));   // gq green
                    totalVpq_G += Color.green(src.getPixel(x, y)) - qq;  // gp - gq

                    qq = Color.blue(src.getPixel(x + dx, y + dy));   // gq blue
                    totalVpq_B += Color.blue(src.getPixel(x, y)) - qq;  // gp - gq
                }
            }
        }
        p.setTotalVpq_R(totalVpq_R);
        p.setTotalVpq_G(totalVpq_G);
        p.setTotalVpq_B(totalVpq_B);
    }


    private void calculaVpq2(Punt p,int x,int y,int w,int h){
        int qq;
        int qq2;

        int totalVpq_R=0;
        int totalVpq_G=0;
        int totalVpq_B=0;

        int tmpR=0;
        int tmpG=0;
        int tmpB=0;

        int tmpR2=0;
        int tmpG2=0;
        int tmpB2=0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if ((dx == 0) && (dy == 0)) continue;

                if ((dx == 0) || (dy == 0)) {

                    qq = Color.red(src.getPixel(x + dx, y + dy));   // gq red
                    tmpR= Color.red(src.getPixel(x, y)) - qq;  // gp - gq

                    qq = Color.green(src.getPixel(x + dx, y + dy));   // gq green
                    tmpG = Color.green(src.getPixel(x, y)) - qq;  // gp - gq

                    qq = Color.blue(src.getPixel(x + dx, y + dy));   // gq blue
                    tmpB = Color.blue(src.getPixel(x, y)) - qq;  // gp - gq

                    //--------------------------------------------------------------
                    qq2 = Color.red(dest.getPixel(p.getX() + dx, p.getY() + dy));   // fq red
                    tmpR2 = Color.red(dest.getPixel(p.getX(), p.getY())) - qq2;  // fp - fq

                    qq2 = Color.green(dest.getPixel(p.getX() + dx, p.getY() + dy));   // fq green
                    tmpG2 = Color.green(dest.getPixel(p.getX(), p.getY())) - qq2;  // gp - gq

                    qq2 = Color.blue(dest.getPixel(p.getX()+ dx, p.getY() + dy));   // fq blue
                    tmpB2 = Color.blue(dest.getPixel(p.getX(), p.getY())) - qq2;  // fp - fq

                    if(Math.abs(tmpR)>Math.abs(tmpR2)){
                        totalVpq_R+=tmpR;
                    }else {
                        totalVpq_R += tmpR2;
                    }
                    if(Math.abs(tmpG)>Math.abs(tmpG2)){
                        totalVpq_G+=tmpG;
                    }else {
                        totalVpq_G += tmpG2;
                    }

                    if(Math.abs(tmpB)>Math.abs(tmpB2)){
                        totalVpq_B+=tmpB;
                    }else {
                        totalVpq_B += tmpB2;
                    }
                }
            }
        }
        p.setTotalVpq_R(totalVpq_R);
        p.setTotalVpq_G(totalVpq_G);
        p.setTotalVpq_B(totalVpq_B);
    }

    public void solve() {

        float np;  // nombre de veins que pertanyen a omega del pixel actual
        int x, y; // coordenades x,y del punt actual
        int ind;
        float vores_R,vores_G, vores_B;
        float valors_R,valors_G,valors_B;
        float newVal_R,newVal_G,newVal_B; //el nou valor del color obtingut
        Punt p;
        float temp;
        float totalR;
        float totalG;
        float totalB;

        double pj2= Math.pow( Math.cos(Math.PI/(1+punts.size())),2.0);
        this.W= (float) (2/(1+Math.sqrt(1-pj2) ));


        for (int i = 0; i < MAX_ITERS; i++) {
            totalR=0;
            totalG=0;
            totalB=0;
            for (int j=0;j<punts.size();j++) {
                p = punts.get(j);
                np = 0;
                vores_R = 0;
                vores_G = 0;
                vores_B = 0;

                valors_R = 0;
                valors_G = 0;
                valors_B = 0;


                x = p.getX();
                y = p.getY();

                for (int dx = -1; dx <= 1; dx++)
                    for (int dy = -1; dy <= 1; dy++) {
                        if ((dx == 0) && (dy == 0)) continue;

                        if ((dx == 0) || (dy == 0)) {
                            if (mask.getPixel(x + dx-offset_x, y + dy-offset_y) == Color.WHITE) {
                                np++;
                                ind = mat[x + dx-offset_x][y + dy-offset_y];
                                valors_R += punts.get(ind).getRed();
                                valors_G += punts.get(ind).getGreen();
                                valors_B += punts.get(ind).getBlue();

                            } else {
                                vores_R += Color.red(dest.getPixel(x + dx, y + dy));
                                vores_G += Color.green(dest.getPixel(x + dx, y + dy));
                                vores_B += Color.blue(dest.getPixel(x + dx, y + dy));
                            }
                        }
                    }

                np = 4;

                newVal_R = (1 - W) * p.getRed() + W * (valors_R + vores_R + p.getTotalVpq_R()) / np;
                newVal_G = (1 - W) * p.getGreen() + W * (valors_G + vores_G + p.getTotalVpq_G()) / np;
                newVal_B = (1 - W) * p.getBlue() + W * (valors_B + vores_B + p.getTotalVpq_B()) / np;

                totalR+=newVal_R;
                totalG+=newVal_G;
                totalB+=newVal_B;

                p.setRed(newVal_R);
                p.setGreen(newVal_G);
                p.setBlue(newVal_B);

            }
            //System.out.println("mitjana R "+totalR/(float)punts.size());
            //System.out.println("mitjana G "+totalG/(float)punts.size());
            //System.out.println("mitjana B "+totalB/(float)punts.size());
        }
    }



    public Bitmap principal(){
        if (mixed)
            maskLoop2();
        else
            maskLoop();

        lStartTime = System.nanoTime();
        solve();
        copiaResultats();

        lEndTime = System.nanoTime();
        elapsedTime = (lEndTime - lStartTime)/1000000;
        return this.sortida;
    }


    private void copiaResultats() {
        int R,G,B;
        int A=0xFF;
        for(Punt p:punts){
            if(Math.round(p.getRed())>255)
                R=255;
            else if(Math.round(p.getRed())<0)
                R=0;
            else
                R=Math.round(p.getRed());

            if(Math.round(p.getGreen())>255)
                G=255;
            else if(Math.round(p.getGreen())<0)
                G=0;
            else
                G=Math.round(p.getGreen());


            if(Math.round(p.getBlue())>255)
                B=0xFF;
            else if(Math.round(p.getBlue())<0)
                B=0;
            else
                B=Math.round(p.getBlue());

            sortida.setPixel(p.getX(), p.getY(), Color.argb(A, R, G, B));
        }
        src=null;
        dest=null;
        mask=null;

    }

    public int getNumPunts() {
        return punts.size();
    }


    public int getIters() {
        return this.MAX_ITERS;
    }
}

package com.gachi.tucusports;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {
    private ImageButton logo;
    private Typeface robotoFont;
    private final int delay=2;
    int progress=0;
    private ProgressBar barradeprogreso;
    Handler h = new Handler();
    TextView cargando, titulo;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        robotoFont = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        barradeprogreso=(ProgressBar)findViewById(R.id.progressbar);
        cargando = (TextView) findViewById(R.id.txtCargando);
        cargando.setTypeface(robotoFont);
        titulo = (TextView) findViewById(R.id.txtTituloSplash);
        titulo.setTypeface(robotoFont);
        Animation overshoot = AnimationUtils.loadAnimation(this, R.anim.overshoot);
        overshoot.reset();
        cargando.startAnimation(overshoot);
        titulo.startAnimation(overshoot);
        barradeprogreso.startAnimation(overshoot);
        logo = (ImageButton) findViewById(R.id.imgLogo);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        bounce.reset();
        logo.startAnimation(bounce);


        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<5; i++)
                {
                progress+=20;
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                        barradeprogreso.setProgress(progress);
                            if(progress==barradeprogreso.getMax()){
                                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){

                    }
                }

            }
        }).start();
    }

}
package com.gachi.tucusports;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class Descripcion extends MapsActivity {
    private Typeface robotoFont, robotoFontBold;

    String info[];
    TextView labelContacto, labelDireccion, labelDeportes;
    EditText numero, direccion, deportes;
    ImageButton btnCopiar1, btnCopiar2, btnLlamar, btnMensaje;

    protected void onCreate(Bundle savedInstanceState) {
        robotoFont = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        robotoFontBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-BoldItalic.ttf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.descripcion);
        labelContacto = (TextView) findViewById(R.id.labelContacto);
        labelDireccion = (TextView) findViewById(R.id.labelDireccion);
        labelDeportes = (TextView) findViewById(R.id.labelDeportes);
        numero = (EditText) findViewById(R.id.txtTel);
        direccion = (EditText) findViewById(R.id.txtDireccion);
        deportes = (EditText) findViewById(R.id.txtDeportes);
        btnCopiar1 = (ImageButton) findViewById(R.id.btnCopiarContacto);
        btnCopiar2 = (ImageButton) findViewById(R.id.btnCopiarDireccion);
        btnLlamar = (ImageButton) findViewById(R.id.btnLlamar);
        btnMensaje = (ImageButton) findViewById(R.id.btnMensaje);
        numero.setTypeface(robotoFont);
        direccion.setTypeface(robotoFont);
        deportes.setTypeface(robotoFont);
        labelContacto.setTypeface(robotoFontBold);
        labelDireccion.setTypeface(robotoFontBold);
        labelDeportes.setTypeface(robotoFontBold);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
       obtenerInfo();
    }
    public void obtenerInfo(){

        Intent men = getIntent();
        info = men.getStringArrayExtra(ACT_INFO);
        direccion.setText(info[1]);
        numero.setText(info[0]);
        deportes.setText(info[2]);
        Animation transparencia =  AnimationUtils.loadAnimation(this, R.anim.transparencia);
        transparencia.reset();
        labelContacto.startAnimation(transparencia);
        labelDireccion.startAnimation(transparencia);
        labelDeportes.startAnimation(transparencia);
        numero.startAnimation(transparencia);
        direccion.startAnimation(transparencia);
        deportes.startAnimation(transparencia);
        btnMensaje.startAnimation(transparencia);
        btnLlamar.startAnimation(transparencia);
        btnCopiar1.startAnimation(transparencia);
        btnCopiar2.startAnimation(transparencia);
    }
    public void llamarContacto(View view){
        String num = numero.getText().toString();
        if(num.matches("[0-9]*")) {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + numero.getText()));
            startActivity(i);
        }
        else {
            CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
            t.show("Opción solo es válida si hay disponible un número telefónico");
        }
    }

    public void mensajeContacto (View view){
        String num = numero.getText().toString();
        if(num.matches("[0-9]*")) {
            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+numero.getText()));
            startActivity(i);
        }
        else {
            CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
            t.show("Opción solo es válida si hay disponible un número telefónico");
        }
    }


public void copiarNumero(View view){
    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    clipboard.setText(numero.getText());
    CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
    t.show("Numero copiado al portapapeles");
}
    public void copiarDireccion(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(direccion.getText());
        CustomToast t = new CustomToast(this, Toast.LENGTH_SHORT);
        t.show("Dirección copiada al portapapeles");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
//para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }
}

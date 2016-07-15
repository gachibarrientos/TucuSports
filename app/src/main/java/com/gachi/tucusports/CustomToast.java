package com.gachi.tucusports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gachi.tucusports.R;

public class CustomToast extends Toast {
    private Context context;
    public CustomToast(Context cont, int duration ) {
        super(cont);
        context = cont;
        this.setDuration(duration);
    }
    public void show(CharSequence text) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = (View) li.inflate(R.layout.customtoast, null);
        TextView tv = (TextView) vi.findViewById(R.id.txtToast);
        this.setView(vi);
        tv.setText(text);
        super.show();
    }
}

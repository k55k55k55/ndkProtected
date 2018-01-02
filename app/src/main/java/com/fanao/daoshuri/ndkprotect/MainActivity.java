package com.fanao.daoshuri.ndkprotect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    static {
        System.loadLibrary( "daoshuriwidget" );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Intent intent = new Intent(this, LittleWidgetService.class);
        startService(intent);

        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText(getString());
    }

    public native String getString();


}

package com.example.hi.socket_chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void server(View v)
    {
        Intent i=new Intent(MainActivity.this,Server1.class);
        startActivity(i);
        finish();
    }

    public void client(View v)
    {
        Intent i=new Intent(MainActivity.this,Client1.class);
        startActivity(i);
        finish();
    }
}

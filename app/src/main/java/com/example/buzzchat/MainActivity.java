package com.example.buzzchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Home(View view) {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
    }

    public void IgScreen(View view) {
        Intent i = new Intent(this,IgScreenActivity.class);
        startActivity(i);
    }

    public void Profile(View view) {
        Intent i = new Intent(this,ProfileActivity.class);
        startActivity(i);
    }

    public void chatScreen(View view) {
        Intent i = new Intent(this,chatScreen.class);
        startActivity(i);
    }
}
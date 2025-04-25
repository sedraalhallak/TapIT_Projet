package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import android.os.Handler;
import android.widget.TextView;
import android.widget.VideoView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ImageView backgroundGif = findViewById(R.id.backgroundgif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.giphyf)
                .placeholder(R.drawable.photo)
                .into(backgroundGif);
        ImageView gifImage = findViewById(R.id.gifImage);
        Glide.with(this).load(R.drawable.start).into(gifImage);



        FrameLayout splashLayout = findViewById(R.id.splash_layout);


        splashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }



    private void startGame() {

        Intent intent = new Intent(SplashActivity.this,SongSelectionActivity.class);
        startActivity(intent);
        finish();
    }
}

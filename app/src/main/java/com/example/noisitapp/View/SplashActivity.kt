package com.example.noisitapp.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.noisitapp.R

class SplashActivity :AppCompatActivity(){
    private val SPLASH_TIME_OUT:Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        Handler(Looper.getMainLooper()).postDelayed({

                //Your Code
                startActivity(Intent(this@SplashActivity,MenuNavigationActivity::class.java))
                finish()

        }, SPLASH_TIME_OUT)
    }
}
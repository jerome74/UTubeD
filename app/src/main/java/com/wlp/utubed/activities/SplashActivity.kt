package com.wlp.utubed.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.wlp.utubed.R
import com.wlp.utubed.UTubeDActivity
import com.wlp.utubed.controller.handler.SplashHandler
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {


    var animation : Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashHandler = SplashHandler(this,this)
        val splashMessage = Message()



        splashMessage.what = 0


        if (savedInstanceState == null) {
            flyIn()
        }



        splashHandler.postDelayed({endSplash()}, 5000)

    }

    fun flyIn() {

        animation = AnimationUtils.loadAnimation(this,
            R.anim.app_name_animation);
        tv_utubed.startAnimation(animation);

        //animation = AnimationUtils.loadAnimation(this, R.anim.pro_animation);
        //tv_downloader.startAnimation(animation);
    }


    fun endSplash() {

        animation = AnimationUtils.loadAnimation(this,
            R.anim.app_name_animation_back);
        tv_utubed.startAnimation(animation);

        //animation = AnimationUtils.loadAnimation(this,
        //    R.anim.pro_animation_back);
        //tv_downloader.startAnimation(animation);

        animation?.setAnimationListener(object : Animation.AnimationListener
        {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                var intent = Intent(getApplicationContext(),
                    UTubeDActivity::class.java);
                startActivity(intent);
                finish();
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        }
        )

    }
}
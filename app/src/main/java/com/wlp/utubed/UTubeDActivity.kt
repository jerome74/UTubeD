package com.wlp.utubed

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.wlp.utubed.activities.LoginActivity
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.UserObj
import com.wlp.utubed.util.BROADCAST_LOGIN
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class UTubeDActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar
            , R.string.navigation_drawer_open
            , R.string.navigation_drawer_close)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener { true }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            loginReceiver, IntentFilter(
                BROADCAST_LOGIN
            )
        )

        notifyEventuallyLogin()
    }

    val loginReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            if (AuthObj.isLoggIn && emailTxt != null && userImg != null)
            {
                emailTxt.text = UserObj.userProfile?.nickname
                var nav_view = findViewById<NavigationView>(R.id.nav_view)
                nav_view.menu.findItem(R.id.loginItem).setTitle("Log-out");
                nav_view.menu.findItem(R.id.loginItem).setIcon(android.R.drawable.ic_partial_secure)

                val identifier = resources.getIdentifier(UserObj.userProfile?.avatarname,"mipmap",packageName)
                val bitmap_1 = BitmapFactory.decodeResource(resources, identifier)
                val rounded_1 = RoundedBitmapDrawableFactory.create(resources,bitmap_1);

                rounded_1.cornerRadius = 15f;
                rounded_1.isCircular = true;

                userImg.setImageDrawable(rounded_1);
            }
        }
    }


    fun notifyEventuallyLogin() {

        if (AuthObj.isLoggIn)
        {

            var nav_view = findViewById<NavigationView>(R.id.nav_view)

            nav_view.menu.findItem(R.id.loginItem).setTitle("Log-out");
            nav_view.menu.findItem(R.id.loginItem).setIcon(android.R.drawable.ic_partial_secure)


            var email = nav_view.getHeaderView(0).findViewById<TextView>(R.id.emailTxt)
            var image = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.userImg)

            email.text = UserObj.userProfile?.nickname

            val identifier = resources.getIdentifier(UserObj.userProfile?.avatarname,"mipmap",packageName)
            val bitmap_1 = BitmapFactory.decodeResource(resources, identifier)
            val rounded_1 = RoundedBitmapDrawableFactory.create(resources,bitmap_1);

            rounded_1.cornerRadius = 15f;
            rounded_1.isCircular = true;

            image.setImageDrawable(rounded_1);
        }
    }

    fun onSinginBntClicked(view : MenuItem) {

        if (!AuthObj.isLoggIn) {

            Toast.makeText(this, "eseguire il Login!", Toast.LENGTH_SHORT).show()

        }else {

        }
    }

    fun onFinderBntClicked(view : MenuItem) {

        if (!AuthObj.isLoggIn) {

            Toast.makeText(this, "eseguire il Login!", Toast.LENGTH_SHORT).show()

        }else {

        }
    }

    fun onLoginBntClicked(view : MenuItem) {

        if (!AuthObj.isLoggIn) {
            val intentLogin: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        } else {


            var nav_view = findViewById<NavigationView>(R.id.nav_view)
            nav_view.menu.findItem(R.id.loginItem).setTitle("log-In");
            nav_view.menu.findItem(R.id.loginItem).setIcon(android.R.drawable.ic_secure)
            emailTxt.text = ""
            userImg.setImageResource(R.mipmap.profiledefault)

            UserObj.reset()
            AuthObj.reset()
        }
    }
}
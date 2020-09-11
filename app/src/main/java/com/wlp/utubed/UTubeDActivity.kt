package com.wlp.utubed

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.wlp.utubed.activities.LoginActivity
import com.wlp.utubed.adapters.ListVideosAdapter
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.UserObj
import com.wlp.utubed.models.FindVideos
import com.wlp.utubed.models.Video
import com.wlp.utubed.services.VideoService
import com.wlp.utubed.util.BROADCAST_DOWNLOAD_VIDEO
import com.wlp.utubed.util.BROADCAST_FIND_VIDEOS
import com.wlp.utubed.util.BROADCAST_LOGIN
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

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

        LocalBroadcastManager.getInstance(this).registerReceiver(
            listVideosReceiver, IntentFilter(
                BROADCAST_FIND_VIDEOS
            )
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            videoDownloadReceiver, IntentFilter(
                BROADCAST_DOWNLOAD_VIDEO
            )
        )

        notifyEventuallyLogin()

        manageSpinner(true, View.INVISIBLE)
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

    fun onIconSearchBtnClick(view: View) {

        if (!AuthObj.isLoggIn) {
            Toast.makeText(this, "eseguire il Login!", Toast.LENGTH_SHORT).show()
        }
        else
        {
            if(nameFindTxt.text.toString().trim().isNullOrEmpty())
            {
                Toast.makeText(this, "inserire il nome del video da ricercare!", Toast.LENGTH_SHORT).show()
                return
            }

            val findVideos = FindVideos(nameFindTxt.text.toString())

            manageSpinner(false, View.VISIBLE)
            hideKeyboard()
            callFindVideos(findVideos)
        }


    }

    fun callFindVideos(findVideos : FindVideos) {


            VideoService.findVideos(this
                , findVideos,
                { esito: Boolean, messaggio: String ->
                    if (esito) {
                        try {

                            if (messaggio.length > 0 && !messaggio.equals("[]")) {

                                videos.clear()

                                val responseJson: JSONArray = JSONArray(messaggio)

                                var i = 0
                                var channel = ""

                                val firstLength = responseJson.getJSONObject(0).getString("length")


                                if(firstLength.isNullOrBlank()) {
                                    channel = responseJson.getJSONObject(0).getString("title")
                                    i++
                                }

                                while (i < responseJson.length()) {

                                    val id = responseJson.getJSONObject(i).getString("id")
                                    val title = Html.fromHtml(responseJson.getJSONObject(i).getString("title")).toString()
                                    val length = responseJson.getJSONObject(i).getString("length")

                                    val thumbnails: JSONObject = JSONObject(responseJson.getJSONObject(i).getString("thumbnails"))
                                    val defaultJson: JSONObject = JSONObject(thumbnails.getString("default"))

                                    val video: Video = Video(id
                                                            , title
                                                            , defaultJson.getString("url")
                                                            ,channel
                                                            ,length)

                                    videos.add(video)

                                    i++
                                }
                            }

                            manageSpinner(true, View.INVISIBLE)
                            LocalBroadcastManager.getInstance(this)
                                .sendBroadcast(Intent(BROADCAST_FIND_VIDEOS))

                            Toast.makeText(this, "videos found successfully", Toast.LENGTH_SHORT)
                                .show()

                        } catch (e: Exception) {
                            Toast.makeText(this, "error : ${e.message}", Toast.LENGTH_SHORT).show()
                            manageSpinner(true, View.INVISIBLE)
                        }

                    } else {
                        Toast.makeText(
                            this,
                            "videos found by error : $messaggio",
                            Toast.LENGTH_SHORT
                        ).show()
                        manageSpinner(true, View.INVISIBLE)
                    }
                })
    }

    lateinit var listVideosAdapter: ListVideosAdapter
    val videos = mutableListOf<Video>()

    val listVideosReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            listVideosAdapter = ListVideosAdapter(this@UTubeDActivity, videos)
            videoListView.adapter = listVideosAdapter

            val linearLayout: GridLayoutManager = GridLayoutManager(context, 1)
            videoListView.layoutManager = linearLayout
        }
    }

    val videoDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            try
            {

                val decode = Base64.decode(intent!!.getStringExtra("payloadBase64"), Base64.DEFAULT)
                val downloadDir =  this@UTubeDActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

                File("$downloadDir/${intent!!.getStringExtra("title")}.mp3").writeBytes(decode)

                manageOnlySpinner(View.INVISIBLE)

                Toast.makeText(this@UTubeDActivity, "download successfully in $downloadDir", Toast.LENGTH_SHORT).show()

             }catch(e : Exception){
            Toast.makeText(this@UTubeDActivity, "error : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun manageSpinner(enable: Boolean, visibility : Int)
    {
        pbFindVideos.visibility = visibility;

        nameFindTxt.isEnabled    = enable
        icon_search_btn.isEnabled   = enable
        mic_search_btn.isEnabled = enable
    }

    fun manageOnlySpinner(visibility : Int)
    {
        pbFindVideos.visibility = visibility;
    }


    fun hideKeyboard(){
        val inputManager : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText)
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
    }
}
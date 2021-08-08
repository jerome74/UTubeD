package com.wlp.utubed

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.text.Html
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.wlp.utubed.activities.LoginActivity
import com.wlp.utubed.activities.SigninActivity
import com.wlp.utubed.adapters.FolderListAdapter
import com.wlp.utubed.adapters.ListVideosAdapter
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.UserObj
import com.wlp.utubed.models.DownloadVideo
import com.wlp.utubed.models.FindVideos
import com.wlp.utubed.models.Video
import com.wlp.utubed.services.VideoService
import com.wlp.utubed.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.util.*

class UTubeDActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

     var language : String? = null
     var fileTypeDownloadVideo: String? = null

    val SPEECH_REQUEST_CODE = 1234
    val REQUEST_DIRECTORY = 1235

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        folder_in_tv.text = getString(R.string.download_in, getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath)


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
            logoutReceiver, IntentFilter(
                BROADCAST_LOGOUT
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

        LocalBroadcastManager.getInstance(this).registerReceiver(
            listFolderReceiver, IntentFilter(
                BROADCAST_FOLDER
            )
        )

        notifyEventuallyLogin()

        manageSpinner(true, View.INVISIBLE)
        manageSpinnerH(View.INVISIBLE,  true)

        tv_choice_en.setOnClickListener(click)
        tv_choice_it.setOnClickListener(click)
        tv_choice_mp3.setOnClickListener(click)
        tv_choice_video.setOnClickListener(click)
    }

    val click = View.OnClickListener {

        when((it as TextView).text)
        {
            getString(R.string.english) -> {
                it.background = resources.getDrawable(R.drawable.background_color_button)
                tv_choice_it.background = resources.getDrawable(R.drawable.background_black_tras)

                language = LANGUAGE_EN
            }

            getString(R.string.italiano) -> {
                it.background = resources.getDrawable(R.drawable.background_color_button)
                tv_choice_en.background = resources.getDrawable(R.drawable.background_black_tras)

                language = LANGUAGE_IT
            }
            getString(R.string.mp3) -> {
                it.background = resources.getDrawable(R.drawable.background_color_button)
                tv_choice_video.background = resources.getDrawable(R.drawable.background_black_tras)

                fileTypeDownloadVideo = FILE_TYPE_MP3
            }
            getString(R.string.video) -> {
                it.background = resources.getDrawable(R.drawable.background_color_button)
                tv_choice_mp3.background = resources.getDrawable(R.drawable.background_black_tras)

                fileTypeDownloadVideo = FILE_TYPE_VIDEO
            }
        }
    }

    val loginReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            if (AuthObj.isLoggIn && emailTxt != null && userImg != null)
            {
                emailTxt.text = UserObj.userProfile?.nickname
                var nav_view = findViewById<NavigationView>(R.id.nav_view)
                nav_view.menu.findItem(R.id.loginItem).setTitle(getString(R.string.logout));
                nav_view.menu.findItem(R.id.loginItem).setIcon(android.R.drawable.ic_partial_secure)

                val identifier = resources.getIdentifier(UserObj.userProfile?.avatarname,"mipmap",packageName)
                val bitmap_1 = BitmapFactory.decodeResource(resources, identifier)
                val rounded_1 = RoundedBitmapDrawableFactory.create(resources,bitmap_1);

                rounded_1.cornerRadius = 15f;
                rounded_1.isCircular = true;

                userImg.setImageDrawable(rounded_1);

                drawer_layout.closeDrawer(GravityCompat.START, false);
            }
        }
    }

    override fun onBackPressed()  {
        if(drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    fun notifyEventuallyLogin() {

        if (AuthObj.isLoggIn)
        {

            var nav_view = findViewById<NavigationView>(R.id.nav_view)

            nav_view.menu.findItem(R.id.loginItem).setTitle(getString(R.string.logout));
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
            ToastCustom.show(this@UTubeDActivity,getString(R.string.do_login))

        }else {

        }
    }

    fun onFinderBntClicked(view : MenuItem) {

        if (!AuthObj.isLoggIn) {

            ToastCustom.show(this@UTubeDActivity,getString(R.string.do_login))

        }else {

        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun onClickMusicFolderBtn(view : View)
    {
        if (!AuthObj.isLoggIn) {
            ToastCustom.show(this@UTubeDActivity,getString(R.string.do_login))
        }
        else{

            val permissionCheck_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission((this@UTubeDActivity),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

            val permissionCheck_READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission((this@UTubeDActivity),
                Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck_WRITE_EXTERNAL_STORAGE==PackageManager.PERMISSION_GRANTED
                && permissionCheck_READ_EXTERNAL_STORAGE==PackageManager.PERMISSION_GRANTED){
                //this means permission is granted and you can do read and write
            }else{
                ActivityCompat.requestPermissions((this@UTubeDActivity),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_WRITE_PERMISSION);
            }

            val builder = AlertDialog.Builder(this@UTubeDActivity)
            dialogView = layoutInflater.inflate(R.layout.folder_dialog_destination, null)

            val path_dir_tv = dialogView.findViewById<TextView>(R.id.path_dir_tv)
            val up_dir_btn = dialogView.findViewById<ImageView>(R.id.up_dir_btn)
            val dir_exit_tv = dialogView.findViewById<TextView>(R.id.dir_exit_tv)
            val dir_ok_tv = dialogView.findViewById<TextView>(R.id.dir_ok_tv)
            folderLV = dialogView.findViewById<RecyclerView>(R.id.folderListView)

            dir_exit_tv.setOnClickListener {
                alertDialog.dismiss()
            }

            dir_ok_tv.setOnClickListener {
                folder_in_tv.text = getString(R.string.download_in, path_dir_tv.text)
                alertDialog.dismiss()
            }

            up_dir_btn.setOnClickListener {
                folders.clear()
                val originalPath = path_dir_tv.text
                var path : String? = null

                if(originalPath.equals("/storage/emulated/0")) path = "/storage/emulated/0"
                else path = originalPath.substring(0, originalPath.lastIndexOf("/"))

                val listDirectories = File(path).getListDirectories()
                if(listDirectories.size == 0) listDirectories.add(getString(R.string.double_dot))
                folders.addAll(listDirectories)
                path_dir_tv.text = path
                folderLV.adapter!!.notifyDataSetChanged()
            }


            val path = folder_in_tv.text.substring((folder_in_tv.text.indexOf(":") + 1),folder_in_tv.text.length)

            path_dir_tv.text = path

            folders = File(path).getListDirectories()

            if(folders.size == 0) folders.add(getString(R.string.double_dot))

            folderListAdapter = FolderListAdapter(this@UTubeDActivity, folders)
            folderLV.adapter = folderListAdapter

            val linearLayout: GridLayoutManager = GridLayoutManager(this@UTubeDActivity, 1)
            folderLV.layoutManager = linearLayout

            alertDialog = builder.setView(dialogView).create()
            alertDialog.show()
        }
    }

    lateinit var alertDialog: AlertDialog
    lateinit var dialogView: View
    lateinit var folderListAdapter: FolderListAdapter
    lateinit var folderLV : RecyclerView

    var folders = mutableListOf<String>()


    val listFolderReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            folders.clear()

            val path_dir_tv = dialogView.findViewById<TextView>(R.id.path_dir_tv)
            val fold = intent!!.getStringExtra(INTENT_FOLDER)
            val path = "${path_dir_tv.text}/${fold}"

            val listDirectories = File(path).getListDirectories()

            if(listDirectories.size == 0) listDirectories.add(getString(R.string.double_dot))
            folders.addAll(listDirectories)
            path_dir_tv.text = path
            folderLV.adapter!!.notifyDataSetChanged()
        }
    }

    fun File.getListDirectories() : MutableList<String> {

        var folders = mutableListOf<String>()
        try {
            var list = File(absolutePath).list()

            Arrays.sort(list)

            if (list == null) {
                folders.add(getText(R.string.no_access).toString())
                return folders
            }

            list?.forEach {
                //if (File("$absolutePath/$it").isDirectory) {
                    folders.add("$absolutePath/$it")
                //}
            }
        }catch (e : Exception){
            Log.e(UTubeDActivity::class.java.name, e.message , e)
        }
        return folders
    }

    val logoutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            var nav_view = findViewById<NavigationView>(R.id.nav_view)
            nav_view.menu.findItem(R.id.loginItem).setTitle(getString(R.string.login));
            nav_view.menu.findItem(R.id.loginItem).setIcon(android.R.drawable.ic_secure)
            emailTxt.text = ""
            userImg.setImageResource(R.mipmap.profiledefault)

            UserObj.reset()
            AuthObj.reset()
        }
    }

    fun onLoginBntClicked(view : MenuItem) {

        if (!AuthObj.isLoggIn) {
            val intentLogin: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        } else {


            var nav_view = findViewById<NavigationView>(R.id.nav_view)
            nav_view.menu.findItem(R.id.loginItem).setTitle(getString(R.string.login));
            nav_view.menu.findItem(R.id.loginItem).setIcon(android.R.drawable.ic_secure)
            emailTxt.text = ""
            userImg.setImageResource(R.mipmap.profiledefault)

            UserObj.reset()
            AuthObj.reset()
        }
    }

    fun onSigninBntClicked(view : MenuItem) {

        if (!AuthObj.isLoggIn) {
            val intentSignin: Intent = Intent(this, SigninActivity::class.java)
            startActivity(intentSignin)
        } else {


            var nav_view = findViewById<NavigationView>(R.id.nav_view)
            nav_view.menu.findItem(R.id.loginItem).setTitle(getString(R.string.login));
            nav_view.menu.findItem(R.id.loginItem).setIcon(android.R.drawable.ic_secure)
            emailTxt.text = ""
            userImg.setImageResource(R.mipmap.profiledefault)

            UserObj.reset()
            AuthObj.reset()
        }
    }

    fun onMicSearchBtnClick(view: View) {

        if (!AuthObj.isLoggIn) {
            ToastCustom.show(this@UTubeDActivity,getString(R.string.do_login))
        }
        if (language.isNullOrEmpty()) {
            ToastCustom.show(this@UTubeDActivity,getString(R.string.select_language))
        }
        else
        {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2);
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode)
        {
            SPEECH_REQUEST_CODE ->  if (resultCode == RESULT_OK)
            {
                val listWords = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                if(listWords.size == 0 )
                    ToastCustom.show(this@UTubeDActivity,getString(R.string.no_vaild_word))
                else
                {
                    val message = listWords.get(0)
                    nameFindTxt.text = SpannableStringBuilder(message)
                }
            }
        }
    }

    fun onIconSearchBtnClick(view: View) {

        if (!AuthObj.isLoggIn) {
            ToastCustom.show(this@UTubeDActivity,getString(R.string.do_login))
        }
        else
        {
            if(nameFindTxt.text.toString().trim().isNullOrEmpty())
            {
                ToastCustom.show(this@UTubeDActivity,getString(R.string.insert_title_video))
                return
            }
            if(fileTypeDownloadVideo.isNullOrEmpty())
            {
                ToastCustom.show(this@UTubeDActivity,getString(R.string.select_type_dwonload))
                return
            }

            AuthObj.fileTypeDownloadVideo = fileTypeDownloadVideo!!


            val findVideos = FindVideos(nameFindTxt.text.toString())

            manageSpinner(false, View.VISIBLE)
            hideKeyboard()
            callFindVideos(findVideos)
        }


    }

    fun callFindVideos(findVideos : FindVideos) {

        runOnUiThread {
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


                                if (firstLength.isNullOrBlank()) {
                                    channel = responseJson.getJSONObject(0).getString("title")
                                    i++
                                }

                                while (i < responseJson.length()) {

                                    val id = responseJson.getJSONObject(i).getString("id")
                                    val title = Html.fromHtml(
                                        responseJson.getJSONObject(i).getString("title")
                                    ).toString()
                                    val length = responseJson.getJSONObject(i).getString("length")

                                    val thumbnails: JSONObject = JSONObject(
                                        responseJson.getJSONObject(i).getString("thumbnails")
                                    )
                                    val defaultJson: JSONObject =
                                        JSONObject(thumbnails.getString("default"))

                                    val video: Video = Video(
                                        id
                                        , title
                                        , defaultJson.getString("url")
                                        , channel
                                        , length
                                    )

                                    videos.add(video)

                                    i++
                                }
                            }

                            manageSpinner(true, View.INVISIBLE)
                            LocalBroadcastManager.getInstance(this)
                                .sendBroadcast(Intent(BROADCAST_FIND_VIDEOS))

                            ToastCustom.show(this,getString(R.string.videos_found_successfully))

                        } catch (e: Exception) {
                            ToastCustom.show(this,getString(R.string.find_file_error, e.message))
                            manageSpinner(true, View.INVISIBLE)
                        }

                    } else {
                        ToastCustom.show(this,getString(R.string.find_file_failed, messaggio))
                        manageSpinner(true, View.INVISIBLE)
                    }
                })
        }
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
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onReceive(context: Context?, intent: Intent?)
        {
            try
            {
                val id = intent!!.getStringExtra(INTENT_ID_VIDEO)
                val title = intent!!.getStringExtra(INTENT_TITLE)

                runOnUiThread {

                    manageSpinnerH(View.VISIBLE, false)

                    tv_event_download.text = getString(R.string.status_7)

                    val downloadVideo = DownloadVideo(id)

                    VideoService.downloadVideo(this@UTubeDActivity
                        , downloadVideo,
                        { esito: Boolean, messaggio: ByteArray ->
                            if (esito) {
                                if (messaggio.size > 0 ) {
                                    try
                                    {
                                        ToastCustom.show(this@UTubeDActivity,getString(R.string.status_2))

                                        val path = folder_in_tv.text.substring((folder_in_tv.text.indexOf(":") + 1),folder_in_tv.text.length)

                                        Converter.mp4ConvertTo(messaggio, title, this@UTubeDActivity, path)

                                        ToastCustom.show(this@UTubeDActivity,getString(R.string.download_success, path ))

                                        tv_event_download.text.drop(tv_event_download.text.length)
                                        tv_event_download.text= getString(R.string.status_ini)

                                        manageSpinnerH(View.INVISIBLE, true)
                                    }
                                    catch (e: Exception) {
                                        ToastCustom.show(this@UTubeDActivity,getString(R.string.download_error,e.message))
                                        manageSpinnerH(View.INVISIBLE, true)
                                    }
                                }
                            } else {
                                ToastCustom.show(this@UTubeDActivity,getString(R.string.download_error,messaggio))
                                manageSpinnerH(View.INVISIBLE, true)
                            }
                        })
                }
             }
            catch(e : Exception){
                ToastCustom.show(this@UTubeDActivity,getString(R.string.download_error,e.message))
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

    fun manageSpinnerH(visibility : Int, enable: Boolean)
    {
        pb_download_video.visibility = visibility;
        videoListView.isEnabled    = enable
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

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_PERMISSION-> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
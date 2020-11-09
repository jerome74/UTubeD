package com.wlp.ibolletta.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import com.theartofdev.edmodo.cropper.CropImageView
import com.wlp.ibolletta.R
import com.wlp.ibolletta.domain.AuthObj
import com.wlp.ibolletta.util.EXTRA_FILE
import kotlinx.android.synthetic.main.activity_keep_and_flip_imge.*

class KeepAndFlipImgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keep_and_flip_imge)

        val uriImg = intent.getStringExtra(EXTRA_FILE)
        setSupportActionBar(toolbar_keep);
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_toolbar)

        cropImageView.setImageUriAsync(Uri.parse(uriImg));


    }

    companion object {
        var croppedImage : Bitmap? = null
    }

    fun onNextStepClicked(view : View){

        cropImageView.setOnCropImageCompleteListener(CropImageView.OnCropImageCompleteListener { view, result ->
            croppedImage = result.bitmap
            val localIntent = Intent(this@KeepAndFlipImgeActivity, ScannerizationActivity::class.java)
            startActivity(localIntent);
            })

        cropImageView.getCroppedImageAsync();

    }
}
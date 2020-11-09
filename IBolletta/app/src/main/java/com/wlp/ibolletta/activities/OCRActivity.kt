package com.wlp.ibolletta.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import com.wlp.ibolletta.R
import com.wlp.ibolletta.util.TaskOCR
import com.wlp.ibolletta.util.TessOCR
import com.wlp.ibolletta.util.ToastCustom
import kotlinx.android.synthetic.main.activity_ocr.*

class OCRActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)


    lateinit var tessOCR : TessOCR
    lateinit var result : String

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        setSupportActionBar(toolbar_ocr);
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_toolbar)

        tessOCR = TessOCR(this@OCRActivity)

        val taskOCR = TaskOCR({
            try {
                runOnUiThread({ ToastCustom.show(this@OCRActivity, getString(R.string.elab_ocr))})
                elaborate()
            }catch (e : Exception){
                Log.e("ERROR", e.localizedMessage , e)
            }

        },{
            try {
                runOnUiThread({ ToastCustom.show(this@OCRActivity, getString(R.string.end_elab_ocr))})
                postElaborate()
            }catch (e : Exception){
                Log.e("ERROR", e.localizedMessage , e)
            }

        }, "taskOCR")

        taskOCR.start()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun elaborate() {
        result = tessOCR.getOCRResult(ScannerizationActivity.bitScannerization!!);
    }

    fun postElaborate()
    {
        //if (result.length > 0)  ocr_result_tv.text = result
        //else
        ocr_result_tv.text = getString(R.string.no_elab_ocr)
    }

}
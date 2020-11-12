package com.wlp.ibolletta.activities

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.wlp.ibolletta.R
import com.wlp.ibolletta.util.ParseORCBolletta
import com.wlp.ibolletta.util.TaskOCR
import com.wlp.ibolletta.util.TessOCR
import kotlinx.android.synthetic.main.activity_ocr.*

class OCRActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)


    lateinit var tessOCR : TessOCR
    lateinit var result : String

    var edit =  Editable.Factory.getInstance()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setSupportActionBar(toolbar_ocr);
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_toolbar)


        tessOCR = TessOCR(this@OCRActivity)

        val taskOCR = TaskOCR({
            try {
                runOnUiThread({
                    custom_ocr_tv.visibility = View.VISIBLE
                    custom_ocr_tv.text = " ${getString(R.string.elab_ocr)} "
                })
                ocr()
                barcode()
            }catch (e : Exception){
                Log.e("ERROR", e.localizedMessage , e)
            }

        },{
            try {
                runOnUiThread({
                    custom_ocr_tv.text = ""
                    custom_ocr_tv.visibility = View.INVISIBLE

                    postOcr()
                })

            }catch (e : Exception){
                Log.e("ERROR", e.localizedMessage , e)
            }

        }, "taskOCR")

        taskOCR.start()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun ocr() {
        result = tessOCR.getOCRResult(ScannerizationActivity.bitScannerization!!);
    }

    fun barcode(){
        val detector = BarcodeDetector.Builder(getApplicationContext())
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.CODABAR)
        .build();

        val frame = Frame.Builder().setBitmap(KeepAndFlipImgeActivity.croppedImage!!).build();

        val barcodes = detector.detect(frame)

        for(i in 0 until  barcodes.size()){
            val barcode = barcodes[i]
            barcode.rawValue
        }
    }

    fun postOcr()
    {
       if(result.length > 0){

           ocr_result_tv.text = result

           val parseORCBolletta = ParseORCBolletta(result)
           parseORCBolletta.parseNumberBarCode()

           import_value.text = edit.newEditable(parseORCBolletta.importo)
           number_value.text = edit.newEditable(parseORCBolletta.numero)
           cc_value.text = edit.newEditable(parseORCBolletta.cc)

           scad_value.text = edit.newEditable(parseORCBolletta.getScadenza())
       }
        else{
           ocr_result_tv.text = getString(R.string.no_elab_ocr)
       }
    }
}
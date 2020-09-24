package com.wlp.utubed.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wlp.utubed.R
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.CompleteObj
import com.wlp.utubed.model.User
import com.wlp.utubed.model.UserObj
import com.wlp.utubed.model.UserProfile
import com.wlp.utubed.service.EmailService
import com.wlp.utubed.service.LoginService
import com.wlp.utubed.util.BROADCAST_LOGIN
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var biometricPrompt : BiometricPrompt
    lateinit var promptInfo : BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        manageSpinner(true, View.INVISIBLE)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val user : User = User(getString(R.string.client_id),getString(R.string.secret_id))
                    //if(cb_notifica_wa.isChecked) AuthObj.notify = "true"
                    //else
                    AuthObj.notify = "false"
                    manageSpinner(false, View.VISIBLE)
                    hideKeyboard()
                    callLoginUser(user)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

         promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()




    }


    fun onLoginBtnClicked(view : View){

        val user : User = User(nameLoginTxt.text.toString(),passLoginTxt.text.toString())
        //if(cb_notifica_wa.isChecked) AuthObj.notify = "true"
        //else
        AuthObj.notify = "false"
        manageSpinner(false, View.VISIBLE)
        hideKeyboard()
        callLoginUser(user)

    }

    fun onBioBtnClicked(view : View){
        biometricPrompt.authenticate(promptInfo)
    }


    /**
     *
     */

    fun callLoginUser(user : User)
    {
        runOnUiThread {
            LoginService.loginUser(this
                , user,
                { esito: Boolean, messaggio: String ->
                    if (esito) {
                        try {
                            AuthObj.isLoggIn = true
                            Toast.makeText(this, "user Login successfully", Toast.LENGTH_SHORT)
                                .show()
                            callFindProfileByEmail(user)

                        } catch (e: Exception) {
                            Toast.makeText(this, "error : ${e.message}", Toast.LENGTH_SHORT).show()
                            manageSpinner(true, View.INVISIBLE)
                        }

                    } else {
                        Toast.makeText(this, "login error : $messaggio", Toast.LENGTH_SHORT).show()
                        manageSpinner(true, View.INVISIBLE)
                    }

                    CompleteObj.esitoLoginUser = esito
                })
        }
    }

    fun callFindProfileByEmail(user : User)
    {
        EmailService.findProfileByEmail(this
            ,user,
            {
                    esito: Boolean, messaggio: String ->
                if(esito)
                {
                    try{
                        val responseJson : JSONObject = JSONObject(messaggio)

                        val userProfile : UserProfile = UserProfile( responseJson.getString("id")
                            , responseJson.getString("nickname")
                            , responseJson.getString("email")
                            , responseJson.getString("avatarname")
                            , responseJson.getString("avatarcolor"))
                        UserObj.userProfile = userProfile
                        manageSpinner(true, View.INVISIBLE)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(BROADCAST_LOGIN))
                        finish()

                        Toast.makeText(this, "profile found successfully", Toast.LENGTH_SHORT).show()

                    }catch(e : Exception){
                        Toast.makeText(this, "error : ${e.message}", Toast.LENGTH_SHORT).show()
                        manageSpinner(true, View.INVISIBLE)
                    }

                }
                else
                {
                    Toast.makeText(this, "profile found error : $messaggio", Toast.LENGTH_SHORT).show()
                    manageSpinner(true, View.INVISIBLE)
                    finish()
                }

                CompleteObj.esitoLoginUser = esito
            })

    }

    fun manageSpinner(enable: Boolean, visibility : Int)
    {
        SigninProgBar.visibility = visibility;

        nameLoginTxt.isEnabled    = enable
        passLoginTxt.isEnabled   = enable
        LoginBtn.isEnabled = enable
        BioBtn.isEnabled = enable
    }


    fun hideKeyboard(){
        val inputManager : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText)
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
    }
}

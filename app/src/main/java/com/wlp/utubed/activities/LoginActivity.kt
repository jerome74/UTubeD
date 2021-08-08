package com.wlp.utubed.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
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
import com.wlp.utubed.util.ToastCustom
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
                    ToastCustom.show(this@LoginActivity,getString(R.string.auth_error, errString))
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val user : User = User(getString(R.string.client_id),getString(R.string.secret_id))

                    AuthObj.notify = getString(R.string._false_)
                    manageSpinner(false, View.VISIBLE)
                    hideKeyboard()
                    callLoginUser(user)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    ToastCustom.show(this@LoginActivity,getString(R.string.auth_failed))
                }
            })

         promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.bio_title))
            .setSubtitle(getString(R.string.bio_subtitle))
            .setNegativeButtonText(getString(R.string.bio_neg_btn))
            .build()




    }


    fun onLoginBtnClicked(view : View){

        val user : User = User(nameLoginTxt.text.toString(),passLoginTxt.text.toString())
        //if(cb_notifica_wa.isChecked) AuthObj.notify = "true"
        //else
        AuthObj.notify = getString(R.string._false_)
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
                            ToastCustom.show(this@LoginActivity,getString(R.string.login_success))
                            callFindProfileByEmail(user)

                        } catch (e: Exception) {
                            ToastCustom.show(this@LoginActivity,getString(R.string.login_error,e.message))
                            manageSpinner(true, View.INVISIBLE)
                        }

                    } else {
                        ToastCustom.show(this@LoginActivity,getString(R.string.login_failed, messaggio))
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

                        val userProfile : UserProfile = UserProfile(responseJson.getString("nickname")
                            , responseJson.getString("email")
                            , responseJson.getString("avatarname")
                            , responseJson.getString("avatarcolor"))
                        UserObj.userProfile = userProfile
                        manageSpinner(true, View.INVISIBLE)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(BROADCAST_LOGIN))

                        ToastCustom.show(this@LoginActivity,getString(R.string.profile_found_success))
                        finish()

                    }catch(e : Exception){
                        ToastCustom.show(this@LoginActivity,getString(R.string.profile_found_error,e.message))
                        manageSpinner(true, View.INVISIBLE)
                    }

                }
                else
                {
                    ToastCustom.show(this@LoginActivity,getString(R.string.profile_found_failed,messaggio))
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

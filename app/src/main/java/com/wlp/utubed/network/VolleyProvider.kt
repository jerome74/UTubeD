package com.wlp.utubed.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import com.wlp.utubed.R
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class VolleyProvider private constructor(context: Context) {

    private var queue: RequestQueue? = null

    companion object {

        private var instance: VolleyProvider? = null

        fun getInstance(context: Context): VolleyProvider? {
            if (instance == null) instance =
                VolleyProvider(context)
            return instance
        }

    }

    init {
        try {
            val instream = context.applicationContext.resources.openRawResource(R.raw.keystore);

            val trustStore = KeyStore.getInstance("JKS");

            val algorithm = TrustManagerFactory.getDefaultAlgorithm();

            val tmf = TrustManagerFactory.getInstance(algorithm);

            tmf.init(trustStore);

            val sslcontext = SSLContext.getInstance("TLS");

            sslcontext.init(null, tmf.getTrustManagers(), null);

            val sslFactory = sslcontext.socketFactory

            val hurlStack = HurlStack(null, sslFactory);

            queue = Volley.newRequestQueue(context, hurlStack);
        } catch (e: Exception) {
            Log.e("UTUBED_LOG", "VolleyProvider error", e)
        }
    }

    fun getQueue(): RequestQueue {
        return this.queue!!;
    }

    fun <T> addRequest(req: Request<T>): Request<T> {
        return getQueue().add(req);
    }

    fun <T> addRequest(req: Request<T>, tag: String): Request<T> {
        req.setTag(tag);
        return getQueue().add(req);
    }
}

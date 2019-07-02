package com.mdev.revit

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login_ffcs.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.Exception

class LoginFFCSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fade = Fade()
        fade.duration = 125
        window.enterTransition = fade
        window.exitTransition = fade

        setContentView(R.layout.activity_login_ffcs)

        FirebaseFirestore.getInstance().collection("app_data").document("vitcc")
            .get()
            .addOnSuccessListener {doc->
                val loginUrl = doc["ffcs_login"].toString()
                val homeUrl = doc["ffcs_home"].toString()
                val ttUrl = doc["time_table"].toString()
                val urls = listOf(loginUrl,homeUrl,ttUrl)

                web_view.webViewClient = MyWebViewClient(this,
                    progress_circular_ffcs,
                    progress_circular_ffcs_text,
                    error_screen,
                    back_button_ffcs_login,
                    urls)
                web_view.settings.javaScriptEnabled = true
                web_view.loadUrl(urls[0])
            }
            .addOnFailureListener {
                toast("Aw, Snap!")
                Log.d(TAG,"error: $it")
            }

        back_button_ffcs_login.setOnClickListener {
            finish()
        }
    }

    class MyWebViewClient(
        val context: Context,
        private val progress_bar: ProgressBar,
        private val progress_text: TextView,
        private val error_screen: TextView,
        private val back_button: ImageButton,
        private val urls: List<String>) : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (view?.url == urls[1]){
                view.visibility = View.GONE
                progress_text.visibility = View.VISIBLE
                back_button.visibility = View.GONE
            }
            progress_bar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {

            when {
                view?.url == urls[1] -> view.loadUrl(urls[2])
                view?.url == urls[2] -> {
                    storeSchedule(view,context)
                }
                else -> progress_bar.visibility = View.GONE
            }
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            view?.visibility = View.GONE
            error_screen.visibility = View.VISIBLE
        }

        private fun storeSchedule(view: WebView?, context: Context){


            val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preference_schedule), Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            view?.evaluateJavascript("(function(){ return document.course_regular.children[1].rows.length })();") {
                try {
                    val rowNum = it.toInt() - 3
                    for (i in 1..rowNum) {
                        view.evaluateJavascript("(function(){ return document.course_regular.children[1].rows[$i].children[0].innerText })();") { pos ->
                            var cc = 1
                            var ct = 2
                            var cty = 3
                            var sl = 7
                            var v = 8
                            var fac = 9
                            if (pos.length <= 2) {
                                cc = 3
                                ct = 4
                                cty = 5
                                sl = 9
                                v = 10
                                fac = 11
                            }
                            view.evaluateJavascript("(function(){ return document.course_regular.children[1].rows[$i].children[$sl].innerText })();") { slot ->
                                if (slot.removeSurrounding("\"").toLowerCase() != "nil") {
                                    val slots = slot.removeSurrounding("\"").split("+")
                                    val details = mutableSetOf<String>()

                                    view.evaluateJavascript("(function(){ return document.course_regular.children[1].rows[$i].children[$cc].innerText })();") { cCode ->
                                        details.add("a: ${cCode.removeSurrounding("\"")}")

                                        view.evaluateJavascript("(function(){ return document.course_regular.children[1].rows[$i].children[$ct].innerText })();") { cTitle ->
                                            details.add("b: ${cTitle.removeSurrounding("\"")}")

                                            view.evaluateJavascript("(function(){ return document.course_regular.children[1].rows[$i].children[$cty].innerText })();") { cType ->
                                                details.add("c: ${cType.removeSurrounding("\"")}")

                                                view.evaluateJavascript("(function(){ return document.course_regular.children[1].rows[$i].children[$v].innerText })();") { venue ->
                                                    details.add("d: ${venue.removeSurrounding("\"")}")

                                                    view.evaluateJavascript("(function(){ return document.course_regular.children[1].rows[$i].children[$fac].innerText })();") { faculty ->
                                                        details.add("e: ${faculty.removeSurrounding("\"")}")

                                                        for (j in 0 until slots.size) {
                                                            with(sharedPref.edit()) {
                                                                putStringSet(slots[j], details)
                                                                apply()
                                                            }
                                                        }


                                                    }
                                                }
                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }

                    with(sharedPref.edit()) {
                        putBoolean("tt_set", true)
                        apply()
                    }
                }
                catch (e: Exception){
                    Log.d(TAG, "Error Occurred: $e")
                    context.longToast("Error Occurred").show()
                    with(sharedPref.edit()) {
                        putBoolean("tt_set", false)
                        apply()
                    }
                }
            }
            context.startActivity<MainActivity>()
        }

    }

    companion object{
        const val TAG = "LoginFFCSActivity"
    }
}
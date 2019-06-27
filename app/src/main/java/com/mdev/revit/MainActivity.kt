package com.mdev.revit

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ad_banner.*
import kotlinx.android.synthetic.main.app_bar.*
import org.jetbrains.anko.longToast

class MainActivity : AppCompatActivity(),NavigationHost {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(window){
            val fade = Fade()
            fade.duration=125
            exitTransition = fade
            enterTransition = fade
        }

        val model = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        model.searchByName("")

        MobileAds.initialize(this,getString(R.string.ad_mob_id))
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setupNavMenu()
        anonymousLogin()
        val adRequest = AdRequest.Builder().addTestDevice("49981328ED379EE87B9840F843BFAA61").build()
        adView.loadAd(adRequest)

        if (savedInstanceState != null) {
            return
        }

        val sharedPref = getSharedPreferences(getString(R.string.shared_preference_schedule), Context.MODE_PRIVATE)
        val flagTTSet = sharedPref.getBoolean("tt_set", false)
        if (flagTTSet) {
            navigateTo(TimeTableFragment(), true)
        } else {
            navigateTo(LoginFFCSFragment(), false)
        }


        search_fac.setOnClickListener {
            val intent = Intent(this@MainActivity,SearchActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent,options.toBundle())
        }


    }


    private fun anonymousLogin() {
        if (auth.currentUser==null){
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful){
                        longToast("Something went wrong. Restart App")
                    }
                }
        }
    }


    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_frame, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
        Log.d(TAG,"navigating to $fragment")
    }


    override fun onBackPressed() {
        if (nav_menu_drawer.isDrawerOpen(GravityCompat.START))
            nav_menu_drawer.closeDrawer(GravityCompat.START)
        else
            this.moveTaskToBack(true)
    }

    private fun setupNavMenu(){
        nav_menu_drawer.fitsSystemWindows = true

        nav_button.setOnClickListener {
            if (!(nav_menu_drawer.isDrawerOpen(GravityCompat.START))) {
                nav_menu_drawer.openDrawer(GravityCompat.START)
            }
        }

        if (!auth.currentUser?.email.isNullOrEmpty()){
            nav_menu.menu.setGroupVisible(R.id.nav_menu_logged_in,true)
            nav_menu.menu.setGroupVisible(R.id.nav_menu_logged_out,false)
        }
        else{
            nav_menu.menu.setGroupVisible(R.id.nav_menu_logged_in,false)
            nav_menu.menu.setGroupVisible(R.id.nav_menu_logged_out,true)
        }

        nav_menu.setNavigationItemSelectedListener {item->
            val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            when(item.itemId){
                R.id.nav_reset_tt -> {
                    val intent = Intent(this@MainActivity,LoginFFCSActivity::class.java)
                    startActivity(intent,bundle)
                }
                R.id.nav_reset_tt1 -> {
                    val intent = Intent(this@MainActivity,LoginFFCSActivity::class.java)
                    startActivity(intent,bundle)
                }
                R.id.nav_login -> {
                    val intent = Intent(this@MainActivity,LoginActivity::class.java)
                    startActivity(intent,bundle)
                }
                R.id.nav_logout ->{
                    auth.signOut()
                    Log.d("GoogleSignIn", "Signed Out")
                    finish()
                }
                R.id.nav_feedback -> {
                    val intent = Intent(this@MainActivity,FeedbackActivity::class.java)
                    startActivityForResult(intent, feedbackRequestCode,bundle)
                }
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val db= FirebaseFirestore.getInstance()
        if (requestCode == feedbackRequestCode && resultCode == Activity.RESULT_OK)
        data?.let{
            val feedback = it.getStringExtra(FeedbackActivity.FEEDBACK_ID)
            val fb = HashMap<String,Any>()
            fb["feedback"] = feedback
            fb["timestamp"] = FieldValue.serverTimestamp()
            fb["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
            db.collection("feedback")
                .add(fb)
                .addOnSuccessListener {
                    longToast("Thanks")
                }
                .addOnFailureListener { exception ->
                    longToast("Failed to submit feedback")
                    Log.d(TAG, "error: $exception")
                }
        }
    }

    companion object{
        const val TAG = "MainActivityLog"
        const val feedbackRequestCode = 1
    }
}

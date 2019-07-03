package com.mdev.revit.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mdev.revit.R
import com.mdev.revit.utils.ReviewOnlyAdapter
import kotlinx.android.synthetic.main.activity_fac_details.*
import kotlinx.android.synthetic.main.no_reviews.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class FacDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fac_details)

        setupWindowAnimation()

        val adRequest = AdRequest.Builder().build()
        adView_fac_details.loadAd(adRequest)

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true

        if (!isConnected){
            network_status_fac_details.visibility = View.VISIBLE
        }

        val auth = FirebaseAuth.getInstance()

        faculty = intent.getStringExtra("faculty")
        school = intent.getStringExtra("school")
        val designation = intent.getStringExtra("designation")
        val db = FirebaseFirestore.getInstance()

        fac_name.text = faculty
        fac_school.text = school
        fac_designation.text = designation


        db.collection("user_review")
            .whereEqualTo("faculty", faculty)
            .whereEqualTo("school", school)
            .whereEqualTo("status",9001)
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                loading_circular.visibility = View.GONE
                if (documents.size() == 0){
                    no_reviews_page.visibility = View.VISIBLE
                    reviewCardList.visibility = View.GONE
                }
                else {
                    reviewCardList.visibility = View.VISIBLE
                    reviewCardList.layoutManager = LinearLayoutManager(this)
                    reviewCardList.adapter = ReviewOnlyAdapter(documents)
                }
            }
            .addOnFailureListener{exception ->
                toast("Aw, Snap!")
                Log.d(TAG,"Can't get documents. Error: $exception")
            }

        back_button_fac_details.setOnClickListener { finish() }

        add_review_button.setOnClickListener{
            addReview(isConnected,auth)
        }

        add_review_button_large.setOnClickListener {
            addReview(isConnected,auth)
        }
    }

    private fun setupWindowAnimation() {
        val fade = Fade()
        fade.duration = 125
        window.enterTransition = fade
        window.exitTransition = fade
    }

    private fun addReview(isConnected: Boolean,auth: FirebaseAuth){
        if (isConnected) {
            if (!auth.currentUser?.email.isNullOrEmpty()) {
                val intent = Intent(this@FacDetailsActivity, AddReviewActivity::class.java)
                val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                intent.putExtra("faculty", faculty)
                intent.putExtra("school", school)
                startActivityForResult(intent,
                    addReviewRequestCode,bundle)
            } else {
                longToast("You must be Signed In to Add Reviews!")
                startActivity<LoginActivity>()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == addReviewRequestCode && resultCode == Activity.RESULT_OK)
            data?.let { it ->
                val review = it.getStringExtra(AddReviewActivity.REVIEW_ID)
                val userReview = HashMap<String,Any>()
                userReview["faculty"] = faculty
                userReview["school"] = school
                userReview["review"] = review
                userReview["timestamp"] = FieldValue.serverTimestamp()
                userReview["authorID"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                userReview["status"] = 401
                FirebaseFirestore.getInstance().collection("user_review")
                    .add(userReview)
                    .addOnSuccessListener {
                        Snackbar.make(fac_details_parent,"Review Submitted.",Snackbar.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {exception->
                        Snackbar.make(fac_details_parent,"Aw, Snap! Review not submitted.",Snackbar.LENGTH_SHORT).show()
                        Log.d(TAG,exception.toString())
                    }
            }

    }

    companion object{
        const val TAG = "FacDetailsActivityLog"
        const val addReviewRequestCode = 1
        lateinit var faculty: String
        lateinit var school: String
    }
}

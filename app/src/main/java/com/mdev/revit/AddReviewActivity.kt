package com.mdev.revit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_review.*

class AddReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fade = Fade()
        fade.duration = 125
        window.enterTransition = fade
        window.exitTransition = fade

        setContentView(R.layout.activity_add_review)


        fac_name_review.text = intent.getStringExtra("faculty")
        fac_school_review.text = intent.getStringExtra("school")

        submit_review.setOnClickListener {

            val replyIntent = Intent()
            if (TextUtils.isEmpty(review.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val review = review.text.toString()
                replyIntent.putExtra(REVIEW_ID, review)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        back_button_add_review.setOnClickListener { finish() }
    }
    companion object{
        const val REVIEW_ID = "com.ugdev.review.REPLY"
    }
}

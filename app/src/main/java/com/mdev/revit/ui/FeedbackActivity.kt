package com.mdev.revit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.mdev.revit.R
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)


        submit_feedback.setOnClickListener {

            val replyIntent = Intent()
            if (TextUtils.isEmpty(feedback.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val review = feedback.text.toString()
                replyIntent.putExtra(FEEDBACK_ID, review)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
        back_button_feedback.setOnClickListener { finish() }
    }

    companion object{
        const val TAG = "FeedbackActivityLog"
        const val FEEDBACK_ID = "com.ugdev.feedack"
    }
}

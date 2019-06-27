package com.mdev.revit

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_login_ffcs.view.*

class LoginFFCSFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login_ffcs,container,false)

        view.login_ffcs_button.setOnClickListener {
            val intent = Intent(activity,LoginFFCSActivity::class.java)
            val bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            activity?.startActivity(intent,bundle)
        }

        return view
    }
}
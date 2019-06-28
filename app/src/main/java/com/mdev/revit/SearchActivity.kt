package com.mdev.revit

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.actions.SearchIntents
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = getColor(R.color.statusBar)
            }
            val slide = Slide()
            slide.duration = 250
            slide.slideEdge = Gravity.START
            exitTransition = slide
            val fade = Fade()
            fade.duration = 250
            enterTransition = fade
        }
        setContentView(R.layout.activity_search)
        val model = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        search_results.layoutManager = LinearLayoutManager(this)
        search_results.setHasFixedSize(true)
        val adapter = SearchResultsAdapter()
        adapter.setActivityForTransition(this)
        search_results.adapter = adapter

        val intent = intent
        if(SearchIntents.ACTION_SEARCH == intent.action){
            val query = intent.getStringExtra(SearchManager.QUERY)
            model.searchByName(query)
        }
        else{
            model.searchByName("")
        }

        model.searchResults.observe(this, androidx.lifecycle.Observer {
            it?.let {result->
                if (result.isEmpty()){
                    no_result.visibility = View.VISIBLE
                }
                else{
                    no_result.visibility = View.GONE
                }
                adapter.setResults(result)
            }
        })

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true

        if (!isConnected){
            network_status_search.visibility = View.VISIBLE
        }


        if(search_box.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }



        search_box.addTextChangedListener(PerformSearch(model))


        back_button.setOnClickListener { finish() }
    }

    class PerformSearch(private val model: SearchViewModel): TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            model.searchByName(text)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //
        }
    }
    companion object{
        const val TAG = "SearchActivityLog"
    }

}

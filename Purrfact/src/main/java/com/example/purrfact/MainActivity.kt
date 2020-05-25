package com.purrfact

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private val model: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_with_loader)
        model.loadFactDataList()
        val dataFetchedObserver = Observer<String> { status ->
            if(status == "fetched"){
                navigateToCardActivity()
            }
        }

        model.dataFetched.observe(this, dataFetchedObserver)
    }

    private fun navigateToCardActivity() {
        val intent = Intent(this, CardsActivity::class.java).apply {
        }
        startActivity(intent)
    }

}

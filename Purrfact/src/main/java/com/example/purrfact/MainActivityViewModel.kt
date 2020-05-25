package com.purrfact

import android.app.Application
import androidx.lifecycle.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.purrfact.services.CatFactService
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception


class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    val dataFetched: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private val reqQueue: RequestQueue = Volley.newRequestQueue(getApplication());
    private val job = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)

    suspend fun loadCatPictures() {

        withContext(Dispatchers.IO) {
            try{

                for(i in 0 until 5){

                    val jsonObjectRequest = JsonArrayRequest(
                        Request.Method.GET, CatFactService.imageServiceUrl, null,
                        Response.Listener { response ->
                            try{
                                val obj: JSONObject = response.getJSONObject(0)
                                val url = obj.getString("url")
                                CatFactService.catPictureUrls.add(url)
                            }
                            catch (e: Exception){

                            }
                        },
                        null

                    )
                    reqQueue.add(jsonObjectRequest)
                    delay(200L)
                }
            }
            catch (e:Exception){

            }
        }

    }
    suspend fun loadCatFacts(){
        withContext(Dispatchers.IO) {
            for(i in 0 until 5){

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, CatFactService.factUrl, null,
                    Response.Listener { response ->
                        try{
                            val fact: String = response.getString("fact")
                            CatFactService.catFacts.add(fact)
                            if(i==2){
                                dataFetched.setValue("fetched");
                            }
                        }
                        catch (e: Exception){

                        }
                    },
                    null
                )
                reqQueue.add(jsonObjectRequest)
                delay(200L)
            }

        }
    }
    fun loadFactDataList() {
        uiScope.launch {
                loadCatPictures()
                loadCatFacts()
        }


    }
}
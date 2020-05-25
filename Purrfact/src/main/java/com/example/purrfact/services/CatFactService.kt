package com.purrfact.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlinx.coroutines.*

class CatFactService() {

    companion object{
        val factUrl:String = "https://catfact.ninja/fact"
        val imageServiceUrl:String = "https://api.thecatapi.com/v1/images/search"
        private val prefetchSize:Int = 20
        val catPictureUrls:Queue<String> = LinkedList<String>()
        val catFacts:Queue<String> = LinkedList<String>()
        var lastImage: String = "https://cdn2.thecatapi.com/images/bfl.jpg"
        var lastFact: String = "Cats can jump 5 times their height"


        suspend fun fetchImages(context: Context?) {

            withContext(Dispatchers.IO) {
                // HTTP GET request code...
                    val reqQueue: RequestQueue = Volley.newRequestQueue(context)
                    for(i in 0 until prefetchSize){

                        val jsonObjectRequest = JsonArrayRequest(
                            Request.Method.GET, imageServiceUrl, null,
                            Response.Listener { response ->
                                try{
                                    val obj: JSONObject = response.getJSONObject(0)
                                    val url = obj.getString("url")
                                    catPictureUrls.add(url)
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
        suspend fun fetchFacts(context: Context?) {

           withContext(Dispatchers.IO) {
                    val reqQueue: RequestQueue = Volley.newRequestQueue(context)
                    for(i in 0 until prefetchSize){

                        val jsonObjectRequest = JsonObjectRequest(
                            Request.Method.GET, factUrl, null,
                            Response.Listener { response ->
                                try{
                                    val fact: String = response.getString("fact")
                                    catFacts.add(fact)
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

        fun getFactObj():Fact{
            return Fact(getCatFact(), getPictureUrl())
        }
        fun getPictureUrl(): String? {
            if(this.catPictureUrls.size == 0){
                return lastImage
            }
            return this.catPictureUrls.poll();

        }
        fun getCatFact(): String? {
            if(this.catFacts.size == 0){
                return lastFact
            }
            return this.catFacts.poll();

        }
    }
}
package com.sheraz.loadimagesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.sheraz.loadimagesapp.adapter.DisplayImagesAdapter
import com.sheraz.loadimagesapp.databinding.ActivityMainBinding
import com.sheraz.loadimagesapp.model.GetUnSplashApiResponse
import com.sheraz.loadimagesapp.remote.ServiceAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchImagesData()
    }


    private fun fetchImagesData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/") //
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ServiceAPI::class.java)
        val call = apiService.getAllPhotosData("iAMHZ0C4HE45oQ6BuP95RyPaHjVJ4bo01hvvpwzA9s0", 50)
        binding.progressBar.visibility = View.VISIBLE


        call.enqueue(object : Callback<ArrayList<GetUnSplashApiResponse>> {
            override fun onResponse(call: Call<ArrayList<GetUnSplashApiResponse>>, response: Response<ArrayList<GetUnSplashApiResponse>>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {

                    val data = response.body()
                    // Process the data here
                    initImagesDisplayAdapter(data ?: ArrayList())

                } else {

                    // Handle error
                    Log.e("ApiCallReceiver", "API call failed: ${response.code()}")

                }
            }

            override fun onFailure(call: Call<ArrayList<GetUnSplashApiResponse>>, t: Throwable) {

                //Handle failure
                binding.progressBar.visibility = View.GONE
                Log.e("ApiCallReceiver", "API call failed: ${t.message}")

            }
        })
    }


    fun initImagesDisplayAdapter(imageDataList : ArrayList<GetUnSplashApiResponse>){

        val adapter = DisplayImagesAdapter(imageDataList)
        binding.displayImagesList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.displayImagesList.adapter = adapter
    }
}
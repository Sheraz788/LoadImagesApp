package com.sheraz.loadimagesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.sheraz.loadimagesapp.adapter.DisplayImagesAdapter
import com.sheraz.loadimagesapp.cache.LocalCacheStorage
import com.sheraz.loadimagesapp.databinding.ActivityMainBinding
import com.sheraz.loadimagesapp.model.GetUnSplashApiResponse
import com.sheraz.loadimagesapp.remote.ServiceAPI
import com.sheraz.loadimagesapp.utils.hideKeyboard
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

        binding.btnGet.setOnClickListener {

            hideKeyboard()
            if (binding.txtImagesCount.text.isNotEmpty()){

                try {
                    val savedDataSize = LocalCacheStorage.getImagesDataSize(this@MainActivity)
                    if (savedDataSize == binding.txtImagesCount.text.toString().toInt()) {

                        initImagesDisplayAdapter(
                            LocalCacheStorage.getImagesData(this@MainActivity) ?: ArrayList()
                        )
                    } else {

                        fetchImagesData(binding.txtImagesCount.text.toString().toInt())

                    }
                }catch (e  : Exception){
                    Toast.makeText(this, "Fetching Failed Wrong Input", Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this, "Number of images cannot be empty", Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun fetchImagesData(imagesCount : Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/") //
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ServiceAPI::class.java)
        val call = apiService.getAllPhotosData("iAMHZ0C4HE45oQ6BuP95RyPaHjVJ4bo01hvvpwzA9s0", imagesCount)
        binding.progressBar.visibility = View.VISIBLE


        call.enqueue(object : Callback<ArrayList<GetUnSplashApiResponse>> {
            override fun onResponse(call: Call<ArrayList<GetUnSplashApiResponse>>, response: Response<ArrayList<GetUnSplashApiResponse>>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {

                    val data = response.body()
                    // Process the data here
                    initImagesDisplayAdapter(data ?: ArrayList())

                    //save fetched data locally
                    LocalCacheStorage.saveImagesData(this@MainActivity, LocalCacheStorage.ImagesData, Gson().toJson(data))

                } else {

                    // Handle error
                    Log.e("ApiCallReceiver", "API call failed: ${response.code()}")

                    Toast.makeText(this@MainActivity, "Images Fetching Response Error", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<GetUnSplashApiResponse>>, t: Throwable) {

                //Handle failure
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Images Fetching Failed", Toast.LENGTH_LONG).show()

            }
        })
    }


    fun initImagesDisplayAdapter(imageDataList : ArrayList<GetUnSplashApiResponse>){

        val adapter = DisplayImagesAdapter(this, imageDataList)
        binding.displayImagesList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.displayImagesList.adapter = adapter
    }
}
package com.sheraz.loadimagesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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

    var isLoading = false
    var isLastPage = false
    var pageNumber = 1
    var imageDataList : ArrayList<GetUnSplashApiResponse> = ArrayList()
    lateinit var adapter : DisplayImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.totalImages.text = "${getString(R.string.total_images)} = 0"
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

                        binding.progressBar.visibility = View.VISIBLE
                        isLoading = true
                        fetchImagesData(binding.txtImagesCount.text.toString().toInt(), pageNumber)

                    }
                }catch (e  : Exception){
                    Toast.makeText(this, getString(R.string.fetching_failed_wrong_input), Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this, getString(R.string.number_of_images_cannot_be_empty), Toast.LENGTH_LONG).show()
            }

        }



//        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        binding.displayImagesList.layoutManager = staggeredGridLayoutManager

        // Pagination
        binding.displayImagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {


            private var visibleThreshold = 10 // Adjust according to your needs
            private var previousTotalItemCount = 0
            private var loading = true

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val totalItemCount = (binding.displayImagesList.layoutManager as StaggeredGridLayoutManager).itemCount
                val lastVisibleItemPosition = getLastVisibleItem((binding.displayImagesList.layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null))


                if (totalItemCount < previousTotalItemCount) {
                    // Data has been reset (e.g., refreshed), reset the state
                    previousTotalItemCount = totalItemCount
                    if (totalItemCount == 0) {
                        loading = true
                    }
                }

                if (loading && totalItemCount > previousTotalItemCount) {
                    // Data has been loaded
                    loading = false
                    previousTotalItemCount = totalItemCount
                }

                if (!loading && !isLastPage && totalItemCount - visibleThreshold <= lastVisibleItemPosition + 1) {
                    // End has been reached, load more items
                    // Load more items
                    if (binding.txtImagesCount.text.isNotEmpty()){

                        try {

                            binding.progressBarPagination.visibility = View.VISIBLE
                            isLoading = true
                            fetchImagesData(binding.txtImagesCount.text.toString().toInt(), pageNumber++)

                        }catch (e  : Exception){
                            Toast.makeText(this@MainActivity, getString(R.string.fetching_failed_wrong_input), Toast.LENGTH_LONG).show()
                        }

                    }else{
                        Toast.makeText(this@MainActivity, getString(R.string.number_of_images_cannot_be_empty), Toast.LENGTH_LONG).show()
                    }
                }




            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


            }


            private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
                var maxSize = 0
                for (i in lastVisibleItemPositions.indices) {
                    if (i == 0 || lastVisibleItemPositions[i] > maxSize) {
                        maxSize = lastVisibleItemPositions[i]
                    }
                }
                return maxSize
            }
        })
    }



    private fun fetchImagesData(imagesCount : Int, pageNumber : Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/") //
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ServiceAPI::class.java)
        val call = apiService.getAllPhotosData("iAMHZ0C4HE45oQ6BuP95RyPaHjVJ4bo01hvvpwzA9s0", imagesCount, pageNumber)



        call.enqueue(object : Callback<ArrayList<GetUnSplashApiResponse>> {
            override fun onResponse(call: Call<ArrayList<GetUnSplashApiResponse>>, response: Response<ArrayList<GetUnSplashApiResponse>>) {
                if (binding.progressBar.visibility == View.VISIBLE){
                    binding.progressBar.visibility = View.GONE
                    isLoading = false
                }else{
                    binding.progressBarPagination.visibility = View.GONE
                    isLoading = false
                }


                if (response.isSuccessful) {

                    val data = response.body()
                    // Process the data here
                    if (data?.isNotEmpty() == true){
                        if (::adapter.isInitialized){
                            adapter.addItems(data)
                        }else{

                            initImagesDisplayAdapter(data)
                        }


                        LocalCacheStorage.getImagesData(this@MainActivity)?.let {

                            if (it.isNotEmpty()){
                                val savedImagesList = LocalCacheStorage.getImagesData(this@MainActivity)
                                savedImagesList?.addAll(data)
                                LocalCacheStorage.saveImagesData(this@MainActivity, LocalCacheStorage.ImagesData, Gson().toJson(savedImagesList))
                                Log.i("MainActivityLogs", "Local saved data size ${savedImagesList?.size}")


                                binding.totalImages.text = "${getString(R.string.total_images)} = ${savedImagesList?.size}"
                            }

                        } ?: kotlin.run {
                            LocalCacheStorage.saveImagesData(this@MainActivity, LocalCacheStorage.ImagesData, Gson().toJson(data))
                            Log.i("MainActivityLogs", "First time local saved data size ${data.size}")

                            binding.totalImages.text = "${getString(R.string.total_images)} = ${data.size}"
                        }


                    }else{
                        isLastPage = true
                        Toast.makeText(this@MainActivity, getString(R.string.end_of_page_no_more_images), Toast.LENGTH_LONG).show()
                    }


                } else {

                    // Handle error
                    Log.e("ApiCallReceiver", "API call failed: ${response.code()}")

                    Toast.makeText(this@MainActivity, getString(R.string.images_fetching_response_error), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<GetUnSplashApiResponse>>, t: Throwable) {

                //Handle failure
                if (binding.progressBar.visibility == View.VISIBLE){
                    binding.progressBar.visibility = View.GONE
                    isLoading = false
                }else{
                    binding.progressBarPagination.visibility = View.GONE
                    isLoading = false
                }
                Toast.makeText(this@MainActivity, getString(R.string.images_fetching_failed), Toast.LENGTH_LONG).show()

            }
        })
    }


    fun initImagesDisplayAdapter(imageDataList : ArrayList<GetUnSplashApiResponse>){

        adapter = DisplayImagesAdapter(this, imageDataList)
        binding.displayImagesList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.displayImagesList.adapter = adapter
    }
}
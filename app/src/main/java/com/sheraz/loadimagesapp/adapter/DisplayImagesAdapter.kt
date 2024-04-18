package com.sheraz.loadimagesapp.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sheraz.loadimagesapp.MainActivity
import com.sheraz.loadimagesapp.R
import com.sheraz.loadimagesapp.databinding.DisplayImagesAdapterLayoutItemBinding
import com.sheraz.loadimagesapp.model.GetUnSplashApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DisplayImagesAdapter(var context : MainActivity, var imageDataList : ArrayList<GetUnSplashApiResponse>) : RecyclerView.Adapter<DisplayImagesAdapter.DisplayImagesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DisplayImagesAdapter.DisplayImagesViewHolder {

        val binding = DisplayImagesAdapterLayoutItemBinding.inflate(LayoutInflater.from(parent.context))

        return DisplayImagesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DisplayImagesAdapter.DisplayImagesViewHolder,
        position: Int
    ) {

        val imageData = imageDataList[position]
        if (position == 0){
            imageData.urls?.thumb = ""
        }else if (position == 4){
            imageData.urls?.thumb = ""
        }else if (position == 8){
            imageData.urls?.thumb = ""
        }else if (position == 15){
            imageData.urls?.thumb = ""
        }else if (position == 20){
            imageData.urls?.thumb = ""
        }else if (position == 25){
            imageData.urls?.thumb = ""
        }
        holder.onBind(imageData, context)

    }


    override fun getItemCount(): Int {
        return imageDataList.size
    }


    inner class DisplayImagesViewHolder(var binding : DisplayImagesAdapterLayoutItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun onBind(imageData : GetUnSplashApiResponse, context: MainActivity){

            GlobalScope.launch(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
              withContext(Dispatchers.IO) {

                   val bitmap = downloadBitmapFromUrl(imageData.urls?.thumb ?: "")

                  withContext(Dispatchers.Main){
                      binding.progressBar.visibility = View.GONE
                      if (bitmap != null){
                          binding.displayImg.setImageBitmap(bitmap)
                      }else{
                          binding.displayImg.setImageResource(R.drawable.dummy_image)
                      }
                  }
                }

            }
        }
    }


    private fun downloadBitmapFromUrl(imageUrl: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            bitmap = BitmapFactory.decodeStream(input)

            return bitmap

        } catch (e: Exception) {
            e.printStackTrace()

            return null

        }
    }



}
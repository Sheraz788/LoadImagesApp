package com.sheraz.loadimagesapp.cache

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sheraz.loadimagesapp.model.GetUnSplashApiResponse

object LocalCacheStorage {


    private const val appPreferencesName = "LoadImagesApp"
    const val ImagesData = "ImagesData"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(appPreferencesName, Context.MODE_PRIVATE)
    }


    fun saveImagesData(context: Context, key: String, value : String){
        val editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.commit()

    }


    fun getImagesData(context: Context, key:String):String?{
        return sharedPreferences.getString(key,null)
    }

    fun getImagesData(context : Activity) : ArrayList<GetUnSplashApiResponse>?{

        val userDataJson = context.let { getImagesData(context = it, ImagesData) }

        userDataJson?.let {
            val type = object : TypeToken<ArrayList<GetUnSplashApiResponse>>() {}.type
            val userData: ArrayList<GetUnSplashApiResponse> = Gson().fromJson(userDataJson, type)
            return userData
        } ?: run{
            return null
        }
    }


    fun getImagesDataSize(context : Activity) : Int{

        val userDataJson = context.let { getImagesData(context = it, ImagesData) }

        userDataJson?.let {
            val type = object : TypeToken<ArrayList<GetUnSplashApiResponse>>() {}.type
            val userData: ArrayList<GetUnSplashApiResponse> = Gson().fromJson(userDataJson, type)
            return userData.size
        } ?: run{
            return 0
        }
    }

}
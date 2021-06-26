package com.example.myapplication.nadutkin

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.myapplication.nadutkin.adapter.Video
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class DownloadApplication : Application() {
    companion object {
        lateinit var instance: DownloadApplication
            private set
        const val PickFromGallery = 228
        var fold: Boolean = false
        var side: Boolean = false
        var withoutBlocking: Boolean = true
    }

    val uploads = mutableListOf<Video>()
    private lateinit var call: Call
    private lateinit var pauseLink: String
    private lateinit var pauseVideoPath: String
    private lateinit var pauseVideoName: String

    private var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(160, TimeUnit.SECONDS)
        .readTimeout(160, TimeUnit.SECONDS).build()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getPath(context: Context, uri: Uri?): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri!!, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        if (result == null) {
            Toast.makeText(this, "Incorrect link", Toast.LENGTH_SHORT).show()
            result = "Not found"
        }
        return result
    }


    fun pause() {
        call.cancel()
    }

    fun resume() {
        handleSuccess(pauseLink, pauseVideoPath, pauseVideoName)
    }

    fun handleSuccess(link: String, videoPath: String, videoName: String) {
        Log.i("Link", link)
        pauseLink = link
        pauseVideoPath = videoPath
        pauseVideoName = videoName
        val videoFile = File(videoPath)
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "video_file", videoFile.name,
                RequestBody.create("multipart/form_data/".toMediaTypeOrNull(), videoFile)
            )
            .build()
        val request: Request = Request.Builder()
            .url(link)
            .post(requestBody)
            .build()
        call = client.newCall(request)
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.i("Mistake", e.message!!)
                Log.i("Mistake", e.stackTraceToString())
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                uploads.add(Video(videoName))
                Log.i("Success", "success")
            }
        })
    }
}
package com.example.myapplication.nadutkin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.nadutkin.DownloadApplication.Companion.PickFromGallery
import com.example.myapplication.nadutkin.DownloadApplication.Companion.instance
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.sdk.api.video.VideoService
import com.vk.sdk.api.video.dto.VideoSaveResult
import okhttp3.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        login()
        val upload: Button = findViewById(R.id.upload_button)
        upload.setOnClickListener {
            //val myId = VK.getUserId()
            val permissionStatus = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                getVideo()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PickFromGallery
                )
            }
        }
        val logout: Button = findViewById(R.id.logout_button)
        logout.setOnClickListener {
            Toast.makeText(this, "End of the session", Toast.LENGTH_LONG).show()
            logout()
        }
    }

    private fun login() {
        if (!VK.isLoggedIn()) {
            VK.login(this, arrayListOf(VKScope.WALL, VKScope.PHOTOS, VKScope.VIDEO))
        }
    }

    private fun logout() {
        VK.logout()
        login()
    }

    private fun getVideo() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, PickFromGallery)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PickFromGallery -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Let's upload videos!", Toast.LENGTH_LONG).show()
                    getVideo()
                } else {
                    Toast.makeText(this, "An application needs the permission to upload videos", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                Toast.makeText(
                    this@MainActivity,
                    "Authorisation Succeed",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onLoginFailed(errorCode: Int) {
                Toast.makeText(
                    this@MainActivity,
                    "Authorisation Failed, Error code: $errorCode",
                    Toast.LENGTH_LONG
                ).show()
                logout()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == PickFromGallery && resultCode == RESULT_OK) {
            val selectedImageUri: Uri = data!!.data!!
            val videoPath = instance.getPath(this.applicationContext, selectedImageUri)
            Log.i("videoPath", videoPath)
            VideoService().videoSave(
                "kek",
                "blabla bla",
                isPrivate = false,
                wallpost = true,
                compression = false
            ).also {
                VK.execute(it, object : VKApiCallback<VideoSaveResult> {
                    override fun fail(error: Exception) {
                        Toast.makeText(this@MainActivity, "Failed to get a link", Toast.LENGTH_LONG).show()
                    }

                    override fun success(result: VideoSaveResult) {
                        result.uploadUrl?.let { it1 -> instance.handleSuccess(it1, videoPath) }
                    }
                })
            }
        }
    }




}
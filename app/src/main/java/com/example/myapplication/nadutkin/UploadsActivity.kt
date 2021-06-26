package com.example.myapplication.nadutkin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.nadutkin.DownloadApplication.Companion.instance
import com.example.myapplication.nadutkin.adapter.UploadsAdapter

class UploadsActivity : AppCompatActivity() {

    private lateinit var uploadsRecycler: RecyclerView
    private lateinit var uploadsAdapter: UploadsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uploads)
        initUploadsRecycler()
    }

    private fun initUploadsRecycler(){
        uploadsRecycler = findViewById(R.id.uploads_recycler)
        uploadsAdapter = UploadsAdapter(instance.uploads)
        uploadsRecycler.adapter = uploadsAdapter
        uploadsRecycler.layoutManager = LinearLayoutManager(this as Context)
    }
}
package com.example.kotline_test1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var  recyclerView :RecyclerView;
    private var adapter:Radapter ?= null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview);


        handleRecyclerView();

        recyclerView.adapter = adapter;





    }

    private fun handleRecyclerView() {
        val list : ArrayList<Photo> = ArrayList();
        list.add(Photo("https://www.google.com/images/branding/googlelogo/1x/googlelogo_light_color_272x92dp.png","Google"))
        list.add(Photo("https://s.yimg.com/rz/p/yahoo_frontpage_en-US_s_f_p_205x58_frontpage_2x.png","Yahoo"))
        list.add(Photo("https://www.google.com/images/branding/googlelogo/1x/googlelogo_light_color_272x92dp.png","Google2"))
        list.add(Photo("https://www.google.com/images/branding/googlelogo/1x/googlelogo_light_color_272x92dp.png","Google3"))
       adapter = Radapter(list)
    }
}
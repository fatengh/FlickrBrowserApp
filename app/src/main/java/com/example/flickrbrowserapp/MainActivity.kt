package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var rvAdap: MyAdap
    private lateinit var llBottom: LinearLayout
    private lateinit var edSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var imBIG: ImageView
    private lateinit var imgs: ArrayList<Image>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgs = arrayListOf()
        rv = findViewById(R.id.rv)
        llBottom = findViewById(R.id.llBotom)
        edSearch = findViewById(R.id.edSearch)
        btnSearch = findViewById(R.id.btnSearch)
        imBIG = findViewById(R.id.imBIG)


        rvAdap = MyAdap(this, imgs)
        rv.adapter = rvAdap
        rv.layoutManager = LinearLayoutManager(this)


        btnSearch.setOnClickListener {
            imgs.clear()
            if(edSearch.textSize != 0f){
                requestAPI()
            }else{
                Toast.makeText(this,"Please Enter Things",Toast.LENGTH_LONG).show()
            }
        }
        imBIG.setOnClickListener {
            closeImage()
        }
    }

    private fun requestAPI(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = async {
                getPhoto()
            }.await()
            if(data.isNotEmpty()){
                println(data)
                showPhotos(data)
            }else{
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun getPhoto(): String{
        var response = ""
        try{
            response = URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&per_page=10&api_key=740f37f6d88a3fd469985c0dab901c99&tags=${edSearch.text}&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
        }catch(e: Exception){
            println("ISSUE: $e")
        }
        return response
    }


    private suspend fun showPhotos(data: String){ //json
        withContext(Dispatchers.Main){
            val jsonObj = JSONObject(data)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            for(i in 0 until photos.length()){
                val title = photos.getJSONObject(i).getString("title")
                val farmID = photos.getJSONObject(i).getString("farm")
                val serverID = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_$secret.jpg"
                imgs.add(Image(title, photoLink))
            }
            rvAdap.notifyDataSetChanged()
        }
    }

    private fun closeImage(){
        imBIG.isVisible = false
        rv.isVisible = true
        llBottom.isVisible = true
    }

    fun openImg(link: String){
        Glide.with(this).load(link).into(imBIG)
        imBIG.isVisible = true
        rv.isVisible = false
        llBottom.isVisible = false
    }

}
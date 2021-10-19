package com.example.myapplicationsqd
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.io.File

class MainActivity : AppCompatActivity() {
    var textBottom: EditText?=null
    var textUp: EditText?=null
    var ImgUrls: ArrayList<String> = ArrayList()
    var Listmeme: ArrayList<String> = ArrayList()
    var recyclerViewListMeme: RecyclerView? = null
    var managerViewListMeme: LinearLayoutManager?=null
    var adapterViewListMeme: ListMemeAdapter?=null
    var recyclerView: RecyclerView? = null
    var bouttonGenerer: Button?=null
    var showMemeListButton: Button?=null
    var Manager: LinearLayoutManager? = null
    var adapter: DataAdapter? = null
    var msg: String? = ""
    var lastMsg = ""
    fun generateName() :String{
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString = (1..15)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
        return randomString + ".jpg"
    }
    @SuppressLint("Range")
    fun downloadImage(url: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        //Environment.getStorageDirectory()
        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    File.separator + "MemeCreator" + File.separator + generateName()
                    //url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()
    }
    private fun statusMessage(url: String, directory: File, status: Int): String? {
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
        return msg
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bouttonGenerer= findViewById<Button>(R.id.bouttonSent)
        textUp= findViewById<EditText>(R.id.textUp)
        textBottom= findViewById<EditText>(R.id.textDown)

        Listmeme.add("https://apimeme.com/meme?meme=10-Guy&top=Top+text&bottom=Bottom+text")
        Listmeme.add("https://apimeme.com/meme?meme=1990s-First-World-Problems&top=Top+text&bottom=Bottom+text")
        var textHaut= textUp!!.text
        var textBas= textBottom!!.text

    /*    showMemeListButton!!.setOnClickListener{
            recyclerViewListMeme!!.setVisibility(View.VISIBLE)
        }*/
        ImgUrls.add("https://apimeme.com/meme?meme=Bonobo-Lyfe&top=${textHaut}&bottom=${textBas}")
        bouttonGenerer!!.setOnClickListener{



            //val sizeArray=ImgUrls.size
            //downloadImage("https://apimeme.com/meme?meme=Bonobo-Lyfe&top=${textHaut}&bottom=${textBas}")

        }
        recyclerViewListMeme= findViewById<View>(R.id.listOfPictures) as RecyclerView
        managerViewListMeme= LinearLayoutManager(this)
        recyclerViewListMeme!!.layoutManager=managerViewListMeme
        adapterViewListMeme= ListMemeAdapter(this,Listmeme)
        recyclerViewListMeme!!.adapter = adapterViewListMeme

        recyclerView = findViewById<View>(R.id.card_current_recycler_view) as RecyclerView
        Manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = Manager
        adapter = DataAdapter(this,ImgUrls)
        recyclerView!!.adapter = adapter

    }
}
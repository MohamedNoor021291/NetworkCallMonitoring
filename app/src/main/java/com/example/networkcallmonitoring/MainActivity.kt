package com.example.networkcallmonitoring

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networkcallmonitorinterceptor.HttpMonitorInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val TAG = "NetworkCallMonitoring"
    private val recyclerView: RecyclerView? = null
    private var adapter: MyAdapter? = null
    private val itemList: MutableList<String> = mutableListOf()
    private lateinit var url:TextView;
    private lateinit var method:TextView;
    private lateinit var status:TextView;
    private lateinit var duration:TextView;
    private lateinit var size:TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        url = findViewById(R.id.url)
        method = findViewById(R.id.method)
        status = findViewById(R.id.status)
        duration = findViewById(R.id.duration)
        size = findViewById(R.id.size)
        val recyclerView = findViewById<RecyclerView>(R.id.data_rv)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        adapter = MyAdapter(itemList)
        recyclerView.setAdapter(adapter)
        fetchItems();
    }

    private fun fetchItems() {
        // Get the OkHttpClient instance
        // Get the OkHttpClient instance
        val client: OkHttpClient? = NetworkClient.getHttpClient()

        // Build a request

        // Build a request
        val request: Request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts")
            .build()

        // Execute the request in a separate thread

        // Execute the request in a separate thread
        Thread {
            try {
                val response = client!!.newCall(request).execute()
                if (response.isSuccessful) {
                    // Log the response body
                    val responseData = response.body!!.string()
                    runOnUiThread {
                        // Update the UI or log the result
                        println("Response: $responseData")
                        parseAndDisplayItems(responseData)
                        val networkInfo = HttpMonitorInterceptor.networkDetails;
                        url.text = "Network URL :" + networkInfo.get("url")
                        method.text = "Network method :" + networkInfo.get("method")
                        status.text = "Network status :" + networkInfo.get("status_code")
                        duration.text = "Network duration :" + networkInfo.get("duration")
                        duration.text = "Response Body size :" + networkInfo.get("size")
                    }
                } else {
                    System.err.println("Request Failed: " + response.code)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun parseAndDisplayItems(jsonResponse: String) {
        try {
            val jsonArray = JSONArray(jsonResponse)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val title = jsonObject.getString("title")
                itemList.add(title)
            }
            runOnUiThread { adapter!!.notifyDataSetChanged() }
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON response", e)
        }
    }
}
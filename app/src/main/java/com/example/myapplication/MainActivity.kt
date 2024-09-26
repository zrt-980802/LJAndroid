package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Data.CardItem
import com.example.myapplication.Helper.SimpleItemTouchHelperCallback
import com.example.myapplication.Listener.OnLoadMoreListener
import com.example.myapplication.RecyclerView.CardAdapter
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.min
import kotlin.random.Random

data class ColorItem(val color: Int)


class MainActivity : ComponentActivity(), OnLoadMoreListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private val cardItem = ArrayList<CardItem>()
    private var batch_teachers = 0
    private var batch_size = 5
    private var isLoadingMore = false
    private var loadMoreCount = 0 // 上滑次数计数器
    private var reflashLoadMore = 3
    private var jsonArray: JSONArray? = null
    private var lastPullTime: Long = 0
    private val minInterval: Long = 300 // 最小间隔时间，单位毫秒


    fun Context.readRawResourceFile(resId: Int): String {
        val inputStream: InputStream = resources.openRawResource(resId)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val textBuilder = StringBuilder()
        bufferedReader.lines().forEach { line ->
            textBuilder.append(line)
            textBuilder.append("\n")
        }
        return textBuilder.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        jsonArray = JSONArray(readRawResourceFile(R.raw.teachers_data))
        recyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        loadInitialData()

        adapter = CardAdapter(
            cardItem, this, onDeleteItem = { position ->
                // 从列表中移除项目并更新适配器
                cardItem.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, cardItem.size)
            }
        )
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (dy > 0 && lastVisibleItemPosition == totalItemCount - 1) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastPullTime >= minInterval) {
                        loadMoreCount++
                        lastPullTime = currentTime
                        if (batch_teachers > 5) {
                            showNoMoreDataMessage()
                            loadMoreCount = 0
                        } else if (loadMoreCount >= reflashLoadMore) { // 每三次到达底部加载更多数据
                            onLoadMore()
                            loadMoreCount = 0
                        }
                    }
                }
            }
        })

        val itemTouchHelperCallback = SimpleItemTouchHelperCallback(adapter)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        recyclerView.setOnLongClickListener { v ->
            val position = (v.tag as? RecyclerView.ViewHolder)?.adapterPosition
                ?: return@setOnLongClickListener false
            adapter.deleteItem(position)
            true
        }
    }

    fun load_teacher_data() {
        val moreCardItem = ArrayList<CardItem>()

        for (i in batch_teachers * batch_size until min(
            batch_size + batch_teachers * batch_size,
            jsonArray?.length() ?: 5
        )) {
            val randomInt = Random.nextInt(0, 700)
            val jsonObject = jsonArray?.getJSONObject(randomInt) ?: break
            val infoObject = jsonObject.optJSONObject("info") // 使用 optJSONObject 避免 JSONException
            var tmpPath = "/attachment/staff/man.jpg"
            var education: String = null.toString()
            if (infoObject != null) {
                // 检查 "en" 是否存在，并且是一个 JSONObject
                val enObject = infoObject.optJSONObject("en")
                val cnObject = infoObject.optJSONObject("cn")
                if (enObject != null && enObject.has("photo")) {
                    tmpPath = enObject.getString("photo")
                    education = enObject.getString("education")
                } else if (cnObject != null && cnObject.has("photo")) {
                    tmpPath = cnObject.getString("photo")
                    education = cnObject.getString("education")
                }
            }

            val photoResId = "https://staff.uic.edu.cn" + tmpPath
            val name = jsonObject.getString("name_en")
            val title = jsonObject.getString("title")
            val email = jsonObject.getString("email").split('@').first()
            val linkUrl = "https://www.uic.edu.cn/en/faculty.htm#/" + email + "/en"
            println(linkUrl)
            // 将 对象添加到列表中
            moreCardItem.add(CardItem(photoResId, name, title, education, linkUrl))
        }
        cardItem.addAll(moreCardItem)
        batch_teachers += 1
    }

    private fun loadInitialData() {
        if (batch_teachers <= 5)
            load_teacher_data()
    }

    override fun onLoadMore() {
        if (!isLoadingMore) {
            isLoadingMore = true
            loadMoreCount++
            if (loadMoreCount >= 3) {
                load_teacher_data()
                Thread {
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                        isLoadingMore = false
                    }
                }.start()
                loadMoreCount = 0 // 重置计数器
            }
        }
    }

    private fun showNoMoreDataMessage() {
        Toast.makeText(this, "没有更多数据了", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}


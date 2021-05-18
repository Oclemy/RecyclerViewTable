package info.camposha.recyclerview_tableview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mTableAdapter: ScrollTableAdapter? = null
    private var mTableView: ScrollTableView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.table_activity_layout)
        mTableView = findViewById<View>(R.id.scrollable_table_view) as ScrollTableView
        mTableAdapter = ScrollTableAdapter()
        generateTestData(mTableAdapter!!)
        mTableView!!.setTableBaseAdapter(mTableAdapter)
    }

    private fun generateTestData(scrollablePanelAdapter: ScrollTableAdapter) {
        val roomInfoList: MutableList<String> = ArrayList()
        roomInfoList.add("1")
        roomInfoList.add("2")
        roomInfoList.add("3")
        roomInfoList.add("4")
        roomInfoList.add("5")
        roomInfoList.add("6")
        roomInfoList.add("7")
        roomInfoList.add("8")
        scrollablePanelAdapter.setmRowHeaderDatas(roomInfoList)
        val dateInfoList: MutableList<String> = ArrayList()
        val calendar = Calendar.getInstance()
        for (i in 0..13) {
            val date = DAY_UI_MONTH_DAY_FORMAT.format(calendar.time)
            dateInfoList.add(date)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        scrollablePanelAdapter.setmHeaderDatas(dateInfoList)
        val ordersList: MutableList<List<String>> = ArrayList()
        for (i in 0..29) {
            val orderInfoList: MutableList<String> = ArrayList()
            orderInfoList.add("as00")
            orderInfoList.add("as01")
            orderInfoList.add("as02")
            orderInfoList.add("as03")
            orderInfoList.add("as04")
            orderInfoList.add("as05")
            ordersList.add(orderInfoList)
        }
        scrollablePanelAdapter.setmCellDatas(ordersList)
    }

    companion object {
        val DAY_UI_MONTH_DAY_FORMAT = SimpleDateFormat("MM-dd")
    }
}
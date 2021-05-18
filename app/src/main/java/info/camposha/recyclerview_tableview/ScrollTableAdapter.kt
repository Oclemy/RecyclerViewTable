package info.camposha.recyclerview_tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ScrollTableAdapter : TableBaseAdapter() {
    private var mRowHeaderDatas: List<String> = ArrayList()
    private var mHeaderDatas: List<String> = ArrayList()
    private var mCellDatas: List<List<String>> = ArrayList()
    override val rowCount: Int
        get() = mRowHeaderDatas.size + 1
    override val columnCount: Int
        get() =  mHeaderDatas.size

//    override fun getColumnCount(): Int {
//        return mHeaderDatas.size
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, row: Int, column: Int) {
        val viewType = getItemViewType(row, column)
        when (viewType) {
            HEADER_TYPE -> setHeaderView(column, holder as HeaderViewHolder?)
            ROW_HEADER_TYPE -> setRowHeaderView(row, holder as RowHeaderViewHolder?)
            CELL_TYPE -> setCellView(row, column, holder as CellViewHolder?)
            FIRST_CELL_TYPE -> {
            }
            else -> setCellView(row, column, holder as CellViewHolder?)
        }
    }

    override fun getItemViewType(row: Int, column: Int): Int {
        if (column == 0 && row == 0) {
            return FIRST_CELL_TYPE
        }
        if (column == 0) {
            return ROW_HEADER_TYPE
        }
        return if (row == 0) {
            HEADER_TYPE
        } else CELL_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            HEADER_TYPE -> return HeaderViewHolder(
                LayoutInflater.from(
                    parent!!.context
                )
                    .inflate(R.layout.table_header_layout, parent, false)
            )
            ROW_HEADER_TYPE -> return RowHeaderViewHolder(
                LayoutInflater.from(
                    parent!!.context
                )
                    .inflate(R.layout.table_row_header_layout, parent, false)
            )
            CELL_TYPE -> return CellViewHolder(
                LayoutInflater.from(
                    parent!!.context
                )
                    .inflate(R.layout.table_cell_layout, parent, false)
            )
            FIRST_CELL_TYPE -> return FirstCellViewHolder(
                LayoutInflater.from(
                    parent!!.context
                )
                    .inflate(R.layout.table_first_cell_layout, parent, false)
            )
            else -> {
            }
        }
        return CellViewHolder(
            LayoutInflater.from(parent!!.context)
                .inflate(R.layout.table_cell_layout, parent, false)
        )
    }

    private fun setHeaderView(pos: Int, viewHolder: HeaderViewHolder?) {
        val headerInfo = mHeaderDatas[pos - 1]
        if (headerInfo != null && pos > 0) {
            viewHolder!!.mHeaderTxt.text = headerInfo
        }
    }

    private fun setRowHeaderView(pos: Int, viewHolder: RowHeaderViewHolder?) {
        val rowHeaderInfo = mRowHeaderDatas[pos - 1]
        if (rowHeaderInfo != null && pos > 0) {
            viewHolder!!.mRowHeaderTxt.text = rowHeaderInfo
        }
    }

    private fun setCellView(row: Int, column: Int, viewHolder: CellViewHolder?) {
        if (column - 1 < mCellDatas[row].size) {
            val cellInfo = mCellDatas[row - 1][column - 1]
            if (cellInfo != null) {
                viewHolder!!.mCellContent.text = cellInfo
            } else {
                viewHolder!!.mCellContent.text = "N/A"
            }
        } else {
            viewHolder!!.mCellContent.text = "N/A"
        }
    }

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mHeaderTxt: TextView

        init {
            mHeaderTxt = itemView.findViewById<View>(R.id.table_header_title) as TextView
        }
    }

    private class RowHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mRowHeaderTxt: TextView

        init {
            mRowHeaderTxt = view.findViewById<View>(R.id.row_header_view) as TextView
        }
    }

    private class CellViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mCellContent: TextView

        init {
            mCellContent = view.findViewById<View>(R.id.table_cell_content) as TextView
        }
    }

    private class FirstCellViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mFirstCellTitle: TextView

        init {
            mFirstCellTitle = view.findViewById<View>(R.id.first_cell_title) as TextView
        }
    }

    fun setmRowHeaderDatas(mRowHeaderDatas: List<String>) {
        this.mRowHeaderDatas = mRowHeaderDatas
    }

    fun setmHeaderDatas(mHeaderDatas: List<String>) {
        this.mHeaderDatas = mHeaderDatas
    }

    fun setmCellDatas(mCellDatas: List<List<String>>) {
        this.mCellDatas = mCellDatas
    }

    companion object {
        private const val FIRST_CELL_TYPE = 4
        private const val ROW_HEADER_TYPE = 0
        private const val HEADER_TYPE = 1
        private const val CELL_TYPE = 2
    }
}
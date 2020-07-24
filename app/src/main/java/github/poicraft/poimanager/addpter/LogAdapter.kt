@file:Suppress("PropertyName")

package github.poicraft.poimanager.addpter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import github.poicraft.poimanager.R


class LogAdapter() : RecyclerView.Adapter<LogAdapter.LogHolder>() {

    private val data = ArrayList<MLog>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
        return LogHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LogHolder, position: Int) {
        val mLog = data[position]
        holder.log_type.text = "${mLog.log_type}:"
        holder.log_msg.text = mLog.log_msg
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addLog(log: MLog){
        data.add(log)
        notifyItemInserted(data.size)
    }
    class LogHolder(itemView: View):
            RecyclerView.ViewHolder(itemView) {
        var log_type: TextView = itemView.findViewById(R.id.log_type)
        var log_msg: TextView = itemView.findViewById(R.id.log_msg)
    }
}

class MLog(val log_type:String, val log_msg:String)
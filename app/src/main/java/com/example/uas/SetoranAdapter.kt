package com.example.uas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SetoranAdapter : RecyclerView.Adapter<SetoranAdapter.ViewHolder>() {

    private var setoranList = listOf<DataModels.SetoranItem>()

    fun submitList(list: List<DataModels.SetoranItem>) {
        setoranList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSurah: TextView = view.findViewById(R.id.tvSurah)
        private val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        private val tvStatus: TextView = view.findViewById(R.id.tvStatus)

        fun bind(item: DataModels.SetoranItem) {
            tvSurah.text = item.namaSurah
            tvTanggal.text = item.tanggalSetoran ?: "Belum disetor"
            tvStatus.text = item.status ?: "-"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setoran, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = setoranList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(setoranList[position])
    }
}

package com.example.fitpal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.databinding.ItemTipBinding
import com.example.fitpal.model.fitness.entities.Tip

class TipsRecyclerAdapter : RecyclerView.Adapter<TipsViewHolder>() {

    private val tips = mutableListOf<Tip>()

    fun updateTips(newTips: List<Tip>) {
        tips.clear()
        tips.addAll(newTips)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        val binding = ItemTipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TipsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        holder.bind(tips[position])
    }

    override fun getItemCount(): Int = tips.size
}
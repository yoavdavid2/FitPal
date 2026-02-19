package com.example.fitpal3.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.databinding.ItemTipBinding
import com.example.fitpal3.model.fitness.entities.Tip

class TipsViewHolder(private val binding: ItemTipBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(tip: Tip) {
        binding.apply {
            tipTitle.text = tip.title
            tipContent.text = tip.content
        }
    }
}
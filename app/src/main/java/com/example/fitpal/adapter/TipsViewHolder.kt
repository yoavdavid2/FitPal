package com.example.fitpal.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.databinding.ItemTipBinding
import com.example.fitpal.model.fitness.entities.Tip

class TipsViewHolder(private val binding: ItemTipBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(tip: Tip) {
        binding.apply {
            tipTitle.text = tip.title
            tipContent.text = tip.content
        }
    }
}
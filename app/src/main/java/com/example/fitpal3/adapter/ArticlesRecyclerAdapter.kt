package com.example.fitpal3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.databinding.ItemArticleBinding
import com.example.fitpal3.model.fitness.entities.Article

class ArticlesRecyclerAdapter(private val onArticleClicked: (Article) -> Unit) :
    RecyclerView.Adapter<ArticlesViewHolder>() {

    private val articles = mutableListOf<Article>()

    fun updateArticles(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticlesViewHolder(binding, onArticleClicked)
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size
}
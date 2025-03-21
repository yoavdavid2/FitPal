package com.example.fitpal.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.databinding.ItemArticleBinding
import com.example.fitpal.model.fitness.entities.Article
import com.squareup.picasso.Picasso

class ArticlesViewHolder(
    private val binding: ItemArticleBinding,
    private val onArticleClicked: (Article) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(article: Article) {
        binding.articleTitle.text = article.title
        binding.articleCategory.text = article.category
        binding.articlePreview.text = article.content.take(100) + "..."

        article.imageUrl?.let { url ->
            Picasso.get()
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.articleImage)
        } ?: run {
            Picasso.get()
                .load("https://via.placeholder.com/400x200?text=Fitness")
                .into(binding.articleImage)
        }

        binding.root.setOnClickListener {
            onArticleClicked(article)
        }
    }
}
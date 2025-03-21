package com.example.fitpal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitpal.databinding.FragmentArticleDetailBinding
import com.example.fitpal.viewmodels.ArticleDetailsViewModel
import com.squareup.picasso.Picasso

class ArticleDetailsFragment : Fragment() {

    private var binding: FragmentArticleDetailBinding? = null

    private val viewModel: ArticleDetailsViewModel by viewModels()
    private val args: ArticleDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.apply {
            loadArticle(args.articleId)

            article.observe(viewLifecycleOwner) { article ->
                article?.let {
                    binding?.apply {
                        articleTitle.text = it.title
                        articleCategory.text = it.category
                        articleContent.text = it.content
                    }

                    it.imageUrl?.let { url ->
                        Picasso.get()
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(binding?.articleImage)
                    } ?: run {
                        Picasso.get()
                            .load("https://via.placeholder.com/800x400?text=Fitness+Article")
                            .into(binding?.articleImage)
                    }
                }
            }

            loading.observe(viewLifecycleOwner) { loading ->
                binding?.loader?.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
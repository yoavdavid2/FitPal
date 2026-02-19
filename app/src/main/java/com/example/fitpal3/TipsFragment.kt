package com.example.fitpal3

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitpal3.adapter.ArticlesRecyclerAdapter
import com.example.fitpal3.adapter.TipsRecyclerAdapter
import com.example.fitpal3.adapter.WorkoutPlanAdapter
import com.example.fitpal3.databinding.FragmentTipsBinding
import com.example.fitpal3.model.fitness.entities.Article
import com.example.fitpal3.model.fitness.entities.Tip
import com.example.fitpal3.model.fitness.entities.WorkoutPlan
import com.example.fitpal3.services.GeminiService
import com.example.fitpal3.services.GeminiService.ContentCallback
import com.example.fitpal3.viewmodels.TipsViewModel
import androidx.core.content.edit

class TipsFragment : Fragment(), ContentCallback {
    private var binding: FragmentTipsBinding? = null

    private val viewModel: TipsViewModel by viewModels()

    private lateinit var tipAdapter: TipsRecyclerAdapter
    private lateinit var articleAdapter: ArticlesRecyclerAdapter
    private lateinit var workoutPlanAdapter: WorkoutPlanAdapter

    private lateinit var geminiService: GeminiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupServices()
        setupAdapters()
        setupRecyclerViews()
        setupClickListeners()
        setupObservers()
        setupSwipeRefresh()

        viewModel.loadSavedContent()
    }

    private fun setupServices() {
        geminiService = GeminiService.getInstance(requireContext())
    }

    private fun setupAdapters() {
        tipAdapter = TipsRecyclerAdapter()

        articleAdapter = ArticlesRecyclerAdapter { article ->
            findNavController().navigate(
                TipsFragmentDirections.actionTipsFragmentToArticleDetailFragment(
                    article.id
                )
            )
        }

        workoutPlanAdapter = WorkoutPlanAdapter { workoutPlan ->
            findNavController().navigate(
                TipsFragmentDirections.actionTipsFragmentToWorkoutDetailFragment(
                    workoutPlan.id
                )
            )
        }
    }

    private fun setupRecyclerViews() {
        binding?.apply {
            tipsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = tipAdapter
            }
            articlesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = articleAdapter
            }
            workoutPlansRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = workoutPlanAdapter
            }
        }
    }

    private fun setupClickListeners() {
        binding?.generateContentButton?.setOnClickListener {
            generateContent()
        }
    }

    private fun setupSwipeRefresh() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel.loadSavedContent()
        }
    }

    private fun setupObservers() {

        viewModel.apply {
            loading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)
                if (!isLoading) {
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
            }
            error.observe(viewLifecycleOwner) { errorMessage ->
                if (errorMessage != null) {
                    showError(errorMessage)
                    viewModel.clearError()
                } else {
                    hideError()
                }
            }
            tips.observe(viewLifecycleOwner) { tips ->
                tipAdapter.updateTips(tips)
                binding?.tipsEmptyState?.visibility =
                    if (tips.isEmpty()) View.VISIBLE else View.GONE
            }
            articles.observe(viewLifecycleOwner) { articles ->
                articleAdapter.updateArticles(articles)
                binding?.articlesEmptyState?.visibility =
                    if (articles.isEmpty()) View.VISIBLE else View.GONE
            }
            workoutPlans.observe(viewLifecycleOwner) { workoutPlans ->
                if (workoutPlans != null) {
                    workoutPlanAdapter.updateWorkoutPlans(workoutPlans)
                    binding?.workoutsEmptyState?.visibility =
                        if (workoutPlans.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun generateContent() {
        if (!viewModel.canGenerateContent()) {
            binding?.apply {
                errorLayout.visibility = View.VISIBLE
                errorText.text = "Reached content generating limit. Try again tomorrow."
            }

            return
        }

        hideError()
        hideEmptyStates()
        showLoading(true)
        viewModel.setLoading(true)

        geminiService.generateContent(this)
    }

    private fun hideEmptyStates() {
        binding?.apply {
            tipsEmptyState.visibility = View.GONE
            articlesEmptyState.visibility = View.GONE
            workoutsEmptyState.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.apply {
                progressBar.visibility = View.VISIBLE
                progressBar.alpha = 0f
                progressBar.animate().alpha(1f).setDuration(200).start()
                generateContentButton.isEnabled = false
            }
        } else {
            binding?.apply {
                progressBar.animate().alpha(0f).setDuration(200).withEndAction {
                    progressBar.visibility = View.GONE
                }.start()
                generateContentButton.isEnabled = true
            }
        }
    }

    private fun showError(errorMessage: String) {
        binding?.apply {
            errorLayout.visibility = View.VISIBLE
            errorText.text = errorMessage
        }
    }

    private fun hideError() {
        binding?.errorLayout?.visibility = View.GONE
    }

    override fun onSuccess(
        tips: List<Tip>,
        articles: List<Article>,
        workoutPlans: List<WorkoutPlan>,
    ) {
        saveGenerationTime()
        viewModel.saveContent(tips, articles, workoutPlans)
    }

    private fun saveGenerationTime() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit() {
            putLong("last_content_generation_time", System.currentTimeMillis())
        }
    }

    override fun onError(error: String) {
        viewModel.apply {
            setError(error)
            setLoading(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
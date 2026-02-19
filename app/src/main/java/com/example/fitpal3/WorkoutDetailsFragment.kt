package com.example.fitpal3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitpal3.databinding.FragmentWorkoutDetailsBinding
import com.example.fitpal3.viewmodels.WorkoutDetailsViewModel
import com.squareup.picasso.Picasso

class WorkoutDetailsFragment : Fragment() {

    private var binding: FragmentWorkoutDetailsBinding? = null

    private val viewModel: WorkoutDetailsViewModel by viewModels()
    private val args: WorkoutDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadWorkoutPlan(args.workoutPlanId)

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.workoutPlan.observe(viewLifecycleOwner) { workoutPlan ->
            workoutPlan?.let {
                binding?.apply {
                    workoutTitle.text = it.title
                    workoutContent.text = it.content
                    difficultyChip.text = it.difficulty
                    durationChip.text = it.duration
                    targetChip.text = it.targetMuscleGroup
                }

                when (it.difficulty.lowercase()) {
                    "beginner" -> binding?.difficultyChip?.setChipBackgroundColorResource(android.R.color.holo_green_light)
                    "intermediate" -> binding?.difficultyChip?.setChipBackgroundColorResource(android.R.color.holo_blue_light)
                    "advanced" -> binding?.difficultyChip?.setChipBackgroundColorResource(android.R.color.holo_red_light)
                }

                val imageUrl = when (it.targetMuscleGroup.lowercase()) {
                    "core", "abs" -> "https://via.placeholder.com/800x400?text=Core+Workout"
                    "upper body", "arms", "chest" -> "https://via.placeholder.com/800x400?text=Upper+Body+Workout"
                    "lower body", "legs" -> "https://via.placeholder.com/800x400?text=Lower+Body+Workout"
                    "back" -> "https://via.placeholder.com/800x400?text=Back+Workout"
                    else -> "https://via.placeholder.com/800x400?text=Full+Body+Workout"
                }

                Picasso.get()
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(binding?.workoutImage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
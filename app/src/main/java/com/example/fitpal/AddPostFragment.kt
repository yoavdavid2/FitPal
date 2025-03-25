package com.example.fitpal

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.navigation.Navigation
import com.example.fitpal.databinding.FragmentAddPostBinding
import com.example.fitpal.model.Model
import com.example.fitpal.model.Post
import java.util.UUID
import androidx.navigation.findNavController

class AddPostFragment : Fragment() {
    private var binding: FragmentAddPostBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)

        binding?.apply {
            saveButton.setOnClickListener(::onSaveClicked)
            cancelButton.setOnClickListener(::onCancelClicked)
            cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {bitmap ->
                imageView.setImageBitmap(bitmap)
                didSetProfileImage = true
            }

            takePhotoButton.setOnClickListener {
                cameraLauncher?.launch(null)
            }
        }
        return binding?.root
    }

    private fun onSaveClicked(view: View) {
        val uuid: String = UUID.randomUUID().toString()
        val author: String = "**************temp-author**************" //TODO get user Email
        val title: String = binding?.titleTextView?.text?.toString() ?: ""
        val text: String = binding?.textTextView?.text?.toString() ?: ""
        val image: String = ""
        val likes: List<String> = listOf()
        val comments: List<Comment> = listOf()
        val date: String = binding?.dateEditText?.text?.toString() ?: ""
        val post = Post(uuid, author, title, text, image, likes, comments, date)

        binding?.progressBar?.visibility = View.VISIBLE

        if (didSetProfileImage) {
            binding?.imageView?.isDrawingCacheEnabled = true
            binding?.imageView?.buildDrawingCache()
            val bitmap = (binding?.imageView?.drawable as BitmapDrawable).bitmap

            Model.shared.add(post, bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                Navigation.findNavController(view).popBackStack()
            }
        } else {
            Model.shared.add(post, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.visibility = View.GONE
                Navigation.findNavController(view).popBackStack()
            }
        }
    }

    private fun onCancelClicked(view: View) {
        view.findNavController().popBackStack()
    }
}
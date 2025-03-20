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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.fitpal.adapter.PostsRecyclerAdapter
import com.example.fitpal.databinding.FragmentFeedBinding
import com.example.fitpal.databinding.FragmentNewPostBinding
import com.example.fitpal.model.Comment
import com.example.fitpal.model.Model
import com.example.fitpal.model.Post
import java.util.UUID


/**
 * A simple [Fragment] subclass.
 * Use the [NewPostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewPostFragment : Fragment() {
    private var binding: FragmentNewPostBinding? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding?.saveButton?.setOnClickListener(::onSaveClicked)
        binding?.cancelButton?.setOnClickListener(::onCancelClick)
        binding?.toolbar?.setNavigationOnClickListener(::onCancelClick)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.imageView?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }

        binding?.takePhotoButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

//        binding?.cancelButton?.setOnClickListener {
//            Log.d("TAG", "Button clicked!")
//            val action = NewPostFragmentDirections.actionNewPostFragmentToFeedFragment()
//            findNavController().navigate(action)
//        }
//
//        binding?.toolbar?.setNavigationOnClickListener {
//            Log.d("TAG", "Button clicked!")
//            val action = NewPostFragmentDirections.actionNewPostFragmentToFeedFragment()
//            findNavController().navigate(action)
//        }


        return binding?.root
    }

    private fun onSaveClicked(view: View) {
        Log.d("TAG", "SaveButton clicked!")

        val uuid: String = UUID.randomUUID().toString()
        val author: String = "**************temp-author**************" //TODO get user Email
        val title: String = binding?.postTitle?.text?.toString() ?: ""
        val text: String = binding?.postText?.text?.toString() ?: ""
        val image: String = ""
        val likes: List<String> = listOf()
        val comments: List<Comment> = listOf()
        val date: String = binding?.postDate?.text?.toString() ?: ""
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

    private fun onCancelClick(view: View) {
        Navigation.findNavController(view).popBackStack()
    }
}


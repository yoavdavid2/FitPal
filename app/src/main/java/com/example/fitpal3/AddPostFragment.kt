package com.example.fitpal3

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.example.fitpal3.databinding.FragmentAddPostBinding
import com.example.fitpal3.model.Model
import com.example.fitpal3.model.Post
import java.util.UUID
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

            dateEditText.setOnClickListener {
                showDatePicker()
            }

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

    private fun getAuthor( callback: (String) -> Unit) {
        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        var author: String = "**************temp-author**************"
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        author = document.getString("email") ?: ""
                        callback(author)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun onSaveClicked(view: View) {
        getAuthor() { author ->
            val uuid: String = UUID.randomUUID().toString()
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
    }

    private fun onCancelClicked(view: View) {
        view.findNavController().popBackStack()
    }

    private fun showDatePicker() {
        val calender = java.util.Calendar.getInstance()

        val dialog = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val formatted = "%02d/%02d/%04d".format(day, month + 1, year)
                binding?.dateEditText?.setText(formatted)
            },
            calender.get(java.util.Calendar.YEAR),
            calender.get(java.util.Calendar.MONTH),
            calender.get(java.util.Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }
}
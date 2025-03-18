package com.example.fitpal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.fitpal.databinding.FragmentChatBinding
import com.example.fitpal.model.Chat


class ChatFragment : Fragment() {
    private var binding: FragmentChatBinding? = null
    private var username: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        username = ChatFragmentArgs.fromBundle(requireArguments()).chatterUsername

        setUserName()
        setOnReturnButtonClick()

        return binding?.root
    }


    private fun setOnReturnButtonClick() {
        val backButton: ImageButton? = binding?.backButton
        backButton?.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setUserName() {
        binding?.chatName?.text = username
    }
}

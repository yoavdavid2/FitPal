package com.example.fitpal

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitpal.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val selectedSports = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        val sports = listOf("Football", "Basketball", "Tennis", "Swimming", "Running")

        binding.sportsDropdown.setOnClickListener {
            val selectedItems = BooleanArray(sports.size)

            AlertDialog.Builder(requireContext())
                .setTitle("Select Sports")
                .setMultiChoiceItems(sports.toTypedArray(), selectedItems) { _, index, isChecked ->
                    if (isChecked) {
                        selectedSports.add(sports[index])
                    } else {
                        selectedSports.remove(sports[index])
                    }
                }
                .setPositiveButton("OK") { _, _ ->
                    binding.sportsDropdown.setText(selectedSports.joinToString(", "))
                    Log.d("Dropdown", "Selected Sports: $selectedSports")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        binding.apply {

            maleCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    femaleCheckbox.isChecked = false
                }
            }

            femaleCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    maleCheckbox.isChecked = false
                }
            }

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val emailNotEmpty = emailInput.text.toString().isNotEmpty()
                    val passwordNotEmpty = passwordInput.text.toString().isNotEmpty()

                    signUpButton.isEnabled = emailNotEmpty && passwordNotEmpty
                    signUpButton.animate().alpha(if (loginButton.isEnabled) 1f else 0.5f).setDuration(300).start()
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            firstNameInput.addTextChangedListener(textWatcher)
            lastNameInput.addTextChangedListener(textWatcher)
            emailInput.addTextChangedListener(textWatcher)
            passwordInput.addTextChangedListener(textWatcher)

            signUpButton.apply {
                setOnClickListener {
                    val firstName = firstNameInput.text.toString().trim()
                    val lastName = lastNameInput.text.toString().trim()
                    val email = emailInput.text.toString().trim()
                    val password = passwordInput.text.toString().trim()
                    val isMale = maleCheckbox.isChecked

                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.i("Signup Success", "Email: $email, Password: $password")

                                val user = User(firstName, lastName, email, isMale, 0, selectedSports.toList())

                                val userId = mAuth.currentUser?.uid ?: ""

                                db.collection("users").document(userId).set(user)
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "DocumentSnapshot successfully written!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firestore", "Error writing document", e)
                                    }

                                parentFragmentManager.popBackStack()
                            } else {
                                Toast.makeText(requireContext(), "Signup failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                isEnabled = false
                alpha = 0.5f

            }

            loginButton.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

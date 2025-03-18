package com.example.fitpal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitpal.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.loginGoogleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.apply {

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val emailNotEmpty = emailInput.text.toString().isNotEmpty()
                    val passwordNotEmpty = passwordInput.text.toString().isNotEmpty()

                    loginButton.isEnabled = emailNotEmpty && passwordNotEmpty
                    loginButton.animate().alpha(if (loginButton.isEnabled) 1f else 0.5f).setDuration(300).start()
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            emailInput.addTextChangedListener(textWatcher)
            passwordInput.addTextChangedListener(textWatcher)

            loginButton.apply {
                setOnClickListener {
                    val email = emailInput.text.toString()
                    val password = passwordInput.text.toString()

                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                updateUI(mAuth.currentUser)
                            } else {
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }
                        }
                }

                isEnabled = false
                alpha = 0.5f
            }
        }

        binding.signUpButton.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SignupFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.result
            if (account != null) {
                firebaseAuthWithGoogle(account)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    updateUI(mAuth.currentUser)
                } else {
                    Toast.makeText(requireContext(), "Google sign in failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FeedFragment())
                .commit()
        } else {
            Log.i("LoginFragment", "Login failed or user is null.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}

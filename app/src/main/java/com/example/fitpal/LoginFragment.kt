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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.fitpal.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
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
        db = FirebaseFirestore.getInstance()


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

            signUpButton.apply {
                setOnClickListener {
                    val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
                    findNavController().navigate(action)
                }
            }
        }

    }

    private fun checkIfUserExistsInFirestore(user: FirebaseUser) {
        val userId = user.uid
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Firestore", "User already exists in Firestore")
                    val action = LoginFragmentDirections.actionLoginFragmentToFeedFragment()
                    findNavController().navigate(action)
                } else {
                    saveGoogleUserToFirestore(user)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error checking if user exists", e)
            }
    }

    private fun saveGoogleUserToFirestore(user: FirebaseUser) {
        val userId = user.uid
        val userEmail = user.email
        val userFirstName = user.displayName?.split(" ")?.get(0) ?: ""
        val userLastName = user.displayName?.split(" ")?.get(1) ?: ""

        val googleUser = User(
            firstName = userFirstName,
            lastName = userLastName,
            email = userEmail ?: "",
            isMale = true
        )

        db.collection("users").document(userId).set(googleUser)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully written!")
                val action = LoginFragmentDirections.actionLoginFragmentToFeedFragment()
                findNavController().navigate(action)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing document", e)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign-in failed: ${e.statusCode}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Save Google user to Firestore
                val user = mAuth.currentUser
                if (user != null) {
                    checkIfUserExistsInFirestore(user)
                }
            } else {
                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()
            val action = LoginFragmentDirections.actionLoginFragmentToFeedFragment()
            findNavController().navigate(action)

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

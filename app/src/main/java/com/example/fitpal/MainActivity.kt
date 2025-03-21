package com.example.fitpal

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fitpal.databinding.ActivityMainBinding
import com.example.fitpal.model.Post
import com.google.android.libraries.places.api.Places
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp

interface OnItemClickListener {
    fun onItemClick(position: Int)
    fun onItemClick(student: Post?)
    fun onCommentClick(postId: String) // Add this for the comment button
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostController =
            supportFragmentManager.findFragmentById(binding.mainNavFragment.id) as? NavHostFragment
        navController = navHostController?.navController

        navController?.let { navController ->
            binding.bottomNavigationBar.setupWithNavController(navController)

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!navController.popBackStack()) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
        }

        FirebaseApp.initializeApp(this);
        Places.initialize(applicationContext, BuildConfig.GOOGLE_MAPS_API_KEY)
    }
}
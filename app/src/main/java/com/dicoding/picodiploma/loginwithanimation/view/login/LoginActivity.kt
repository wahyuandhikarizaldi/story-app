package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val viewModelFactory = ViewModelFactory.getInstance(this@LoginActivity)
            viewModel = ViewModelProvider(this@LoginActivity, viewModelFactory).get(LoginViewModel::class.java)
        }

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            if (checkInput(email, password)) {
                viewModel.login(email, password).observe(this) { user ->
                    if (user != null) {
                        viewModel.saveSession(UserModel(user.userId, user.token, true))
                        showSuccessDialog()
                    } else {
                        showErrorDialog()
                    }
                }
            }
        }
    }

    private fun checkInput(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edLoginEmail.error = getString(R.string.invalid_email)
            isValid = false
        }
        if (password.isEmpty() || password.length < 8) {
            binding.edLoginPassword.error = getString(R.string.invalid_password)
            isValid = false
        }
        return isValid
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Hooray!")
            setMessage("You have logged in. Let's share your story!")
            setPositiveButton("OK") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Oh no!")
            setMessage("Login failed. Make sure your email and password are correct!.")
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, "alpha", 0f, 1f).setDuration(100)
        val descTextView = ObjectAnimator.ofFloat(binding.descTextView, "alpha", 0f, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, "alpha", 0f, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.edLoginEmail, "alpha", 0f, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, "alpha", 0f, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.edLoginPassword, "alpha", 0f, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, "alpha", 0f, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                descTextView,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

}

package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.api.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.repository.SignupRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupRepository: SignupRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = applicationContext.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN_KEY", "") ?: ""
        signupRepository = SignupRepository(token)

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
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            lifecycleScope.launch {
                try {
                    val response = signupRepository.register(name, email, password)
                    val message = response.message
                    if (!response.error) {
                        showAlertDialog(
                            title = R.string.app_name,
                            errorMessage = "Your account has been created. Ready to share your story?",
                            type = DialogType.SUCCESS,
                            icon = R.drawable.outline_how_to_reg_24,
                            doAction = {
                                finish()
                            }
                        )
                    } else {
                        showAlertDialog(
                            title = R.string.app_name,
                            errorMessage = message,
                            type = DialogType.ERROR,
                            icon = R.drawable.baseline_error_outline_24,
                            doAction = {}
                        )
                    }
                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    val errorMessage = errorBody.message
                    showAlertDialog(
                        title = R.string.app_name,
                        errorMessage = errorMessage ?: "An unexpected error occurred.",
                        type = DialogType.ERROR,
                        icon = R.drawable.baseline_error_outline_24,
                        doAction = {}
                    )
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, "translationY", -30f, 50f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, "alpha", 0f, 1f).setDuration(100)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, "alpha", 0f, 1f).setDuration(100)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.edRegisterName, "alpha", 0f, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, "alpha", 0f, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.edRegisterEmail, "alpha", 0f, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, "alpha", 0f, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.edRegisterPassword, "alpha", 0f, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, "alpha", 0f, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }

    enum class DialogType {
        ERROR,
        SUCCESS
    }

    private fun showAlertDialog(
        title: Int,
        errorMessage: String,
        icon: Int,
        type: DialogType,
        doAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(errorMessage)
            setIcon(icon)
        }

        val alertDialog: AlertDialog = builder.create().apply {
            setCancelable(false)
            show()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            when (type) {
                DialogType.ERROR -> {}
                DialogType.SUCCESS -> doAction()
            }
            alertDialog.dismiss()
        }, DELAY_TIME)
    }

    companion object {
        private const val DELAY_TIME = 3000L
    }
}

package com.example.storyapp.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.MainActivity
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.LoginResponse
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.model.User
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference
import com.example.storyapp.model.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    private fun setupAction() {

        binding.mybutton1.setOnClickListener {
            showLoading(true)
            val email = binding.EditText1.text.toString().trim()
            val password = binding.EditText2.text.toString().trim()
            when {
                email.isEmpty() -> {
                    binding.EditText1.error = "Masukkan email"
                    Toast.makeText(
                        applicationContext,
                        "Masukan Email",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
                password.isEmpty() -> {
                    binding.EditText2.error = "Masukkan password"
                    Toast.makeText(
                        applicationContext,
                        "Masukan Password",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
                else -> {
                    val login = ApiConfig().getApiService().login(email, password)
                    login.enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful && response.body()?.loginResult != null) {
                                val responseBody = response.body()?.loginResult
                                val token = responseBody?.token.toString()
                                val userId = responseBody?.userId.toString()
                                val name = responseBody?.name.toString()
                                loginViewModel.saveToken(User(userId, name, token, false))
                                loginViewModel.login()
                                Log.d("UWU", token)
                                showLoading(false)
                                AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle("Yeah!")
                                    setMessage("Anda berhasil login.")
                                    setPositiveButton("Lanjut") { _, _ ->
                                        val intent = Intent(context, MainActivity::class.java)
                                        val bundle = Bundle()
                                        bundle.putString("token", token)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            } else {
                                showLoading(false)
                                Toast.makeText(
                                    applicationContext,
                                    "Email atau Password Salah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            showLoading(false)
                            Toast.makeText(
                                applicationContext,
                                t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.textView3, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.EditText1, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.EditText2, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.mybutton1, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 500
        }.start()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}
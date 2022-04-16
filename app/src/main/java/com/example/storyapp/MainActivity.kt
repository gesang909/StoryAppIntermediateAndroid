package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.StoryResponse
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.detail.DetailStoryActivity
import com.example.storyapp.model.StoryModel
import com.example.storyapp.model.User
import com.example.storyapp.model.UserPreference
import com.example.storyapp.model.ViewModelFactory
import com.example.storyapp.upload.UploadActivity
import com.example.storyapp.welcome.WelcomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter

    private lateinit var user: User
//    private var token: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        manager = LinearLayoutManager(this)

        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]
        mainViewModel.getToken().observe(this) { userToken ->
            this.user = userToken
            getAllStories(userToken.token)
            setupAction(userToken.token)
            if (user.isLogin) {
                binding.textView.text = user.name
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun getAllStories(token: String) {
        showLoading(true)
        Log.d("TOKEN", token)
        val stories = ApiConfig().getApiService().fetchPosts("Bearer $token")
        stories.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("Berhasil", response.message())
                    val responseBody = response.body()
                    recyclerView = binding.recycleView.apply {
                        if (responseBody != null) {
                            storyAdapter = StoryAdapter(responseBody.listStory)
                            storyAdapter.setOnItemClickCallback(object :
                                StoryAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: StoryModel) {
                                    Intent(
                                        this@MainActivity,
                                        DetailStoryActivity::class.java
                                    ).also {
                                        it.apply {
                                            val userdata = Bundle()
                                            userdata.putString("id", data.id)
                                            userdata.putString("name", data.name)
                                            userdata.putString("photoUrl", data.photoUrl)
                                            userdata.putString("description", data.description)
                                            putExtras(userdata)
                                        }
                                        startActivity(it)
                                    }
                                }
                            })
                        }
                        layoutManager = manager
                        adapter = storyAdapter
                        showLoading(false)
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupAction(token: String) {
        binding.buttonlogout.setOnClickListener {
            showLoading(true)
            mainViewModel.logout()
        }

        binding.buttonupload.setOnClickListener {
            Log.d("TOKEN FOR UPOAD", token)
            val tokenforupload = Bundle()
            tokenforupload.putString("token", token)
            val intent = Intent(this, UploadActivity::class.java)
            intent.putExtras(tokenforupload)
            startActivity(intent)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}


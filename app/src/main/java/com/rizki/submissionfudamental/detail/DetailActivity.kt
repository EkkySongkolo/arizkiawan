package com.rizki.submisionandroidfudamental.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rizki.submisionandroidfudamental.R
import com.rizki.submisionandroidfudamental.data.local.DbModule
import com.rizki.submisionandroidfudamental.data.model.Item
import com.rizki.submisionandroidfudamental.data.model.ResponseDetailUser
import com.rizki.submisionandroidfudamental.databinding.ActivityDetailBinding
import com.rizki.submisionandroidfudamental.detail.follow.FollowsFragment
import com.rizki.submisionandroidfudamental.utils.Result

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModel.Factory(DbModule(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()

        val item = intent.getParcelableExtra<Item>("item")
        val username = item?.login ?: ""

        viewModel.resultDetailUser.observe(this) {
            when (it) {
                is Result.Success<*> -> {
                    val user = it.data as ResponseDetailUser
                    binding.imageUser.load(user.avatar_url) {
                        transformations(CircleCropTransformation())
                    }

                    binding.username.text = user.name

                }

                is Result.Error -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
            }
        }
        viewModel.getDetailUser(username)

        val fragments = mutableListOf<Fragment>(
            FollowsFragment.newInstance(FollowsFragment.FOLLOWERS),
            FollowsFragment.newInstance(FollowsFragment.FOLLOWINGS)
        )

        val tittleFragment = mutableListOf(
            getString(R.string.followers),
            getString(R.string.following)
        )

//        viewModel.resultFollowerslUser.observe(this) {
//            when (it) {
//                is Result.Success<*> -> {
//                    val followers = (it.data as List<*>).size
//                    binding.tvJmlFollowers.text = followers.toString()
//                }
//
//                is Result.Error -> {
//                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
//                }
//
//                is Result.Loading -> {
//                }
//            }
//        }

//        viewModel.resultFollowinglUser.observe(this) {
//            when (it) {
//                is Result.Success<*> -> {
//                    val following = (it.data as List<*>).size
//                    binding.tvJmlFollowing.text = following.toString()
//                }
//
//                is Result.Error -> {
//                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
//                }
//
//                is Result.Loading -> {
//                }
//            }
//        }

        viewModel.resultDetailUser.observe(this) {
            when(it) {
                is Result.Success<*> -> {
                    val user = it.data as? ResponseDetailUser
                    user?.let {
                        binding.imageUser.load(it.avatar_url) {
                            transformations(CircleCropTransformation())
                        }
                        binding.username.text = it.name
                        binding.tvJmlFollowers.text = it.followers.toString()
                        binding.tvJmlFollowing.text = it.following.toString()
                    }
                }

                is Result.Error -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
            }
        }

        viewModel.getFollowing(username)
        viewModel.getFollowers(username)

        viewModel.resultDetailUser.observe(this) {
            when (it) {
                is Result.Success<*> -> {
                    val user = it.data as ResponseDetailUser
                    binding.imageUser.load(user.avatar_url) {
                        transformations(CircleCropTransformation())
                    }
                    binding.username.text = user.login
                    binding.tvName.text   = user.name
                }

                is Result.Error -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
            }
        }


        val adapter = DetailAdapter(this, fragments)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
            tab.text = tittleFragment[position]
        }.attach()

        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    viewModel.getFollowers(username)
                } else {
                    viewModel.getFollowing(username)
                }
            }
        })

        viewModel.getFollowers(username)

        viewModel.resultSuccessFavorite.observe(this) {
            binding.btnFavorite.changeIconColor(R.color.red)
        }

        viewModel.resultDeleteFavorite.observe(this) {
            binding.btnFavorite.changeIconColor(R.color.white)
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.setFavorite(item)
        }

        viewModel.findFavorite(item?.id ?: 0) {
            binding.btnFavorite.changeIconColor(R.color.red)
        }

        fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> {
                    finish()
                }
            }
            return super.onOptionsItemSelected(item)
        }

        if (username.isNotEmpty()) {
            viewModel.getFollowers(username)
        }
    }
}

fun FloatingActionButton.changeIconColor(@ColorRes color: Int) {
    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, color))
}
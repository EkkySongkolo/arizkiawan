package com.rizki.submisionandroidfudamental.detail.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizki.submisionandroidfudamental.data.model.Item
import com.rizki.submisionandroidfudamental.databinding.FragmentFollowsBinding
import com.rizki.submisionandroidfudamental.detail.DetailViewModel
import com.rizki.submisionandroidfudamental.ui.UserAdapter
import com.rizki.submisionandroidfudamental.utils.Result

class FollowsFragment : Fragment() {

    private var binding: FragmentFollowsBinding? = null
    private val adapter by lazy {
        UserAdapter {

        }
    }
    private val viewModel by activityViewModels<DetailViewModel>()
    private var type = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.rvFollows?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@FollowsFragment.adapter
        }

        when (type) {
            FOLLOWINGS -> {
                viewModel.resultFollowinglUser.observe(viewLifecycleOwner, this::manageResultFollows)
            }
            FOLLOWERS -> {
                viewModel.resultFollowerslUser.observe(viewLifecycleOwner, this::manageResultFollows)
            }
        }
    }

    private fun manageResultFollows(state: Result) {
        when (state) {
            is Result.Success<*> -> {
                adapter.setData(state.data as MutableList<Item>)
            }
            is Result.Error -> {
                Toast.makeText(
                    requireContext(),
                    state.exception.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
            is Result.Loading -> {
                binding?.progressBar?.isVisible = state.isLoading
            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }

    companion object {
        const val FOLLOWINGS = 100
        const val FOLLOWERS  = 101

        fun newInstance(type: Int) = FollowsFragment()
            .apply {
                this.type = type
        }
    }
}
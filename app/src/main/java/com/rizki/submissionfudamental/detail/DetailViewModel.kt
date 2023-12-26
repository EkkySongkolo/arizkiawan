package com.rizki.submisionandroidfudamental.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rizki.submisionandroidfudamental.data.local.DbModule
import com.rizki.submisionandroidfudamental.data.model.Item
import com.rizki.submisionandroidfudamental.data.remoteUser.ApiClient
import com.rizki.submisionandroidfudamental.utils.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel(private val db:DbModule) : ViewModel() {
    val resultDetailUser = MutableLiveData<Result>()
    val resultFollowerslUser = MutableLiveData<Result>()
    val resultFollowinglUser = MutableLiveData<Result>()

    val resultSuccessFavorite = MutableLiveData<Boolean>()
    val resultDeleteFavorite  = MutableLiveData<Boolean>()

    private var isFavorite = false

    fun setFavorite(item: Item?) {
        viewModelScope.launch {
            item?.let {
                if (isFavorite) {
                    db.userDao.delete(item)
                    resultDeleteFavorite.value = true
                } else {
                    db.userDao.insert(item)
                    resultSuccessFavorite.value = true
                }
            }
            isFavorite = !isFavorite
        }
    }

    fun findFavorite(id: Int, listenFavorites: () -> Unit) {
        viewModelScope.launch {
            val user = db.userDao.findById(id)
            if (user != null) {
                listenFavorites()
                isFavorite = true
            }
        }
    }

    fun getDetailUser(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getDetailUserGithub(username)

                emit(response)
            }
                .onStart {
                    resultDetailUser.value = Result.Loading(true)
                }
                .onCompletion {
                    resultDetailUser.value = Result.Loading(false)
                }
                .catch {
                    it.printStackTrace()
                    resultDetailUser.value = Result.Error(it)
                }
                .collect {
                    resultDetailUser.value = Result.Success(it)
                }

        }
    }

    fun getFollowing(username: String) {

        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getFollowingUserGithub(username)

                emit(response)
            }
                .onStart {
                    resultFollowinglUser.value = Result.Loading(true)
                }
                .onCompletion {
                    resultFollowinglUser.value = Result.Loading(false)
                }
                .catch {
                    it.printStackTrace()
                    resultFollowinglUser.value = Result.Error(it)
                }
                .collect {
                    resultFollowinglUser.value = Result.Success(it)
                }

        }
    }

    fun getFollowers(username: String) {

        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getFollowersUserGithub(username)

                emit(response)
            }
                .onStart {
                    resultFollowerslUser.value = Result.Loading(true)
                }
                .onCompletion {
                    resultFollowerslUser.value = Result.Loading(false)
                }
                .catch {
                    it.printStackTrace()
                    resultFollowerslUser.value = Result.Error(it)
                }
                .collect {
                    resultFollowerslUser.value = Result.Success(it)
                }

        }
    }

    class Factory(private val db: DbModule) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailViewModel(db) as T
    }
}
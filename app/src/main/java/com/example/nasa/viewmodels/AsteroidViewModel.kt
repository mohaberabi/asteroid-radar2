package com.example.nasa.viewmodels

import android.app.Application
import android.media.Image
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.nasa.database.getDataBase
import com.example.nasa.database.toAsteroid
import com.example.nasa.model.Asteroid
import com.example.nasa.model.ImageOfDay
import com.example.nasa.repository.AsteroidRepository
import com.example.nasa.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class FilterEnum {
    ALL,
    TODAY,
    WEEK,
    FAVORITE;
}

class AsteroidViewModel(app: Application) : AndroidViewModel(app) {


    private val database = getDataBase(app)
    private val repository = AsteroidRepository(database)


    private val _choosedFilter = MutableLiveData<FilterEnum>(FilterEnum.TODAY)

    private val _goToImageOfDay = MutableLiveData<Boolean>(false)
    val goToImageOfDay: LiveData<Boolean>
        get() = _goToImageOfDay


    fun onImageOfDayPressed() {
        _goToImageOfDay.value = true
    }

    fun resetNavToImageOfDay() {
        _goToImageOfDay.value = false
    }

    private val _imageOfTheDay = MutableLiveData<ImageOfDay>(ImageOfDay.empty)
    val imageOfTheDay: LiveData<ImageOfDay>
        get() = _imageOfTheDay


    val asteroids: LiveData<List<Asteroid>> = _choosedFilter.switchMap { filter ->
        when (filter) {
            FilterEnum.TODAY -> repository.asteroidOfToday
            FilterEnum.WEEK -> repository.asteroidOfWeek
            FilterEnum.FAVORITE -> repository.favoritesAsteroid

            else -> {
                repository.asteroids
            }
        }
    }


    init {

        viewModelScope.launch {
            repository.refreshAsteroids()

        }
        getImageOfTheDay()


    }

    private fun getImageOfTheDay() {
        viewModelScope.launch {
            val img = repository.refreshImageOfDay()
            _imageOfTheDay.value = img
        }
    }


    fun updateAsteroidFavorite(id: Long, isFav: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {


            database.asteroidDao.updateAsteroidFavorite(id, isFav)
        }
    }

    fun onFilterRequested(filter: FilterEnum) {


        _choosedFilter.value = filter
    }


    class Factory(private val app: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AsteroidViewModel::class.java)) {
                return AsteroidViewModel(app) as T
            }
            throw IllegalArgumentException("ERROR CREATING THE VIEWMODEL")
        }
    }

}


package com.yaundecode.examenadopcionapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yaundecode.examenadopcionapp.models.Dog
import com.yaundecode.examenadopcionapp.models.ListAllBreeds
import com.yaundecode.examenadopcionapp.models.RandomDogImage
import com.yaundecode.examenadopcionapp.service.ActivityServiceApiBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

class DogsViewModel : ViewModel() {
    val dogList: MutableLiveData<List<Dog>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()


    fun loadDogs() {
        val dogService = ActivityServiceApiBuilder.create()
        dogService.getAllBreeds().enqueue(object : Callback<ListAllBreeds> {
            override fun onResponse(call: Call<ListAllBreeds>, response: Response<ListAllBreeds>) {
                if (response.isSuccessful) {
                    val breeds = response.body()?.breeds
                    if (breeds != null) {

                        val newDogList = mutableListOf<Dog>()


                        for ((breed, subBreeds) in breeds.entries.take(20)) {
                            for (subBreed in subBreeds) {
                                dogService.getRandomDogImage(breed).enqueue(object :
                                    Callback<RandomDogImage> {
                                    override fun onResponse(call: Call<RandomDogImage>, response: Response<RandomDogImage>) {
                                        if (response.isSuccessful) {
                                            val imageUrl = response.body()?.imageUrl
                                            if (imageUrl != null) {
                                                val random = Random()
                                                val age = random.nextInt(10) + 1
                                                val gender = if (random.nextBoolean()) "Male" else "Female"
                                                val favorite = random.nextBoolean()
                                                val weight = random.nextDouble() * (25 - 4) + 4
                                                val names = arrayOf("Woody", "Lito", "Pepa", "Mou", "Toto", "Rocio", "Alegria", "Firulais", "Tommy", "Roco", "Rosita", "Negro", "Gomez", "Churchill")
                                                val name = names.random()
                                                val state = if (random.nextBoolean()) "in adoption" else "adopted"
                                                newDogList.add(
                                                    Dog(
                                                        saved = false,
                                                        image = imageUrl,
                                                        name = name,
                                                        breed = breed,
                                                        subBreed = subBreeds,
                                                        age = age,
                                                        gender = gender,
                                                        publishedDate = "2023_08_23",
                                                        favorite = favorite,
                                                        weight = weight,
                                                        location = "Ciudad de buenos Aires",
                                                        description = "Descripcion generica",
                                                        state = state
                                                    )
                                                )
                                                dogList.value = newDogList
                                            }
                                        } else {
                                            errorMessage.value = "Error al obtener todas las razas: ${response.errorBody()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<RandomDogImage>, t: Throwable) {
                                        errorMessage.value = "Fallo al obtener todas las razas: ${t.message}"
                                    }
                                })
                            }
                        }
                    }
                } else {
                    errorMessage.value = "Error al obtener todas las razas: ${response.errorBody()}"
                }
            }

            override fun onFailure(call: Call<ListAllBreeds>, t: Throwable) {
                errorMessage.value = "Fallo al obtener todas las razas: ${t.message}"
            }
        })
    }
}

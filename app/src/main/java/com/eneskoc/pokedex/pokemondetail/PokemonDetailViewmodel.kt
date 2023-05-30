package com.eneskoc.pokedex.pokemondetail

import androidx.lifecycle.ViewModel
import com.eneskoc.pokedex.data.remote.responses.Pokemon
import com.eneskoc.pokedex.repository.PokemonRepository
import com.eneskoc.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewmodel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName:String):Resource<Pokemon>{
        return  repository.getPokemonInfo(pokemonName)
    }

}
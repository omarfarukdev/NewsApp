package com.example.newsapp

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.usecases.AppEntryUseCase
import com.example.newsapp.presentation.nvgraph.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCase: AppEntryUseCase
): ViewModel() {
     var splashCondition by mutableStateOf(true)
         private set
    //val splashCondition: State<Boolean> = _splashCondition
    var startDestination by mutableStateOf(Route.AppStartNavigation.route)
        private set
    init {
        appEntryUseCase.readAppEntry().onEach { shouldStartFromHomeScreen->
            if(shouldStartFromHomeScreen){
                startDestination = Route.NewsNavigation.route
            }else{
                startDestination = Route.AppStartNavigation.route
            }
            delay(300)
            splashCondition= false
        }.launchIn(viewModelScope)
    }
}
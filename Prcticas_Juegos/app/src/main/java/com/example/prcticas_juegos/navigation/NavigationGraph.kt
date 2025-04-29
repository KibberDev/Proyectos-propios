package com.example.prcticas_juegos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.prcticas_juegos.screens.MenuScreen
import com.example.prcticas_juegos.screens.TresEnRayaScreen


@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "Menu"){
        composable("Menu"){ MenuScreen(navController) }
        composable("tres_en_raya"){ TresEnRayaScreen(navController) }

    }

}
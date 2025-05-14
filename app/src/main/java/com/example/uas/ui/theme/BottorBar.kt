package com.example.uas.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.uas.DataModels.NavRoutes

@Composable
fun BottomBar(navController: NavHostController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Beranda") },
            selected = currentRoute == NavRoutes.HOME,
            onClick = {
                if (currentRoute != NavRoutes.HOME)
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, null) },
            label = { Text("Setoran") },
            selected = currentRoute == NavRoutes.SETORAN,
            onClick = {
                if (currentRoute != NavRoutes.SETORAN)
                    navController.navigate(NavRoutes.SETORAN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profil") },
            selected = currentRoute == NavRoutes.PROFIL,
            onClick = {
                if (currentRoute != NavRoutes.PROFIL)
                    navController.navigate(NavRoutes.PROFIL) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
            }
        )
    }
}

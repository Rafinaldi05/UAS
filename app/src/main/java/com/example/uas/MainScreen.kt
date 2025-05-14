package com.example.uas

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.uas.ui.theme.*
import com.example.uas.ui.theme.SetoranItemCard

@Composable
fun MainScreen(
    nama: String,
    nim: String,
    setoranList: List<DataModels.SetoranItem>,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: DataModels.NavRoutes.HOME

    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomBar(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DataModels.NavRoutes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DataModels.NavRoutes.HOME) {
                HomeScreen(nama, nim, setoranList)
            }
            composable(DataModels.NavRoutes.SETORAN) {
                SetoranScreen(setoranList = setoranList)
            }
            composable(DataModels.NavRoutes.PROFIL) {
                ProfilScreen(nama = nama, nim = nim, onLogout = onLogout)
            }
        }
    }
}

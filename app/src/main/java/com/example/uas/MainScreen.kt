package com.example.uas

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.uas.ui.theme.*
import com.example.uas.NavRoutes

@Composable
fun MainScreen(
    mahasiswaInfo: DataModels.MahasiswaInfo,
    setoranList: List<DataModels.SetoranItem>,
    ringkasanList: List<DataModels.RingkasanItem>,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: NavRoutes.HOME

    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomBar(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.HOME) {
                HomeScreen(
                    mahasiswaInfo = mahasiswaInfo,
                    setoranList = setoranList,
                    ringkasanList = ringkasanList
                )
            }

            composable(NavRoutes.SETORAN) {
                SetoranScreen(
                    setoranList = setoranList,
                    mahasiswaInfo = mahasiswaInfo
                )
            }

            composable(NavRoutes.PROFIL) {
                ProfilScreen(
                    mahasiswaInfo = mahasiswaInfo,
                    onLogout = onLogout
                )
            }
        }
    }
}

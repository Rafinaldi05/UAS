package com.example.uas

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.uas.ui.theme.*

@Composable
fun MainScreen(
    nama: String,
    nim: String,
    email: String,
    angkatan: String,
    semester: Int,
    dosenPa: String,
    nipPa: String,
    emailPa: String,
    setoranList: List<DataModels.SetoranItem>,
    ringkasanList: List<DataModels.RingkasanItem>,
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
                HomeScreen(nama = nama, nim = nim, setoranList = setoranList, ringkasanList = ringkasanList)
            }
            composable(DataModels.NavRoutes.SETORAN) {
                SetoranScreen(
                    setoranList = setoranList,
                    nama = nama,
                    nim = nim,
                    dosenPa = dosenPa
                )
            }

            composable(DataModels.NavRoutes.PROFIL) {
                ProfilScreen(
                    nama = nama,
                    nim = nim,
                    email = email,
                    angkatan = angkatan,
                    semester = semester,
                    dosenPaNama = dosenPa,
                    dosenPaNip = nipPa,
                    dosenPaEmail = emailPa,
                    onLogout = onLogout
                )
            }
        }
    }
}

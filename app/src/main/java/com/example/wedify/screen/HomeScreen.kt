package com.example.wedify.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wedify.pages.CartPage
import com.example.wedify.pages.HomePage
import com.example.wedify.pages.ProfilePage
import com.example.wedify.pages.TransactionPage
import com.example.wedify.ui.theme.pinkbut
import com.example.wedify.ui.theme.poppinsFont

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val navItemList = listOf(
        NavItem("BERANDA", Icons.Default.Home),
        NavItem("KERANJANG", Icons.Default.ShoppingCart),
        NavItem("TRANSKASI", Icons.AutoMirrored.Filled.LibraryBooks),
        NavItem("PROFIL", Icons.Default.Person),
    )

    var selectedIndex by rememberSaveable {
        mutableStateOf(0)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label
                            )
                        },
                        label = {
                            Text(
                                text = navItem.label,
                                fontSize = 12.sp,
                                fontFamily = poppinsFont,
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = pinkbut,
                            selectedTextColor = pinkbut,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .navigationBarsPadding(),
            selectedIndex = selectedIndex,
            navController = navController // ← kirim navController
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavHostController // ← hanya ditambahkan ini
) {
    when (selectedIndex) {
        0 -> HomePage(modifier)
        1 -> CartPage(modifier)
        2 -> TransactionPage(navController = navController, modifier = modifier) // ← perbaikan di sini
        3 -> ProfilePage(modifier)
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)

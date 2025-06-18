package com.example.wedify

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wedify.pages.CategoryProductPage
import com.example.wedify.pages.CheckOutPage
import com.example.wedify.pages.ProductDetailsPage
import com.example.wedify.screen.AuthScreen
import com.example.wedify.screen.EditProfileScreen
import com.example.wedify.screen.HomeScreen
import com.example.wedify.screen.LoginScreen
import com.example.wedify.screen.PaymentScreen
import com.example.wedify.screen.SignupScreen
import com.example.wedify.screen.SplashScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggeidn = Firebase.auth.currentUser != null

    val firstPage = "splash" // ← ganti startDestination menjadi splash

    NavHost(navController = navController, startDestination = firstPage) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("auth") {
            AuthScreen(modifier, navController)
        }
        composable("login") {
            LoginScreen(modifier, navController)
        }
        composable("signup") {
            SignupScreen(modifier, navController)
        }

        composable("checkout") {
            CheckOutPage(modifier, navController)
        }

        composable("home") {
            HomeScreen(modifier, navController)
        }
        composable("category-products/{categoryId}") {
            val categoryId = it.arguments?.getString("categoryId")
            CategoryProductPage(modifier, categoryId ?: "")
        }
        composable("product-details/{productId}") {
            val productId = it.arguments?.getString("productId")
            ProductDetailsPage(modifier, productId ?: "")
        }
        composable("payment/{bookingId}") { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            PaymentScreen(navController, bookingId)
        }
        composable("editProfile") {
            EditProfileScreen()
        }
    }
}

object GlobalNavigation {
    lateinit var navController: NavHostController
}

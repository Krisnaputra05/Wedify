package com.example.wedify

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wedify.pages.*
import com.example.wedify.profile.FaqPage
import com.example.wedify.profile.KebijakanPage
import com.example.wedify.profile.LayananPage
import com.example.wedify.profile.SyaratPage
import com.example.wedify.screen.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("auth") { AuthScreen(modifier, navController) }
        composable("login") { LoginScreen(modifier, navController) }
        composable("signup") { SignupScreen(modifier, navController) }
        composable("checkout/{productIds}") { backStackEntry ->
            val ids = backStackEntry.arguments?.getString("productIds") ?: ""
            val productIds = ids.split(",")
            CheckOutPage(modifier, navController, productIds)
        }
        composable("transaction") { TransactionPage(navController = navController) }
        composable("home") { HomeScreen(modifier, navController) }
        composable("home/{tabIndex}") { backStackEntry ->
            val tabIndex = backStackEntry.arguments?.getString("tabIndex")?.toIntOrNull() ?: 0
            HomeScreen(navController = navController, initialTabIndex = tabIndex)
        }

        composable("category-products/{categoryId}") {
            val categoryId = it.arguments?.getString("categoryId") ?: ""
            CategoryProductPage(modifier, categoryId)
        }
        composable("product-details/{productId}") {
            val productId = it.arguments?.getString("productId") ?: ""
            ProductDetailsPage(modifier, productId)
        }
        composable("payment/{bookingId}") {
            val bookingId = it.arguments?.getString("bookingId") ?: ""
            PaymentScreen(navController, bookingId)
        }
        composable("editProfile") { EditProfileScreen() }
        composable("search") { SearchPage(navController) }
        composable("vendor-dashboard") { VendorDashboardPage(navController) }
        composable("add-edit-page/{productId}") {
            val productId = it.arguments?.getString("productId")
            AddEditProductPage(navController = navController, productId = productId)
        }
        composable("add-edit-page") {
            AddEditProductPage(navController = navController, productId = null)
        }
        composable("vendor-product-detail/{productId}") {
            val productId = it.arguments?.getString("productId") ?: ""
            VendorProductDetailPage(navController, productId)
        }

        composable("verifikasi-booking") {
            VerifikasiBookingPage(navController)
        }
        composable("faq") {
            FaqPage(modifier, navController)
        }
        composable("layanan") {
            LayananPage(modifier, navController)
        }
        composable("kebijakan") {
            KebijakanPage(modifier, navController)
        }
        composable("syarat") {
            SyaratPage(modifier, navController)
        }

    }
}

object GlobalNavigation {
    lateinit var navController: NavHostController
}

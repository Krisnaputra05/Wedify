package com.example.wedify.pages

import CategoriesView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wedify.GlobalNavigation.navController
import com.example.wedify.components.BannerView
import com.example.wedify.components.FeaturedProductSectionView
import com.example.wedify.components.HeaderView



@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp)
            .navigationBarsPadding()
    ) {
        HeaderView(navController = navController)
        Spacer(modifier = Modifier.height(24.dp))
        BannerView()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Categories", style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        CategoriesView()
        Spacer(modifier = Modifier.height(24.dp))
        FeaturedProductSectionView(horizontalPadding = 0.dp)
    }
}
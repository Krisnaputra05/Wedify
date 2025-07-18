package com.example.wedify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wedify.ui.theme.pinkbut
import com.example.wedify.ui.theme.poppinsFont

@Composable
fun HeaderView(modifier: Modifier = Modifier, navController: NavController) {
    val searchText = remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Kolom search
        Row(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, pinkbut, RoundedCornerShape(24.dp))
                .background(Color(0xFFF6F6F6))
                .clickable {
                    navController.navigate("search")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchText.value,
                onValueChange = {},
                enabled = false, // supaya tidak bisa diketik, hanya untuk klik
                placeholder = {
                    Text(
                        text = "CARI",
                        fontFamily = poppinsFont,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = Color.Black
                ),
                singleLine = true
            )

            IconButton(
                onClick = { navController.navigate("search") },
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Black)
            }
        }
    }
}


package com.example.wedify.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wedify.ui.theme.pinkbut
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.wedify.ui.theme.poppinsFont


@Composable
fun NavActionBar(
    onChatClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White), // Sesuaikan background
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onChatClick,
            modifier = Modifier.padding(start = 16.dp).size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = "Chat",
                tint = pinkbut
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        IconButton(
            onClick = onCartClick,
            modifier = Modifier.padding(start = 16.dp).size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                tint = pinkbut
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Hanya tombol ini yang diperbesar agar full tinggi
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(160.dp)
                .background(pinkbut),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onOrderClick,
                modifier = Modifier.fillMaxSize(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White,
                    backgroundColor = Color.Transparent
                )
            ) {
                Text(
                    text = "Pesan Sekarang",
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = poppinsFont,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}




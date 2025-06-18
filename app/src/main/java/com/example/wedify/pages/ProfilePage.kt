package com.example.wedify.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wedify.GlobalNavigation.navController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val user = auth.currentUser

    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(user) {
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userData = document.data
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Text(
            text = "PROFIL",
            modifier = Modifier.padding(20.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userData?.get("username")?.toString() ?: "Belum Ada Username",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = "Edit Profil >",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.clickable {
                    navController.navigate("editProfile")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(title = "UMUM", items = listOf(
                ProfileMenuItem("Bahasa", "Indonesia", Icons.Default.Public),
                ProfileMenuItem("Kebijakan Privasi", "", Icons.Default.CheckCircle),
                ProfileMenuItem("Syarat Dan Ketentuan", "", Icons.Default.Info)
            ))

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSection(title = "BANTUAN", items = listOf(
                ProfileMenuItem("Layanan Pelanggan", "", Icons.Default.Call),
                ProfileMenuItem("FAQ", "", Icons.Default.HelpOutline)
            ))
        }
    }
}

data class ProfileMenuItem(
    val title: String,
    val value: String = "",
    val icon: ImageVector
)

@Composable
fun ProfileSection(title: String, items: List<ProfileMenuItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.title,
                            modifier = Modifier.weight(1f)
                        )
                        if (item.value.isNotEmpty()) {
                            Text(text = item.value, color = Color.Gray)
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                    if (index < items.lastIndex) {
                        Divider(color = Color.LightGray)
                    }
                }
            }
        }
    }
}

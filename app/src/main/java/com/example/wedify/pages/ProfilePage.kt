package com.example.wedify.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.wedify.GlobalNavigation.navController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val storage = Firebase.storage
    val user = auth.currentUser

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val blacklistKeys = listOf("password", "uid", "cartitems", "email")

    var editingKey by remember { mutableStateOf<String?>(null) }
    var editingValue by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val storageRef = storage.reference.child("profileImages/${user?.uid}.jpg")
            storageRef.putFile(it)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        db.collection("users").document(user!!.uid)
                            .update("photoUrl", downloadUrl.toString())
                            .addOnSuccessListener {
                                userData = userData?.toMutableMap()?.apply {
                                    this["photoUrl"] = downloadUrl.toString()
                                }
                            }
                    }
                }
        }
    }

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
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color(0xFFE91E63),
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val success = navController.popBackStack()
                        if (!success) {
                            navController.navigate("home") // fallback
                        }
                    }
            )

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Edit Profil",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Foto Profil
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (userData?.get("photoUrl") != null) {
                val painter = rememberAsyncImagePainter(userData?.get("photoUrl").toString())
                Image(
                    painter = painter,
                    contentDescription = "Foto Profil",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(60.dp),
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ubah Foto",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.clickable { launcher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                user?.email?.let {
                    ProfileFieldItem(
                        label = "Email",
                        value = it,
                        isEditing = false,
                        onClick = {},
                        onValueChange = {},
                        onSave = {}
                    )
                }

                userData?.forEach { (key, value) ->
                    if (key.lowercase() !in blacklistKeys) {
                        val isEditing = editingKey == key
                        ProfileFieldItem(
                            label = key.replaceFirstChar { it.uppercase() },
                            value = if (isEditing) editingValue else value.toString(),
                            isEditing = isEditing,
                            onClick = {
                                editingKey = key
                                editingValue = value.toString()
                            },
                            onValueChange = { editingValue = it },
                            onSave = {
                                val updates = hashMapOf<String, Any>(key to editingValue)
                                db.collection("users").document(user!!.uid)
                                    .update(updates as Map<String, Any>)
                                    .addOnSuccessListener {
                                        userData = userData?.toMutableMap()?.apply {
                                            this[key] = editingValue
                                        }
                                        editingKey = null
                                    }
                            }
                        )
                    }
                } ?: Text("Data tidak ditemukan")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                Firebase.auth.signOut()
                navController.navigate("auth") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Keluar")
        }
    }
}

@Composable
fun ProfileFieldItem(
    label: String,
    value: String,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onSave) {
                    Text("Simpan")
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                Text(text = value, color = Color.Black, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Edit",
                    tint = Color.Black
                )
            }
        }
    }
}

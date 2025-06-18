package com.example.wedify.screen

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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun EditProfileScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val storage = Firebase.storage
    val user = auth.currentUser

    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var showDialogForField by remember { mutableStateOf<String?>(null) }
    var tempInput by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            val storageRef = storage.reference.child("profileImages/${user?.uid}.jpg")
            storageRef.putFile(it).addOnSuccessListener {
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

    LaunchedEffect(Unit) {
        user?.let {
            db.collection("users").document(it.uid).get().addOnSuccessListener { doc ->
                userData = doc.data
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Edit Profil", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Foto
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
                Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize())
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(60.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Ubah Foto", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        val profileFields = listOf(
            "Username" to (userData?.get("username")?.toString() ?: ""),
            "Nama Lengkap" to (userData?.get("name")?.toString() ?: ""),
            "Gender" to (userData?.get("gender")?.toString() ?: ""),
            "Tanggal Lahir" to (userData?.get("birthdate")?.toString() ?: ""),
            "No. Handpone" to (userData?.get("telephone")?.toString() ?: ""),
            "Email" to (user?.email ?: "")
        )

        profileFields.forEach { (label, value) ->
            ProfileEditRow(label = label, value = value) {
                if (label != "Email") {
                    showDialogForField = label
                    tempInput = value
                }
            }
        }
    }

    showDialogForField?.let { fieldLabel ->
        AlertDialog(
            onDismissRequest = { showDialogForField = null },
            confirmButton = {
                TextButton(onClick = {
                    val fieldKey = when (fieldLabel) {
                        "Username" -> "username"
                        "Nama Lengkap" -> "name"
                        "Gender" -> "gender"
                        "Tanggal Lahir" -> "birthdate"
                        "No. Handpone" -> "telephone"
                        else -> null
                    }

                    if (fieldKey != null && fieldKey.isNotEmpty()) {
                        db.collection("users").document(user!!.uid)
                            .update(mapOf(fieldKey to tempInput))
                            .addOnSuccessListener {
                                userData = userData?.toMutableMap()?.apply {
                                    this[fieldKey] = tempInput
                                }
                                showDialogForField = null
                            }
                    }

                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogForField = null }) {
                    Text("Batal")
                }
            },
            title = { Text("Edit $fieldLabel") },
            text = {
                OutlinedTextField(
                    value = tempInput,
                    onValueChange = { tempInput = it },
                    singleLine = true,
                    label = { Text("Masukkan $fieldLabel") }
                )
            }
        )
    }
}

@Composable
fun ProfileEditRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Text(text = value, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(">", color = Color.Gray)
    }
}

package com.example.wedify.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import androidx.compose.material3.TextFieldDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val storage = Firebase.storage
    val user = auth.currentUser

    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var showDialogForField by remember { mutableStateOf<String?>(null) }
    var tempInput by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            isUploading = true
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
                                isUploading = false
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Failed to update Firestore: ${e.message}")
                                isUploading = false
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseStorage", "Upload failed: ${e.message}")
                    isUploading = false
                }
        }
    }

    // Load user data
    LaunchedEffect(Unit) {
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { doc ->
                    userData = doc.data
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to load user data: ${e.message}")
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = pinkbut
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = pinkbut
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = pinkbut, strokeWidth = 3.dp)
                } else {
                    val photoUrl = userData?.get("photoUrl")?.toString()
                    if (!photoUrl.isNullOrEmpty()) {
                        val painter = rememberAsyncImagePainter(photoUrl)
                        Image(
                            painter = painter,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Ubah Foto", fontSize = 14.sp, color = pinkbut)

            Spacer(modifier = Modifier.height(24.dp))

            val profileFields = listOf(
                "Username" to (userData?.get("username")?.toString() ?: ""),
                "Nama Lengkap" to (userData?.get("name")?.toString() ?: ""),
                "Gender" to (userData?.get("gender")?.toString() ?: ""),
                "Tanggal Lahir" to (userData?.get("birthdate")?.toString() ?: ""),
                "No. Handphone" to (userData?.get("telephone")?.toString() ?: ""),
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
    }

    // Dialog edit
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
                        "No. Handphone" -> "telephone"
                        else -> null
                    }

                    if (!fieldKey.isNullOrEmpty()) {
                        db.collection("users").document(user!!.uid)
                            .update(fieldKey, tempInput)
                            .addOnSuccessListener {
                                userData = userData?.toMutableMap()?.apply {
                                    this[fieldKey] = tempInput
                                }
                                showDialogForField = null
                            }
                            .addOnFailureListener {
                                Log.e("Firestore", "Gagal update field $fieldKey: ${it.message}")
                            }
                    }
                }) {
                    Text("Simpan", color = pinkbut)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogForField = null }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            title = { Text("Edit $fieldLabel") },
            text = {
                OutlinedTextField(
                    value = tempInput,
                    onValueChange = { tempInput = it },
                    singleLine = true,
                    label = { Text("Masukkan $fieldLabel") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = pinkbut,
                        cursorColor = pinkbut
                    )
                )
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun ProfileEditRow(label: String, value: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF9F9F9),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Text(text = value, fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

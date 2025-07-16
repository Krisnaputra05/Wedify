package com.example.wedify.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wedify.GlobalNavigation
import com.example.wedify.components.CartItemView
import com.example.wedify.components.HorizontalLine
import com.example.wedify.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.wedify.ui.theme.pinkbut // gunakan pink yang kamu sudah definisikan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(modifier: Modifier = Modifier) {
    val userModel = remember { mutableStateOf(UserModel()) }

    val selectedItemsState = remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    DisposableEffect(key1 = Unit) {
        val listener = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val result = snapshot.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result
                        val defaultSelection = result.cartItems.mapValues { false }
                        selectedItemsState.value = defaultSelection
                    }
                }
            }

        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Keranjang",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        val cartItems = userModel.value.cartItems

        if (cartItems.isEmpty()) {
            Text(text = "Keranjang kamu kosong.")
        } else {
            Column {
                cartItems.toList().reversed().forEach { (productId, quantity) ->
                    val isSelected = selectedItemsState.value[productId] ?: false

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                selectedItemsState.value = selectedItemsState.value.toMutableMap().apply {
                                    this[productId] = checked
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = pinkbut,
                                uncheckedColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        CartItemView(productId = productId, quantity = quantity)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selectedIds = selectedItemsState.value.filter { it.value }.map { it.key }

        Button(
            onClick = {
                val ids = selectedIds.joinToString(",")
                GlobalNavigation.navController.navigate("checkout/$ids")
            },
            enabled = selectedIds.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = pinkbut,
                contentColor = Color.White
            )
        ) {
            Text(text = "Checkout (${selectedIds.size})")
        }
    }
}

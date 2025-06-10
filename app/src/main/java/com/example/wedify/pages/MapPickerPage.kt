package com.example.wedify.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.wedify.components.LocationPickerMap
import com.google.android.gms.maps.model.LatLng


@Composable
fun MapPickerPage(
    onLocationPicked: (LatLng, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Pilih Lokasi", modifier = Modifier.padding(16.dp))

        LocationPickerMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onLocationSelected = { latLng ->
                selectedLatLng = latLng
            }
        )

        Button(
            onClick = {
                selectedLatLng?.let {
                    val address = getAddressFromLatLng(context, it)
                    onLocationPicked(it, address)
                }
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Pilih Lokasi Ini")
        }
    }
}

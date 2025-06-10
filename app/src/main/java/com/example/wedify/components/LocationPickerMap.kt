package com.example.wedify.components

// Import
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
@Composable
fun LocationPickerMap(
    modifier: Modifier = Modifier, onLocationSelected: (LatLng) -> Unit
) {
    // Awal kamera (bisa kamu ubah ke lokasi Indonesia misalnya Jakarta)
    val initialPosition = LatLng(-8.409518, 115.188919) // Bali
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    // State untuk Marker (bisa dibuat lebih advance pakai remember mutableStateOf)
    val selectedMarker = remember { mutableStateOf<LatLng?>(null) }

    GoogleMap(
        modifier = modifier, cameraPositionState = cameraPositionState, onMapClick = { latLng ->
            // Update marker
            selectedMarker.value = latLng
            // Callback ke pemanggil
            onLocationSelected(latLng)
        }) {
        selectedMarker.value?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Lokasi Terpilih"
            )

        }
    }
}

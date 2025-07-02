package com.example.wedify.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wedify.GlobalNavigation.navController

@Composable
fun KebijakanPage(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFE91E63),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Kebijakan Privasi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            KebijakanCard(
                title = "Kebijakan Privasi Wedify",
                content = buildAnnotatedString {
                    append("Privasi Anda adalah prioritas kami. Wedify berkomitmen untuk melindungi data pribadi pengguna yang dikumpulkan selama penggunaan aplikasi. Dengan menggunakan Wedify, Anda menyetujui pengumpulan dan penggunaan data sebagaimana dijelaskan di sini.\n\n")

                    bold("1. Informasi yang Kami Kumpulkan")
                    append("Kami mengumpulkan data pribadi seperti nama, email, nomor telepon, tanggal dan lokasi pernikahan, preferensi paket, serta data teknis perangkat (IP, jenis perangkat, aktivitas). Pesan, ulasan, dan interaksi dengan vendor juga dapat dicatat untuk keperluan pelayanan.\n\n")

                    bold("2. Penggunaan Informasi")
                    append("Data digunakan untuk memproses pemesanan, menghubungkan dengan vendor, mendukung layanan pelanggan, serta memberikan informasi dan promosi terkait. Penggunaan di luar tujuan layanan hanya dilakukan dengan persetujuan Anda.\n\n")

                    bold("3. Perlindungan Data")
                    append("Kami menggunakan enkripsi, sistem keamanan aplikasi, dan kontrol akses ketat untuk melindungi data. Pengguna juga wajib menjaga kerahasiaan akun dan perangkat masing-masing.\n\n")

                    bold("4. Berbagi Informasi kepada Pihak Ketiga")
                    append("Wedify tidak akan menjual atau membagikan data kepada pihak ketiga untuk tujuan komersial tanpa izin. Data hanya dibagikan kepada vendor mitra resmi atau jika diwajibkan oleh hukum.\n\n")

                    bold("5. Hak Pengguna")
                    append("Pengguna dapat mengakses, memperbarui, atau menghapus data melalui aplikasi. Pengguna juga dapat menolak penggunaan data untuk promosi dan mengajukan keluhan jika terjadi penyalahgunaan.\n\n")

                    bold("6. Perubahan Kebijakan")
                    append("Kebijakan dapat diperbarui sewaktu-waktu. Jika ada perubahan signifikan, kami akan memberitahu pengguna melalui aplikasi atau email.")
                }
            )
        }
    }
}

// Helper extension function
fun AnnotatedString.Builder.bold(text: String) {
    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
    append(text + "\n")
    pop()
}


@Composable
fun KebijakanCard(
    title: String,
    content: AnnotatedString,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F4F4),
            contentColor = Color.Black
        ),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )
            if (!actionText.isNullOrEmpty() && onActionClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = actionText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE91E63),
                    modifier = Modifier.clickable { onActionClick() }
                )
            }
        }
    }
}

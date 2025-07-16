package com.example.wedify.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.wedify.profile.bold

@Composable
fun SyaratPage(modifier: Modifier = Modifier,navHostController: NavHostController) {
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
                text = "Syarat Dan Ketentuan",
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
                title = "Syarat Dan Ketentuan",
                content = buildAnnotatedString {
                    bold("Selamat datang di Wedify!")
                    append("Wedify adalah aplikasi perencana pernikahan digital yang membantu Anda mewujudkan momen istimewa secara lebih mudah, praktis, dan terorganisir. Dengan menggunakan aplikasi ini, Anda dianggap telah membaca dan menyetujui seluruh syarat dan ketentuan yang berlaku. Jika tidak setuju, mohon untuk tidak menggunakan layanan.\n\n")

                    bold("1. Ketentuan Umum")
                    append("Wedify menyediakan layanan perencanaan pernikahan profesional melalui kerja sama dengan vendor-vendor terpercaya, mulai dari dekorasi, katering, hingga dokumentasi. Layanan ini hanya tersedia untuk wilayah dan tanggal yang didukung mitra kami. Pengguna bertanggung jawab memastikan ketersediaan lokasi dan waktu acara.\n\n")

                    bold("2. Akun Penggunaan")
                    append("Pengguna wajib mendaftar dengan data diri valid (nama, email, nomor telepon). Pengguna bertanggung jawab menjaga keamanan akun, termasuk kerahasiaan kata sandi. Apabila terjadi penyalahgunaan, segera laporkan ke tim Wedify. Kami berhak menonaktifkan akun jika ditemukan pelanggaran atau aktivitas mencurigakan.\n\n")

                    bold("3. Layanan Dan Reservasi")
                    append("Pengguna dapat memilih berbagai paket pernikahan dan melakukan reservasi sesuai kebutuhan. Pemesanan sah setelah dikonfirmasi sistem atau tim resmi Wedify. Perubahan jadwal atau permintaan khusus harus diinformasikan minimal 30 hari sebelum acara.\n\n")

                    bold("4. Pembayaran")
                    append("Pembayaran dilakukan online melalui metode yang tersedia, seperti transfer bank, kartu kredit, dan dompet digital. Beberapa layanan membutuhkan uang muka (DP). Keterlambatan pembayaran dapat menyebabkan pembatalan otomatis. Semua transaksi akan tercatat dan bisa diakses melalui akun pengguna.\n\n")

                }
            )
        }
    }

    // Helper extension function
    fun AnnotatedString.Builder.bold(text: String) {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(text + "\n")
        pop()
    }


    @Composable
    fun SyaratCard(
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
}

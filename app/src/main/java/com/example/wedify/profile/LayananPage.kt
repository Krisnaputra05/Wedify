package com.example.wedify.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun LayananPage(modifier: Modifier,navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

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
                text = "Layanan Pelanggan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            LayananCard(
                title = "📞 Hubungi Kami",
                content = "Jika Anda membutuhkan bantuan langsung, tim layanan pelanggan Wedify siap membantu dengan ramah dan cepat.\n\n" +
                        "WhatsApp: 0812-3456-7890\n" +
                        "Email: support@wedify.id\n" +
                        "Jam Operasional: Setiap hari, pukul 08.00 – 20.00 WITA\n\n" +
                        "Anda juga dapat menghubungi kami melalui fitur Live Chat yang tersedia di dalam aplikasi Wedify untuk respon yang lebih cepat."
            )

            LayananCard(
                title = "❓ Pusat Bantuan",
                content = "Sebelum menghubungi tim kami, Anda dapat mencari jawaban dari pertanyaan-pertanyaan umum melalui halaman FAQ. Di sana tersedia informasi tentang cara pemesanan, pilihan paket, pembayaran, pembatalan, dan lain-lain."

            )

            LayananCard(
                title = "📦 Cek Status Pemesanan",
                content = "Lihat progres acara Anda secara real-time!\n" +
                        "• Cek status vendor (Makeup, Dekorasi, MC, dll.)\n" +
                        "• Pantau jadwal fitting, survei lokasi, dan rapat teknis\n" +
                        "• Dapatkan notifikasi jika ada perubahan jadwal"
            )

            LayananCard(
                title = "🛠️ Lapor Kendala atau Keluhan",
                content = "Jika Anda mengalami kendala dalam layanan atau ingin menyampaikan komplain, silakan isi formulir berikut:\n\n" +
                        "Mohon lampirkan bukti (foto, chat, invoice, dll.) jika tersedia agar tim kami bisa menindaklanjuti lebih cepat. Semua keluhan akan kami tangani dalam waktu maksimal 2x24 jam."
            )

            LayananCard(
                title = "💬 Konsultasi & Rekomendasi",
                content = "Butuh bantuan memilih vendor terbaik atau tema dekorasi sesuai anggaran? Tim konsultan kami siap membantu memberikan saran berdasarkan preferensi dan lokasi Anda."
            )

            LayananCard(
                title = "📄 Kebijakan & Dokumen",
                content = "Untuk kenyamanan bersama, Anda dapat membaca kembali dokumen penting berikut:\n\n" +
                        "• Syarat & Ketentuan Layanan\n" +
                        "• Kebijakan Privasi\n" +
                        "• Kebijakan Pembatalan dan Pengembalian Dana"
            )

            LayananCard(
                title = "⭐ Beri Ulasan & Penilaian",
                content = "Setelah acara Anda selesai, kami sangat menghargai jika Anda dapat memberikan ulasan terhadap vendor dan layanan Wedify. Ulasan Anda membantu kami menjadi lebih baik dan membantu pengguna lain dalam mengambil keputusan."

            )
        }
    }
}

@Composable
fun LayananCard(
    title: String,
    content: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F4F4),
            contentColor = Color.Black
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
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


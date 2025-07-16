package com.example.wedify.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun FaqPage(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { navController.popBackStack() }
                .padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFF5C8D),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = "FAQ",
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        FaqSection(
            title = "Pertanyaan Umum",
            items = listOf(
                "Apa itu Wedify?",
                "Siapa yang bisa menggunakan layanan Wedify?",
                "Apakah Wedify tersedia di seluruh Indonesia?",
                "Apakah saya bisa konsultasi terlebih dahulu sebelum memesan?"
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        FaqSection(
            title = "Paket dan Layanan",
            items = listOf(
                "Apa saja jenis paket wedding yang tersedia?",
                "Apakah saya bisa request tema sendiri?",
                "Apakah paket bisa disesuaikan dengan budget saya?",
                "Apa saja yang termasuk dalam paket lengkap pernikahan?"
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        FaqSection(
            title = "Reservasi & Pembayaran",
            items = listOf(
                "Bagaimana cara memesan layanan di Wedify?",
                "Apakah saya harus membayar di muka?",
                "Metode pembayaran apa saja yang tersedia?",
                "Bagaimana jika saya ingin membatalkan pesanan?"
            )
        )
    }
}

@Composable
fun FaqSection(title: String, items: List<String>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            for (item in items) {
                FaqItem(question = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FaqItem(question: String) {
    var expanded by remember { mutableStateOf(false) }
    val answer = when (question) {
        // Pertanyaan Umum
        "Apa itu Wedify?" -> "Wedify adalah platform layanan pernikahan yang membantu pasangan dalam merencanakan dan menyelenggarakan acara pernikahan secara mudah, terorganisir, dan sesuai impian."
        "Siapa yang bisa menggunakan layanan Wedify?" -> "Layanan Wedify bisa digunakan oleh siapa saja yang ingin menyelenggarakan pernikahan, baik berskala kecil, sedang, hingga besar, tanpa repot mengurus semua detailnya sendiri."
        "Apakah Wedify tersedia di seluruh Indonesia?" -> "Ya, Wedify melayani pelanggan di berbagai daerah di Indonesia dan terus memperluas jangkauan layanannya ke kota-kota lainnya."
        "Apakah saya bisa konsultasi terlebih dahulu sebelum memesan?" -> "Tentu, Wedify menyediakan layanan konsultasi gratis agar Anda bisa berdiskusi dan mendapatkan solusi terbaik sebelum melakukan pemesanan layanan."

        // Paket dan Layanan
        "Apa saja jenis paket wedding yang tersedia?" -> "Wedify menawarkan berbagai jenis paket seperti intimate wedding, paket reguler, hingga full-service premium wedding, yang bisa disesuaikan dengan kebutuhan dan jumlah tamu."
        "Apakah saya bisa request tema sendiri?" -> "Bisa, Anda bebas menentukan tema pernikahan sesuai keinginan, dan tim Wedify akan membantu mewujudkannya secara profesional dan estetis."
        "Apakah paket bisa disesuaikan dengan budget saya?" -> "Ya, paket yang ditawarkan fleksibel dan bisa disesuaikan dengan budget yang Anda miliki tanpa mengurangi kualitas layanan secara keseluruhan."
        "Apa saja yang termasuk dalam paket lengkap pernikahan?" -> "Paket lengkap biasanya mencakup dekorasi, rias pengantin, dokumentasi (foto/video), katering, MC, hiburan, hingga tim koordinasi acara dari awal sampai selesai."

        // Reservasi & Pembayaran
        "Bagaimana cara memesan layanan di Wedify?" -> "Anda bisa memesan melalui situs resmi atau aplikasi Wedify dengan memilih paket yang diinginkan dan mengisi formulir pemesanan yang tersedia."
        "Apakah saya harus membayar di muka?" -> "Ya, untuk mengamankan jadwal dan layanan, pelanggan diwajibkan membayar uang muka (DP) sesuai nominal yang telah disepakati."
        "Metode pembayaran apa saja yang tersedia?" -> "Wedify menerima pembayaran melalui transfer bank, e-wallet (seperti OVO, GoPay, dan lainnya), serta kartu debit/kredit melalui aplikasi."
        "Bagaimana jika saya ingin membatalkan pesanan?" -> "Pembatalan bisa dilakukan sesuai kebijakan yang berlaku. Jika pembatalan dilakukan dalam waktu tertentu, Anda mungkin masih berhak atas pengembalian dana sebagian."

        else -> "Ini adalah jawaban contoh untuk \"$question\". Kamu bisa isi dengan penjelasan sesuai kebutuhan."
    }
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = question,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand"
                )
            }
            AnimatedVisibility(expanded) {
                Text(
                    text = answer,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
package com.example.wedify.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import kotlinx.coroutines.delay

@Composable
fun BannerView(modifier: Modifier = Modifier) {
    var bannerList by remember {
        mutableStateOf<List<String>>(emptyList())
    }
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("banners")
            .get().addOnCompleteListener() {
                bannerList = it.result.get("urls") as List<String>

            }
    }

    Column(
        modifier = modifier
    ) {
        val pagerState = rememberPagerState(0) {
            bannerList.size
        }
        HorizontalPager(
            state = pagerState,
            pageSpacing = 24.dp
        ) {
            AsyncImage(
                model = bannerList.get(it),
                contentDescription = "Banner image",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        LaunchedEffect(bannerList) {
            while (true) {
                delay(3000)
                val nextPage =
                    (pagerState.currentPage + 1) % (bannerList.size.takeIf { it > 0 } ?: 1)
                pagerState.animateScrollToPage(nextPage)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        val pageCount = 5
        DotsIndicator(
            dotCount = pageCount,
            type = ShiftIndicatorType(
                dotsGraphic = DotGraphic(
                    color = pinkbut,
                    size = 6.dp
                )
            ),
            pagerState = pagerState
        )

    }
}
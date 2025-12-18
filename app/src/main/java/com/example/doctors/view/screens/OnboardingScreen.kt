package com.example.doctors.view.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctors.R
import com.example.doctors.navigation.Routes
import com.example.doctors.ui.theme.PrimaryBlue
import com.example.doctors.ui.theme.White
import kotlinx.coroutines.launch


data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Find Your Specialist",
        description = "Discover and book appointments with verified specialists across various locations, right at your fingertips.",
        imageRes = R.drawable.img_doctor_welcome
    ),
    OnboardingPage(
        title = "Effortless Booking & Schedule",
        description = "Schedule, confirm, and manage all your healthcare appointments in minutes, simplifying your calendar easily.",
        imageRes = R.drawable.img_onboard_schedule
    ),
    OnboardingPage(
        title = "Anytime, Anywhere Care",
        description = "Connect with healthcare providers online through secure video consultations for follow-ups and care.",
        imageRes = R.drawable.img_onboard_telemed
    )
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val pageCount = onboardingPages.size
    // Remember the state for the pager
    val pagerState = rememberPagerState(initialPage = 0) { pageCount }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            OnboardingItem(page = onboardingPages[pageIndex])
        }

        Spacer(modifier = Modifier.height(16.dp))

        // indicator
        PageIndicator(pageCount = pageCount, currentPage = pagerState.currentPage)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (pagerState.currentPage < pageCount - 1) {

                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1,
                            animationSpec = tween(600)
                        )
                    }
                } else {

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White)
        ) {
            Text(
                text = if (pagerState.currentPage < pageCount - 1) "Next" else "Get Started",
                color = PrimaryBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun OnboardingItem(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(White)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {

                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = page.title,
                    modifier = Modifier
                        .size(360.dp)
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = page.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = page.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPage == iteration) White else White.copy(alpha = 0.5f)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }
}
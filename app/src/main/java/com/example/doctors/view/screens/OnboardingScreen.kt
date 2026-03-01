package com.example.doctors.view.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.doctors.model.OnboardingPage


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController) {

    // onboarding data
    val onboardingPages = remember {
        listOf(
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
    }

    val pageCount = onboardingPages.size
    val pagerState = rememberPagerState(initialPage = 0) { pageCount }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            pageSpacing = 16.dp
        ) { pageIndex ->
            OnboardingItem(
                page = onboardingPages[pageIndex],
                isVisible = pagerState.currentPage == pageIndex
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // dot indicator
        PageIndicator(pageCount = pageCount, currentPage = pagerState.currentPage)

        Spacer(modifier = Modifier.height(24.dp))

        // main action button
        Button(
            onClick = {
                if (pagerState.currentPage < pageCount - 1) {
                    // scroll to next
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1,
                            animationSpec = tween(700)
                        )
                    }
                } else {
                    // navigate to login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text(
                text = if (pagerState.currentPage < pageCount - 1) "Next" else "Get Started",
                color = PrimaryBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun OnboardingItem(page: OnboardingPage, isVisible: Boolean) {
    var hasStarted by remember { mutableStateOf(false) }

    // start animation
    LaunchedEffect (Unit) {
        hasStarted = true
    }

    val active = isVisible && hasStarted

    // animations
    val alpha by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = tween(1000),
        label = "fade"
    )

    val translateY by animateDpAsState(
        targetValue = if (active) 0.dp else 60.dp,
        animationSpec = tween(1000),
        label = "float"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // white card container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(White)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(32.dp)
                    .alpha(alpha)
                    .offset(y = translateY)
            ) {
                // illustration
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = page.title,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Fit
                )

                // title
                Text(
                    text = page.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // description
                Text(
                    text = page.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
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
            // animate width for active dot
            val width by animateDpAsState(targetValue = if (currentPage == iteration) 24.dp else 8.dp, label = "")
            val color = if (currentPage == iteration) White else White.copy(alpha = 0.4f)

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
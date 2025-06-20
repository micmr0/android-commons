package com.micmr0.androidcommons.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.micmr0.androidcommons.ui.theme.CommonTheme
import kotlinx.coroutines.launch
import com.micmr0.androidcommons.R

@Composable
fun OnboardingScreen(onboardingPages: List<OnBoardingPage>, onFinishOnboarding: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            Box(modifier = Modifier) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = onboardingPages[page].imageRes),
                        contentDescription = "Obrazek dla strony ${onboardingPages[page].titleRes}",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .wrapContentSize(),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = stringResource(onboardingPages[page].titleRes),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.End,

            ) {
            if (pagerState.currentPage < onboardingPages.size - 1) {
                Button(
                    onClick = {
                        onFinishOnboarding()
                    }
                ) {
                    Text(stringResource(R.string.onboarding_button_skip))
                }
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text(stringResource(R.string.onboarding_button_next))
                }
            } else {
                Button(
                    onClick = {
                        onFinishOnboarding()
                    }
                ) {
                    Text(stringResource(R.string.onboarding_button_start))
                }
            }
        }
    }
}

data class OnBoardingPage(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int
)


@Preview
@Composable
fun SearchResultPreview() {
    CommonTheme {
        OnboardingScreen(
            onboardingPages = emptyList(),
            onFinishOnboarding = {}
        )
    }
}
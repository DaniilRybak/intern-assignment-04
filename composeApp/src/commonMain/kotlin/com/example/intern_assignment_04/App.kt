package com.example.intern_assignment_04

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.intern_assignment_04.feature.stopwatch.StopwatchScreen
import com.example.intern_assignment_04.feature.stopwatch.StopwatchViewModel
import com.example.intern_assignment_04.feature.timer.TimerScreen
import com.example.intern_assignment_04.feature.timer.TimerViewModel
import com.example.intern_assignment_04.feature.timer.melody.ITunesMelodyRepository
import com.example.intern_assignment_04.feature.usecase.FormatTimeUseCase
import internassignment04.composeapp.generated.resources.Res
import internassignment04.composeapp.generated.resources.nav_stopwatch
import internassignment04.composeapp.generated.resources.nav_timer
import internassignment04.composeapp.generated.resources.timer
import internassignment04.composeapp.generated.resources.timer_sec
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
@Preview
fun App() {
    val formatTimeUseCase = remember { FormatTimeUseCase() }
    val melodyRepository = remember { ITunesMelodyRepository() }
    val nowMillis = remember { { Clock.System.now().toEpochMilliseconds() } }
    val timerViewModel = remember {
        TimerViewModel(
            nowMillis = nowMillis,
            melodyRepository = melodyRepository,
        )
    }
    val stopwatchViewModel = remember {
        StopwatchViewModel(
            formatTimeUseCase = formatTimeUseCase,
            nowMillis = nowMillis,
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            timerViewModel.clear()
            stopwatchViewModel.clear()
        }
    }

    MaterialTheme {
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf(
            NavBarTab(
                title = Res.string.nav_timer,
                icon = Res.drawable.timer,
            ),
            NavBarTab(
                title = Res.string.nav_stopwatch,
                icon = Res.drawable.timer_sec,
            ),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .safeContentPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
            ) {
                when (selectedTabIndex) {
                    0 -> TimerScreen(
                        timerViewModel = timerViewModel,
                        modifier = Modifier.fillMaxSize(),
                    )

                    else -> StopwatchScreen(
                        stopwatchViewModel = stopwatchViewModel,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            BottomCenterTabRow(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            )
        }
    }
}

private data class NavBarTab(
    val title: StringResource,
    val icon: DrawableResource,
)

@Composable
private fun BottomCenterTabRow(
    tabs: List<NavBarTab>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF1F1F1))
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            divider = {},
            indicator = {},
        ) {
            tabs.forEachIndexed { index, tab ->
                val tabTitle = stringResource(tab.title)
                val isSelected = index == selectedTabIndex
                val itemBg = if (isSelected) Color.White else Color.Transparent
                val itemTextColor = if (isSelected) Color.Black else Color(0xFF707070)

                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    selectedContentColor = itemTextColor,
                    unselectedContentColor = itemTextColor,
                    modifier = Modifier.padding(horizontal = 3.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(itemBg)
                            .widthIn(min = 120.dp)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(tab.icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(itemTextColor),
                            )
                            Text(
                                text = tabTitle,
                                color = itemTextColor,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}

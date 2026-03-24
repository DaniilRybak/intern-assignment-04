package com.example.intern_assignment_04.feature.stopwatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intern_assignment_04.model.domain.LapTime
import com.example.intern_assignment_04.model.domain.StopwatchState
import internassignment04.composeapp.generated.resources.Res
import internassignment04.composeapp.generated.resources.action_reset
import internassignment04.composeapp.generated.resources.action_start
import internassignment04.composeapp.generated.resources.action_stop
import internassignment04.composeapp.generated.resources.stopwatch_lap_button
import internassignment04.composeapp.generated.resources.stopwatch_lap_label
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun StopwatchScreen(
    stopwatchViewModel: StopwatchViewModel,
    modifier: Modifier = Modifier,
) {
    val lapButtonText = stringResource(Res.string.stopwatch_lap_button)
    val actionReset = stringResource(Res.string.action_reset)
    val actionStart = stringResource(Res.string.action_start)
    val actionStop = stringResource(Res.string.action_stop)

    val state by stopwatchViewModel.state.collectAsState()
    val isRunning = state is StopwatchState.Running
    val circleSize = 80.dp
    val surfaceGray = Color(0xFFBDBDBD)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = buildStopwatchTimeLabel(
                base = stopwatchViewModel.formatElapsedTime(state.elapsedTimeMillis),
                elapsedMillis = state.elapsedTimeMillis,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 60.dp),
            style = MaterialTheme.typography.displayLarge,
            fontSize = 72.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isRunning) {
                CircleActionButton(
                    text = lapButtonText,
                    onClick = stopwatchViewModel::recordLap,
                    enabled = true,
                    circleSize = circleSize,
                    containerColor = surfaceGray,
                    contentColor = Color(0xFF424242),
                )
            } else {
                CircleActionButton(
                    text = actionReset,
                    onClick = stopwatchViewModel::reset,
                    enabled = state.elapsedTimeMillis > 0L || state.laps.isNotEmpty(),
                    circleSize = circleSize,
                    containerColor = surfaceGray,
                    contentColor = Color(0xFF424242),
                )
            }

            CircleActionButton(
                text = if (isRunning) actionStop else actionStart,
                onClick = if (isRunning) stopwatchViewModel::stop else stopwatchViewModel::start,
                enabled = true,
                circleSize = circleSize,
                containerColor = if (isRunning) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color(0xFF2E7D32)
                },
                contentColor = Color.White,
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = state.laps.asReversed(),
                key = { lap -> lap.lapNumber },
            ) { lap ->
                LapRow(lap = lap)
            }
        }
    }
}

@Composable
private fun CircleActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    circleSize: androidx.compose.ui.unit.Dp,
    containerColor: Color,
    contentColor: Color,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(circleSize),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
private fun LapRow(lap: LapTime) {
    val lapLabel = stringResource(Res.string.stopwatch_lap_label, lap.lapNumber)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color(0xFFBDBDBD),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = lapLabel)
        Text(text = formatMillisWithCentiseconds(lap.lapDurationMillis))
    }
}

private fun buildStopwatchTimeLabel(
    base: String,
    elapsedMillis: Long,
): String {
    val centiseconds = ((elapsedMillis % 1000L) / 10L)
        .toString()
        .padStart(2, '0')

    return "$base:$centiseconds"
}

private fun formatMillisWithCentiseconds(millis: Long): String {
    val clamped = millis.coerceAtLeast(0L)
    val totalSeconds = clamped / 1000L
    val minutes = (totalSeconds / 60L).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60L).toString().padStart(2, '0')
    val centiseconds = ((clamped % 1000L) / 10L).toString().padStart(2, '0')

    return "$minutes:$seconds:$centiseconds"
}

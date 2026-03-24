package com.example.intern_assignment_04.feature.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intern_assignment_04.model.domain.TimerState
import internassignment04.composeapp.generated.resources.Res
import internassignment04.composeapp.generated.resources.action_reset
import internassignment04.composeapp.generated.resources.action_start
import internassignment04.composeapp.generated.resources.action_stop
import internassignment04.composeapp.generated.resources.timer_seconds_label
import internassignment04.composeapp.generated.resources.timer_seconds_placeholder
import internassignment04.composeapp.generated.resources.timer_set_time_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimerScreen(
    timerViewModel: TimerViewModel,
    modifier: Modifier = Modifier,
) {
    val titleSetTime = stringResource(Res.string.timer_set_time_title)
    val actionReset = stringResource(Res.string.action_reset)
    val actionStart = stringResource(Res.string.action_start)
    val actionStop = stringResource(Res.string.action_stop)

    val state by timerViewModel.state.collectAsState()
    val isRunning = state is TimerState.Running
    val showPicker = state is TimerState.Idle || state is TimerState.Finished
    val showTimerDisplay = state is TimerState.Running || state is TimerState.Paused

    var selectedSeconds by remember { mutableIntStateOf(0) }
    var secondsInput by remember { mutableStateOf("0") }
    val pickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 1,
        is24Hour = true,
    )

    LaunchedEffect(showPicker, state.totalTimeMillis) {
        if (showPicker && state.totalTimeMillis > 0L) {
            val totalSeconds = (state.totalTimeMillis / 1000L).toInt().coerceAtLeast(0)
            pickerState.hour = (totalSeconds / 3600).coerceIn(0, 23)
            pickerState.minute = ((totalSeconds % 3600) / 60).coerceIn(0, 59)
            selectedSeconds = (totalSeconds % 60).coerceIn(0, 59)
            secondsInput = selectedSeconds.toString()
        }
    }

    val selectedDurationMillis = (
        pickerState.hour * 3600L + pickerState.minute * 60L + selectedSeconds
    ) * 1000L

    val totalMillis = state.totalTimeMillis.takeIf { it > 0L } ?: selectedDurationMillis
    val progress = if (totalMillis > 0L) {
        (state.remainingTimeMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            if (showPicker) {
                Text(
                    text = titleSetTime,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp),
                )

                TimePicker(state = pickerState)

                SecondsField(
                    secondsInput = secondsInput,
                    onSecondsInputChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }.take(2)
                        if (digitsOnly.isEmpty()) {
                            secondsInput = ""
                            selectedSeconds = 0
                        } else {
                            val parsed = digitsOnly.toIntOrNull() ?: 0
                            val clamped = parsed.coerceIn(0, 59)
                            selectedSeconds = clamped
                            secondsInput = if (parsed > 59) clamped.toString() else digitsOnly
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                )
            }

            if (showTimerDisplay) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 12.dp,
                    )
                    Text(
                        text = timerViewModel.formatRemainingTime(),
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 64.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleActionButton(
                text = actionReset,
                onClick = { timerViewModel.reset() },
                enabled = state !is TimerState.Idle || selectedDurationMillis > 0L,
                circleSize = 80.dp,
                containerColor = Color(0xFFBDBDBD),
                contentColor = Color(0xFF424242),
            )

            CircleActionButton(
                text = if (isRunning) actionStop else actionStart,
                onClick = {
                    if (isRunning) {
                        timerViewModel.stop()
                    } else {
                        val durationMillis = if (state is TimerState.Paused) 0L else selectedDurationMillis
                        timerViewModel.start(durationMillis)
                    }
                },
                enabled = isRunning || state is TimerState.Paused || selectedDurationMillis > 0L,
                circleSize = 80.dp,
                containerColor = if (isRunning) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color(0xFF2E7D32)
                },
                contentColor = Color.White,
            )
        }
    }
}

@Composable
private fun SecondsField(
    secondsInput: String,
    onSecondsInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val secondsLabel = stringResource(Res.string.timer_seconds_label)
    val secondsPlaceholder = stringResource(Res.string.timer_seconds_placeholder)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = secondsLabel,
            style = MaterialTheme.typography.titleMedium,
        )

        OutlinedTextField(
            value = secondsInput,
            onValueChange = onSecondsInputChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.size(width = 96.dp, height = 56.dp),
            placeholder = { Text(secondsPlaceholder) },
        )
    }
}

@Composable
private fun CircleActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    circleSize: Dp,
    containerColor: Color,
    contentColor: Color,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(circleSize),
        shape = androidx.compose.foundation.shape.CircleShape,
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

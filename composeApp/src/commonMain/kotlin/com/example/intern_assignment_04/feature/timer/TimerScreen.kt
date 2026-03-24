package com.example.intern_assignment_04.feature.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intern_assignment_04.model.domain.TimerState

@Composable
internal fun TimerScreen(
    timerViewModel: TimerViewModel,
    modifier: Modifier = Modifier,
) {
    val state by timerViewModel.state.collectAsState()
    var hoursInput by remember { mutableStateOf("00") }
    var minutesInput by remember { mutableStateOf("01") }
    var secondsInput by remember { mutableStateOf("00") }

    val inputHours = hoursInput.toLongOrNull()?.coerceAtLeast(0L) ?: 0L
    val inputMinutes = minutesInput.toLongOrNull()?.coerceIn(0L, 59L) ?: 0L
    val inputSeconds = secondsInput.toLongOrNull()?.coerceIn(0L, 59L) ?: 0L
    val inputDurationMillis = ((inputHours * 3600L) + (inputMinutes * 60L) + inputSeconds) * 1000L

    val totalMillis = state.totalTimeMillis.takeIf { it > 0L } ?: inputDurationMillis
    val remainingMillis = state.remainingTimeMillis
    val progress = if (totalMillis > 0L) {
        (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TimePartInput(
                value = hoursInput,
                onValueChange = { hoursInput = it },
                label = "Ч",
            )
            Text(text = ":", style = MaterialTheme.typography.headlineSmall)
            TimePartInput(
                value = minutesInput,
                onValueChange = { minutesInput = it },
                label = "М",
            )
            Text(text = ":", style = MaterialTheme.typography.headlineSmall)
            TimePartInput(
                value = secondsInput,
                onValueChange = { secondsInput = it },
                label = "С",
            )
        }

        CircularProgressIndicator(progress = { progress })

        Text(
            text = timerViewModel.formatRemainingTime(),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displayLarge,
            fontSize = 72.sp,
            textAlign = TextAlign.Center,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (state is TimerState.Paused) {
                        timerViewModel.start(0L)
                    } else {
                        timerViewModel.start(inputDurationMillis)
                    }
                },
                enabled = state !is TimerState.Running,
            ) {
                Text("Старт")
            }
            Button(
                onClick = timerViewModel::stop,
                enabled = state is TimerState.Running,
            ) {
                Text("Пауза")
            }
            Button(onClick = timerViewModel::reset) {
                Text("Сброс")
            }
        }
    }
}

@Composable
private fun TimePartInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }.take(2)
            onValueChange(digits)
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
        modifier = Modifier.width(84.dp),
    )
}

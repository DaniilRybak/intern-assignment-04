package com.example.intern_assignment_04.feature.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intern_assignment_04.feature.components.SplitTimeDisplay
import com.example.intern_assignment_04.feature.timer.melody.Melody
import com.example.intern_assignment_04.model.domain.TimerState
import internassignment04.feature.generated.resources.Res
import internassignment04.feature.generated.resources.action_pause
import internassignment04.feature.generated.resources.action_reset
import internassignment04.feature.generated.resources.action_start
import internassignment04.feature.generated.resources.timer_finished_notification_body
import internassignment04.feature.generated.resources.timer_finished_notification_title
import internassignment04.feature.generated.resources.timer_melody_loading
import internassignment04.feature.generated.resources.timer_melody_pick
import internassignment04.feature.generated.resources.timer_melody_refresh
import internassignment04.feature.generated.resources.timer_melody_unavailable
import internassignment04.feature.generated.resources.timer_seconds_label
import internassignment04.feature.generated.resources.timer_seconds_placeholder
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    timerViewModel: TimerViewModel,
    modifier: Modifier = Modifier,
) {
    val notifier = rememberTimerCompletionNotifier()
    val actionReset = stringResource(Res.string.action_reset)
    val actionPause = stringResource(Res.string.action_pause)
    val actionStart = stringResource(Res.string.action_start)
    val notificationTitle = stringResource(Res.string.timer_finished_notification_title)
    val notificationBody = stringResource(Res.string.timer_finished_notification_body)

    val melodyPick = stringResource(Res.string.timer_melody_pick)
    val melodyRefresh = stringResource(Res.string.timer_melody_refresh)
    val melodyLoadingText = stringResource(Res.string.timer_melody_loading)
    val melodyUnavailable = stringResource(Res.string.timer_melody_unavailable)

    val state by timerViewModel.state.collectAsState()
    val melodies by timerViewModel.melodies.collectAsState()
    val selectedMelody by timerViewModel.selectedMelody.collectAsState()
    val melodyLoading by timerViewModel.melodyLoading.collectAsState()
    val melodyError by timerViewModel.melodyError.collectAsState()

    val isRunning = state is TimerState.Running
    val showPicker = state is TimerState.Idle || state is TimerState.Finished
    val showTimerDisplay = state is TimerState.Running || state is TimerState.Paused

    LaunchedEffect(showPicker) {
        if (showPicker && melodies.isEmpty() && !melodyLoading) {
            timerViewModel.loadMelodies()
        }
    }

    LaunchedEffect(timerViewModel, selectedMelody, notificationTitle, notificationBody) {
        timerViewModel.events.collect { event ->
            if (event is TimerUiEvent.TimerFinished) {
                val selectedName = selectedMelody?.title
                val body = if (selectedName == null) notificationBody else "$notificationBody ($selectedName)"

                notifier.notifyTimerCompleted(
                    title = notificationTitle,
                    body = body,
                )
                notifier.stopPlayback()
                selectedMelody?.previewUrl?.let { previewUrl ->
                    notifier.playMelodyPreview(previewUrl)
                }
            }
        }
    }

    DisposableEffect(notifier) {
        onDispose {
            notifier.stopPlayback()
        }
    }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            if (showPicker) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
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
                    )
                }
            }

            if (showTimerDisplay) {
                val circleTime = timerViewModel.formatRemainingTimeForCircle()

                Box(
                    modifier = Modifier.size(280.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 12.dp,
                    )
                    SplitTimeDisplay(
                        mainText = circleTime.main,
                        secondaryText = circleTime.seconds,
                        mainFontSize = 56.sp,
                        secondaryFontSize = 24.sp,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
                text = if (isRunning) actionPause else actionStart,
                onClick = {
                    if (isRunning) {
                        timerViewModel.pause()
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

        MelodyPicker(
            pickLabel = melodyPick,
            refreshLabel = melodyRefresh,
            loadingLabel = melodyLoadingText,
            unavailableLabel = melodyUnavailable,
            melodies = melodies,
            selected = selectedMelody,
            loading = melodyLoading,
            error = melodyError,
            onRefresh = timerViewModel::loadMelodies,
            onSelect = { melody -> timerViewModel.selectMelody(melody.id) },
        )
    }
}

@Composable
private fun MelodyPicker(
    pickLabel: String,
    refreshLabel: String,
    loadingLabel: String,
    unavailableLabel: String,
    melodies: List<Melody>,
    selected: Melody?,
    loading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onSelect: (Melody) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        if (error != null) {
            showErrorDialog = true
        }
    }

    fun String.truncate(maxLength: Int): String {
        return if (this.length > maxLength) {
            this.take(maxLength) + "..."
        } else {
            this
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { expanded = true }) {
                val selectedText = selected?.let { "${it.title} - ${it.artist}" } ?: pickLabel
                Text(text = selectedText.truncate(30))
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                melodies.take(12).forEach { melody ->
                    DropdownMenuItem(
                        text = { Text("${melody.title} - ${melody.artist}") },
                        onClick = {
                            onSelect(melody)
                            expanded = false
                        },
                    )
                }
            }

            OutlinedButton(
                onClick = onRefresh,
                enabled = !loading,
                modifier = Modifier
                    .size(44.dp)
                    .semantics {
                        contentDescription = if (loading) loadingLabel else refreshLabel
                    },
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                    )
                }
            }
        }

        if (showErrorDialog && error != null) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = {
                    Text(text = unavailableLabel)
                },
                text = {
                    Text(text = error)
                },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text(text = "OK")
                    }
                },
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

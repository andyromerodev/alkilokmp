package dev.andyromero

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import dev.andyromero.learning.LearningIntent
import dev.andyromero.learning.LearningStore
import org.jetbrains.compose.resources.stringResource

import alkilokmp.composeapp.generated.resources.Res
import alkilokmp.composeapp.generated.resources.button_next_day
import alkilokmp.composeapp.generated.resources.button_refresh_clock
import alkilokmp.composeapp.generated.resources.button_toggle_concepts
import alkilokmp.composeapp.generated.resources.label_day
import alkilokmp.composeapp.generated.resources.label_last_refresh
import alkilokmp.composeapp.generated.resources.title_kmp_lab

@Composable
@Preview
fun App() {
    val store = remember { LearningStore() }
    var state by remember { mutableStateOf(store.initialState()) }
    var latestEffect by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.title_kmp_lab),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "${stringResource(Res.string.label_day)} ${state.currentDay}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = state.stepTitle,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.stepGoal,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.platformSummary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "${stringResource(Res.string.label_last_refresh)} ${state.lastRefreshEpochSeconds}",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val update = store.reduce(state, LearningIntent.NextDay)
                    state = update.state
                    latestEffect = update.effect?.message
                },
            ) {
                Text(stringResource(Res.string.button_next_day))
            }
            Spacer(Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val update = store.reduce(state, LearningIntent.ToggleConcepts)
                    state = update.state
                    latestEffect = update.effect?.message
                },
            ) {
                Text(stringResource(Res.string.button_toggle_concepts))
            }
            Spacer(Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val update = store.reduce(state, LearningIntent.RefreshClock)
                    state = update.state
                    latestEffect = update.effect?.message
                },
            ) {
                Text(stringResource(Res.string.button_refresh_clock))
            }
            if (state.showConceptHints) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.currentConceptHint,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            latestEffect?.let { effect ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = effect,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

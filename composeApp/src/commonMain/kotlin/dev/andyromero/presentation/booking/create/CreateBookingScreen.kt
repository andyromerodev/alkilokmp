package dev.andyromero.presentation.booking.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.andyromero.presentation.booking.components.RangeCalendar
import dev.andyromero.presentation.booking.components.formatDisplayDate
import dev.andyromero.presentation.booking.components.parseIsoToSimpleDate
import dev.andyromero.presentation.booking.components.simpleDateToUtcMillis
import dev.andyromero.presentation.booking.components.todayUtcMillis
import dev.andyromero.presentation.booking.components.toMonth
import dev.andyromero.presentation.booking.components.utcMillisToSimpleDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateBookingScreen(
    viewModel: CreateBookingViewModel,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    val todayUtcMillis = remember { todayUtcMillis() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CreateBookingEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is CreateBookingEffect.OpenWhatsApp -> {
                    try {
                        uriHandler.openUri(effect.url)
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("No se pudo abrir WhatsApp")
                    }
                }
                CreateBookingEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    if (state.showManualPhoneDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.sendIntent(CreateBookingIntent.DismissManualPhoneDialog) },
            title = { Text("Número de WhatsApp") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("El anfitrión no tiene teléfono configurado. Ingresa un número para continuar.")
                    OutlinedTextField(
                        value = state.manualPhone,
                        onValueChange = { viewModel.sendIntent(CreateBookingIntent.ManualPhoneChanged(it)) },
                        singleLine = true,
                        label = { Text("Número") },
                        placeholder = { Text("Ej: +18095551234") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.sendIntent(CreateBookingIntent.ConfirmManualPhone) }) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.sendIntent(CreateBookingIntent.DismissManualPhoneDialog) }) {
                    Text("Cancelar")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservar") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.sendIntent(CreateBookingIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (state.error != null && state.availability.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = state.error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                )
                Button(
                    onClick = { viewModel.sendIntent(CreateBookingIntent.Retry) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text("Reintentar")
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = state.propertyTitle,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = formatPrice(state.pricePerNight) + " / noche",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            val availableLocalDates = remember(state.availability) {
                state.availability
                    .filter { it.isAvailable }
                    .mapNotNull { parseIsoToSimpleDate(it.date) }
                    .toSet()
            }
            val unavailableLocalDates = remember(state.availability) {
                state.availability
                    .filterNot { it.isAvailable }
                    .mapNotNull { parseIsoToSimpleDate(it.date) }
                    .toSet()
            }

            Text(
                text = "Selecciona un rango disponible",
                style = MaterialTheme.typography.titleMedium,
            )

            val selectedStartDate = state.selectedCheckIn?.let { utcMillisToSimpleDate(it) }
            val selectedEndDate = state.selectedCheckOut?.let { utcMillisToSimpleDate(it) }
            val initialMonth = remember(selectedStartDate, availableLocalDates) {
                selectedStartDate?.toMonth()
                    ?: availableLocalDates.minOrNull()?.toMonth()
                    ?: utcMillisToSimpleDate(todayUtcMillis).toMonth()
            }
            val minMonth = remember(availableLocalDates) {
                availableLocalDates.minOrNull()?.toMonth()
                    ?: initialMonth.plusMonths(-12)
            }
            val maxMonth = remember(availableLocalDates) {
                availableLocalDates.maxOrNull()?.toMonth()
                    ?: initialMonth.plusMonths(12)
            }
            var currentMonth by remember(initialMonth) { mutableStateOf(initialMonth) }

            LaunchedEffect(selectedStartDate) {
                selectedStartDate?.let { target ->
                    if (target >= minMonth.atDay(1) && target <= maxMonth.atEndOfMonth()) {
                        currentMonth = target.toMonth()
                    }
                }
            }

            RangeCalendar(
                currentMonth = currentMonth,
                minMonth = minMonth,
                maxMonth = maxMonth,
                availableDates = availableLocalDates,
                unavailableDates = unavailableLocalDates,
                selectedStart = selectedStartDate,
                selectedEnd = selectedEndDate,
                onMonthChange = { month ->
                    currentMonth = when {
                        month < minMonth -> minMonth
                        month > maxMonth -> maxMonth
                        else -> month
                    }
                },
                onDaySelected = { localDate ->
                    viewModel.sendIntent(CreateBookingIntent.SelectDate(simpleDateToUtcMillis(localDate)))
                },
            )

            Text(
                text = "Check-in: ${state.selectedCheckIn?.let { formatDisplayDate(it) } ?: "--/--/----"}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Check-out: ${state.selectedCheckOut?.let { formatDisplayDate(it) } ?: "--/--/----"}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Noches: ${state.selectedNights}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Button(
                onClick = { viewModel.sendIntent(CreateBookingIntent.SendWhatsAppClicked) },
                enabled = state.isSendEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Enviar por WhatsApp")
            }

            TextButton(
                onClick = { viewModel.sendIntent(CreateBookingIntent.ClearSelection) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Limpiar selección")
            }
        }
    }
}

private fun formatPrice(value: Double): String {
    return if (value % 1.0 == 0.0) {
        "USD ${value.toInt()}"
    } else {
        val cents = ((value * 100).toLong() % 100).toString().padStart(2, '0')
        "USD ${value.toLong()}.$cents"
    }
}

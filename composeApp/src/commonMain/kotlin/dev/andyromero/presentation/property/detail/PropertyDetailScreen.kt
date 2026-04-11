package dev.andyromero.presentation.property.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import coil3.compose.AsyncImage
import dev.andyromero.domain.model.Property
import dev.andyromero.presentation.components.rememberNetworkImageLoader

@Composable
internal fun PropertyDetailScreen(
    viewModel: PropertyDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onShowMessage: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PropertyDetailEffect.NavigateBack -> onNavigateBack()
                is PropertyDetailEffect.NavigateToBooking -> onNavigateToBooking(effect.propertyId)
                is PropertyDetailEffect.ShowError -> onShowMessage(effect.message)
            }
        }
    }

    Scaffold(
        containerColor = BeigeBackground,
        bottomBar = {
            state.property?.let { property ->
                BookingBottomBar(property = property) {
                    viewModel.sendIntent(PropertyDetailIntent.BookNow)
                }
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BeigeBackground)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.property == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BeigeBackground)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.errorMessage ?: "No se pudo cargar la propiedad",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            else -> {
                PropertyDetailContent(
                    property = state.property!!,
                    contentPadding = paddingValues,
                    onBack = { viewModel.sendIntent(PropertyDetailIntent.Back) },
                )
            }
        }
    }
}

@Composable
private fun PropertyDetailContent(
    property: Property,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
) {
    val imageLoader = rememberNetworkImageLoader()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 8.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 112.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(height = 210.dp, width = 280.dp)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                if (property.mainImageUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFD6DADF), Color(0xFFE0B18F))
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Sin imagen")
                    }
                } else {
                    AsyncImage(
                        model = property.mainImageUrl,
                        imageLoader = imageLoader,
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.24f))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CircleIconButton(
                        icon = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        onClick = onBack,
                    )
                    CircleIconButton(
                        icon = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        onClick = {},
                    )
                }
            }
        }

        item {
            ElevatedAssistChip(
                onClick = {},
                label = { Text(property.type.name.replace('_', ' ')) },
            )
        }

        item {
            Text(
                text = property.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFD9E2EF)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF1B2C43),
                            )
                            Text(
                                text = "4.9",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(start = 2.dp),
                            )
                        }
                    }
                    Text(
                        text = "(128 reseñas)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                Text(
                    text = "USD ${formatPrice(property.pricePerNight)} / noche",
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentOrange,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        if (property.description.isNotBlank()) {
            item {
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            item {
                Text(
                    text = property.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Text(
                text = "Lo que ofrece",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(property.amenities.ifEmpty { defaultAmenities }, key = { it }) { amenity ->
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF4F1EC)) {
                        Text(
                            text = amenity,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip(icon = Icons.Default.Bed, text = "${property.bedrooms} hab")
                StatChip(icon = Icons.Default.People, text = "${property.maxGuests} huésp")
            }
        }

        item {
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF8F5EF)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFD3DCE9)),
                    )
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = "Anfitrión: Carlos M.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Superhost · Responde en 1 hora",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFD3DCE9),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(height = 100.dp, width = 300.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "A 3 min caminando de la playa",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingBottomBar(
    property: Property,
    onBookNow: () -> Unit,
) {
    Surface(color = BeigeBackground, tonalElevation = 0.dp) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "USD ${formatPrice(property.pricePerNight)} / noche",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "14 - 18 Jul · 4 noches",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Button(onClick = onBookNow, shape = RoundedCornerShape(999.dp)) {
                    Text("Reservar")
                }
            }
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    Surface(shape = RoundedCornerShape(999.dp), color = Color.White.copy(alpha = 0.92f)) {
        IconButton(onClick = onClick, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color(0xFF332E2A),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun StatChip(icon: ImageVector, text: String) {
    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF4F1EC)) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

private val defaultAmenities = listOf(
    "Piscina privada",
    "Vista al mar",
    "Parrilla",
    "Wi-Fi rapido",
)

private val BeigeBackground = Color(0xFFF3EEE7)
private val AccentOrange = Color(0xFFD3885D)

private fun formatPrice(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        val cents = (value * 100).toInt()
        val whole = cents / 100
        val decimals = (cents % 100).toString().padStart(2, '0')
        "$whole.$decimals"
    }
}

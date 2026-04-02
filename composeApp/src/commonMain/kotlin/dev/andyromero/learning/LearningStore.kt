package dev.andyromero.learning

import dev.andyromero.clock.ClockProvider
import dev.andyromero.clock.PlatformClockProvider
import dev.andyromero.getPlatform

data class LearningUiState(
    val currentDay: Int,
    val stepTitle: String,
    val stepGoal: String,
    val currentConceptHint: String,
    val showConceptHints: Boolean,
    val platformSummary: String,
    val lastRefreshEpochSeconds: Long,
)

sealed interface LearningIntent {
    data object NextDay : LearningIntent
    data object ToggleConcepts : LearningIntent
    data object RefreshClock : LearningIntent
}

data class LearningEffect(val message: String)

data class LearningUpdate(
    val state: LearningUiState,
    val effect: LearningEffect? = null,
)

data class LearningStep(
    val title: String,
    val goal: String,
    val conceptHint: String,
)

interface LearningTipRepository {
    fun steps(): List<LearningStep>
}

class StaticLearningTipRepository : LearningTipRepository {
    override fun steps(): List<LearningStep> = listOf(
        LearningStep(
            title = "Dia 1: Anatomia KMP",
            goal = "Mapear targets y source sets para entender que es comun y que es nativo.",
            conceptHint = "commonMain comparte logica/UI; <target>Main encapsula SDK y APIs nativas.",
        ),
        LearningStep(
            title = "Dia 2: Source sets",
            goal = "Mover una parte de la UI o logica a commonMain sin dependencias Android-only.",
            conceptHint = "Si un import no existe en iOS/JS, ese codigo no debe vivir en commonMain.",
        ),
        LearningStep(
            title = "Dia 3: expect/actual",
            goal = "Crear contrato comun y resolver implementaciones por plataforma.",
            conceptHint = "Usa expect para el contrato y actual para SDK nativo en cada target.",
        ),
        LearningStep(
            title = "Dia 4: Estado compartido",
            goal = "Gestionar estado con un flujo MVI simple y unidireccional.",
            conceptHint = "State inmutable + intents/eventos + efectos one-shot para UI.",
        ),
        LearningStep(
            title = "Dia 5: Recursos y limites",
            goal = "Usar Res.string/Res.drawable y separar navegacion host-specific.",
            conceptHint = "La pantalla se comparte; el host (Android/iOS/Desktop/Web) integra entrada/salida.",
        ),
        LearningStep(
            title = "Dia 6: Testing",
            goal = "Cubrir logica compartida en commonTest y dejar tests por target solo si aplica.",
            conceptHint = "La mayor parte de regresiones en KMP se detecta temprano con commonTest.",
        ),
        LearningStep(
            title = "Dia 7: Hardening",
            goal = "Revisar build matrix, dependencias por source set y performance base.",
            conceptHint = "No cierres una feature sin compilar Android+iOS+JVM y revisar web targets.",
        ),
    )
}

class LearningStore(
    private val clockProvider: ClockProvider = PlatformClockProvider(),
    private val tips: LearningTipRepository = StaticLearningTipRepository(),
) {
    private val steps: List<LearningStep> = tips.steps()

    fun initialState(): LearningUiState {
        val firstStep = steps.first()
        return LearningUiState(
            currentDay = 1,
            stepTitle = firstStep.title,
            stepGoal = firstStep.goal,
            currentConceptHint = firstStep.conceptHint,
            showConceptHints = false,
            platformSummary = "Corriendo en ${getPlatform().name}",
            lastRefreshEpochSeconds = clockProvider.nowEpochSeconds(),
        )
    }

    fun reduce(state: LearningUiState, intent: LearningIntent): LearningUpdate {
        return when (intent) {
            LearningIntent.NextDay -> {
                val nextDay = if (state.currentDay == steps.size) 1 else state.currentDay + 1
                val step = steps[nextDay - 1]
                LearningUpdate(
                    state = state.copy(
                        currentDay = nextDay,
                        stepTitle = step.title,
                        stepGoal = step.goal,
                        currentConceptHint = step.conceptHint,
                    ),
                    effect = LearningEffect("Cambiado a ${step.title}"),
                )
            }

            LearningIntent.ToggleConcepts -> LearningUpdate(
                state = state.copy(showConceptHints = !state.showConceptHints),
                effect = LearningEffect(
                    if (state.showConceptHints) "Pistas ocultas" else "Pistas visibles",
                ),
            )

            LearningIntent.RefreshClock -> {
                val refreshed = clockProvider.nowEpochSeconds()
                LearningUpdate(
                    state = state.copy(lastRefreshEpochSeconds = refreshed),
                    effect = LearningEffect("Reloj actualizado"),
                )
            }
        }
    }
}


package dev.mcd.chess.ui.screen.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.feature.common.domain.AppColorScheme
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.common.domain.Environment
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val environment: Environment,
) : ViewModel(), ContainerHost<SettingsViewModel.State, SettingsViewModel.SideEffect> {

    override val container = container<State, SideEffect>(State()) {
        intent {
            val host = appPreferences.host()
            val prefillHosts = listOf(
                environment.apiUrl,
                "http://10.0.2.2:8080",
            )

            val colorScheme = appPreferences.colorScheme()?.let {
                AppColorScheme.valueOf(it)
            } ?: AppColorScheme.default()

            val soundsEnabled = appPreferences.soundsEnabled()

            reduce {
                state.copy(
                    debugModel = DebugModel(
                        host = host,
                        prefillHosts = prefillHosts,
                    ),
                    showDebug = BuildConfig.DEBUG,
                    colorScheme = colorScheme,
                    soundsEnabled = soundsEnabled,
                )
            }
        }
    }

    fun setColorScheme(colorScheme: AppColorScheme) {
        intent {
            appPreferences.setColorScheme(colorScheme.name)
            reduce { state.copy(colorScheme = colorScheme) }
        }
    }

    fun setHost(host: String) {
        intent {
            appPreferences.setHost(host)
            reduce { state.copy(debugModel = state.debugModel.copy(host = host)) }
        }
    }

    fun setSoundsEnabled(soundsEnabled: Boolean) {
        intent {
            appPreferences.setSoundsEnabled(soundsEnabled)
            reduce { state.copy(soundsEnabled = soundsEnabled) }
        }
    }

    fun clearAuthData() {
        intent {
            appPreferences.storeToken(token = null)
            appPreferences.storeUserId(userId = null)
        }
    }

    object SideEffect

    @Stable
    data class State(
        val debugModel: DebugModel = DebugModel(),
        val showDebug: Boolean = false,
        val colorScheme: AppColorScheme = AppColorScheme.default(),
        val soundsEnabled: Boolean = false,
    )

    @Stable
    data class DebugModel(
        val host: String = "",
        val prefillHosts: List<String> = emptyList(),
    )
}

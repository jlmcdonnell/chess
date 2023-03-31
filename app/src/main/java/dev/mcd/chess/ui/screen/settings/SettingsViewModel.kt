package dev.mcd.chess.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.BuildConfig
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
) : ViewModel(), ContainerHost<SettingsViewModel.State, SettingsViewModel.SideEffect> {

    override val container = container<State, SideEffect>(State()) {
        intent {
            val host = appPreferences.host()
            val prefillHosts = listOf(
                Environment.Production.apiUrl,
                "http://10.0.2.2:8080",
            )
            reduce {
                state.copy(
                    host = host,
                    prefillHosts = prefillHosts,
                    showDebug = BuildConfig.DEBUG,
                )
            }
        }
    }

    fun updateHost(host: String) {
        intent {
            appPreferences.setHost(host)
            reduce { state.copy(host = host) }
        }
    }

    fun clearAuthData() {
        intent {
            appPreferences.storeToken(token = null)
            appPreferences.storeUserId(userId = null)
        }
    }

    object SideEffect

    data class State(
        val host: String = "",
        val prefillHosts: List<String> = emptyList(),
        val showDebug: Boolean = false,
    )
}

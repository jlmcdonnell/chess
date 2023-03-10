package dev.mcd.chess.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.data.api.ApiCredentialsStore
import dev.mcd.chess.domain.Environment
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.prefs.AppPreferences
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val apiCredentialsStore: ApiCredentialsStore,
) : ViewModel(), ContainerHost<SettingsViewModel.State, SettingsViewModel.SideEffect> {

    override val container = container<State, SideEffect>(State()) {
        intent {
            val host = appPreferences.host()
            reduce {
                state.copy(
                    host = host,
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
            apiCredentialsStore.clear()
        }
    }

    object SideEffect

    data class State(
        val productionUrl: String = Environment.Production.apiUrl,
        val host: String = "",
        val showDebug: Boolean = false,
    )
}

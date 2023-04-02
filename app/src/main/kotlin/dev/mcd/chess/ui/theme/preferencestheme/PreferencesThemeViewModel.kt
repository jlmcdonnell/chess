package dev.mcd.chess.ui.theme.preferencestheme

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.feature.common.domain.AppColorScheme
import dev.mcd.chess.feature.common.domain.AppPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PreferencesThemeViewModel @Inject constructor(
    preferences: AppPreferences,
) : ViewModel(), ContainerHost<PreferencesThemeViewModel.State, Unit> {

    override val container = container<State, Unit>(State()) {
        intent {
            preferences.colorSchemeUpdates()
                .map {
                    it?.let {
                        AppColorScheme.valueOf(it)
                    } ?: AppColorScheme.default()
                }
                .collectLatest { colorScheme ->
                    reduce { state.copy(colorScheme = colorScheme) }
                }
        }
    }

    data class State(
        val colorScheme: AppColorScheme? = null,
    )
}

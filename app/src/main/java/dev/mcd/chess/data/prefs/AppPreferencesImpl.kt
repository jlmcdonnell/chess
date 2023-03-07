package dev.mcd.chess.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.domain.prefs.AppPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("prefs")

class AppPreferencesImpl @Inject constructor(@ApplicationContext context: Context) :
    AppPreferences {

    private val store = context.dataStore
    private val hostKey = stringPreferencesKey("debug-host")

    override suspend fun setHost(host: String) {
        store.edit { it[hostKey] = host }
    }

    override suspend fun host(): String = store.data.first()[hostKey] ?: ""
}

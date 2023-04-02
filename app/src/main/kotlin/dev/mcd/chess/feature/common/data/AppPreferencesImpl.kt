package dev.mcd.chess.feature.common.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.feature.common.domain.AppPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("prefs")

class AppPreferencesImpl @Inject constructor(@ApplicationContext context: Context) :
    AppPreferences {

    private val store = context.dataStore
    private val hostKey = stringPreferencesKey("debug-host")
    private val clickToMove = booleanPreferencesKey("click-to-move")
    private val tokenKey = stringPreferencesKey("token")
    private val userKey = stringPreferencesKey("user")

    override suspend fun setHost(host: String) {
        store.edit { it[hostKey] = host }
    }

    override suspend fun host(): String = store.data.first()[hostKey] ?: ""

    override suspend fun clickToMove(): Boolean {
        return store.data.first()[clickToMove] ?: false
    }

    override suspend fun setClickToMove(enabled: Boolean) {
        store.edit { it[clickToMove] = enabled }
    }

    override suspend fun storeToken(token: String?) {
        store.edit { prefs ->
            token?.let { prefs[tokenKey] = token } ?: run { prefs.remove(tokenKey) }
        }
    }

    override suspend fun storeUserId(userId: UserId?) {
        store.edit { prefs ->
            userId?.let { prefs[userKey] = userId } ?: run { prefs.remove(userKey) }
        }
    }

    override suspend fun userId(): UserId? {
        return store.data.first()[userKey]
    }

    override suspend fun token(): String? {
        return store.data.first()[tokenKey]
    }

    override suspend fun clear() {
        store.edit { it.clear() }
    }
}

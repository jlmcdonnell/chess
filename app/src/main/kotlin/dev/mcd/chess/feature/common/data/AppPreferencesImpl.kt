package dev.mcd.chess.feature.common.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.feature.common.domain.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("prefs")

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context,
) : AppPreferences {

    private val store = context.dataStore
    private val hostKey = stringPreferencesKey("debug-host")
    private val clickToMove = booleanPreferencesKey("click-to-move")
    private val tokenKey = stringPreferencesKey("token")
    private val userKey = stringPreferencesKey("user")
    private val colorSchemeKey = stringPreferencesKey("color-scheme")
    private val soundsEnabledKey = booleanPreferencesKey("sounds-enabled")
    private val puzzleRatingRangeStartKey = intPreferencesKey("rating-range-start")
    private val puzzleRatingRangeEndKey = intPreferencesKey("rating-range-end")

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

    override suspend fun colorSchemeUpdates(): Flow<String?> {
        return store.data.map { it[colorSchemeKey] }
    }

    override suspend fun colorScheme(): String? {
        return store.data.first()[colorSchemeKey]
    }

    override suspend fun setColorScheme(colorScheme: String) {
        store.edit { it[colorSchemeKey] = colorScheme }
    }

    override suspend fun setSoundsEnabled(enabled: Boolean) {
        store.edit { it[soundsEnabledKey] = enabled }
    }

    override suspend fun soundsEnabled(): Boolean {
        return store.data.first()[soundsEnabledKey] ?: false
    }

    override suspend fun setPuzzleRatingRange(range: IntRange) {
        store.edit {
            it[puzzleRatingRangeStartKey] = range.first
            it[puzzleRatingRangeEndKey] = range.last
        }
    }

    override suspend fun puzzleRatingRange(): IntRange {
        return store.data.first().let {
            val start = it[puzzleRatingRangeStartKey] ?: 0
            val end = it[puzzleRatingRangeEndKey] ?: 0
            start..end
        }
    }

    override suspend fun clear() {
        store.edit { it.clear() }
    }
}

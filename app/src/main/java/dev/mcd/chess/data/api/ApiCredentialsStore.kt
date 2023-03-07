package dev.mcd.chess.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.mcd.chess.domain.player.UserId
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface ApiCredentialsStore {
    suspend fun storeToken(token: String)
    suspend fun storeUserId(userId: UserId)
    suspend fun userId(): UserId?
    suspend fun token(): String?
    suspend fun clear()
}

private val Context.dataStore by preferencesDataStore(name = "api-prefs")

class ApiCredentialsStoreImpl @Inject constructor(
    context: Context,
) : ApiCredentialsStore {
    private val store = context.dataStore

    private val tokenKey = stringPreferencesKey("token")
    private val userKey = stringPreferencesKey("user")

    override suspend fun storeToken(token: String) {
        store.edit { it[tokenKey] = token }
    }

    override suspend fun storeUserId(userId: UserId) {
        store.edit { it[userKey] = userId }
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

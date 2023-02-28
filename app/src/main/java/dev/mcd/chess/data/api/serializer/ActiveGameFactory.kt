package dev.mcd.chess.data.api.serializer

import dev.mcd.chess.domain.api.ActiveGame
import dev.mcd.chess.domain.game.GameMessage
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

typealias ActiveGameFactory = (SendChannel<String>, ReceiveChannel<GameMessage>) -> ActiveGame

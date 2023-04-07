package dev.mcd.chess.engine.lc0

enum class MaiaWeights(
    val asset: String,
    val elo: Int,
) {
    ELO_1100(
        asset = "weights/maia-1100.pb",
        elo = 1100,
    ),
    ELO_1200(
        asset = "weights/maia-1200.pb",
        elo = 1200,
    ),
    ELO_1300(
        asset = "weights/maia-1300.pb",
        elo = 1300,
    ),
    ELO_1400(
        asset = "weights/maia-1400.pb",
        elo = 1400,
    ),
    ELO_1900(
        asset = "weights/maia-1900.pb.gz",
        elo = 1900,
    ),
}

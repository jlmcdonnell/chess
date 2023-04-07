package dev.mcd.chess.engine.lc0

enum class MaiaWeights(
    val asset: String,
) {
    ELO_1100(asset = "weights/maia-1100.pb"),
    ELO_1200(asset = "weights/maia-1200.pb"),
    ELO_1300(asset = "weights/maia-1300.pb"),
    ELO_1400(asset = "weights/maia-1400.pb"),
    ELO_1900(asset = "weights/maia-1900.pb"),
}

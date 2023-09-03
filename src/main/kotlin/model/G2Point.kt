package model

data class G2Point(
    val info: G2ModelInfo = G2ModelInfo.NONE,
    val x: Int = 0,
    val y: Int = 0
) {

    fun isEmpty() = x == 0 && y == 0
}

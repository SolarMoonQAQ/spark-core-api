package cn.solarmoon.sparkcore.api.util

import cn.solarmoon.sparkcore.api.math.Int3
import kotlinx.serialization.Serializable

@Serializable
enum class Direction(val normal: Int3) {
    NORTH(Int3(0, 0, -1)),
    SOUTH(Int3(0, 0, 1)),
    WEST(Int3(-1, 0, 0)),
    EAST(Int3(1, 0, 0)),
    UP(Int3(0, 1, 0)),
    DOWN(Int3(0, -1, 0));
}
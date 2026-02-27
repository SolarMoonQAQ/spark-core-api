package cn.solarmoon.sparkcore.api.geometry.model

import cn.solarmoon.sparkcore.api.util.Direction
import cn.solarmoon.sparkcore.api.math.Float2
import cn.solarmoon.sparkcore.api.math.Float3
import cn.solarmoon.sparkcore.api.math.Mat4
import cn.solarmoon.sparkcore.api.math.Quaternionf
import cn.solarmoon.sparkcore.api.math.cartesianProduct
import cn.solarmoon.sparkcore.api.math.rotate
import cn.solarmoon.sparkcore.api.math.rotateLocalZYX
import cn.solarmoon.sparkcore.api.math.toFloat
import cn.solarmoon.sparkcore.api.math.translate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 顶点顺序规范 (Corners):
 *
 * ```
 *             topLeftBack (2) ----------- topRightBack (3)
 *                /|                          /|
 *               / |                         / |
 *              /  |                        /  |
 *   topLeftFront (4) ------ topRightFront (5) |
 *             |   |                       |   |
 *             |   |                       |   |
 *             |   |                       |   |
 *             |  bottomLeftBack (0) ------|-- bottomRightBack (1)
 *             |  /                        |  /
 *             | /                         | /
 *             |/                          |/
 *   bottomLeftFront (6) -------- bottomRightFront (7)
 * ```
 *
 * 八个角点：
 *   (0) bottomLeftBack
 *   (1) bottomRightBack
 *   (2) topLeftBack
 *   (3) topRightBack
 *   (4) topLeftFront
 *   (5) topRightFront
 *   (6) bottomLeftFront
 *   (7) bottomRightFront
 *
 * 十二条边：
 *   底面: (0–1), (1–7), (7–6), (6–0)
 *   顶面: (2–3), (3–5), (5–4), (4–2)
 *   竖直: (0–2), (1–3), (7–5), (6–4)
 */
@Serializable
data class Cube(
    val origin: Float3,
    val pivot: Float3 = Float3(),
    val rotation: Float3 = Float3(),
    val size: Float3,
    val inflate: Float = 0f,
    @SerialName("uv") val uvUnion: UVUnion,
    val mirror: Boolean = false,
) {

    @Transient
    val rotationQuaternion = Quaternionf().rotateLocalZYX(rotation)

    @Transient
    val transform = Mat4()
        .translate(origin)
        .translate(pivot)
        .rotate(rotationQuaternion)
        .translate(-pivot)

    internal val corners by lazy {
        val xs = listOf(origin.x - inflate, origin.x + size.x + inflate)
        val ys = listOf(origin.y - inflate, origin.y + size.y + inflate)
        val zs = listOf(origin.z - inflate, origin.z + size.z + inflate)
        val coords = cartesianProduct(listOf(xs, ys, zs)).map { (x, y, z) ->
            Float3(x, y, z)
        }
        Corners(
            coords[0],
            coords[4],
            coords[2],
            coords[6],
            coords[3],
            coords[7],
            coords[1],
            coords[5]
        )
    }

    internal data class Corners(
        val bottomLeftBack: Float3,   // 0
        val bottomRightBack: Float3,  // 1
        val topLeftBack: Float3,      // 2
        val topRightBack: Float3,     // 3
        val topLeftFront: Float3,     // 4
        val topRightFront: Float3,    // 5
        val bottomLeftFront: Float3,  // 6
        val bottomRightFront: Float3  // 7
    ) {
        // 左下 → 右下 → 右上 → 左上
        val quadWest  get() = listOf(bottomLeftBack, bottomRightBack, topRightBack, topLeftBack)
        val quadEast  get() = listOf(bottomRightBack, bottomRightFront, topRightFront, topRightBack)
        val quadNorth get() = listOf(bottomLeftBack, bottomLeftFront, topLeftFront, topLeftBack)
        val quadSouth get() = listOf(bottomRightBack, bottomRightFront, topRightFront, topRightBack)
        val quadUp    get() = listOf(topLeftBack, topRightBack, topRightFront, topLeftFront)
        val quadDown  get() = listOf(bottomLeftBack, bottomRightBack, bottomRightFront, bottomLeftFront)
    }

}

fun Cube.getQuadPositions(direction: Direction) = with(corners) {
    when (direction) {
        Direction.WEST -> if (mirror) quadEast else quadWest
        Direction.EAST -> if (mirror) quadWest else quadEast
        Direction.NORTH -> quadNorth
        Direction.SOUTH -> quadSouth
        Direction.UP -> if (mirror && uvUnion is UVUnion.Face) quadDown else quadUp
        Direction.DOWN -> if (mirror && uvUnion is UVUnion.Face) quadUp else quadDown
    }
}

fun Cube.getQuadVertices(direction: Direction, meta: ModelDescription): List<Vertex> {
    val poses = getQuadPositions(direction)
    val normal = direction.normal.toFloat()
    return when(uvUnion) {
        is UVUnion.Box -> {
            val sx = size.x
            val sy = size.y
            val sz = size.z
            val ux = uvUnion.uv.x
            val uy = uvUnion.uv.y

            // 按BB矩形展开规范获取每个面的矩形区域（左上起点+矩形长宽）
            val (u, v, w, h) = when (direction) {
                Direction.EAST  -> listOf(ux, uy + sz, sz, sy)
                Direction.WEST  -> listOf(ux + sx + sz, uy + sz, sz, sy)
                Direction.NORTH -> listOf(ux + sz, uy + sz, sx, sy)
                Direction.SOUTH -> listOf(ux + sx + sz + sz, uy + sz, sx, sy)
                Direction.UP    -> listOf(ux + sz, uy, sx, sz)
                Direction.DOWN  -> listOf(ux + sz + sx, uy, sx, sz)
            }

            // 计算矩形四边
            val u1o = u
            val u2o = u + w
            val v1o = v
            val v2o = v + h

            // 应用 mirror
            val (u1, u2) = if (mirror) u2o to u1o else u1o to u2o
            val (v1, v2) = v1o to v2o

            val uvList = listOf(
                Float2(u1, v1),
                Float2(u2, v1),
                Float2(u2, v2),
                Float2(u1, v2)
            )

            poses.zip(uvList) { pos, uv ->
                Vertex(pos, uv, normal)
            }
        }
        is UVUnion.Face -> {
            val faceUV = uvUnion.uvMap[direction]!!
            val u1o = faceUV.uv.x / meta.textureWidth
            val v1o = faceUV.uv.y / meta.textureHeight
            val u2o = (faceUV.uv.x + faceUV.uvSize.x) / meta.textureWidth
            val v2o = (faceUV.uv.y + faceUV.uvSize.y) / meta.textureHeight

            // 应用 mirror
            val (u1, u2) = if (mirror) u2o to u1o else u1o to u2o
            val (v1, v2) = v1o to v2o

            val uvList = listOf(
                Float2(u1, v1),
                Float2(u2, v1),
                Float2(u2, v2),
                Float2(u1, v2)
            )

            poses.zip(uvList) { pos, uv ->
                Vertex(pos, uv, normal)
            }
        }
    }
}
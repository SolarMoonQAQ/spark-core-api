package cn.solarmoon.sparkcore.api.math

import kotlin.math.pow

/**
 * 任意阶笛卡尔积
 */
fun <T> cartesianProduct(lists: List<List<T>>): List<List<T>> =
    lists.fold(listOf(emptyList())) { acc, list ->
        acc.flatMap { prefix ->
            list.map { element -> prefix + element }
        }
    }

/**
 * 插值算法
 */
object Interpolations {

    /**
     * 线性插值
     *
     * @param t 插值进度，范围通常在 [0.0, 1.0]
     * @param a 起始值
     * @param b 结束值
     * @return 在进度 t 下的插值结果
     */
    fun linear(t: Float, a: Float, b: Float): Float =
        a + (b - a) * t

    /**
     * 二次插值 (Quadratic)
     *
     * @param t 插值进度，范围通常在 [0.0, 1.0]
     * @param a 第一个控制点的值
     * @param b 第二个控制点的值
     * @param c 第三个控制点的值
     * @return 在进度 t 下的二次插值结果
     */
    fun quadratic(t: Float, a: Float, b: Float, c: Float): Float {
        val u = 1 - t
        return u * u * a + 2 * u * t * b + t * t * c
    }

    /**
     * 三次插值 (Cubic Bezier)
     *
     * @param t 插值进度，范围通常在 [0.0, 1.0]
     * @param a 起点值 (Bezier 曲线的第一个控制点)
     * @param b 控制点 1 的值
     * @param c 控制点 2 的值
     * @param d 终点值 (Bezier 曲线的最后一个控制点)
     * @return 在进度 t 下的三次 Bezier 插值结果
     */
    fun cubicBezier(t: Float, a: Float, b: Float, c: Float, d: Float): Float {
        val u = 1 - t
        return u.pow(3) * a +
                3 * u.pow(2) * t * b +
                3 * u * t.pow(2) * c +
                t.pow(3) * d
    }

    /**
     * Catmull-Rom 样条插值
     *
     * @param t 插值进度，范围通常在 [0.0, 1.0]
     * @param p0 前一个点的值
     * @param p1 当前点的值
     * @param p2 下一个点的值
     * @param p3 下下一个点的值
     * @return 在进度 t 下的 Catmull-Rom 样条插值结果
     */
    fun catmullRom(t: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
        val t2 = t * t
        val t3 = t2 * t
        return 0.5f * (
                (2 * p1) +
                        (-p0 + p2) * t +
                        (2 * p0 - 5 * p1 + 4 * p2 - p3) * t2 +
                        (-p0 + 3 * p1 - 3 * p2 + p3) * t3
                )
    }

    /**
     * Hermite 插值 (带张力/偏移参数)
     *
     * @param t 插值进度，范围通常在 [0.0, 1.0]
     * @param p0 起点值
     * @param m0 起点的切线/斜率 (控制曲线形状)
     * @param p1 终点值
     * @param m1 终点的切线/斜率 (控制曲线形状)
     * @return 在进度 t 下的 Hermite 插值结果
     */
    fun hermite(t: Float, p0: Float, m0: Float, p1: Float, m1: Float): Float {
        val t2 = t * t
        val t3 = t2 * t
        val h00 = 2 * t3 - 3 * t2 + 1
        val h10 = t3 - 2 * t2 + t
        val h01 = -2 * t3 + 3 * t2
        val h11 = t3 - t2
        return h00 * p0 + h10 * m0 + h01 * p1 + h11 * m1
    }
}


package cn.solarmoon.sparkcore.api

import cn.solarmoon.sparkcore.api.ffi.slice_ref_uint8_t
import cn.solarmoon.sparkcore.api.ffi.spark_core_h
import java.lang.AutoCloseable
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class GeoEngine : AutoCloseable {

    private val arena = Arena.ofShared()
    private val animations = mutableSetOf<MemorySegment>()

    fun loadAnimation(json: ByteArray): MemorySegment {
        val nativeString = arena.allocateFrom(ValueLayout.JAVA_BYTE, *json)

        val cSlice = slice_ref_uint8_t.allocate(arena)
        slice_ref_uint8_t.ptr(cSlice, nativeString)
        slice_ref_uint8_t.len(cSlice, json.size.toLong())

        val animPtr = spark_core_h.load_animation_file(cSlice)

        if (animPtr == MemorySegment.NULL) {
            throw IllegalStateException("Rust引擎加载动画失败")
        }

        animations.add(animPtr)
        return animPtr
    }

    override fun close() {
        animations.forEach { spark_core_h.free_animation_file(it) }
    }
}
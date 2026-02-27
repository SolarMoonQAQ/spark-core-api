package cn.solarmoon.sparkcore.api.ecs.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class TickCountComponent(
    var count: Int = 0
) : Component<TickCountComponent> {
    override fun type() = TickCountComponent

    companion object : ComponentType<TickCountComponent>()
}
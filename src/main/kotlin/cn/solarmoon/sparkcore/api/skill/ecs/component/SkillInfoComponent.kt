package cn.solarmoon.sparkcore.api.skill.ecs.component

import cn.solarmoon.sparkcore.api.util.Identifier
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class SkillInfoComponent(
    val id: Identifier
) : Component<SkillInfoComponent> {
    override fun type() = SkillInfoComponent

    companion object : ComponentType<SkillInfoComponent>()
}
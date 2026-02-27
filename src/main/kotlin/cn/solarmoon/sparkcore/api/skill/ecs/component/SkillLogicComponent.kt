package cn.solarmoon.sparkcore.api.skill.ecs.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

data class SkillLogicComponent(
    val onAdd: List<World.(Entity) -> Unit>,
    val onUpdate: List<World.(Entity) -> Unit>,
    val onRemove: List<World.(Entity) -> Unit>
): Component<SkillLogicComponent> {

    override fun World.onAdd(entity: Entity) {
        onAdd.forEach { it(entity) }
    }

    override fun World.onRemove(entity: Entity) {
        onRemove.forEach { it(entity) }
        entity.configure {
            it -= SkillInfoComponent
        }
    }

    override fun type() = SkillLogicComponent
    companion object : ComponentType<SkillLogicComponent>()
}
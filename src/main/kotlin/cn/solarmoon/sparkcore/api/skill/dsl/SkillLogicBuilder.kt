package cn.solarmoon.sparkcore.api.skill.dsl

import cn.solarmoon.sparkcore.api.skill.ecs.component.SkillLogicComponent
import cn.solarmoon.sparkcore.api.util.Identifier
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

@SkillDsl
class SkillLogicBuilder {
    private val onAdd = mutableListOf<World.(Entity) -> Unit>()
    private val onUpdate = mutableListOf<World.(Entity) -> Unit>()
    private val onRemove = mutableListOf<World.(Entity) -> Unit>()

    fun onAdd(block: World.(Entity) -> Unit) { onAdd.add(block) }
    fun onUpdate(block: World.(Entity) -> Unit) { onUpdate.add(block) }
    fun onRemove(block: World.(Entity) -> Unit) { onRemove.add(block) }

    fun build() = SkillLogicComponent(
        onAdd.toList(),
        onUpdate.toList(),
        onRemove.toList()
    )
}
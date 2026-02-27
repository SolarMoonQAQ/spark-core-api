package cn.solarmoon.sparkcore.api.skill.ecs.system

import cn.solarmoon.sparkcore.api.ecs.component.TickCountComponent
import cn.solarmoon.sparkcore.api.skill.ecs.component.SkillLogicComponent
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family

class SkillLifeCycleSystem: IteratingSystem(
    family { all(SkillLogicComponent) }
) {
    override fun onTickEntity(entity: Entity) {
        entity[SkillLogicComponent].onUpdate.forEach {
            with(world) {
                it(entity)
            }
        }
        entity[TickCountComponent].count++
    }
}
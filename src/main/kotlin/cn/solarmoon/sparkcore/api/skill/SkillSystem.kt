package cn.solarmoon.sparkcore.api.skill

import cn.solarmoon.sparkcore.api.ecs.component.TickCountComponent
import cn.solarmoon.sparkcore.api.skill.ecs.component.SkillInfoComponent
import cn.solarmoon.sparkcore.api.skill.ecs.component.SkillLogicComponent
import cn.solarmoon.sparkcore.api.skill.ecs.system.SkillLifeCycleSystem
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateContext
import com.github.quillraven.fleks.configureWorld

class SkillSystem {

    val ecsWorld = configureWorld(5096) {
        systems {
            add(SkillLifeCycleSystem())
        }
    }

    fun cast(skill: SkillType, addon: EntityCreateContext.(Entity) -> Unit = {}) {
        ecsWorld.entity {
            it += skill.info
            it += skill.logic
            it += TickCountComponent()
            addon(it)
        }
    }

    fun tick(deltaTime: Float) {
        ecsWorld.update(deltaTime)
    }

}

data class SkillType(
    val info: SkillInfoComponent,
    val logic: SkillLogicComponent
)

typealias SkillFactory<T> = (T) -> SkillType

operator fun SkillFactory<Unit>.invoke(): SkillType = this(Unit)
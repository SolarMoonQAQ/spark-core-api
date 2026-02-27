package cn.solarmoon.sparkcore.api.skill.dsl

import cn.solarmoon.sparkcore.api.skill.SkillFactory
import cn.solarmoon.sparkcore.api.skill.SkillType
import cn.solarmoon.sparkcore.api.skill.ecs.component.SkillInfoComponent
import cn.solarmoon.sparkcore.api.util.Identifier

@DslMarker
annotation class SkillDsl

fun <T> skillFactory(id: Identifier, builder: SkillLogicBuilder.(T) -> Unit): SkillFactory<T> = {
    SkillType(SkillInfoComponent(id), SkillLogicBuilder().apply { builder(this, it) }.build())
}

fun skillFactory(id: Identifier, builder: SkillLogicBuilder.() -> Unit): SkillFactory<Unit> = {
    SkillType(SkillInfoComponent(id), SkillLogicBuilder().apply(builder).build())
}


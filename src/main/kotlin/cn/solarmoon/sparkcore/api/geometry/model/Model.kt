package cn.solarmoon.sparkcore.api.geometry.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(ModelSerializer::class)
data class Model(
    val description: ModelDescription,
    val bones: Map<String, Bone>
)

@Serializable
private data class ModelDTO(
    val description: ModelDescription,
    val bones: List<Bone>
)

private object ModelSerializer : KSerializer<Model> {
    override val descriptor: SerialDescriptor = ModelDTO.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Model) {
        val dto = ModelDTO(value.description, value.bones.values.toList())
        encoder.encodeSerializableValue(ModelDTO.serializer(), dto)
    }

    override fun deserialize(decoder: Decoder): Model {
        val dto = decoder.decodeSerializableValue(ModelDTO.serializer())
        return Model(
            dto.description,
            dto.bones.associateBy { it.name }
        )
    }
}

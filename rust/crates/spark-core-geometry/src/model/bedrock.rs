use crate::model::bone::Bone;
use crate::model::cube::Cube;
use crate::model::locator::Locator;
use crate::model::model::Model;
use crate::model::model_description::ModelDescription;
use crate::model::uv::{Direction, FaceUV, UVUnion};
use glam::{EulerRot, Quat};
use spark_core_geometry_bedrock::model::bone::BoneDto;
use spark_core_geometry_bedrock::model::cube::CubeDto;
use spark_core_geometry_bedrock::model::locator::LocatorDto;
use spark_core_geometry_bedrock::model::model::ModelDto;
use spark_core_geometry_bedrock::model::model_description::ModelDescriptionDto;
use spark_core_geometry_bedrock::model::uv::{DirectionDto, FaceUVDto, UVUnionDto};

impl From<FaceUVDto> for FaceUV {
    fn from(dto: FaceUVDto) -> Self {
        Self {
            uv: dto.uv,
            uv_size: dto.uv_size,
        }
    }
}

impl From<DirectionDto> for Direction {
    fn from(dto: DirectionDto) -> Self {
        match dto {
            DirectionDto::North => Direction::North,
            DirectionDto::South => Direction::South,
            DirectionDto::East => Direction::East,
            DirectionDto::West => Direction::West,
            DirectionDto::Up => Direction::Up,
            DirectionDto::Down => Direction::Down,
        }
    }
}

impl From<UVUnionDto> for UVUnion {
    fn from(dto: UVUnionDto) -> Self {
        match dto {
            UVUnionDto::Box(box_dto) => UVUnion::Box(box_dto),
            UVUnionDto::Face(map) => UVUnion::Face(
                map.into_iter()
                    .map(|(d, f)| (d.into(), f.into()))
                    .collect()
            ),
        }
    }
}

impl From<CubeDto> for Cube {
    fn from(dto: CubeDto) -> Self {
        Self {
            origin: dto.origin,
            pivot: dto.pivot,
            rotation: Quat::from_euler(EulerRot::ZYX, dto.rotation.z, dto.rotation.y, dto.rotation.x),
            size: dto.size,
            inflate: dto.inflate,
            mirror: dto.mirror,
            uv: dto.uv.into()
        }
    }
}

trait LocatorDtoExt {
    fn into_locator(self, name: String) -> Locator;
}

impl LocatorDtoExt for LocatorDto {
    fn into_locator(self, name: String) -> Locator {
        match self {
            LocatorDto::Simple(offset) => Locator { name, offset, rotation: Default::default() },
            LocatorDto::Full { offset, rotation } => { 
                let rotation = Quat::from_euler(EulerRot::ZYX, rotation.z, rotation.y, rotation.x);
                Locator { name, offset, rotation }
            },
        }
    }
}

impl From<BoneDto> for Bone {
    fn from(dto: BoneDto) -> Self {
        Self {
            name: dto.name,
            parent: dto.parent,
            pivot: dto.pivot,
            rotation: Quat::from_euler(EulerRot::ZYX, dto.rotation.z, dto.rotation.y, dto.rotation.x),
            cubes: dto.cubes
                .into_iter()
                .map(|c| c.into())
                .collect(),
            locators: dto.locators
                .into_iter()
                .map(|(name, l_dto)| (name.to_string(), l_dto.into_locator(name)))
                .collect(),
        }
    }
}

impl From<ModelDescriptionDto> for ModelDescription {
    fn from(dto: ModelDescriptionDto) -> Self {
        Self {
            texture_width: dto.texture_width,
            texture_height: dto.texture_height,
            identifier: dto.identifier
        }
    }
}

impl From<ModelDto> for Model {
    fn from(dto: ModelDto) -> Self {
        Self {
            description: dto.description.into(),
            bones: dto.bones.into_iter().map(|bone_dto| (bone_dto.name.to_string(), bone_dto.into())).collect(),
        }
    }
}
use crate::model::cube::Cube;
use crate::model::error::ModelError;
use crate::model::locator::Locator;
use glam::{EulerRot, Mat4, Quat, Vec3};
use indexmap::IndexMap;
#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize))]
#[derive(Debug, Clone)]
pub struct Bone {
    pub name: String,
    #[cfg_attr(feature = "serde", serde(skip_serializing_if = "Option::is_none"))]
    pub parent: Option<String>,
    #[cfg_attr(feature = "serde", serde(default))]
    pub pivot: Vec3,
    #[cfg_attr(feature = "serde", serde(default))]
    pub rotation: Quat,
    #[cfg_attr(feature = "serde", serde(default))]
    pub cubes: Vec<Cube>,
    #[cfg_attr(feature = "serde", serde(default, skip_serializing_if = "IndexMap::is_empty"))]
    pub locators: IndexMap<String, Locator>,
}

impl Bone {
    pub fn local_transform(&self) -> Mat4 {
        Mat4::from_translation(self.pivot)
            * Mat4::from_quat(self.rotation)
            * Mat4::from_translation(-self.pivot)
    }

    pub fn global_transform(&self, bones: &IndexMap<String, Bone>) -> Result<Mat4, ModelError> {
        let local = self.local_transform();
        match &self.parent {
            None => Ok(local),
            Some(parent_name) => {
                let parent_bone = bones.get(parent_name).ok_or_else(|| {
                    ModelError::ParentBoneNotFound {
                        bone_name: self.name.clone(),
                        parent_name: parent_name.clone(),
                    }
                })?;
                let parent_global = parent_bone.global_transform(bones)?;
                Ok(parent_global * local)
            }
        }
    }
}
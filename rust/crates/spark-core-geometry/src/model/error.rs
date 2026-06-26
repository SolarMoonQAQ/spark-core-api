use thiserror::Error;

#[derive(Error, Debug)]
pub enum ModelError {
    #[error("Bone '{bone_name}' relies on parent '{parent_name}', but it was not found in the model.")]
    ParentBoneNotFound {
        bone_name: String,
        parent_name: String,
    },
}
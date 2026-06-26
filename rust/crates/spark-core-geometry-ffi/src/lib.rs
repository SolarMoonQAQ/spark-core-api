use std::str::from_utf8;
#[cfg(feature = "headers")]
use safer_ffi::headers;
use safer_ffi::prelude::*;
use safer_ffi::slice::{slice_ref, Ref};
use spark_core_geometry::animation::animation_file::AnimationFile;

#[derive_ReprC]
#[repr(opaque)]
pub struct CAnimationFile(AnimationFile);

#[ffi_export]
pub fn load_animation_file(json_bytes: slice_ref<'_, u8>) -> Option<repr_c::Box<CAnimationFile>> {
    let json_slice = json_bytes.as_slice();
    let json_str = match from_utf8(json_slice) {
        Ok(s) => s,
        Err(e) => {
            eprintln!("JSON UTF-8 解码失败: {}", e);
            return None;
        }
    };

    match serde_json::from_str::<AnimationFile>(json_str) {
        Ok(anim_file) => {
            println!("{:#?}", anim_file);
            Some(ThinBox::new(CAnimationFile(anim_file)))
        }
        Err(e) => {
            eprintln!("JSON 解析失败: {}", e);
            None
        }
    }
}



#[ffi_export]
pub fn free_animation_file(anim: repr_c::Box<CAnimationFile>) {
    drop(anim);
}

#[cfg(feature = "headers")]
pub fn generate_headers() -> std::io::Result<()> {
    headers::builder()
        .to_file("spark_core.h")?
        .generate()
}
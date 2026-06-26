use serde::de::DeserializeOwned;
use spark_core_geometry::animation::animation_file::AnimationFile;
use std::fs::read_to_string;
use std::path::PathBuf;

fn load_test_json<T: DeserializeOwned>(dir: &str, name: &str) -> T {
    let path = PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .join("benches")
        .join(dir)
        .join(format!("{}.json", name));

    let json = read_to_string(path).expect("无法找到 JSON 文件");
    serde_json::from_str(&json).expect("JSON 解析失败")
}

#[test]
fn test() {
    let anim_file = load_test_json::<AnimationFile>("animation", "baby_water_buffalo");
    let anim = anim_file.animations.get("displaying").unwrap();
    println!("{:#?}", anim);
}

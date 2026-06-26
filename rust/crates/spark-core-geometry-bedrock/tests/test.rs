use std::collections::HashMap;
use spark_core_geometry_bedrock::animation::bone_animation::TrackDto;
use spark_core_geometry_bedrock::animation::keyframe::KeyframeDto;

#[test]
fn debug_track_dto() {
    let json = r#"{
       "0.0": [0, 0, 0],
       "0.7463": [0, 0.04, 0],
       "1.4925": [0, 0, 0]
    }"#;

    // 尝试解析 TrackDto
    let res: Result<TrackDto, _> = serde_json::from_str(json);

    match res {
        Ok(_) => println!("解析 TrackDto 成功!"),
        Err(e) => {
            println!("TrackDto 解析失败原因: {:?}", e);
        }
    }
}


#[test]
fn debug_track_dto_2() {
    let json = r#"
    {
		"post": [1, 1, 1],
		"lerp_mode": "catmullrom"
	}
	"#;

    // 尝试解析 TrackDto
    let res: Result<TrackDto, _> = serde_json::from_str(json);

    match res {
        Ok(_) => println!("解析 TrackDto 成功!"),
        Err(e) => {
            println!("TrackDto 解析失败原因: {:?}", e);
        }
    }
}

#[test]
fn debug_map_deserialization() {
    let json = r#"{
       "0.0": [0, 0, 0],
       "0.7463": [0, 0.04, 0],
       "1.4925": [0, 0, 0]
    }"#;

    // 不经过 TrackDto，直接尝试解析 HashMap
    let res: Result<HashMap<String, KeyframeDto>, _> = serde_json::from_str(json);

    match res {
        Ok(_) => println!("HashMap 解析成功！"),
        Err(e) => println!("HashMap 解析失败，具体原因: {}", e),
    }
}
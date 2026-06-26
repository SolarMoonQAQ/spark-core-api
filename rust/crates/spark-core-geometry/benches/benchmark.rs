use criterion::{criterion_group, criterion_main, BenchmarkId, Criterion};
use glam::Mat4;
use serde::de::DeserializeOwned;
use spark_core_geometry::animation::animation_file::AnimationFile;
use spark_core_geometry::model::geometry_file::GeometryFile;
use spark_core_geometry::model::vertex::Vertex;
use std::collections::HashMap;
use std::fs::read_to_string;
use std::hint::black_box;
use std::path::PathBuf;

// 假设你已经将 BakedBoneAnimation 引入
use spark_core_geometry::animation::bone_animation::BakedBoneAnimation;

fn load_test_json<T: DeserializeOwned>(dir: &str, name: &str) -> T {
    let path = PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .join("benches")
        .join(dir)
        .join(format!("{}.json", name));

    let json = read_to_string(path).expect("无法找到 JSON 文件");
    serde_json::from_str(&json).expect("JSON 解析失败")
}

struct StaticBoneMesh {
    bone_name: String,
    static_vertices: Vec<Vertex>,
}

fn bench_model_vertex_generation(c: &mut Criterion) {
    let anim_file = load_test_json::<AnimationFile>("animation", "baby_water_buffalo");
    let anim = anim_file.animations.get("displaying").unwrap();

    let model_file = load_test_json::<GeometryFile>("model", "baby_water_buffalo");
    let model = model_file.models.first().unwrap();
    let meta = &model.description;

    let cube_count: usize = model.bones.values().map(|bone| bone.cubes.len()).sum();
    let total_vertices = cube_count * 24;

    // ==========================================
    // 阶段 1：模拟【加载期】(执行一次，不计入每帧耗时)
    // ==========================================

    // 1A. 烘焙静态顶点
    let mut static_meshes = Vec::with_capacity(model.bones.len());
    for (bone_name, bone) in &model.bones {
        let vertices: Vec<Vertex> = bone
            .cubes
            .iter()
            .flat_map(|cube| cube.all_vertices(meta))
            .collect();

        static_meshes.push(StaticBoneMesh {
            bone_name: bone_name.clone(),
            static_vertices: vertices,
        });
    }

    // 1B. 烘焙动画曲线 (核心性能提升点)
    // 如果你的 anim 结构体里有 animation_length 字段，请替换这里的 2.0
    let duration = 2.0;
    let tick_step = 0.05; // 20 TPS

    let mut baked_anims: HashMap<String, BakedBoneAnimation> = HashMap::with_capacity(anim.bones.len());
    for (bone_name, bone_anim) in &anim.bones {
        baked_anims.insert(
            bone_name.clone(),
            bone_anim.bake(tick_step, duration)
        );
    }

    let mut group = c.benchmark_group("Animated_Model_Vertex_Generation");
    group.bench_with_input(
        BenchmarkId::from_parameter(format!("cube_count_{}", cube_count)),
        &cube_count,
        |b, &_count| {
            // ==========================================
            // 阶段 2：模拟【渲染期】(Java 传来的渲染参数)
            // ==========================================
            // 假设当前播放到 1.5 秒 (刚好是第 30 个逻辑 Tick)
            // 假设 partial_tick 为 0.5 (在两个逻辑 Tick 之间的渲染帧，考验插值性能)
            let current_tick = 30;
            let partial_tick = 0.5;

            b.iter(|| {
                // 提前分配好这一帧所有顶点的内存，拒绝扩容抖动
                let mut animated_vertices = Vec::with_capacity(total_vertices);

                for mesh in &static_meshes {
                    // 1. O(1) 极速获取并平滑计算当前动画矩阵
                    let transform = baked_anims
                        .get(&mesh.bone_name)
                        .map(|baked| baked.sample_smooth(current_tick, partial_tick))
                        .unwrap_or(Mat4::IDENTITY);

                    // 2. 疯狂应用矩阵乘法（极致性能的热点循环）
                    for static_vertex in &mesh.static_vertices {
                        let mut animated_vertex = *static_vertex;

                        // 将静态位置乘以骨骼的变换矩阵
                        animated_vertex.position = transform.transform_point3(static_vertex.position);

                        // 法线也要旋转 (如果光照需要)
                        // animated_vertex.normal = transform.transform_vector3(static_vertex.normal);

                        animated_vertices.push(animated_vertex);
                    }
                }

                black_box(animated_vertices);
            })
        },
    );
    group.finish();
}

criterion_group!(benches, bench_model_vertex_generation);
criterion_main!(benches);
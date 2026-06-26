use crate::model::model_description::ModelDescription;
use crate::model::uv::{Direction, UVUnion};
use crate::model::vertex::Vertex;
use glam::{Mat4, Quat, Vec2, Vec3};
#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize))]
#[derive(Debug, Clone, Copy)]
pub struct Cube {
    #[cfg_attr(feature = "serde", serde(default))]
    pub origin: Vec3,
    #[cfg_attr(feature = "serde", serde(default))]
    pub pivot: Vec3,
    #[cfg_attr(feature = "serde", serde(default))]
    pub rotation: Quat,
    #[cfg_attr(feature = "serde", serde(default))]
    pub size: Vec3,
    #[cfg_attr(feature = "serde", serde(default))]
    pub inflate: f32,
    #[cfg_attr(feature = "serde", serde(default))]
    pub mirror: bool,
    pub uv: UVUnion
}

impl Cube {
    pub fn local_transform(&self) -> Mat4 {
        Mat4::from_translation(self.origin)
            * Mat4::from_translation(self.pivot)
            * Mat4::from_quat(self.rotation)
            * Mat4::from_translation(-self.pivot)
    }

    /// 获取指定面的四个原始物理坐标
    fn quad_positions(&self, direction: Direction) -> [Vec3; 4] {
        let min = self.origin - Vec3::splat(self.inflate);
        let max = self.origin + self.size + Vec3::splat(self.inflate);
        let mut quad = match direction {
            Direction::North => [
                Vec3::new(max.x, min.y, min.z), Vec3::new(min.x, min.y, min.z),
                Vec3::new(min.x, max.y, min.z), Vec3::new(max.x, max.y, min.z),
            ],
            Direction::South => [
                Vec3::new(min.x, min.y, max.z), Vec3::new(max.x, min.y, max.z),
                Vec3::new(max.x, max.y, max.z), Vec3::new(min.x, max.y, max.z),
            ],
            Direction::West => [
                Vec3::new(min.x, min.y, min.z), Vec3::new(min.x, min.y, max.z),
                Vec3::new(min.x, max.y, max.z), Vec3::new(min.x, max.y, min.z),
            ],
            Direction::East => [
                Vec3::new(max.x, min.y, max.z), Vec3::new(max.x, min.y, min.z),
                Vec3::new(max.x, max.y, min.z), Vec3::new(max.x, max.y, max.z),
            ],
            Direction::Up => [
                Vec3::new(max.x, max.y, max.z), Vec3::new(max.x, max.y, min.z),
                Vec3::new(min.x, max.y, min.z), Vec3::new(min.x, max.y, max.z),
            ],
            Direction::Down => [
                Vec3::new(min.x, min.y, min.z), Vec3::new(max.x, min.y, min.z),
                Vec3::new(max.x, min.y, max.z), Vec3::new(min.x, min.y, max.z),
            ],
        };

        // 处理镜像互换
        if self.mirror {
            match direction {
                Direction::West | Direction::East => quad.reverse(),
                Direction::Up | Direction::Down if matches!(self.uv, UVUnion::Face { .. }) => quad.reverse(),
                _ => {}
            }
        }
        quad
    }

    /// 生成一个面的四个顶点
    pub fn quad_vertices(&self, direction: Direction, meta: &ModelDescription) -> [Vertex; 4] {
        let texture_width = meta.texture_width as f32;
        let texture_height = meta.texture_height as f32;

        let transform = self.local_transform();
        let mut positions = self.quad_positions(direction);
        let normal = direction.normal();

        for pos in &mut positions {
            *pos = transform.transform_point3(*pos);
        }

        let (u1o, v1o, u2o, v2o) = match &self.uv {
            UVUnion::Box(uv) => {
                let (sx, sy, sz) = (self.size.x, self.size.y, self.size.z);
                let (ux, uy) = (uv.x, uv.y);

                let (u, v, w, h) = match direction {
                    Direction::East  => (ux, uy + sz, sz, sy),
                    Direction::West  => (ux + sx + sz, uy + sz, sz, sy),
                    Direction::North => (ux + sz, uy + sz, sx, sy),
                    Direction::South => (ux + sx + sz + sz, uy + sz, sx, sy),
                    Direction::Up    => (ux + sz, uy, sx, sz),
                    Direction::Down  => (ux + sz + sx, uy, sx, sz),
                };

                (
                    u / texture_width,
                    v / texture_height,
                    (u + w) / texture_width,
                    (v + h) / texture_height
                )
            }

            UVUnion::Face(uv_map) => {
                let face_uv = uv_map[direction];
                (
                    face_uv.uv.x / texture_width,
                    face_uv.uv.y / texture_height,
                    (face_uv.uv.x + face_uv.uv_size.x) / texture_width,
                    (face_uv.uv.y + face_uv.uv_size.y) / texture_height,
                )
            }
        };

        let (u1, u2) = if self.mirror { (u2o, u1o) } else { (u1o, u2o) };
        let (v1, v2) = (v1o, v2o);

        let uvs = [
            Vec2::new(u1, v1),
            Vec2::new(u2, v1),
            Vec2::new(u2, v2),
            Vec2::new(u1, v2),
        ];

        [
            Vertex { position: positions[0], uv: uvs[0], normal },
            Vertex { position: positions[1], uv: uvs[1], normal },
            Vertex { position: positions[2], uv: uvs[2], normal },
            Vertex { position: positions[3], uv: uvs[3], normal },
        ]
    }

    pub fn all_vertices(&self, meta: &ModelDescription) -> [Vertex; 24] {
        let n = self.quad_vertices(Direction::North, meta);
        let s = self.quad_vertices(Direction::South, meta);
        let e = self.quad_vertices(Direction::East, meta);
        let w = self.quad_vertices(Direction::West, meta);
        let u = self.quad_vertices(Direction::Up, meta);
        let d = self.quad_vertices(Direction::Down, meta);

        [
            n[0], n[1], n[2], n[3],
            s[0], s[1], s[2], s[3],
            e[0], e[1], e[2], e[3],
            w[0], w[1], w[2], w[3],
            u[0], u[1], u[2], u[3],
            d[0], d[1], d[2], d[3],
        ]
    }

    pub fn bake(self, meta: &ModelDescription) -> BakedCubeData {
        let vertices = self.all_vertices(meta);
        BakedCubeData {
            cube: self,
            vertices
        }
    }
}

#[derive(Debug, Clone)]
pub struct BakedCubeData {
    pub cube: Cube,
    pub vertices: [Vertex; 24],
}
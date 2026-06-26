use crate::animation::keyframe_track::{KeyframeRotTrack, KeyframeTrack};
use glam::{Mat4, Quat, Vec3};
#[cfg(feature = "serde")]
use serde::Deserialize;
#[cfg(feature = "serde")]
use serde::Serialize;

#[cfg_attr(feature = "serde", derive(Deserialize, Serialize))]
#[derive(Debug, Clone)]
pub struct BoneAnimation {
    pub name: String,
    pub position: KeyframeTrack,
    pub rotation: KeyframeRotTrack,
    pub scale: KeyframeTrack,
}

impl BoneAnimation {
    pub fn sample_local_transform(&self, time: f32) -> Mat4 {
        let pos = self.position.clamped_sample(time).unwrap_or_default();
        let rot = self.rotation.clamped_sample(time).unwrap_or_default();
        let scale = self.scale.clamped_sample(time).unwrap_or(Vec3::ONE);
        Mat4::from_scale_rotation_translation(scale, rot, pos)
    }

    pub fn bake(&self, tick_step: f32, duration: f32) -> BakedBoneAnimation {
        // 1. 计算需要的总帧数。
        // 注意末尾的 + 1：如果 duration 是 1.0，步长 0.05，我们要采样 0.0 到 1.0，总共需要 21 帧
        let frame_count = (duration / tick_step).ceil() as usize + 1;
        let mut frames = Vec::with_capacity(frame_count);

        for i in 0..frame_count {
            // 计算当前采样的绝对时间。使用 .min(duration) 防止浮点数精度导致越界采样
            let time = (i as f32 * tick_step).min(duration);

            // 2. 利用你原本的 track 采样逻辑，提取该时刻的 TRS
            // 假设你前面的 KeyframeRotTrack 已经把欧拉角转为了 Quat
            let pos = self.position.clamped_sample(time).unwrap_or(Vec3::ZERO);
            let rot = self.rotation.clamped_sample(time).unwrap_or(Quat::IDENTITY);
            let scale = self.scale.clamped_sample(time).unwrap_or(Vec3::ONE);

            // 3. 压入烘焙数组
            frames.push(TransformFrame { pos, rot, scale });
        }

        BakedBoneAnimation {
            frames,
            tick_step,
        }
    }
}

#[derive(Debug, Clone, Copy)]
pub struct TransformFrame {
    pub pos: Vec3,
    pub rot: Quat,
    pub scale: Vec3,
}

pub struct BakedBoneAnimation {
    pub frames: Vec<TransformFrame>,
    pub tick_step: f32,
}

impl BakedBoneAnimation {
    pub fn sample_smooth(&self, current_tick: usize, partial_tick: f32) -> Mat4 {
        let len = self.frames.len();
        if len == 0 { return Mat4::IDENTITY; }

        let frame_a_idx = current_tick % len;
        let frame_b_idx = (current_tick + 1) % len;

        let f_a = &self.frames[frame_a_idx];
        let f_b = &self.frames[frame_b_idx];

        // 直接用 partial_tick 当作插值权重 alpha
        let pos = f_a.pos.lerp(f_b.pos, partial_tick);
        let rot = f_a.rot.slerp(f_b.rot, partial_tick);
        let scale = f_a.scale.lerp(f_b.scale, partial_tick);

        Mat4::from_scale_rotation_translation(scale, rot, pos)
    }
}


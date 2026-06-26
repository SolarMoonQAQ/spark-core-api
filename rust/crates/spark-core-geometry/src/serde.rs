use indexmap::IndexMap;
use serde::{Deserialize, Deserializer};
use std::collections::HashMap;
use std::hash::Hash;

// 1. 给特征加上泛型参数 K
pub trait InjectKey<K> {
    fn inject_key(&mut self, key: K);
}

// 2. HashMap 版本
// 增加泛型 K，并约束它必须支持反序列化、判等(Eq)、哈希(Hash)以及克隆(Clone)
pub fn deserialize_hashmap_inject_key<'de, D, K, V>(
    deserializer: D,
) -> Result<HashMap<K, V>, D::Error>
where
    D: Deserializer<'de>,
    K: Deserialize<'de> + Eq + Hash + Clone, // HashMap 的 Key 必须实现 Eq + Hash
    V: Deserialize<'de> + InjectKey<K>,
{
    let mut map = HashMap::<K, V>::deserialize(deserializer)?;
    for (k, v) in map.iter_mut() {
        v.inject_key(k.clone());
    }
    Ok(map)
}

// 3. IndexMap 版本
pub fn deserialize_indexmap_inject_key<'de, D, K, V>(
    deserializer: D,
) -> Result<IndexMap<K, V>, D::Error>
where
    D: Deserializer<'de>,
    K: Deserialize<'de> + Eq + Hash + Clone, // IndexMap 的 Key 也需要 Eq + Hash
    V: Deserialize<'de> + InjectKey<K>,
{
    let mut map = IndexMap::<K, V>::deserialize(deserializer)?;
    for (k, v) in map.iter_mut() {
        v.inject_key(k.clone());
    }
    Ok(map)
}
#[cfg(feature = "headers")]
use spark_core_geometry_ffi::generate_headers;

fn main() -> std::io::Result<()> {
    #[cfg(feature = "headers")]
    {
        println!("开始生成 C 头文件...");

        generate_headers()?;

        println!("生成成功！请检查项目根目录下的 spark_core.h");
    }

    #[cfg(not(feature = "headers"))]
    {
        eprintln!("错误: 必须开启 headers 特性才能生成文件！");
        eprintln!("请运行: cargo run --bin generate_headers --features headers");
    }

    Ok(())
}
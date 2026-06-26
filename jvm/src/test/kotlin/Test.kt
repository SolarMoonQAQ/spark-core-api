import cn.solarmoon.sparkcore.api.GeoEngine
import cn.solarmoon.sparkcore.api.NativeLoader
import cn.solarmoon.sparkcore.api.ffi.slice_ref_uint8_t
import cn.solarmoon.sparkcore.api.ffi.spark_core_h
import org.junit.Test
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.charset.StandardCharsets
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

val input = """
{
	"format_version": "1.12.0",
	"minecraft:geometry": [
		{
			"description": {
				"identifier": "geometry.为",
				"texture_width": 64,
				"texture_height": 64
			},
			"bones": [
				{
					"name": "Root",
					"pivot": [0, 0, 0],
					"cubes": [
						{
							"origin": [-7, 0, -5],
							"size": [14, 2, 13],
							"uv": {
								"north": {"uv": [16, 26], "uv_size": [14, 2]},
								"east": {"uv": [0, 29], "uv_size": [13, 2]},
								"south": {"uv": [16, 28], "uv_size": [14, 2]},
								"west": {"uv": [13, 30], "uv_size": [13, 2]},
								"up": {"uv": [0, 0], "uv_size": [14, 13]},
								"down": {"uv": [0, 26], "uv_size": [14, -13]}
							}
						},
						{
							"origin": [-4, 2, -1.75],
							"size": [8, 1, 6.5],
							"uv": {
								"north": {"uv": [36, 9], "uv_size": [8, 1]},
								"east": {"uv": [39, 31], "uv_size": [7, 1]},
								"south": {"uv": [38, 22], "uv_size": [8, 1]},
								"west": {"uv": [39, 32], "uv_size": [7, 1]},
								"up": {"uv": [24, 0], "uv_size": [8, 7]},
								"down": {"uv": [24, 14], "uv_size": [8, -7]}
							}
						},
						{
							"origin": [-5, 3, -3],
							"size": [10, 0, 9],
							"inflate": 0.005,
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [10, 0]},
								"east": {"uv": [0, 0], "uv_size": [9, 0]},
								"south": {"uv": [0, 0], "uv_size": [10, 0]},
								"west": {"uv": [0, 0], "uv_size": [9, 0]},
								"up": {"uv": [14, 0], "uv_size": [10, 9]},
								"down": {"uv": [14, 18], "uv_size": [10, -9]}
							}
						},
						{
							"origin": [4, 1.64645, -7.85355],
							"size": [4, 2, 6],
							"pivot": [6, 2.64645, -4.85355],
							"rotation": [67.5, 0, 0],
							"uv": {
								"north": {"uv": [38, 23], "uv_size": [4, 2]},
								"east": {"uv": [13, 34], "uv_size": [6, 2]},
								"south": {"uv": [38, 25], "uv_size": [4, 2]},
								"west": {"uv": [34, 17], "uv_size": [6, 2]},
								"up": {"uv": [30, 17], "uv_size": [4, 6]},
								"down": {"uv": [30, 29], "uv_size": [4, -6]}
							}
						}
					]
				},
				{
					"name": "Move1",
					"parent": "Root",
					"pivot": [1, 0, 4],
					"cubes": [
						{"origin": [-7, 2, 4], "size": [2, 12, 2], "uv": [2, 1], "mirror": true},
						{"origin": [5, 2, 4], "size": [2, 12, 2], "uv": [4, 5]},
						{"origin": [-8, 14, 3], "size": [16, 2.5, 4], "uv": [0, 12]}
					]
				},
				{
					"name": "Move2",
					"parent": "Move1",
					"pivot": [0, 0, 4],
					"cubes": [
						{"origin": [-8, 7.5, 3.5], "size": [4, 3, 3], "uv": [20, 33]},
						{"origin": [-4, 8, 4], "size": [8, 2, 2], "uv": [40, 11]},
						{"origin": [4, 7.5, 3.5], "size": [4, 3, 3], "uv": [35, 2]}
					],
					"locators": {
						"locator2": [0, 0, 0]
					}
				},
				{
					"name": "Move3",
					"parent": "Move2",
					"pivot": [8, 0, 4],
					"cubes": [
						{"origin": [0, 7, 3.5], "size": [4, 4.5, 3], "uv": [13, 35], "mirror": true},
						{"origin": [1.5, 8.75, 3], "size": [1.5, 1.5, 4], "pivot": [1.75, 9.25, 5], "rotation": [0, 0, -45], "uv": [14, 37], "mirror": true},
						{"origin": [0.5, 6.5, 3], "size": [3, 1.5, 4], "uv": [39, 33]}
					],
					"locators": {
						"locator": [3, 7, 4]
					}
				}
			]
		}
	]
}
            """.trimIndent()

val animInput = """
    {
    	"format_version": "1.8.0",
    	"animations": {
    		"sword.running": {
    			"loop": true,
    			"animation_length": 0.9167,
    			"bones": {
    				"root": {
    					"position": {
    						"0.0": {
    							"post": [0, 0.25, 0],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [-0.15, 0.6, 0],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [0, 0.25, 0],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [0.23, 0.62, 0],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [0, 0.25, 0],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"rightArm": {
    					"rotation": {
    						"0.0": {
    							"post": [-81.68261, 54.10145, -82.06829],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [-85.23112, 50.46324, -88.37186],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [-90.03468, 54.91792, -89.79602],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [-86.34935, 53.06028, -87.52037],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [-81.68261, 54.10145, -82.06829],
    							"lerp_mode": "catmullrom"
    						}
    					},
    					"position": {
    						"0.0": {
    							"post": [-0.13964, -0.29252, 0.21906],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [-0.13964, -0.29252, 0.21906],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"rightItem": {
    					"rotation": {
    						"0.0": {
    							"post": [22.57704, 13.40868, 4.6863],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [23.74092, 5.44838, 5.16654],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [22.57704, 13.40868, 4.6863],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"leftArm": {
    					"rotation": {
    						"0.0": {
    							"post": [-121.56755, 49.55455, -72.94895],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [-131.11968, 47.28126, -81.54262],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [-125.22064, 49.84402, -75.1278],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [-123.81153, 45.77148, -77.14074],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [-121.56755, 49.55455, -72.94895],
    							"lerp_mode": "catmullrom"
    						}
    					},
    					"position": {
    						"0.0": {
    							"post": [0.11424, -1.76596, 0.12556],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [0.11424, -1.76596, 0.12556],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [0.11424, -1.76596, 0.12556],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"head": {
    					"rotation": {
    						"0.0": {
    							"post": [-10.80882, -40.06612, 4.46],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [-9.66573, -38.20008, 2.56401],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [-9.15434, -34.9481, 1.6079],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [-11.12124, -37.01899, 4.95753],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [-10.80882, -40.06612, 4.46],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"rightLeg": {
    					"rotation": {
    						"0.0": {
    							"post": [-7.40679, -5.73576, 0.17472],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [0.30837, -1.23836, -0.93053],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [12.45987, 1.42192, 2.39313],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [5.35437, -1.32692, 1.32514],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [-7.40679, -5.73576, 0.17472],
    							"lerp_mode": "catmullrom"
    						}
    					},
    					"position": {
    						"0.0": {
    							"post": [-0.43457, -0.34917, -1.00252],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [-0.36086, -0.32, -0.03695],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [-0.38047, -0.1, 0.91798],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [-0.32947, 0.77, -1.04144],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [-0.43457, -0.34917, -1.00252],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"leftLeg": {
    					"rotation": {
    						"0.0": {
    							"post": [18.99515, -11.04134, -3.77407],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [10.14578, -10.51367, -3.45924],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [-3.73784, -10.27927, -2.87206],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [4.52767, -10.7847, -1.13054],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [18.99515, -11.04134, -3.77407],
    							"lerp_mode": "catmullrom"
    						}
    					},
    					"position": {
    						"0.0": {
    							"post": [-0.13382, -0.25, 0.14574],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [0.17967, 0.75, -1.47839],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [0.04044, -0.25, -1.14701],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [0.03914, -0.63, -0.36695],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [-0.13382, -0.25, 0.14574],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"body": {
    					"rotation": {
    						"0.0": [0, 7.5, 0],
    						"0.9167": {
    							"post": [0, 7.5, 0],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				},
    				"waist": {
    					"rotation": {
    						"0.0": {
    							"post": [13.78827, 31.87459, 4.6828],
    							"lerp_mode": "catmullrom"
    						},
    						"0.2083": {
    							"post": [13.62667, 29.32251, 5.36685],
    							"lerp_mode": "catmullrom"
    						},
    						"0.4583": {
    							"post": [13.40362, 26.7763, 6.02746],
    							"lerp_mode": "catmullrom"
    						},
    						"0.6667": {
    							"post": [13.79446, 29.30995, 4.33363],
    							"lerp_mode": "catmullrom"
    						},
    						"0.9167": {
    							"post": [13.78827, 31.87459, 4.6828],
    							"lerp_mode": "catmullrom"
    						}
    					}
    				}
    			}
    		}
    	}
    }
""".trimIndent()

class Test {

    @Test
    fun testAnimationLoading() {
        NativeLoader.loadLibrary()
        println("动态库加载完成。")

        val animBytes = animInput.toByteArray(StandardCharsets.UTF_8)

        GeoEngine().use { engine ->
            engine.loadAnimation(animBytes)
        }
    }

}


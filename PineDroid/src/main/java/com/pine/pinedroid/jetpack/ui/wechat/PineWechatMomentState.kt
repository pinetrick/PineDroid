package com.pine.pinedroid.jetpack.ui.wechat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.pine.pinedroid.activity.image_pickup.OneImage
import java.util.Date

data class PineWechatMomentState(
    val icon: String,
    val nickname: String,
    val rightIcon: String? = null,  // 改为可空，默认null更合适
    val rightText: String? = null,   // 改为可空，默认null更合适
    val content: String? = null,
    val likePeople: List<String> = emptyList(),
    val images: List<OneImage> = emptyList(),
    val datetime: Date? = null,      // 添加时间字段
    val allowDelete: Boolean = false,
    val id: String = "",             // 添加唯一标识符
    val isLiked: Boolean = false,    // 当前用户是否已点赞
    val isMenuOpenedState: MutableState<Boolean> = mutableStateOf(false)
) {
    // 为了方便访问，提供一个属性委托
    var isMenuOpened: Boolean
        get() = isMenuOpenedState.value
        set(value) { isMenuOpenedState.value = value }
}

val DEMO_PINE_WECHAT_MOMENT_STATE_1 = PineWechatMomentState(
    id = "moment_001",
    icon = "https://picsum.photos/100",
    nickname = "微信用户",
    rightIcon = "\uf005",
    rightText = "4.5",
    content = "这是朋友圈的内容示例，可以显示多行文本。今天天气真好，适合出去散步！",
    likePeople = listOf("张三", "李四", "王五", "赵六", "钱七", "孙八"),
    images = listOf(
        OneImage.HttpImage("https://picsum.photos/200/300"),
        OneImage.HttpImage("https://picsum.photos/201/301"),
        OneImage.HttpImage("https://picsum.photos/202/302"),
        OneImage.HttpImage("https://picsum.photos/200/300"),
    ),
    datetime = Date(),
    allowDelete = true,
    isLiked = false,
    isMenuOpenedState = mutableStateOf(true)
)

val DEMO_PINE_WECHAT_MOMENT_STATE_2 = PineWechatMomentState(
    id = "moment_002",
    icon = "https://picsum.photos/101",
    nickname = "另一个用户",
    content = "单张图片的示例",
    likePeople = listOf("小明", "小红"),
    images = listOf(
        OneImage.HttpImage("https://picsum.photos/300/400")
    ),
    datetime = Date(),
    allowDelete = false,
    isLiked = true,
    isMenuOpenedState = mutableStateOf(true)
)

val DEMO_PINE_WECHAT_MOMENT_STATE_3 = PineWechatMomentState(
    id = "moment_003",
    icon = "https://picsum.photos/102",
    nickname = "测试用户",
    content = "没有点赞的示例",
    likePeople = emptyList(),
    images = listOf(
        OneImage.HttpImage("https://picsum.photos/301/401"),
        OneImage.HttpImage("https://picsum.photos/302/402")
    ),
    datetime = Date(),
    allowDelete = false,
    isLiked = true, // 假设用户已经点赞了这个
    isMenuOpenedState = mutableStateOf(false)
)

val DEMO_PINE_WECHAT_MOMENT_STATE_4 = PineWechatMomentState(
    id = "moment_004",
    icon = "https://picsum.photos/103",
    nickname = "已点赞用户",
    content = "这个朋友圈用户已经点赞了",
    likePeople = listOf("当前用户", "张三", "李四"),
    images = listOf(
        OneImage.HttpImage("https://picsum.photos/303/403"),
        OneImage.HttpImage("https://picsum.photos/304/404"),
        OneImage.HttpImage("https://picsum.photos/305/405")
    ),
    datetime = Date(System.currentTimeMillis() - 3600000), // 1小时前
    allowDelete = false,
    isLiked = true,
    isMenuOpenedState = mutableStateOf(false)
)

// 如果需要的话，可以创建一个列表
val DEMO_PINE_WECHAT_MOMENT_STATES = listOf(
    DEMO_PINE_WECHAT_MOMENT_STATE_1,
    DEMO_PINE_WECHAT_MOMENT_STATE_2,
    DEMO_PINE_WECHAT_MOMENT_STATE_3,
    DEMO_PINE_WECHAT_MOMENT_STATE_4,
)
package com.pine.pinedroid.jetpack.ui.list.shopping

import androidx.compose.ui.Alignment
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage


data class PineShoppingItemBean(
    var reference: Any? = null,
    var image: OneImage? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var priceUnit: String? = null,
    var price: Double? = null,
    var textOnImage: List<TextOnImage> = emptyList(),
    var priceHint: String? = null,
) {

    data class TextOnImage(
        var text: String = "",
        var alignment: Alignment = Alignment.TopStart,
        var style: ShoppingItemBeanLabelStyle = ShoppingItemBeanLabelStyle.STYLE_NEUTRAL, // 修改这里
    )

    companion object {
        val ShoppingItemBeanDemo = listOf(
            // 第一个商品：展示所有位置的标签
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "秋季新款纯棉T恤",
                subtitle = "100%纯棉材质，舒适透气，多色可选",
                textOnImage = listOf(
                    TextOnImage("热销爆款", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_DANGER),
                    TextOnImage("🔥限时抢", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_WARNING),
                    TextOnImage("👍人气推荐", Alignment.TopCenter, ShoppingItemBeanLabelStyle.STYLE_PRIMARY),
                    TextOnImage("✓正品保障", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                    TextOnImage("🚚包邮", Alignment.BottomEnd, ShoppingItemBeanLabelStyle.STYLE_INFO),
                    TextOnImage("💎旗舰店", Alignment.BottomCenter, ShoppingItemBeanLabelStyle.STYLE_PREMIUM),
                ),
                priceHint = "已售 1.2万件",
                price = 129.0,
                priceUnit = "¥"
            ),

            // 第二个商品：展示不同样式的标签
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "MacBook Pro 16寸",
                subtitle = "M2 Max芯片，32GB内存，1TB SSD",
                textOnImage = listOf(
                    TextOnImage("🔥新品上市", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_DANGER),
                    TextOnImage("会员专享", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_PREMIUM),
                    TextOnImage("活动价", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_WARNING),
                    TextOnImage("官方正品", Alignment.BottomEnd, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                ),
                priceHint = "24期免息",
                price = 24999.0,
                priceUnit = "¥"
            ),

            // 第三个商品：浅色系标签展示
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "简约休闲双肩包",
                subtitle = "轻便防水，大容量设计",
                textOnImage = listOf(
                    TextOnImage("休闲风", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_LIGHT),
                    TextOnImage("多色可选", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_SECONDARY),
                    TextOnImage("学生适用", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_NEUTRAL),
                ),
                priceHint = "颜色：黑/灰/蓝",
                price = 199.0,
                priceUnit = "¥"
            ),

            // 第四个商品：深色系标签展示
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "4K超高清电视",
                subtitle = "75英寸，杜比全景声",
                textOnImage = listOf(
                    TextOnImage("高端旗舰", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_DARK),
                    TextOnImage("🎯性价比之选", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_PRIMARY),
                    TextOnImage("现货速发", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                ),
                priceHint = "比上次降价¥500",
                price = 8999.0,
                priceUnit = "¥"
            ),

            // 第五个商品：iPhone 15
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "iPhone 15 Pro Max",
                subtitle = "钛金属边框，A17 Pro芯片",
                textOnImage = listOf(
                    TextOnImage("💰预约抢购", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_WARNING),
                    TextOnImage("官方授权", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_PRIMARY),
                ),
                priceHint = "蓝色/白色/黑色可选",
                price = 9999.0,
                priceUnit = "¥"
            ),

            // 第六个商品：平板电脑
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "iPad Pro 12.9寸",
                subtitle = "M2芯片，Liquid视网膜XDR显示屏",
                textOnImage = listOf(
                    TextOnImage("教育优惠", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_INFO),
                    TextOnImage("送配件", Alignment.BottomEnd, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                ),
                priceHint = "学生专享价",
                price = 7999.0,
                priceUnit = "¥"
            ),

            // 第七个商品：电竞显示器
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "27寸电竞显示器",
                subtitle = "240Hz刷新率，1ms响应",
                textOnImage = listOf(
                    TextOnImage("🎮电竞专享", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_DANGER),
                    TextOnImage("顺丰包邮", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                ),
                priceHint = "晒单返现¥50",
                price = 2499.0,
                priceUnit = "¥"
            ),

            // 第八个商品：数码相机（无图片，测试空图情况）
            PineShoppingItemBean(
                image = null,
                title = "全画幅微单相机",
                subtitle = "专业摄影，4K视频拍摄",
                textOnImage = listOf(
                    TextOnImage("专业级", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_DARK),
                    TextOnImage("赠原装包", Alignment.BottomEnd, ShoppingItemBeanLabelStyle.STYLE_INFO),
                ),
                priceHint = "套餐更优惠",
                price = 15999.0,
                priceUnit = "¥"
            ),

            // 第九个商品：智能手表
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "智能运动手表",
                subtitle = "心率监测，50米防水",
                textOnImage = listOf(
                    TextOnImage("运动版", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_SECONDARY),
                    TextOnImage("⌚新品", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_DANGER),
                    TextOnImage("续航15天", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_LIGHT),
                ),
                priceHint = "晒单送表带",
                price = 799.0,
                priceUnit = "¥"
            ),

            // 第十个商品：无线耳机
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "降噪无线耳机",
                subtitle = "主动降噪，30小时续航",
                textOnImage = listOf(
                    TextOnImage("🎧音频旗舰", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_PREMIUM),
                    TextOnImage("无线充电", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_INFO),
                    TextOnImage("白/黑/蓝", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_LIGHT),
                ),
                priceHint = "蓝牙5.3版本",
                price = 1299.0,
                priceUnit = "¥"
            ),

            // 第十一个商品：健身器材
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "家用健身单车",
                subtitle = "磁控阻力，智能APP",
                textOnImage = listOf(
                    TextOnImage("🏋️居家健身", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_PRIMARY),
                    TextOnImage("静音设计", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                    TextOnImage("免安装", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_NEUTRAL),
                ),
                priceHint = "送货上门安装",
                price = 1899.0,
            ),

            // 第十二个商品：厨房电器
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "智能空气炸锅",
                subtitle = "无油烹饪，6L大容量",
                textOnImage = listOf(
                    TextOnImage("🔥热卖榜TOP1", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_DANGER),
                    TextOnImage("健康烹饪", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_SECONDARY),
                ),
                priceHint = "赠食谱+配件",
                price = 399.0,
            ),

            // 第十三个商品：家具
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "北欧简约沙发",
                subtitle = "小户型客厅，可拆洗设计",
                textOnImage = listOf(
                    TextOnImage("🛋️热销同款", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_WARNING),
                    TextOnImage("上门安装", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                    TextOnImage("多色可选", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_LIGHT),
                ),
                priceHint = "颜色：灰/蓝/米白",
                price = 2999.0,
            ),

            // 第十四个商品：美妆产品
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "补水护肤套装",
                subtitle = "敏感肌适用，温和配方",
                textOnImage = listOf(
                    TextOnImage("💄口碑爆款", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_DANGER),
                    TextOnImage("买一送一", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_INFO),
                    TextOnImage("适合所有肤质", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_LIGHT),
                ),
                priceHint = "活动最后1天",
                price = 299.0,
            ),

            // 第十五个商品：户外用品
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "自动帐篷",
                subtitle = "3秒速开，防雨防晒",
                textOnImage = listOf(
                    TextOnImage("🏕️户外必备", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_PRIMARY),
                    TextOnImage("便携收纳", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                ),
                priceHint = "适合2-4人",
                price = 499.0,
            ),

            // 第十六个商品：图书
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "全彩编程入门指南",
                subtitle = "零基础学习，实例丰富",
                textOnImage = listOf(
                    TextOnImage("📚畅销书榜", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_WARNING),
                    TextOnImage("附赠视频", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_INFO),
                    TextOnImage("初学者推荐", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_LIGHT),
                ),
                priceHint = "作者签名版",
                price = 89.0,
            ),

            // 第十七个商品：玩具
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "智能编程机器人",
                subtitle = "儿童STEM教育玩具",
                textOnImage = listOf(
                    TextOnImage("🤖教育玩具", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_PRIMARY),
                    TextOnImage("安全材质", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_SUCCESS),
                    TextOnImage("6岁+", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_NEUTRAL),
                ),
                priceHint = "赠课程+配件",
                price = 699.0,
            ),

            // 第十八个商品：食品
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "进口黑巧克力礼盒",
                subtitle = "72%可可含量，低糖",
                textOnImage = listOf(
                    TextOnImage("🍫进口零食", Alignment.TopStart, ShoppingItemBeanLabelStyle.STYLE_PREMIUM),
                    TextOnImage("情人节推荐", Alignment.TopEnd, ShoppingItemBeanLabelStyle.STYLE_DANGER),
                    TextOnImage("低糖健康", Alignment.BottomStart, ShoppingItemBeanLabelStyle.STYLE_SECONDARY),
                ),
                priceHint = "节日送礼首选",
                price = 199.0,
            )
        )
    }
}

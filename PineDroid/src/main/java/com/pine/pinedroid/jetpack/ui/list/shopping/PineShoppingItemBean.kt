package com.pine.pinedroid.jetpack.ui.list.shopping

import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage

data class PineShoppingItemBean(
    var reference: Any? = null,
    

    var image: OneImage? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var priceUnit: String? = null,
    var price: Double? = null,
    var textOnImage: String? = null,
    var priceHint: String? = null,

) {

    companion object
    {
        val ShoppingItemBeanDemo = listOf(
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "中文主标题",
                subtitle = "中文副标题 Laptop with M2 Chip",
                textOnImage = "100% off",
                priceHint = "100 人点赞",
                price = 2499.99,
            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "中文主标题",
                subtitle = "中文副标题 Laptop with M2 Chip",
                textOnImage = "100% off",
                priceHint = "100 人点赞",
                priceUnit = null,
            ),

            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "Mac Book Pro",
                subtitle = "Professional Laptop",
                price = 2499.99,
            ),

            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "TV 4K",
                subtitle = "Ultra HD Television",
                price = 899.99,
            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "iPhone 15",
                subtitle = "Latest Smartphone",
                price = 999.99,
            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "iPad Pro",
                subtitle = "Tablet Computer",
                price = 1099.99,
            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "Monitor",
                subtitle = "Gaming Monitor",
                price = 399.99,
            ),
            PineShoppingItemBean(
                image = null,
                title = "Camera",
                subtitle = "Digital Camera",
                price = 599.99,
            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "Watch",
                subtitle = "Smart Watch",
                price = 299.99,

            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "中文主标题",
                subtitle = "中文副标题 Laptop with M2 Chip",
                textOnImage = "100% off",
                priceHint = "100 人点赞",
                price = 2499.99,

            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "中文主标题",
                subtitle = "中文副标题 Laptop with M2 Chip",
                textOnImage = "100% off",
                priceHint = "100 人点赞",
                priceUnit = null,

            ),

            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "Mac Book Pro",
                subtitle = "Professional Laptop",
                price = 2499.99,

            ),

            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "TV 4K",
                subtitle = "Ultra HD Television",
                price = 899.99,

            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "iPhone 15",
                subtitle = "Latest Smartphone",
                price = 999.99,

            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "iPad Pro",
                subtitle = "Tablet Computer",
                price = 1099.99,

            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "Monitor",
                subtitle = "Gaming Monitor",
                price = 399.99,

            ),
            PineShoppingItemBean(
                image = null,
                title = "Camera",
                subtitle = "Digital Camera",
                price = 599.99,

            ),
            PineShoppingItemBean(
                image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                title = "Watch",
                subtitle = "Smart Watch",
                price = 299.99,

            )

        )

    }
}



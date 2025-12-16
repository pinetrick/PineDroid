package com.pine.pinedroid.jetpack.ui.wechat

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.ui.bottom_input.PineBottomEditText
import com.pine.pinedroid.utils.pineToString
import com.pine.pinedroid.utils.ui.pct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun PineWechatMoments(
    data: PineWechatMomentState,
    onImageClick: ((List<OneImage>, Int) -> Unit)? = null,
    onLike: (() -> Unit)? = null,
    onComment: ((reply: String) -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    // 使用状态来存储 TimeAndMenuLine 的位置信息
    var timeLineBottom by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    data.isMenuOpened = false
                }
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            // 用户头像
            PineAsyncImage(
                model = OneImage.HttpImage(data.icon),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 第一行：昵称和右侧信息
                RightLine1(
                    nickname = data.nickname,
                    rightIcon = data.rightIcon,
                    rightText = data.rightText
                )

                Spacer(modifier = Modifier.padding(top = 4.dp))

                // 朋友圈内容
                RightLineFeedback(feedback = data.content)

                Spacer(modifier = Modifier.padding(top = 8.dp))

                // 图片区域
                RightLineImage(
                    images = data.images,
                    onImageClick = onImageClick
                )

                // 最后一行：时间和操作菜单
                TimeAndMenuLine(
                    data = data,
                    onPositionChanged = { bottom ->
                        timeLineBottom = bottom
                    }
                )

                Spacer(modifier = Modifier.padding(top = 8.dp))

                // 点赞和评论区域
                if (data.likePeople.isNotEmpty() || data.feedbacks.isNotEmpty()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        if (data.likePeople.isNotEmpty()) {
                            LikePeopleSection(
                                likePeople = data.likePeople,
                            )
                            if (data.feedbacks.isNotEmpty()) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .height(1.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.3f
                                            )
                                        )
                                )
                            }
                        }
                        if (data.feedbacks.isNotEmpty()) {
                            FeedbackSection(
                                feedbacks = data.feedbacks,
                            )
                        }
                    }
                }


            }
        }

        PopUpSection(
            data = data,
            onLike = onLike,
            onComment = onComment,
            onShare = onShare,
            onDelete = onDelete,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = timeLineBottom) // 调整偏移量
        )
    }
    // 添加横线
    Spacer(modifier = Modifier.padding(top = 8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    )

}

@Composable
fun PopUpSection(
    data: PineWechatMomentState,
    onLike: (() -> Unit)? = null,
    onComment: ((reply: String) -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {

    // 操作按钮（更多图标）
    Box(modifier = modifier) {
        // 微信风格的横向操作栏
        if (data.isMenuOpened) {
            Row(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 点赞按钮
                onLike?.let {
                    IconButton(
                        onClick = {
                            data.isMenuOpened = false
                            onLike.invoke()
                        },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PineIcon(
                                text = "\uf164", // FontAwesome 的 thumbs-up 图标
                                fontSize = 16.sp,
                                color = if (data.isLiked) Color.Blue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (data.isLiked) "取消" else "点赞",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    // 添加分隔线
                    if (onComment != null || onShare != null || (data.allowDelete && onDelete != null)) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                }

                // 评论按钮
                onComment?.let {
                    IconButton(
                        onClick = {
                            data.isMenuOpened = false
                            // 创建协程作用域来调用挂起函数
                            CoroutineScope(Dispatchers.Main).launch {
                                val result = PineBottomEditText.show(
                                    placeholder = "爱回复的都是帅哥美女",
                                    textButton = "回复"
                                )

                                // 确保 result 不为 null 且不为空
                                result?.let { inputText ->
                                    if (inputText.isNotBlank()) {
                                        onComment.invoke(inputText)
                                    }
                                }
                            }
                        },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PineIcon(
                                text = "\uf075", // FontAwesome 的 comment 图标
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "评论",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    // 添加分隔线
                    if (onShare != null || (data.allowDelete && onDelete != null)) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                }

                // 分享按钮
                onShare?.let {
                    IconButton(
                        onClick = {
                            data.isMenuOpened = false
                            onShare.invoke()
                        },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PineIcon(
                                text = "\uf064", // FontAwesome 的 share 图标
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "分享",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    // 添加分隔线
                    if (data.allowDelete && onDelete != null) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                }

                // 如果是自己的朋友圈，显示删除按钮
                if (data.allowDelete) {
                    onDelete?.let {
                        IconButton(
                            onClick = {
                                data.isMenuOpened = false
                                onDelete.invoke()
                            },
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                PineIcon(
                                    text = "\uf1f8", // FontAwesome 的 trash 图标
                                    fontSize = 16.sp,
                                    color = Color.Red, //在白色背景上对，但是深色背景上颜色异常
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "删除",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}

// 新增的评论输入框组件
@Composable
fun CommentInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 输入框
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (!it.isFocused && text.isEmpty()) {
                            onDismiss()
                        }
                    },
                placeholder = {
                    Text(
                        text = "评论...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSend = {
                        if (text.isNotBlank()) {
                            onSendClick()
                            keyboardController?.hide()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 发送按钮
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendClick()
                        keyboardController?.hide()
                    }
                },
                enabled = text.isNotBlank(),
                modifier = Modifier.height(48.dp)
            ) {
                Text("发送")
            }
        }
    }
}

@Composable
fun FeedbackSection(
    feedbacks: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

        // 点赞用户列表
        if (feedbacks.isNotEmpty()) {
            feedbacks.forEach { feedback ->
                Text(
                    text = feedback,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    overflow = TextOverflow.Ellipsis,
                )
            }


        }
    }
}

@Composable
fun LikePeopleSection(
    likePeople: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // 点赞图标
        PineIcon(
            text = "\uf004", // 使用心形图标，您可以根据需要调整
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            modifier = Modifier.padding(end = 8.dp)
        )

        // 点赞用户列表
        if (likePeople.isNotEmpty()) {
            val displayText = buildAnnotatedString {
                likePeople.forEachIndexed { index, name ->
                    if (index > 0) {
                        append("、")
                    }
                    // 为每个用户名添加可点击样式
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(name)
                    }
                }
            }

            Text(
                text = displayText,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}


@Composable
fun TimeAndMenuLine(
    data: PineWechatMomentState,
    onPositionChanged: (bottom: Dp) -> Unit = { _ -> }
) {
    val density = LocalDensity.current

    // 操作按钮（更多图标）
    Box(
        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
            val bottom = with(density) {
                abs(
                    (layoutCoordinates.parentLayoutCoordinates?.size?.height
                        ?: 0) - layoutCoordinates.positionInParent().y - layoutCoordinates.size.height
                ).toDp()
            }
            onPositionChanged(bottom)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 时间显示
            data.datetime?.let { create_time ->
                Text(
                    text = create_time.pineToString("yyyy-MM-dd HH:mm"),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }


            // 更多操作图标（始终显示）
            IconButton(
                onClick = { data.isMenuOpened = !data.isMenuOpened },
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多操作",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }


    }
}


@Composable
fun RightLineImage(images: List<OneImage>, onImageClick: ((List<OneImage>, Int) -> Unit)?) {
    if (images.isNotEmpty()) {
        val imageSize = when (images.size) {
            1 -> 60.pct // 单张图片大一些
            else -> 23.pct // 多张图片小一些
        }

        val maxLines = when (images.size) {
            in 1..2 -> 1
            in 4..6 -> 2
            else -> 3
        }

        FlowRow(
            maxItemsInEachRow = if (images.size == 4) 2 else 3,
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth()
        ) {
            images.forEachIndexed { index, image ->
                Box(
                    modifier = Modifier
                        .size(width = imageSize, height = imageSize)
                        .padding(end = 4.dp, bottom = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onImageClick?.invoke(images, index) },
                    contentAlignment = Alignment.Center
                ) {
                    PineAsyncImage(
                        model = image,
                        useThumbnail = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp)),
                        onClicked = { onImageClick?.invoke(images, index) },
                    )
                }
            }
        }
    }
}

@Composable
fun RightLineFeedback(feedback: String?) {
    if (!feedback.isNullOrEmpty()) {
        Text(
            text = feedback,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 20.sp,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

@Composable
fun RightLine1(nickname: String, rightIcon: String?, rightText: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nickname,
            fontSize = 15.sp,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            rightIcon?.let { right ->
                PineIcon(
                    text = right,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            rightText?.let { right ->
                Text(
                    text = right,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            PineWechatMoments(
                data = DEMO_PINE_WECHAT_MOMENT_STATE_1,
                onLike = { println("点赞点击") },
                onComment = { println("评论点击") },
                onShare = { println("分享点击") },
                onDelete = { println("删除点击") },
            )

            PineWechatMoments(
                data = DEMO_PINE_WECHAT_MOMENT_STATE_2,
                onLike = { println("点赞点击") },
                onComment = { println("评论点击") },
                onShare = { println("分享点击") },
                onDelete = { println("删除点击") },
            )

            // 第三个示例：无点赞
            PineWechatMoments(
                data = DEMO_PINE_WECHAT_MOMENT_STATE_3,
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewLight() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            PineWechatMoments(
                data = DEMO_PINE_WECHAT_MOMENT_STATE_4
            )
        }
    }
}
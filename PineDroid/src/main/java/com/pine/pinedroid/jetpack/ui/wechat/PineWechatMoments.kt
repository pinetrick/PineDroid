package com.pine.pinedroid.jetpack.ui.wechat

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.utils.pineToString
import com.pine.pinedroid.utils.ui.pct
import java.util.Date

@Composable
fun PineWechatMoments(
    icon: String,
    nickname: String,
    rightIcon: String? = "",
    rightText: String? = "",
    content: String? = null,
    likePeople: List<String> = emptyList(),
    images: List<OneImage> = emptyList(),
    datetime: Date? = null,
    onImageClick: ((List<OneImage>, Int) -> Unit)? = null,
    onLike: (() -> Unit)? = null,
    onComment: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    allowDelete: Boolean = false // 是否是自己的朋友圈，用于显示删除选项
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        // 用户头像
        PineAsyncImage(
            model = OneImage.HttpImage(icon),
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
                nickname = nickname,
                rightIcon = rightIcon,
                rightText = rightText
            )

            Spacer(modifier = Modifier.padding(top = 4.dp))

            // 朋友圈内容
            RightLineFeedback(feedback = content)

            Spacer(modifier = Modifier.padding(top = 8.dp))

            // 图片区域
            RightLineImage(
                images = images,
                onImageClick = onImageClick
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            // 点赞和评论区域
            if (likePeople.isNotEmpty()) {
                LikePeopleSection(
                    likePeople = likePeople,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // 最后一行：时间和操作菜单
            RightLastLine(
                datetime = datetime,
                onLike = onLike,
                onComment = onComment,
                onShare = onShare,
                onDelete = onDelete,
                isOwnMoment = allowDelete
            )
        }
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
fun LikePeopleSection(
    likePeople: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 点赞图标
            PineIcon(
                text = "\uf004", // 使用心形图标，您可以根据需要调整
                fontSize = 14.sp,
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
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // 点击点赞列表的回调，可以在这里处理
                            println("点击了点赞列表")
                        }
                )
            }
        }
    }
}

@Composable
fun RightLastLine(
    datetime: Date?,
    onLike: (() -> Unit)? = null,
    onComment: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    isOwnMoment: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 时间显示
        datetime?.let { create_time ->
            Text(
                text = create_time.pineToString("yyyy-MM-dd HH:mm"),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
        var enabledCount = if (onLike != null) 1 else 0
        if (onComment != null) enabledCount++
        if (onShare != null) enabledCount++
        if (isOwnMoment && onDelete != null) enabledCount++

        if (enabledCount > 0) {
            // 更多操作菜单
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多操作",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // 下拉菜单
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    offset = DpOffset(x = (-16).dp, y = (-8).dp),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    // 点赞选项
                    onLike?.let {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "点赞",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                showMenu = false
                                onLike.invoke()
                            }
                        )
                    }


                    // 评论选项
                    onComment?.let {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "评论",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                showMenu = false
                                onComment.invoke()
                            }
                        )
                    }


                    // 分享选项
                    onShare?.let {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "分享",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                showMenu = false
                                onShare.invoke()
                            }
                        )
                    }


                    // 如果是自己的朋友圈，显示删除选项
                    if (isOwnMoment) {
                        onDelete?.let {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "删除",
                                        fontSize = 14.sp,
                                        color = Color.Red
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete.invoke()
                                }
                            )
                        }

                    }
                }
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
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 22.sp,
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
            fontSize = 16.sp,
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
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            rightText?.let { right ->
                Text(
                    text = right,
                    fontSize = 14.sp,
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
                onLike = { println("点赞点击") },
                onComment = { println("评论点击") },
                onShare = { println("分享点击") },
                onDelete = { println("删除点击") },
                allowDelete = true
            )

            // 第二个示例：少量点赞
            PineWechatMoments(
                icon = "https://picsum.photos/101",
                nickname = "另一个用户",
                content = "单张图片的示例",
                likePeople = listOf("小明", "小红"),
                images = listOf(
                    OneImage.HttpImage("https://picsum.photos/300/400")
                ),
                datetime = Date(),
                allowDelete = false
            )

            // 第三个示例：无点赞
            PineWechatMoments(
                icon = "https://picsum.photos/102",
                nickname = "测试用户",
                content = "没有点赞的示例",
                likePeople = emptyList(),
                images = listOf(
                    OneImage.HttpImage("https://picsum.photos/301/401"),
                    OneImage.HttpImage("https://picsum.photos/302/402")
                ),
                datetime = Date(),
                allowDelete = false
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
                icon = "https://picsum.photos/100",
                nickname = "微信用户",
                rightIcon = "\uf005",
                rightText = "4.5",
                content = "这是朋友圈的内容示例",
                likePeople = listOf("张三", "李四", "王五"),
                images = listOf(
                    OneImage.HttpImage("https://picsum.photos/200/300"),
                    OneImage.HttpImage("https://picsum.photos/201/301"),
                ),
                datetime = Date(),
                allowDelete = true
            )
        }
    }
}
package com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import kotlinx.coroutines.delay

@Composable
fun DropdownSearchBar(
    modifier: Modifier = Modifier,
    value: String = "",
    focused: Boolean = false,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search destinations...",
    suggestions: List<SearchSuggestion> = emptyList(),
    onSuggestionSelected: (SearchSuggestion) -> Unit,
    onSearch: (String) -> Unit,
    enabled: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    Box(modifier = modifier) {
        // 搜索输入框
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                isExpanded = it.isNotEmpty() && suggestions.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    hasFocus = focusState.hasFocus
                    isExpanded = hasFocus && value.isNotEmpty() && suggestions.isNotEmpty()
                },
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                Row {
                    if (value.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onValueChange("")
                                isExpanded = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = ""
                            )
                        }
                    }
                    IconButton(
                        onClick = { isExpanded = !isExpanded }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "",
                            modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    onSearch(value)
                    isExpanded = false
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = enabled
        )

        // 下拉建议菜单
        if (isExpanded && suggestions.isNotEmpty() && hasFocus) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(top = 60.dp)
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((suggestions.size * 56.dp).coerceAtMost(280.dp))
                    ) {
                        items(suggestions) { suggestion ->
                            _root_ide_package_.com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar.SearchSuggestionItem(
                                suggestion = suggestion,
                                onClick = {
                                    onSuggestionSelected(suggestion)
                                    onValueChange(suggestion.title)
                                    focusManager.clearFocus()
                                    isExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // 自动获取焦点（可选）
    LaunchedEffect(Unit) {
        if (focused) {
            delay(100)
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun SearchSuggestionItem(
    suggestion: SearchSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标（如果有）
        suggestion.icon?.let { iconRes ->
            PineAsyncImage(
                model = iconRes,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = suggestion.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            suggestion.subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 类型标签（如果有）
        suggestion.type?.let { type ->
            Text(
                text = type,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}



@Preview
@Composable
fun DropdownSearchBarExample() {
    var searchQuery by remember { mutableStateOf("") }
    val suggestions = remember {
        listOf(
            _root_ide_package_.com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar.SearchSuggestion(
                "Auckland",
                "New Zealand",
                "City",
                OneImage.Resource(R.drawable.pinedroid_image_loading)
            ),
            _root_ide_package_.com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar.SearchSuggestion(
                "Queenstown",
                "New Zealand",
                "City",
                OneImage.Resource(R.drawable.pinedroid_image_loading)
            ),
            _root_ide_package_.com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar.SearchSuggestion(
                "Sky Tower",
                "Auckland attraction",
                "Attraction",
                OneImage.Resource(R.drawable.pinedroid_image_loading),
            ),
            _root_ide_package_.com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar.SearchSuggestion(
                "Sydney",
                "Australia",
                "City",
                OneImage.Resource(R.drawable.pinedroid_image_loading)
            ),
            _root_ide_package_.com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar.SearchSuggestion(
                "Melbourne",
                "Australia",
                "City",
                OneImage.Resource(R.drawable.pinedroid_image_loading)
            ),
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        _root_ide_package_.com.pine.pinedroid.jetpack.ui.search.dropdown_search_bar.DropdownSearchBar(
            value = searchQuery,
            focused = false,
            onValueChange = { },
            placeholder = "Search destinations...",
            suggestions = suggestions,
            onSuggestionSelected = { suggestion -> },
            onSearch = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
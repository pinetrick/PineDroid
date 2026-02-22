package com.pine.pindroidpp.table_demo

import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class TableDemoScreenState(
    var columns: List<String> = listOf("ID", "Name", "Score", "Level", "Date", "Status"),
    var rows: List<List<String>> = listOf(
        listOf("1", "Alice", "95.5", "Expert", "2024-01", "Active"),
        listOf("2", "Bob", "78.3", "Beginner", "2024-02", "Active"),
        listOf("3", "Charlie", "88.7", "Advanced", "2024-03", "Inactive"),
        listOf("4", "Diana", "92.1", "Expert", "2024-04", "Active"),
        listOf("5", "Eve", "65.0", "Beginner", "2024-05", "Active"),
        listOf("6", "Frank", "81.5", "Intermediate", "2024-06", "Active"),
        listOf("7", "Grace", "99.9", "Master", "2024-07", "Active"),
        listOf("8", "Henry", "72.4", "Beginner", "2024-08", "Inactive"),
        listOf("9", "Iris", "86.3", "Advanced", "2024-09", "Active"),
        listOf("10", "Jack", "90.0", "Expert", "2024-10", "Active"),
        listOf("11", "Karen", "58.2", "Beginner", "2024-11", "Inactive"),
        listOf("12", "Leo", "94.7", "Expert", "2024-12", "Active"),
    )
)

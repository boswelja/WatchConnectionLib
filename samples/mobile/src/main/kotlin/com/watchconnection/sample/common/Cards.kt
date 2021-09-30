package com.watchconnection.sample.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    contentSpacing: Dp = 16.dp,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier) {
        Column(Modifier.padding(contentSpacing)) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(contentSpacing))
            content()
        }
    }
}

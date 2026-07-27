package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FaqAccordionSection
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryGold

@Composable
fun UserGuideScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "Guide",
                tint = PrimaryGreen,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = stringResource(R.string.menu_guide),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        GuideSectionCard(
            title = stringResource(R.string.guide_section1_title),
            icon = Icons.Default.Payment,
            iconTint = PrimaryGreen,
            steps = listOf(
                stringResource(R.string.guide_section1_step1),
                stringResource(R.string.guide_section1_step2),
                stringResource(R.string.guide_section1_step3),
                stringResource(R.string.guide_section1_step4),
                stringResource(R.string.guide_section1_step5),
                stringResource(R.string.guide_section1_step6)
            )
        )

        GuideSectionCard(
            title = stringResource(R.string.guide_section2_title),
            icon = Icons.Default.TrendingUp,
            iconTint = SecondaryGold,
            steps = listOf(
                stringResource(R.string.guide_section2_step1),
                stringResource(R.string.guide_section2_step2),
                stringResource(R.string.guide_section2_step3),
                stringResource(R.string.guide_section2_step4),
                stringResource(R.string.guide_section2_step5),
                stringResource(R.string.guide_section2_step6)
            )
        )

        GuideSectionCard(
            title = stringResource(R.string.guide_section3_title),
            icon = Icons.Default.Star,
            iconTint = SecondaryGold,
            steps = listOf(
                stringResource(R.string.guide_section3_step1),
                stringResource(R.string.guide_section3_step2),
                stringResource(R.string.guide_section3_step3),
                stringResource(R.string.guide_section3_step4)
            )
        )

        GuideSectionCard(
            title = stringResource(R.string.guide_section4_title),
            icon = Icons.AutoMirrored.Filled.Send,
            iconTint = PrimaryGreen,
            steps = listOf(
                stringResource(R.string.guide_section4_step1),
                stringResource(R.string.guide_section4_step2),
                stringResource(R.string.guide_section4_step3)
            )
        )

        // Accordion FAQ Section
        FaqAccordionSection()
    }
}

@Composable
fun GuideSectionCard(
    title: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    steps: List<String>
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        fontWeight = FontWeight.Bold,
                        color = iconTint,
                        fontSize = 13.sp
                    )
                    Text(
                        text = step,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

package com.kudoryi.physec.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudoryi.physec.ui.theme.*

data class NavItem(
    val screen: ComposeScreen,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun FloatingPillNavigationBar(
    currentScreen: ComposeScreen,
    onScreenSelected: (ComposeScreen) -> Unit,
    pendingFindingsCount: Int = 0
) {
    val items = remember(pendingFindingsCount) {
        listOf(
            NavItem(ComposeScreen.Home, "Home", Icons.Default.Home),
            NavItem(ComposeScreen.Reports, "Reports", Icons.Default.List),
            NavItem(ComposeScreen.Findings, "Findings", Icons.Default.Warning, pendingFindingsCount),
            NavItem(ComposeScreen.Sites, "Sites", Icons.Default.LocationOn)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(32.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(32.dp)
                ),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .height(52.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { navItem ->
                    val isSelected = when (navItem.screen) {
                        is ComposeScreen.Home -> currentScreen is ComposeScreen.Home
                        is ComposeScreen.Reports -> currentScreen is ComposeScreen.Reports
                        is ComposeScreen.Findings -> currentScreen is ComposeScreen.Findings
                        is ComposeScreen.Sites -> currentScreen is ComposeScreen.Sites
                        else -> false
                    }

                    FloatingNavItem(
                        item = navItem,
                        isSelected = isSelected,
                        onClick = { onScreenSelected(navItem.screen) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "NavBgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "NavContentColor"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )

                if (item.badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(IssueRed, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.title,
                    color = contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

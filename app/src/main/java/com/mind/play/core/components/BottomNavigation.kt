package com.mind.play.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mind.play.R
import com.mind.play.core.navigation.BottomNavItem
import com.mind.play.core.navigation.bottomNavItems
import com.mind.play.ui.theme.BackgroundLight
import com.mind.play.ui.theme.MindPlayTheme
import com.mind.play.ui.theme.NavIconUnselected
import com.mind.play.ui.theme.NavLabelColor

@Composable
fun MindPlayBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = BackgroundLight,
        tonalElevation = 0.dp,
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(10.dp),
                shadow = Shadow(
                    radius = 6.dp,
                    spread = 2.dp,
                    color = Color(0x1A000000),
                )
            )
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            val iconRes = when (item) {
                is BottomNavItem.Home -> R.drawable.ic_home
                is BottomNavItem.Games -> R.drawable.ic_games
                is BottomNavItem.Settings -> R.drawable.ic_settings
            }
            
            val labelColor = if (selected) MindPlayTheme.colors.buttonPrimary else NavLabelColor
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = labelColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MindPlayTheme.colors.buttonPrimary,
                    unselectedIconColor = NavIconUnselected,
                    indicatorColor = BackgroundLight
                )
            )
        }
    }
}

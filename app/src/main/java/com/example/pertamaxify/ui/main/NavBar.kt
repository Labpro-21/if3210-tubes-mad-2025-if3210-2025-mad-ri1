package com.example.pertamaxify.ui.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.R
import com.example.pertamaxify.ui.theme.BackgroundColor
import com.example.pertamaxify.ui.theme.WhiteHint
import com.example.pertamaxify.ui.theme.WhiteText

data class NavItem(val label: String, val normalIconRes: Int, val activeIconRes: Int)

@Composable
fun NavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        NavItem("Home", R.drawable.home, R.drawable.home_active),
        NavItem("Your Library", R.drawable.library, R.drawable.library_active),
        NavItem("Profile", R.drawable.profile, R.drawable.profile_active)
    )

    NavigationBar(
        containerColor = BackgroundColor,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedTab == index

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = if (isSelected) item.activeIconRes else item.normalIconRes),
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) WhiteText else WhiteHint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = WhiteHint,
                    selectedTextColor = Color.White,
                    unselectedTextColor = WhiteHint,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview
@Composable
fun NavBarPreview() {
    NavBar(selectedTab = 0, onTabSelected = {})
}

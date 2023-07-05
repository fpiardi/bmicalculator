package com.piardilabs.bmicalculator.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.piardilabs.bmicalculator.BMICalculatorScreen

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
)

/**
 * Composable that displays the bottom bar with navigation controller
 */
@Composable
fun BMICalculatorBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        val bottomNavItems = listOf(
            BottomNavItem(
                name = "Add",
                route = BMICalculatorScreen.ChooseGender.name,
                icon = Icons.Rounded.Create,
            ),
            BottomNavItem(
                name = "Home",
                route = BMICalculatorScreen.Historical.name,
                icon = Icons.Rounded.Home,
            ),
        )
        bottomNavItems.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route

            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(item.route) },
                label = {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "${item.name} Icon",
                    )
                }
            )
        }
    }
}
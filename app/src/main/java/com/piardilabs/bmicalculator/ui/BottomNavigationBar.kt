package com.piardilabs.bmicalculator.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.piardilabs.bmicalculator.BMICalculatorScreen
import com.piardilabs.bmicalculator.R

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
                name = stringResource(R.string.bottom_nav_add),
                route = BMICalculatorScreen.ChooseWeight.name,
                icon = Icons.Rounded.Create,
            ),
            BottomNavItem(
                name = stringResource(R.string.bottom_nav_historical_list),
                route = BMICalculatorScreen.HistoricalList.name,
                icon = Icons.Rounded.Home,
            ),
            BottomNavItem(
                name = stringResource(R.string.bottom_nav_historical_graph),
                route = BMICalculatorScreen.HistoricalGraph.name,
                icon = Icons.Rounded.Home,
            )
        )
        bottomNavItems.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route

            NavigationBarItem(
                selected = selected,
                onClick = { handleBottomNavItemClicked(item, navController) },
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

private fun handleBottomNavItemClicked(item: BottomNavItem, navController: NavHostController) {
    if (item.route == BMICalculatorScreen.ChooseWeight.name) {
        navController.navigate(BMICalculatorScreen.ChooseGender.name)
        navController.navigate(BMICalculatorScreen.ChooseHeight.name)
    }

    navController.navigate(item.route)
}
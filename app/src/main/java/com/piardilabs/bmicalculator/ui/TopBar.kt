package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.piardilabs.bmicalculator.BMICalculatorScreen
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

//TODO: fix preview of top app bar
@Preview
@Composable
fun BMICalculatorAppBarPreview() {
    BMICalculatorTheme {
        BMICalculatorAppBar(
            currentScreen = BMICalculatorScreen.Result,
            canNavigateBack = true,
            navigateUp = {  },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMICalculatorAppBar(
    currentScreen: BMICalculatorScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.text_back)
                    )
                }
            }
        }
    )
}
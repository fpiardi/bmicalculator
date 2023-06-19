package com.piardilabs.bmicalculator

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.piardilabs.bmicalculator.ui.ChooseGenderScreen
import com.piardilabs.bmicalculator.ui.ChooseHeightScreen
import com.piardilabs.bmicalculator.ui.ChooseWeightScreen
import com.piardilabs.bmicalculator.ui.ResultScreen

const val DEFAULT_HEIGHT_SLIDER_POSITION = 0.3f
const val DEFAULT_WEIGHT_SLIDER_POSITION = 0.3f

/**
 * enum values that represent the screens in the app
 */
enum class BMICalculatorScreen(@StringRes val title: Int) {
    ChooseGender(title = R.string.gender_title),
    ChooseHeight(title = R.string.height_title),
    ChooseWeight(title = R.string.weight_title),
    Result(title = R.string.result_title)
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
            containerColor = MaterialTheme.colorScheme.primaryContainer
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

@Composable
fun BMICalculatorApp(
    navController: NavHostController = rememberNavController()
) {
    var selectedGender by rememberSaveable { mutableStateOf(-1) }
    var sliderHeight by rememberSaveable { mutableStateOf(DEFAULT_HEIGHT_SLIDER_POSITION) }
    var sliderWeight by rememberSaveable { mutableStateOf(DEFAULT_HEIGHT_SLIDER_POSITION) }
    var height by rememberSaveable { mutableStateOf(0f) }
    var weight by rememberSaveable { mutableStateOf(0f) }

    val sliderHeightValues = generateSpinnerValues(130, 200)
    val sliderWeightValues = generateSpinnerValues(40, 200)

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = BMICalculatorScreen.valueOf(
        backStackEntry?.destination?.route ?: BMICalculatorScreen.ChooseGender.name
    )

    Scaffold(
        topBar = {
            BMICalculatorAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = BMICalculatorScreen.ChooseGender.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = BMICalculatorScreen.ChooseGender.name) {
                ChooseGenderScreen(
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                    onNextButtonClicked = {
                        navController.navigate(BMICalculatorScreen.ChooseHeight.name)
                    },
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxHeight()
                )
            }

            composable(route = BMICalculatorScreen.ChooseHeight.name) {
                ChooseHeightScreen(
                    selectedGender = selectedGender,
                    onNextButtonClicked = {
                        navController.navigate(BMICalculatorScreen.ChooseWeight.name)
                    },
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxHeight(),
                    sliderValues = sliderHeightValues,
                    sliderPosition = sliderHeight,
                    onSliderValueChange = {
                        sliderHeight = it
                        height = ((it * sliderHeightValues.size) + sliderHeightValues.first()) / 100
                    }
                )
            }

            composable(route = BMICalculatorScreen.ChooseWeight.name) {
                ChooseWeightScreen(
                    selectedGender = selectedGender,
                    onNextButtonClicked = {
                        navController.navigate(BMICalculatorScreen.Result.name)
                    },
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxHeight(),
                    sliderValues = sliderWeightValues,
                    sliderPosition = sliderWeight,
                    onSliderValueChange = {
                        sliderWeight = it
                        weight = (it * sliderWeightValues.size) + sliderWeightValues.first()
                    }
                )
            }

            composable(route = BMICalculatorScreen.Result.name) {
                ResultScreen(
                    selectedGender = selectedGender,
                    height = height,
                    weight = weight,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
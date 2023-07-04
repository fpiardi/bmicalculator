package com.piardilabs.bmicalculator

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.piardilabs.bmicalculator.ui.ChooseGenderScreen
import com.piardilabs.bmicalculator.ui.ChooseHeightScreen
import com.piardilabs.bmicalculator.ui.ChooseWeightScreen
import com.piardilabs.bmicalculator.ui.ResultScreen
import com.piardilabs.bmicalculator.viewmodel.BmiViewModel

const val DEFAULT_HEIGHT_SLIDER_POSITION = 0.50f
const val DEFAULT_WEIGHT_SLIDER_POSITION = 0.15f

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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BMICalculatorApp(
    bmiViewModel: BmiViewModel,
    navController: NavHostController = rememberAnimatedNavController() //rememberNavController()
) {
    var selectedGender by rememberSaveable { mutableStateOf(-1) }
    var sliderHeight by rememberSaveable { mutableStateOf(DEFAULT_HEIGHT_SLIDER_POSITION) }
    var sliderWeight by rememberSaveable { mutableStateOf(DEFAULT_WEIGHT_SLIDER_POSITION) }
    var height by rememberSaveable { mutableStateOf(0f) }
    var weight by rememberSaveable { mutableStateOf(0f) }

    val sliderHeightValues = bmiViewModel.generateSpinnerValues(100, 220)
    val sliderWeightValues = bmiViewModel.generateSpinnerValues(40, 200)

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = BMICalculatorScreen.valueOf(
        backStackEntry?.destination?.route ?: BMICalculatorScreen.ChooseGender.name
    )

    //    val results = bmiViewModel.getSavedResults()
//    val currentScreen = BMICalculatorScreen.valueOf(
//        backStackEntry?.destination?.route?.let {
//            it
//        } ?: run {
//            if (results != null) {
//                BMICalculatorScreen.Result.name
//            } else {
//                BMICalculatorScreen.ChooseGender.name
//            }
//        }
//    )

    Scaffold(
        topBar = {
            BMICalculatorAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
            )
        }
    ) { innerPadding ->

        AnimatedNavHost(
            navController = navController,
            startDestination = BMICalculatorScreen.ChooseGender.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = BMICalculatorScreen.ChooseGender.name,
                exitTransition = {
                    fadeOut(animationSpec = tween(500))
                }
            ) {
                ChooseGenderScreen(
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                    onNextButtonClicked = {
                        navController.navigate(BMICalculatorScreen.ChooseHeight.name)
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxHeight()
                )
            }

            composable(
                route = BMICalculatorScreen.ChooseHeight.name,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500) )
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500))
                }
            ) {
                ChooseHeightScreen(
                    selectedGender = selectedGender,
                    onNextButtonClicked = {
                        navController.navigate(BMICalculatorScreen.ChooseWeight.name)
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxHeight(),
                    sliderValues = sliderHeightValues,
                    sliderPosition = sliderHeight,
                    onSliderValueChange = {
                        sliderHeight = it
                        height = ((it * sliderHeightValues.size) + sliderHeightValues.first()) / 100
                    }
                )
            }

            composable(
                route = BMICalculatorScreen.ChooseWeight.name,
                enterTransition = {
                    fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(500))
                }
            ) {
                ChooseWeightScreen(
                    selectedGender = selectedGender,
                    onNextButtonClicked = {
                        navController.navigate(BMICalculatorScreen.Result.name)
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxHeight(),
                    sliderValues = sliderWeightValues,
                    sliderPosition = sliderWeight,
                    onSliderValueChange = {
                        sliderWeight = it
                        weight = (it * sliderWeightValues.size) + sliderWeightValues.first()
                    }
                )
            }

            composable(
                route = BMICalculatorScreen.Result.name,
                enterTransition = {
                    fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(500))
                }
            ) {
                ResultScreen(
                    bmiViewModel = bmiViewModel,
                    selectedGender = selectedGender,
                    height = height,
                    weight = weight,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
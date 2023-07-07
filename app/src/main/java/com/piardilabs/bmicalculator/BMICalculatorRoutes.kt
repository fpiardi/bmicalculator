package com.piardilabs.bmicalculator

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.piardilabs.bmicalculator.domain.SavedBmiResult
import com.piardilabs.bmicalculator.ui.BMICalculatorAppBar
import com.piardilabs.bmicalculator.ui.BMICalculatorBottomBar
import com.piardilabs.bmicalculator.ui.ChooseGenderScreen
import com.piardilabs.bmicalculator.ui.ChooseHeightScreen
import com.piardilabs.bmicalculator.ui.ChooseWeightScreen
import com.piardilabs.bmicalculator.ui.HistoricalListScreen
import com.piardilabs.bmicalculator.ui.ResultScreen
import com.piardilabs.bmicalculator.ui.LottieScreen
import com.piardilabs.bmicalculator.viewmodel.BmiViewModel

const val DEFAULT_HEIGHT_SLIDER_POSITION = 0.50f
const val DEFAULT_WEIGHT_SLIDER_POSITION = 0.15f
const val MINIMAL_HEIGHT = 100
const val MAXIMUM_HEIGHT = 220
const val MINIMAL_WEIGHT = 40
const val MAXIMUM_WEIGHT = 160

/**
 * enum values that represent the screens in the app
 */
enum class BMICalculatorScreen(@StringRes val title: Int) {
    Splash(title = R.string.app_name),
    ChooseGender(title = R.string.gender_title),
    ChooseHeight(title = R.string.height_title),
    ChooseWeight(title = R.string.weight_title),
    Result(title = R.string.result_title),
    Historical(title = R.string.historical_list_title)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BMICalculatorApp(
    bmiViewModel: BmiViewModel,
    navController: NavHostController = rememberAnimatedNavController()
) {
    val sliderHeightValues = bmiViewModel.generateSpinnerValues(MINIMAL_HEIGHT, MAXIMUM_HEIGHT)
    val sliderWeightValues = bmiViewModel.generateSpinnerValues(MINIMAL_WEIGHT, MAXIMUM_WEIGHT)

    var selectedGender by rememberSaveable { mutableStateOf(-1) }
    var sliderHeight by rememberSaveable { mutableStateOf(DEFAULT_HEIGHT_SLIDER_POSITION) }
    var sliderWeight by rememberSaveable { mutableStateOf(DEFAULT_WEIGHT_SLIDER_POSITION) }
    var height by rememberSaveable { mutableStateOf(0f) }
    var weight by rememberSaveable { mutableStateOf(0f) }

    val savedResults = bmiViewModel.savedResults.observeAsState(listOf()).value
//    if (savedResults.isNotEmpty()) {
//        val lastResult = savedResults.first()
//        selectedGender = lastResult.gender
//        height = lastResult.height
//        weight = lastResult.weight
//        sliderHeight = ((lastResult.height * 100) - MINIMAL_HEIGHT) / (MAXIMUM_HEIGHT - MINIMAL_HEIGHT)
//        sliderWeight = (lastResult.weight - MINIMAL_WEIGHT) / (MAXIMUM_WEIGHT - MINIMAL_WEIGHT)
//    }

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = BMICalculatorScreen.valueOf(
        backStackEntry?.destination?.route ?: BMICalculatorScreen.ChooseGender.name
    )
    Scaffold(
        topBar = {
            if (currentScreen != BMICalculatorScreen.Splash) {
                BMICalculatorAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
                )
            }
        },
        bottomBar = {
            if (currentScreen != BMICalculatorScreen.Splash) {
                BMICalculatorBottomBar(navController)
            }
        }
    ) { innerPadding ->

        AnimatedNavHost(
            navController = navController,
            startDestination = BMICalculatorScreen.Splash.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = BMICalculatorScreen.Splash.name
            ) {
                LottieScreen(
                    LottieCompositionSpec.RawRes(R.raw.splash),
                    animationSpeed = 0.75f,
                    onAnimationFinished = {
                        if (savedResults.isEmpty()) {
                            navController.navigate(BMICalculatorScreen.ChooseGender.name) {
                                navController.popBackStack()
                            }
                        } else {
                            navController.navigate(BMICalculatorScreen.Historical.name) {
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }

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
                    hasHistoricalData = savedResults.isNotEmpty(),
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
                    hasHistoricalData = savedResults.isNotEmpty(),
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
                    onSaveButtonClicked = {
                        navController.navigate(BMICalculatorScreen.Historical.name)
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxHeight(),
                )
            }

            composable(
                route = BMICalculatorScreen.Historical.name,
                enterTransition = {
                    fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(500))
                }
            ) {
                val listSavedBmiResult = bmiViewModel.savedResults.value?.map {
                    SavedBmiResult(
                        id = it.id,
                        date = it.date,
                        gender = selectedGender,
                        height = height,
                        weight = weight,
                        bmi = it.bmi,
                        index = it.index,
                        difference = it.difference
                    )
                } ?: listOf()

                if (listSavedBmiResult.isEmpty()) {
                    LottieScreen(
                        LottieCompositionSpec.RawRes(R.raw.shake_empty_box),
                        title = stringResource(R.string.historical_empty),
                        onAnimationFinished = {}
                    )
                } else {
                    HistoricalListScreen(
                        list = listSavedBmiResult,
                        bmiViewModel,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}
package com.example.caloriescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.caloriescreen.ui.theme.CalorieScreenTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.material3.DropdownMenu as DropdownMenu
import androidx.compose.material3.OutlinedTextField as OutlinedTextField
import androidx.compose.material3.RadioButton as RadioButton
import androidx.compose.material3.Text as Text



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorieScreenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { CalorieScreen() }

            }

        }
    }
}


@Composable
fun Heading(title: String) {

    Text(
        text = title,
        fontSize = 24.sp,

        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun CalorieScreen() {
    // State variables to hold user input and results
    var weightInput by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }
    var intensity by remember { mutableStateOf(1.3f) }
    var result by remember { mutableStateOf(0) }
    var male by remember { mutableStateOf( true) }


    // Main column for layout
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Heading text
        Heading(title = stringResource(R.string.calories))
        Text(
            text = "Calorie Calculator",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Input field for weight
        OutlinedTextField(
            value = weightInput,
            onValueChange = { weightInput = it },
            label = { Text("Enter weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Gender selection radio buttons
        GenderChoices(male, setGenderMale = { male = it })



        // Intensity selection dropdown
        IntensityList(onClick = { intensity = it })

        // Calculate button
        Button(
            onClick = { result = calculateCalories(isMale, weightInput.toIntOrNull() ?: 0, intensity) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }

        // Result display
        if (result > 0) {
            Text(
                text = "Daily Calories: $result",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Calculate calories based on inputs
private fun calculateCalories(isMale: Boolean, weight: Int, intensity: Float): Int {
    return if (isMale) {
        ((879 + 10.2 * weight) * intensity).toInt()
    } else {
        ((795 + 7.18 * weight) * intensity).toInt()
    }
}

// Gender selection component
@Composable
fun GenderChoices(male: Boolean, setGenderMale: (Boolean) -> Unit) {
    Column(Modifier.selectableGroup()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = male,
                onClick = { setGenderMale(true) }
            )
            Text(text = "Male")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = !male,
                onClick = { setGenderMale(false) }
            )
            Text(text = "Female")
        }
    }
}
// Intensity selection component
@Composable
fun IntensityList(onClick: (Float) -> Unit) {
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf("Light") }
        var textFieldSize by remember { mutableStateOf(Size.Zero) }
        val items = listOf("Light", "Usual", "Moderate", "Hard", "Very hard")
        val interactionSource = remember { MutableInteractionSource() } // Remembering the interaction source

        val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

        Column {
            OutlinedTextField(
                value = selectedText,
                onValueChange = { newText -> selectedText = newText },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                label = { Text("Select intensity") },
                trailingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) {
                            expanded = !expanded
                        }
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                items.forEach { label ->
                    DropdownMenuItem(onClick = {
                        selectedText = label
                        val intensityValue = when (label) {
                            "Light" -> 1.3f
                            "Usual" -> 1.5f
                            "Moderate" -> 1.7f
                            "Hard" -> 2f
                            "Very hard" -> 2.2f
                            else -> 0f
                        }
                        onClick(intensityValue)
                        expanded = false
                    }) {
                        Text(text = label)
                    }
                }
            }
        }
    }
@Composable
fun DropdownMenuItem(onClick: () -> Unit, content: @Composable () -> Unit) {
    Column {
        content()
        Divider()
    }
}
    // Implement the intensity selection UI




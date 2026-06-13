package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import java.util.Locale
import androidx.compose.material3.CenterAlignedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text("Калькулятор чаевых")
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    TipCalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * Определяет процент чаевых на основе суммы счета ("блоки"):
 * 1-2 блока (≤1000) → 3%
 * 3-5 блоков (1001-3000) → 5%
 * 6-10 блоков (3001-7000) → 7%
 * >10 блоков (>7000) → 10%
 */
fun calculateTipPercentFromBill(billAmount: Double): Float {
    return when {
        billAmount <= 1000 -> 3f
        billAmount <= 3000 -> 5f
        billAmount <= 7000 -> 7f
        else -> 10f
    }
}

/**
 * Возвращает индекс радиокнопки (0-3) на основе процента
 */
fun getRadioIndexFromPercent(percent: Float): Int {
    return when {
        percent <= 3f -> 0
        percent <= 5f -> 1
        percent <= 7f -> 2
        else -> 3
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculatorScreen(modifier: Modifier = Modifier) {

    // ========== СОСТОЯНИЯ ==========
    var billAmountText by remember { mutableStateOf("") }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var selectedRadioIndex by remember { mutableIntStateOf(-1) }
    val tipOptions = listOf(3, 5, 7, 10)

    // ========== АВТО-ЛОГИКА ==========
    LaunchedEffect(billAmountText) {
        val amount = billAmountText.toDoubleOrNull()
        if (amount != null && amount > 0) {
            val autoPercent = calculateTipPercentFromBill(amount)
            sliderPosition = autoPercent
            selectedRadioIndex = getRadioIndexFromPercent(autoPercent)
        } else {
            selectedRadioIndex = -1
            sliderPosition = 0f
        }
    }

    LaunchedEffect(sliderPosition) {
        if (sliderPosition > 0) {
            selectedRadioIndex = getRadioIndexFromPercent(sliderPosition)
        }
    }

    val displayPercentage = if (sliderPosition > 0f) "${sliderPosition.toInt()}%" else ""

    // ========== UI ==========
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ─── ПОЛЕ 1: СУММА СЧЕТА ───
        OutlinedTextField(
            value = billAmountText,
            onValueChange = { input ->
                if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                    billAmountText = input
                }
            },
            label = { Text("Сумма счета") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Валюта",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            placeholder = { Text("0.00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        // ─── ПОЛЕ 2: ПРОЦЕНТ ЧАЕВЫХ ───
        OutlinedTextField(
            value = displayPercentage,
            onValueChange = { },
            label = { Text("Процент чаевых") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Percent,
                    contentDescription = "Процент",
                    tint = Color(0xFFE91E63)
                )
            },
            placeholder = { Text("—") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE91E63),
                unfocusedBorderColor = Color(0xFFE91E63).copy(alpha = 0.4f),
                focusedLabelColor = Color(0xFFE91E63)
            )
        )

        // ─── СЛАЙДЕР: 0 – 25% ───
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = sliderPosition,
                onValueChange = { newPos -> sliderPosition = newPos },
                valueRange = 0f..25f,
                steps = 24,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", style = MaterialTheme.typography.bodySmall)
                Text("25", style = MaterialTheme.typography.bodySmall)
            }
        }

        // ─── РАДИОКНОПКИ ───
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tipOptions.forEachIndexed { index, percent ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedRadioIndex == index,
                        onClick = { sliderPosition = percent.toFloat() },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(32.dp)
                    )
                }
            }
        }

        // ─── РЕЗУЛЬТАТ (ИСПРАВЛЕНО!) ───
        Spacer(modifier = Modifier.height(16.dp))

        if (billAmountText.isNotEmpty() && sliderPosition > 0) {
            val amount = billAmountText.toDoubleOrNull() ?: 0.0
            val tipAmount = amount * sliderPosition / 100
            val totalAmount = amount + tipAmount

            Text(
                text = "Чаевые: ${String.format(Locale.getDefault(), "%.2f", tipAmount)} ₽\n" +
                        "Итого: ${String.format(Locale.getDefault(), "%.2f", totalAmount)} ₽",
                style = MaterialTheme.typography.bodyLarge, // ★ Безопасный вариант!
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== PREVIEW ====================
// Исправлен импорт Preview!

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipCalculatorPreview() {
    TipCalculatorTheme {
        TipCalculatorScreen()
    }
}
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
import androidx.compose.material3.TopAppBar
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
import com.example.tipcalculator.ui.theme.TipCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Калькулятор чаевых") })
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
 * Определяет количество "блоков" на основе суммы счета
 * и возвращает соответствующий процент чаевых
 */
fun calculateTipPercentFromBill(billAmount: Double): Float {
    return when {
        billAmount <= 1000 -> 3f    // 1-2 блока → 3%
        billAmount <= 3000 -> 5f    // 3-5 блоков → 5%
        billAmount <= 7000 -> 7f    // 6-10 блоков → 7%
        else -> 10f                 // >10 блоков → 10%
    }
}

/**
 * Возвращает индекс выбранной радиокнопки (0-3) на основе процента
 */
fun getRadioIndexFromPercent(percent: Float): Int {
    return when {
        percent <= 3f -> 0   // 3%
        percent <= 5f -> 1   // 5%
        percent <= 7f -> 2   // 7%
        else -> 3            // 10%
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculatorScreen(modifier: Modifier = Modifier) {
    // ========== СОСТОЯНИЯ ПРИЛОЖЕНИЯ ==========

    // Текстовое поле суммы счета (пустое при старте)
    var billAmountText by remember { mutableStateOf("") }

    // Позиция слайдера (0-25), начальное значение 0
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    // Индекс выбранной радиокнопки (0=3%, 1=5%, 2=7%, 3=10%)
    // Управляется ПРОГРАММНО, не пользователем!
    var selectedRadioIndex by remember { mutableIntStateOf(-1) } // -1 = ничего не выбрано

    // Список опций радиокнопок
    val tipOptions = listOf(3, 5, 7, 10)

    // ========== АВТОМАТИЧЕСКАЯ ЛОГИКА ВЫБОРА ==========

    // Когда изменяется сумма счета → автоматически пересчитываем % и выбираем радиокнопку
    LaunchedEffect(billAmountText) {
        val amount = billAmountText.toDoubleOrNull()
        if (amount != null && amount > 0) {
            val autoPercent = calculateTipPercentFromBill(amount)
            sliderPosition = autoPercent
            selectedRadioIndex = getRadioIndexFromPercent(autoPercent)
        } else {
            // Если сумма пустая или некорректная → сброс
            selectedRadioIndex = -1
        }
    }

    // Когда пользователь двигает слайдер вручную → обновляем радиокнопку
    LaunchedEffect(sliderPosition) {
        if (sliderPosition > 0) {
            selectedRadioIndex = getRadioIndexFromPercent(sliderPosition)
        }
    }

    // Отображаемый текст в поле процента
    val displayPercentage = if (sliderPosition > 0f) "${sliderPosition.toInt()}%" else ""

    // ========== UI КОМПОНЕНТЫ ==========

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ────────────────────────────────────────────────
        // ПОЛЕ 1: СУММА СЧЕТА (синяя тема)
        // ────────────────────────────────────────────────
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
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        // ────────────────────────────────────────────────
        // ПОЛЕ 2: ПРОЦЕНТ ЧАЕВЫХ (розовая тема, read-only)
        // ────────────────────────────────────────────────
        OutlinedTextField(
            value = displayPercentage,
            onValueChange = { }, // Запрещаем ручной ввод
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
                focusedLabelColor = Color(0xFFE91E63),
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = Color(0xFFE91E63).copy(alpha = 0.4f),
                disabledLabelColor = Color(0xFFE91E63).copy(alpha = 0.6f),
                disabledPlaceholderColor = Color(0xFFE91E63).copy(alpha = 0.4f)
            )
        )

        // ────────────────────────────────────────────────
        // СЛАЙДЕР: 0 – 25 процентов
        // ────────────────────────────────────────────────
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
                Text(
                    "0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "25",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ────────────────────────────────────────────────
        // ГРУППА РАДИОКНОПОК: 3%, 5%, 7%, 10%
        // (Программный выбор, НЕ пользовательский!)
        // ────────────────────────────────────────────────
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
                        // ★ Выбор управляется переменной selectedRadioIndex
                        selected = selectedRadioIndex == index,
                        onClick = {
                            // Можно разрешить клик для ручного переключения,
                            // но по заданию выбор ПРОГРАММНЫЙ.
                            // Оставляем пустым или синхронизируем со слайдером:
                            sliderPosition = percent.toFloat()
                        },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(32.dp)
                    )
                }
            }
        }

        // ────────────────────────────────────────────────
        // ИНФОРМАЦИОННАЯ ПАНЕЛЬ (опционально)
        // ────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(8.dp))

        if (billAmountText.isNotEmpty()) {
            val amount = billAmountText.toDoubleOrNull() ?: 0.0
            val tipAmount = amount * sliderPosition / 100
            val totalAmount = amount + tipAmount

            Text(
                text = "Чаевые: ${String.format("%.2f", tipAmount)} ₽\n" +
                        "Итого: ${String.format("%.2f", totalAmount)} ₽",
                style = MaterialTheme.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== PREVIEW ====================

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=360dp,height=640dp")
@Composable
fun TipCalculatorPreview() {
    TipCalculatorTheme {
        TipCalculatorScreen()
    }
}
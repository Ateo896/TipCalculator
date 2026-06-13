package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
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
                        TopAppBar(
                            title = { Text("Калькулятор чаевых") }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    TipCalculatorScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculatorScreen(modifier: Modifier = Modifier) {
    // Состояние для суммы счета (пустая строка при старте)
    var billAmount by remember { mutableStateOf("") }

    // Состояние для процента чаевых (пустое/управляется программно)
    var tipPercentageText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ==========================================
        // ПОЛЕ 1: Сумма счета (синий акцент)
        // ==========================================
        OutlinedTextField(
            value = billAmount,
            onValueChange = { newValue ->
                // Разрешаем только цифры и точку
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    billAmount = newValue
                }
            },
            label = { Text("Сумма счета") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Сумма",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            placeholder = { Text("Введите сумму") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        // ==========================================
        // ПОЛЕ 2: Процент чаевых (розовый акцент по дизайну)
        // ==========================================
        OutlinedTextField(
            value = tipPercentageText,
            onValueChange = { /* Только чтение или программное управление */ },
            label = { Text("Процент чаевых") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Percent,
                    contentDescription = "Процент",
                    tint = Color(0xFFE91E63)
                )
            },
            placeholder = { Text("Выберите процент") },
            readOnly = true, // Пользователь не может редактировать напрямую
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE91E63),
                unfocusedBorderColor = Color(0xFFE91E63).copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFFE91E63),
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = Color(0xFFE91E63).copy(alpha = 0.5f),
                disabledLabelColor = Color(0xFFE91E63).copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Заготовка для следующих компонентов (слайдер и радиокнопки)
        Text(
            text = "Здесь будет слайдер и радиокнопки...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipCalculatorPreview() {
    TipCalculatorTheme {
        TipCalculatorScreen()
    }
}
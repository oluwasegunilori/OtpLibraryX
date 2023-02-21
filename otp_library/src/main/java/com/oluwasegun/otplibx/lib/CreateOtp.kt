package com.oluwasegun.otplibx.lib

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly

/**
 * @param length specify the number of otp boxes
 * @param textFieldColors specify textfield default color see [TextFieldDefaults.textFieldColors]
 * @param fieldShape specify otp text field shape default is [CircleShape]
 * @param fieldModifier specify field size
 * @param onFilled a lambda that returns the entered text after completion
 * @param focusedBorderColor border color for textfield when focused
 * @param focusedBorderColor border color for textfield when not focused
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateOtp(
    modifier: Modifier = Modifier,
    length: Int = 5,
    textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
    ),
    fieldModifier: Modifier = Modifier.requiredSize(50.dp),
    fieldShape: Shape = CircleShape,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unFocusedBorderColor: Color = MaterialTheme.colorScheme.onPrimary,
    onFilled: (code: String) -> Unit = {}
) {

    var code by rememberSaveable {
        mutableStateOf(listOf<String>())
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (0 until length).forEach { index ->
            Fields(
                modifier = fieldModifier.weight(1f),
                textFieldColors = textFieldColors,
                shape = fieldShape,
                focusedBorderColor = focusedBorderColor,
                unFocusedBorderColor = unFocusedBorderColor,
                index = index,
                length = length,
                onDone = { it ->
                    val newCode = code.toMutableList()
                    if (newCode.size == length) {
                        newCode[index] = it.toString()
                    } else {
                        newCode.add(index, it.toString())
                    }
                    code = newCode
                    if (newCode.all { codes -> codes.isDigitsOnly() } && newCode.size == length) {
                        onFilled(
                            code.joinToString().replace(",", "").replace(" ", "").trim()
                                .take(length)
                        )
                    }
                },
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Fields(
    modifier: Modifier = Modifier,
    textFieldColors: TextFieldColors,
    shape: Shape,
    focusedBorderColor: Color,
    unFocusedBorderColor: Color,
    index: Int,
    length: Int,
    onDone: (Char) -> Unit,
) {

    var digit by rememberSaveable {
        mutableStateOf("")
    }

    var hasFocus by rememberSaveable {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = modifier
            .onKeyEvent { event: KeyEvent ->
                if (event.key == Key.Backspace) {
                    onDone('-')
                    focusManager.moveFocus(FocusDirection.Left)
                }
                false
            }
            .border(
                width = 1.0.dp,
                color = if (hasFocus) focusedBorderColor else unFocusedBorderColor,
                shape = shape
            )
            .onFocusChanged {
                hasFocus = it.hasFocus
            },
        shape = shape,
        colors = textFieldColors,
        singleLine = true,
        value = digit,
        onValueChange = { value: String ->
            if (!value.isDigitsOnly())
                return@TextField
            if (value.length <= 1) digit = value
            if (value.isNotEmpty()) {
                if (index != length - 1) {
                    focusManager.moveFocus(FocusDirection.Right)
                    onDone(value[0])
                } else {
                    onDone(value[0])
                    focusManager.clearFocus(true)
                }
            } else {
                onDone('-')
            }
        },

        keyboardOptions =
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
        ),
    )
}

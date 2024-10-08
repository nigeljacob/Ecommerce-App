package com.nigel.ecommerce.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.nigel.ecommerce.MainActivity
import com.nigel.ecommerce.R
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme
import com.nigel.ecommerce.repository.AuthRepository
import com.nigel.ecommerce.utils.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface() {
                    RegisterActivityLayout({ closeActivity() })
                }
            }
        }
    }

    private fun closeActivity() {
        finish()
    }
}

private fun openActivity(context: Context, type: String) {
    if(type.equals("Login")) {
        val intent: Intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    } else {
        val intent: Intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
}

@Composable
fun RegisterActivityLayout(onClose: () -> Unit) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var confirmPassword by remember { mutableStateOf("") }

    var containerHeight by remember { mutableStateOf(0) }

    var focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()

    var alertTitle by remember { mutableStateOf("") }

    var alertMessage by remember { mutableStateOf("") }

    var showAlert by remember { mutableStateOf(false) }

    val builder = AlertDialog.Builder(context)

    var showProgress by remember { mutableStateOf(false) }

    val authRepository = AuthRepository(context)

    val isDarkMode = isSystemInDarkTheme()

    var textColor by remember { mutableStateOf(Color(0xff000000)) }

    LaunchedEffect(isDarkMode) {
        if (isDarkMode) {
            textColor = Color(0xffffffff)
        } else {
            textColor = Color(0xff000000)
        }
    }

    LaunchedEffect(showAlert) {
        if(showAlert) {
            showProgress = false
            builder.setTitle(alertTitle)
            builder.setMessage(alertMessage)
            builder.setPositiveButton("OK") { dialog, _ ->
                showAlert = false
                if(alertMessage.equals("Registration Successful. You can login to your account once it's activated.")) {
                    onClose()
                }
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    EcommerceTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(
                    onClick = {
                        focusManager.clearFocus()
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .zIndex(1f)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, top = 40.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .clickable {
                                onClose()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Icon",
                            modifier = Modifier.width(80.dp)
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size ->
                            containerHeight = size.height
                        }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.login),
                            contentDescription = "My Image",
                            modifier = Modifier
                                .offset(y = 38.dp)
                                .padding(end = 20.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp, top = 20.dp)
                ) {
                    Text("Sign Up", fontWeight = FontWeight.Bold, fontSize = 25.sp, modifier = Modifier.padding(bottom = 20.dp, top = 40.dp))
                    Text("Hey there! Sign up to continue", fontSize = 15.sp, color = Color(0xffa7a7a7), modifier = Modifier.padding(bottom = 20.dp))

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.secondaryContainer).padding(start = 10.dp, end = 10.dp)
                    ) {
                        Box() {
                            BasicTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                },
                                textStyle = TextStyle(fontSize = 15.sp, color = textColor),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions (
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if(name == "") {
                                Text("Name", color = Color(0xffA7A7A7), fontSize = 15.sp)
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.secondaryContainer).padding(start = 10.dp, end = 10.dp)
                    ) {
                        Box() {
                            BasicTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                },
                                textStyle = TextStyle(fontSize = 15.sp, color = textColor),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions (
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if(email == "") {
                                Text("Email", color = Color(0xffA7A7A7), fontSize = 15.sp)
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.secondaryContainer).padding(start = 10.dp, end = 10.dp)
                    ) {
                        Box() {
                            BasicTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                },
                                textStyle = TextStyle(fontSize = 15.sp, color = textColor),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions (
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if(password == "") {
                                Text("Password", color = Color(0xffA7A7A7), fontSize = 15.sp)
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.secondaryContainer).padding(start = 10.dp, end = 10.dp)
                    ) {
                        Box() {
                            BasicTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                },
                                textStyle = TextStyle(fontSize = 15.sp, color = textColor),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions (
                                    onNext = {
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if(confirmPassword == "") {
                                Text("Confirm Password", color = Color(0xffA7A7A7), fontSize = 15.sp)
                            }
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFc3e703))
                            .clickable {
                                if(!showProgress) {
                                    showProgress = true
                                    if(name.trim().isEmpty()) {
                                        alertTitle = "Ooops!"
                                        alertMessage = "Name is Missing"
                                        showAlert = true
                                    } else if(email.isEmpty() || !email.contains("@")) {
                                        alertTitle = "Ooops!"
                                        if(email.isEmpty()) {
                                            alertMessage = "Email is Missing"
                                        } else {
                                            alertMessage = "Invalid Email"
                                        }
                                        showAlert = true
                                    } else if (password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                                        alertTitle = "Ooops!"
                                        alertMessage = "Password is Missing"
                                        showAlert = true
                                    } else if(!password.equals(confirmPassword)) {
                                        alertTitle = "Ooops!"
                                        alertMessage = "Password don't match"
                                        showAlert = true
                                    } else {
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                authRepository.signUp(
                                                    name,
                                                    email,
                                                    password,
                                                ) { success, message ->
                                                    println(message)
                                                    if (success) {
                                                        alertTitle = "Success"
                                                        alertMessage = "Registration Successful. You can login to your account once it's activated."
                                                        showAlert = true
                                                    } else {
                                                        if (message.equals("Email already in use. Try Login")) {
                                                            alertTitle = "Email Already in Use"
                                                            alertMessage =
                                                                "The user email already exists. Try signing in to your account"
                                                        } else if (message.equals("Invalid password")) {
                                                            alertTitle = "Invalid Password"
                                                            alertMessage = "The Password you entered is Inavlid"
                                                        } else {
                                                            alertTitle = "Ooops!"
                                                            alertMessage =
                                                                "Something went wrong, try again later"
                                                        }
                                                        showAlert = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    ) {
                        if(showProgress) {
                            CircularProgressIndicator(color = Color(0xff000000))
                        } else {
                            Text("Register", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                ) {
                    Text("Already have an account? ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Login", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.clickable {
                        openActivity(context, "Login")
                        onClose()
                    })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterLayoutPreview() {
    RegisterActivityLayout(onClose = {})
}
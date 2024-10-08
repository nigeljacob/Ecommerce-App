package com.nigel.ecommerce.pages

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nigel.ecommerce.R
import com.nigel.ecommerce.activities.LoginActivity
import com.nigel.ecommerce.activities.OrdersActivity
import com.nigel.ecommerce.activities.RegisterActivity
import com.nigel.ecommerce.activities.WelcomeActivity
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.models.User
import com.nigel.ecommerce.repository.AuthRepository
import com.nigel.ecommerce.ui.theme.EcommerceTheme
import com.nigel.ecommerce.utils.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private fun openActivity(context: Context, type: String, selected: String, id: String) {
    if(type.equals("Order")) {
        val intent: Intent = Intent(context, OrdersActivity::class.java)
        intent.putExtra("type", selected)
        intent.putExtra("userID", id)
        context.startActivity(intent)
    } else {
        val intent: Intent = Intent(context, WelcomeActivity::class.java)
        context.startActivity(intent)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(modifier: Modifier = Modifier, userDetails: User?, onClose: () -> Unit) {

    var context = LocalContext.current

    var loggedIn by remember { mutableStateOf(false) }

    val authRepository = AuthRepository(context)

    var scrollState = rememberScrollState()

    val builder = AlertDialog.Builder(context)

    val scope = rememberCoroutineScope()

    var selected by remember { mutableStateOf("Pending") }

    var focusManager = LocalFocusManager.current

    val scaffoldState = rememberBottomSheetScaffoldState()

    var nameEdit by remember { mutableStateOf("") }

    var alertTitle by remember { mutableStateOf("") }

    var alertMessage by remember { mutableStateOf("") }

    var showAlert by remember { mutableStateOf(false) }

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
            builder.setTitle(alertTitle)
            builder.setMessage(alertMessage)
            builder.setPositiveButton("OK") { dialog, _ ->
                showAlert = false
                focusManager.clearFocus()
                dialog.dismiss()
            }
            builder.create().show()
        }
    }


    EcommerceTheme {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                ) {

                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(start = 10.dp, end = 10.dp)
                    ) {
                        Box() {
                            BasicTextField(
                                value = nameEdit,
                                onValueChange = {
                                    nameEdit = it
                                },
                                textStyle = TextStyle(fontSize = 15.sp, color = textColor),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions (
                                    onNext = {
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if(nameEdit == "") {
                                Text("Name here", color = Color(0xffA7A7A7), fontSize = 15.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF96d1c7))
                            .clickable {
                                focusManager.clearFocus()
                                scope.launch {
                                    withContext(Dispatchers.IO) {

                                        val userMap: Map<String, String> = mapOf(
                                            "name" to nameEdit
                                        )

                                        val response = authRepository.updateUser(userDetails?.id ?: "", userMap)
                                        if(response) {
                                            alertTitle = "Success"
                                            alertMessage = "Profile updated successfully. Refresh to see change"
                                            showAlert = true
                                        }
                                    }
                                }
                            }
                    ) {
                        Text("Update Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp), color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                }
            },
            sheetPeekHeight = 0.dp,
        )
        {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
//                Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
                    ) {
                        Text("Profile", fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .height(55.dp)
                                .width(55.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                                .clickable {
                                    nameEdit = userDetails?.name ?: ""
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Icon",
                                modifier = Modifier.width(20.dp)
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(120.dp)
                                .height(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "My Image",
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(userDetails?.name ?: ""  , fontWeight = FontWeight.Bold)
                            Text(userDetails?.email ?: "", fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color(0xffa7a7a7))
                        }
                    }


                }

                Spacer(modifier = Modifier.fillMaxWidth().height(30.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollState)
                ) {
                    Row(
                        modifier = Modifier.padding(top = 30.dp, start = 30.dp, end = 30.dp)
                    ) {
                        Text("My Orders", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("See all", fontSize = 13.sp, color = Color(0xffA7A7A7), modifier = Modifier.padding(end = 6.dp))
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .clickable {
                                        openActivity(context, "Order", selected, userDetails?.id ?: "")
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowRight,
                                    contentDescription = "Icon",
                                    tint = textColor,
                                    modifier = Modifier.width(20.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 15.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(15.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f).clickable {
                                        selected = "Pending"
                                        openActivity(context, "Order", selected, userDetails?.id ?: "")
                                    }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xffffffff))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.pending),
                                            contentDescription = "My Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 3.dp)
                                    ) {
                                        Text("Pending", fontWeight = FontWeight.Normal, fontSize = 13.sp)
                                    }
                                }

                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f).clickable {
                                        selected = "Processing"
                                        openActivity(context, "Order", selected, userDetails?.id ?: "")
                                    }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xffffffff))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.processing),
                                            contentDescription = "My Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),

                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 3.dp)
                                    ) {
                                        Text("Partially Delivered", textAlign = TextAlign.Center, fontWeight = FontWeight.Normal, fontSize = 13.sp)
                                    }
                                }

                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f).clickable {
                                        selected = "Delivered"
                                        openActivity(context, "Order", selected, userDetails?.id ?: "")
                                    }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xffffffff))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.delivered),
                                            contentDescription = "My Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 3.dp)
                                    ) {
                                        Text("Delivered", fontWeight = FontWeight.Normal, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(20.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .padding(top = 5.dp)
                                .clickable (
                                    onClick = {
                                        builder.setTitle("Are you sure ?")
                                        builder.setMessage("Are you sure you want to logout ?")
                                        builder.setPositiveButton("Yes") { dialog, which ->
                                            authRepository.logout()
                                            openActivity(context, "Welcome", selected, userDetails?.id ?: "")
                                            onClose()
                                            dialog.dismiss()
                                        }

                                        builder.setNegativeButton("No") { dialog, which ->
                                            dialog.dismiss()
                                        }

                                        val alertDialog: AlertDialog = builder.create()
                                        alertDialog.show()
                                    },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text("Logout", fontWeight = FontWeight.Bold, color = Color.Red)
                            }

                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Icon",
                                tint = Color.Red
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .padding(top = 5.dp)
                                .clickable (
                                    onClick = {
                                        builder.setTitle("Are you sure ?")
                                        builder.setMessage("Are you sure you want to deactivate you account ?")
                                        builder.setPositiveButton("Yes") { dialog, which ->

                                            scope.launch{
                                                withContext(Dispatchers.IO) {
                                                    val response = authRepository.deactivateAccount(userDetails!!.id!!)
                                                    if (response) {
                                                        openActivity(context, "Welcome", selected, userDetails?.id ?: "")
                                                        onClose()
                                                    }
                                                    dialog.dismiss()
                                                }
                                            }
                                        }

                                        builder.setNegativeButton("No") { dialog, which ->
                                            dialog.dismiss()
                                        }

                                        val alertDialog: AlertDialog = builder.create()
                                        alertDialog.show()
                                    },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text("Deactivate Account", fontWeight = FontWeight.Bold, color = Color.Red)
                            }

                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Icon",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    ProfilePage(Modifier, User("", "User 1", "user@gmail.com", "", "", true), {})
}
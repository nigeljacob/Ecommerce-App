package com.nigel.ecommerce.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme
import com.nigel.ecommerce.models.Order
import com.nigel.ecommerce.models.OrderItem
import com.nigel.ecommerce.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface() {
                    CheckoutActivityLayout({closeActivity()})
                }
            }
        }
    }

    private fun closeActivity() {
        finish()
    }
}

private fun getCurrentFormattedDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}

private fun updateCart(context: Context, products: MutableList<Product>, quantities: MutableList<Int>, selected: MutableList<Boolean>) {
    val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    println(selected)

    for(index in 0 until selected.size) {
        if(selected[index]) {
            products.removeAt(index)
            quantities.removeAt(index)
        }
    }

    val gson = Gson()

    // Convert the updated lists to JSON
    val productsJson = gson.toJson(products)
    val quantitiesJson = gson.toJson(quantities)

    // Save the updated lists back to shared preferences
    editor.putString("cartProducts", productsJson)
    editor.putString("cartQuantities", quantitiesJson)
    editor.apply()
}

private fun openActivity(context: Context) {
    val intent: Intent = Intent(context, ThankyouActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun CheckoutActivityLayout(onClose: () -> Unit) {

    var context = LocalContext.current

    val intent = (context as? Activity)?.intent

    var deliveryAddress by remember { mutableStateOf(intent?.getStringExtra("deliveryAddress") ?: "") }

    var scrollState = rememberScrollState()

    var totalAmount by remember { mutableStateOf(intent?.getDoubleExtra("total", 0.00)) }

    var items = remember { mutableStateListOf<Product>() }

    var quantityList = remember { mutableStateListOf<Int>() }

    var selected = remember { mutableStateListOf<Boolean>() }

    var deliveryDate by remember { mutableStateOf("") }

    var nameOnCard by remember { mutableStateOf("") }

    var cardNo by remember { mutableStateOf("") }

    var cardExpy by remember { mutableStateOf("") }

    var securityCode by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()

    val productRepository = ProductRepository(context)

    var progress by remember { mutableStateOf(false) }

    var userID by remember { mutableStateOf(intent?.getStringExtra("userID") ?: "") }

    var alertTitle by remember { mutableStateOf("") }

    var alertMessage by remember { mutableStateOf("") }

    var showAlert by remember { mutableStateOf(false) }

    val builder = AlertDialog.Builder(context)

    val isDarkMode = isSystemInDarkTheme()

    var textColor by remember { mutableStateOf(Color(0xff000000)) }

    LaunchedEffect(isDarkMode) {
        if (isDarkMode) {
            textColor = Color(0xffffffff)
        } else {
            textColor = Color(0xff000000)
        }
    }

    LaunchedEffect(Unit) {
        val itemsIntent = intent?.getSerializableExtra("products") as List<Product>
        val quantityListIntent = intent.getSerializableExtra("quantity") as List<Int>
        val selectedList = intent.getSerializableExtra("selected") as List<Boolean>
        items.addAll(itemsIntent)
        quantityList.addAll(quantityListIntent)
        selected.addAll(selectedList)
    }

    LaunchedEffect(showAlert) {
        if(showAlert) {
            builder.setTitle(alertTitle)
            builder.setMessage(alertMessage)
            builder.setPositiveButton("OK") { dialog, _ ->
                showAlert = false
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    EcommerceTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable {
                                onClose()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Icon",
                            tint = textColor,
                            modifier = Modifier.width(80.dp)
                        )
                    }

                }

                Text("Checkout", fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 10.dp), color = textColor)

                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        Text("Placed on: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                        Text(Date().date.toString() + "/" + (Date().month + 1).toString() + "/" + Date().year.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                        Text("$" + totalAmount.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }

                    Text(items.size.toString() + " items", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp), color = textColor)

                    repeat(items.size) { index ->

                        var orderItem = items[index]

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(10.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.background)
                                ) {
                                    AsyncImage(
                                        model = orderItem!!.imageURL[0],
                                        contentDescription = "Image from URL",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(orderItem.title , fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp), color = textColor)
                                    Text("Pending", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF96d1c7))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Units: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                                        Text(quantityList[index].toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                    }
                                }

                                Column(
                                    verticalArrangement = Arrangement.Bottom,
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Text("$" + (quantityList[index] * orderItem.price).toString(), textAlign = TextAlign.End, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp).fillMaxWidth(), color = textColor)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                    }

                    Text("Order Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp), color = textColor)

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Delivery Address: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                        Text(deliveryAddress, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Payment Status: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                        Text("Pending", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }

                    Text("Payment Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp), color = textColor)

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
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
                                    value = nameOnCard,
                                    onValueChange = {
                                        nameOnCard = it
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
                                if(nameOnCard == "") {
                                    Text("Name on Card", color = Color(0xffA7A7A7), fontSize = 15.sp)
                                }
                            }
                        }

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
                                    value = cardNo,
                                    onValueChange = {
                                        if(it.length < 20) {
                                            cardNo = it
                                        }
                                    },
                                    textStyle = TextStyle(fontSize = 15.sp, color = textColor),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions (
                                        onNext = {
                                            focusManager.moveFocus(FocusDirection.Next)
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if(cardNo == "") {
                                    Text("XXXX XXXX XXXX XXXX", color = Color(0xffA7A7A7), fontSize = 15.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(start = 10.dp, end = 10.dp)
                                    .weight(1f)
                            ) {
                                Box() {
                                    BasicTextField(
                                        value = cardExpy,
                                        onValueChange = {
                                            if(it.length < 6) {
                                                cardExpy = it
                                            }
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
                                    if(cardExpy == "") {
                                        Text("24/05", color = Color(0xffA7A7A7), fontSize = 15.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(start = 10.dp, end = 10.dp)
                                    .weight(1f)
                            ) {
                                Box() {
                                    BasicTextField(
                                        value = securityCode,
                                        onValueChange = {
                                            if(it.length < 4) {
                                                securityCode = it
                                            }
                                        },
                                        textStyle = TextStyle(fontSize = 15.sp, color = textColor),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions (
                                            onNext = {
                                                focusManager.moveFocus(FocusDirection.Next)
                                            }
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if(securityCode == "") {
                                        Text("XXX", color = Color(0xffA7A7A7), fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFc3e703))
                            .clickable {
                                progress = true
                                if(nameOnCard.isEmpty() || nameOnCard.equals("")) {
                                    alertTitle = "Missing Payment Details"
                                    alertMessage = "Fill in Name on Card to continue"
                                    showAlert = true
                                    progress = false

                                } else if(cardNo.isEmpty() || cardNo.length < 16) {
                                    alertTitle = "Missing Payment Details"
                                    alertMessage = "Fill in Card Number to continue"
                                    showAlert = true
                                    progress = false
                                } else if(cardExpy.isEmpty() || cardExpy.length < 5) {
                                    alertTitle = "Missing Payment Details"
                                    alertMessage = "Fill in Card expiry to continue"
                                    showAlert = true
                                    progress = false
                                } else if(securityCode.isEmpty() || securityCode.length < 3) {
                                    alertTitle = "Missing Payment Details"
                                    alertMessage = "Fill in Card security number to continue"
                                    showAlert = true
                                    progress = false
                                } else {
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            val tempOrderItems = mutableListOf<Map<String, Any>>().apply {
                                                for(item in items) {
                                                    println(item.vendorId)
                                                    val newOrderItem = mapOf(
                                                        "orderLineNo" to "",
                                                        "productNo" to item.id,
                                                        "orderNo" to "",
                                                        "vendorNo" to item.vendorId,
                                                        "status" to "Pending",
                                                        "qty" to quantityList[items.indexOf(item)],
                                                        "unitPrice" to item.price.toFloat(),
                                                        "total" to item.price * quantityList[items.indexOf(item)].toFloat()
                                                    )
                                                    add(newOrderItem)
                                                }
                                            }

                                            val order: Map<String, Any> = mapOf(
                                                "orderNo" to "",
                                                "customerNo" to userID,
                                                "deliveryAddress" to deliveryAddress,
                                                "orderDate" to getCurrentFormattedDate(),
                                                "status" to "Pending",
                                                "orderLines" to tempOrderItems
                                            )

                                            val response = productRepository.createOrder(context, order)

                                            if(response) {
                                                updateCart(context, ArrayList(items), ArrayList(quantityList), ArrayList(selected))
                                                progress = false
                                                openActivity(context)
                                                onClose()

                                            } else {
                                                progress = false
                                            }
                                        }
                                    }
                                }

                            }
                    ) {
                        if(progress) {
                            CircularProgressIndicator(color = Color.Black)
                        } else {
                            Text("Place Order", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Text("Delivery on " + deliveryDate, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp, bottom = 30.dp).fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xffa7a7a7))

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutPreview() {
    CheckoutActivityLayout({})
}
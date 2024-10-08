package com.nigel.ecommerce.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme
import com.nigel.ecommerce.models.Order
import com.nigel.ecommerce.models.OrderItem
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.models.Review
import com.nigel.ecommerce.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface() {
                    OrderViewActivityLayout({closeActivity()})
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

private fun writeAddress(context: Context, address: String) {
    val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("deliveryAddress", address)
    editor.apply()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OrderViewActivityLayout(onClose: () -> Unit) {

    var context = LocalContext.current

    var totalPrice by remember { mutableStateOf(0.00) }

    val intent = (context as? Activity)?.intent

    var order by remember { mutableStateOf(
        intent?.getSerializableExtra("order") as Order
    ) }

    var orderItems = remember { mutableStateListOf<OrderItem>().apply {
        addAll(order.orderLines)
    } }

    var customerId by remember { mutableStateOf(intent?.getStringExtra("userID") ) }

    var products = remember { mutableStateListOf<Product?>() }

    var review = remember { mutableStateListOf<Review>() }

    var expanded by remember { mutableStateOf(false) }

    var selectedItem by remember { mutableStateOf("Select a product") }

    var selectedIndex by remember { mutableStateOf(-1) }

    val options by remember { mutableStateOf(mutableStateListOf<String>()) }

    var editReview by remember { mutableStateOf(false) }

    var reviewMessage by remember { mutableStateOf("") }

    var reviewRating by remember { mutableStateOf(0.0f) }

    val focusManager = LocalFocusManager.current

    var scrollState = rememberScrollState()

    val productRepository = ProductRepository(context)

    var refresh by remember { mutableStateOf(true) }

    var progress by remember { mutableStateOf(false) }

    var selectedReview by remember { mutableStateOf(-1) }

    var productsIndexMap by remember { mutableStateOf(mutableListOf<String>()) }

    val builder = AlertDialog.Builder(context)

    var alertTitle by remember { mutableStateOf("") }

    var alertMessage by remember { mutableStateOf("") }

    var showAlert by remember { mutableStateOf(false) }

    var reorderProgress by remember { mutableStateOf(false) }

    var type by remember { mutableStateOf("") }

    var deliveryAddressEdit by remember { mutableStateOf("") }

    var orderUpdated by remember { mutableStateOf(false) }

    val quantityList by remember { mutableStateOf(mutableStateListOf<Double>()) }

    val removedItemsStatus by remember { mutableStateOf(mutableStateListOf<Boolean>()) }

    var cancelRequestProgress by remember { mutableStateOf(false) }

    val isDarkMode = isSystemInDarkTheme()

    var textColor by remember { mutableStateOf(Color(0xff000000)) }

    LaunchedEffect(isDarkMode) {
        if (isDarkMode) {
            textColor = Color(0xffffffff)
        } else {
            textColor = Color(0xff000000)
        }
    }

    LaunchedEffect(Unit, refresh) {
        if(refresh) {
            products.clear()
            review.clear()
            withContext(Dispatchers.IO) {

                for(product in order.orderLines) {
                    val productFromDB = productRepository.getProductById(context, product.productNo)
                    products.add(productFromDB)

                    if(productFromDB == null) {
                        quantityList.add(product.qty)
                        options.add(product.productName)
                        productsIndexMap.add(product.productNo)
                        removedItemsStatus.add(false)
                    } else {
                        quantityList.add(product.qty)
                        options.add(productFromDB.title)
                        productsIndexMap.add(productFromDB.id)
                        removedItemsStatus.add(false)
                        val customerReviews = productRepository.getCustomerReviews(context, product.productNo)
                        review.addAll(customerReviews)
                    }

                    totalPrice += product.total
                }

                refresh = false
            }
        }
    }

    LaunchedEffect(showAlert) {
        if(showAlert) {
            builder.setTitle(alertTitle)
            builder.setMessage(alertMessage)
            builder.setPositiveButton("OK") { dialog, _ ->
                showAlert = false
                if(orderUpdated) {
                    orderUpdated = false
                }
                focusManager.clearFocus()
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    val scope = rememberCoroutineScope()

    EcommerceTheme {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                if(type.equals("UpdateReview")) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                    ) {

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                value = if(selectedIndex >= 0) (selectedIndex + 1).toString() + ". " + selectedItem else selectedItem,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(text = "Select Product") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true },
                                trailingIcon = {
                                    // Arrow icon
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                repeat(options.size) { index ->
                                    var option = options.get(index)
                                    DropdownMenuItem(onClick = {
                                        selectedItem = option
                                        selectedIndex = index
                                        expanded = false
                                    }, modifier = Modifier.background(Color(0xffE7E7E7))) {
                                        Text(text = (index + 1).toString() + ". " + option, color = Color(0xff000000))
                                    }
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
                                    value = reviewMessage,
                                    onValueChange = {
                                        reviewMessage = it
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
                                if(reviewMessage == "") {
                                    Text("Add Your Review", color = Color(0xffA7A7A7), fontSize = 15.sp)
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {

                            Text(reviewRating.toString())
                            Spacer(modifier = Modifier.width(10.dp))
                            Slider(
                                value = reviewRating,
                                onValueChange = { reviewRating = it },
                                valueRange = 0f..5f,
                                steps = 4,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            if(progress) {
                                CircularProgressIndicator(color = Color(0xFFc3e703))
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF96d1c7))
                                        .clickable {
                                            if(editReview) {
                                                progress = true
                                                scope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        val newReview: Map<String, Any> = mapOf(
                                                            "id" to "",
                                                            "customerId" to customerId!!,
                                                            "productId" to order.orderLines.get(selectedIndex).productNo,
                                                            "message" to reviewMessage,
                                                            "rating" to reviewRating.toInt(),
                                                        )

                                                        val response = productRepository.updateReview(context, newReview, review[selectedReview].id)
                                                        if(response) {
                                                            alertTitle = "Success"
                                                            alertMessage = "Review Edited Sucessfully"
                                                            showAlert = true
                                                            refresh = true
                                                            progress = false
                                                        } else {
                                                            alertTitle = "Ooops!"
                                                            alertMessage = "An error occurred when updating your review"
                                                            showAlert = true
                                                            progress = false
                                                        }
                                                    }
                                                }
                                            } else {
                                                progress = true
                                                scope.launch {
                                                    withContext(Dispatchers.IO) {

                                                        val newReview: Map<String, Any> = mapOf(
                                                            "id" to "",
                                                            "customerId" to customerId!!,
                                                            "productId" to order.orderLines.get(selectedIndex).productNo,
                                                            "message" to reviewMessage,
                                                            "rating" to reviewRating.toInt(),
                                                        )

                                                        val response = productRepository.addReview(context, newReview)
                                                        if(response) {
                                                            alertTitle = "Success"
                                                            alertMessage = "Review Added Sucessfully"
                                                            showAlert = true
                                                            refresh = true
                                                            progress = false
                                                        } else {
                                                            alertTitle = "Ooops!"
                                                            alertMessage = "An error occurred when adding your review"
                                                            showAlert = true
                                                            progress = false
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                ) {
                                    if (editReview) {
                                        Text("Update Review", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp), color = Color.Black)
                                    } else {
                                        Text("Add Review", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp), color = Color.Black)
                                    }
                                }
                            }
                        }

                    }
                } else {
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
                                    value = deliveryAddressEdit,
                                    onValueChange = {
                                        deliveryAddressEdit = it
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
                                if(deliveryAddressEdit == "") {
                                    Text("92 High Street, London", color = Color(0xffA7A7A7), fontSize = 15.sp)
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
                                    orderUpdated = true
                                    writeAddress(context, deliveryAddressEdit)
                                    alertTitle = "Success"
                                    alertMessage = "Delivery Address set. "
                                    showAlert = true
                                    val tempOrder = order
                                    tempOrder.deliveryAddress = deliveryAddressEdit
                                    order = tempOrder
                                }
                        ) {
                            Text("Set New Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp), color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                    }
                }
            },
            sheetPeekHeight = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .clickable (
                        onClick = {
                            focusManager.clearFocus()
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp).fillMaxWidth())
                    Box(
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
                                    if(orderUpdated) {
                                        builder.setTitle("Are you sure ?")
                                        builder.setMessage("Seems like you have updated the details of the order. Are you sure you want to exit without saving those changes?")
                                        builder.setPositiveButton("Yes") { dialog, _ ->
                                            showAlert = false
                                            onClose()
                                            dialog.dismiss()
                                        }
                                        builder.setNegativeButton("No") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        builder.create().show()
                                    } else {
                                        onClose()
                                    }
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Icon",
                                modifier = Modifier.width(80.dp)
                            )
                        }

                        Text("Order Details", fontSize = 14.sp, modifier = Modifier.align(Alignment.Center))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            Text(order.orderNo, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(order.status, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if(order.status.equals("Pending")) Color(0xFF96d1c7) else if(order.status.equals("Partially Delivered")) Color(0xffffb343) else Color(0xff4bb543))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 5.dp)
                        ) {
                            Text("Placed on: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                            Text(order.orderDate.split("T")[0], fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                            Text("$" + totalPrice.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(order.orderLines.size.toString() + " items", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

                        if(products.isEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                CircularProgressIndicator(color = Color(0xFFc3e703))
                            }
                        } else {
                            repeat(orderItems.size) { index ->

                                var orderItem = orderItems[index]

                                if(products.size > index) {
                                    Row() {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .weight(1f)
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
                                                    if(products[index] == null) {
                                                        AsyncImage(
                                                            model = "https://www.svgrepo.com/show/422038/product.svg",
                                                            contentDescription = "Image from URL",
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(10.dp),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    } else {
                                                        AsyncImage(
                                                            model = products[index]!!.imageURL[0],
                                                            contentDescription = "Image from URL",
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(80.dp),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(10.dp))

                                                Column(
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(products[index]?.title ?: orderItem.productName , fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp))
                                                    Text(orderItem.status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if(orderItem.status.equals("Pending")) Color(0xFF96d1c7) else Color(0xff4bb543), modifier = Modifier.padding(0.dp))
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("Units: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                                                        Text(quantityList[index].toInt().toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier.fillMaxHeight()
                                                ) {
                                                    if(!orderItem.status.equals("Delivered")) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Delete,
                                                            contentDescription = "Icon",
                                                            modifier = Modifier.width(15.dp).align(Alignment.TopEnd).clickable {
                                                                orderItems.removeAt(index)
                                                                removedItemsStatus[index] = true
                                                            },
                                                            tint = Color.Red
                                                        )
                                                    }
                                                    Text("$" + (orderItem.unitPrice * quantityList[index]).toInt().toString(), textAlign = TextAlign.End, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp).fillMaxWidth().align(Alignment.BottomEnd))
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        if(!orderItem.status.equals("Delivered")) {
                                            Column(
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxHeight()
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.width(30.dp).height(30.dp).clip(
                                                        CircleShape).background(MaterialTheme.colorScheme.secondaryContainer).clickable {
                                                        if(quantityList[index] <= products[index]?.stockCount!!) {
                                                            quantityList[index]++

                                                            if(!orderUpdated) {
                                                                orderUpdated = true
                                                            }

                                                        } else {
                                                            Toast.makeText(context, "Stock Limit Reached", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Add,
                                                        contentDescription = "Icon",
                                                        modifier = Modifier.width(15.dp),
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                Column(
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier
                                                        .width(30.dp)
                                                        .height(30.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                                        .clickable {
                                                            if(quantityList[index] > 1) {
                                                                quantityList[index]--

                                                                if(!orderUpdated) {
                                                                    orderUpdated = true
                                                                }

                                                            }
                                                        }
                                                ) {
                                                    Text("-", fontSize = 16.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                            }

                            if(products.size != order.orderLines.size) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    CircularProgressIndicator(color = Color(0xFFc3e703))
                                }
                            }
                        }

                        Text("Order Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Delivery Address: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                            Text(order.deliveryAddress, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            if(!order.status.equals("Delivered")) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Icon",
                                    modifier = Modifier.width(20.dp).clickable {
                                        type = "updateAddress"
                                        scope.launch {
                                            deliveryAddressEdit = order.deliveryAddress
                                            scaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Payment Status: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                            Text("Paid", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if(!order.status.equals("Delivered")) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                if(order.isCancelRequested ?: false) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                    ) {
                                        Text(if(order.status.equals("Cancelled")) "Cancelled" else "Cancel Request Pending", fontSize = 13.sp, color = Color(0xffa7a7a7), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp))
                                    }
                                } else {
                                    if(cancelRequestProgress) {
                                        CircularProgressIndicator(color = Color.Black)
                                    } else {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.Red)
                                                .clickable {
                                                    cancelRequestProgress = true
                                                    scope.launch {
                                                        withContext(Dispatchers.IO) {
                                                            val response = productRepository.cancelOrder(context, order.orderId)
                                                            if(response) {
                                                                alertTitle = "Request sent"
                                                                alertMessage = "The cancel request has been sent"
                                                                val tempOrder = order
                                                                tempOrder.isCancelRequested = true
                                                                order = tempOrder
                                                                showAlert = true
                                                                cancelRequestProgress = false
                                                            } else {
                                                                alertTitle = "Ooops!"
                                                                alertMessage = "An error occured while sending the cancel request"
                                                                showAlert = true
                                                                cancelRequestProgress = false
                                                            }
                                                        }
                                                    }
                                                }
                                        ) {
                                            Text("Cancel Order", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Text("Your Review", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            repeat(review.size) { index ->

                                var reviewItem = review[index]

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(reviewItem.message, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(reviewItem.rating.toString(), fontSize = 13.sp, color = if(review[index].rating > 4.0) Color(0xff4bb543) else if (review[index].rating > 1.0 && review[index].rating < 4.0) Color(0xffffb343) else Color(0xffff0000), fontWeight = FontWeight.Bold)
                                        }

                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Icon",
                                            modifier = Modifier.width(20.dp).clickable {
                                                scope.launch {
                                                    editReview = true
                                                    type = "UpdateReview"
                                                    selectedReview = index
                                                    reviewMessage = reviewItem.message
                                                    reviewRating = reviewItem.rating.toFloat()
                                                    print(productsIndexMap)
                                                    val optionIndex = productsIndexMap.indexOf(reviewItem.productId)
                                                    selectedIndex = optionIndex
                                                    selectedItem = if(selectedIndex > -1)  options[optionIndex] else "Select Product"
                                                    scaffoldState.bottomSheetState.expand()
                                                }
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                                if(refresh) {
                                    CircularProgressIndicator(color = Color(0xFFc3e703))
                                }

                            }

                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF96d1c7))
                                    .clickable {
                                        scope.launch {
                                            editReview = false
                                            type = "UpdateReview"
                                            reviewMessage = ""
                                            reviewRating = 0.0f
                                            selectedItem = "Select a Product"
                                            selectedIndex = -1
                                            scaffoldState.bottomSheetState.expand()
                                        }
                                    }
                            ) {
                                Text("Add Review", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp), color = Color.Black)
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
                                .background(Color(0xFF96d1c7))
                                .clickable {
                                    if(orderUpdated) {
                                        reorderProgress = true
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                val tempOrderItems = mutableListOf<Map<String, Any>>().apply {
                                                    for(orderItem in order.orderLines) {
                                                        val index = order.orderLines.indexOf(orderItem)
                                                        val newOrderItem = mapOf(
                                                            "orderLineNo" to orderItem.orderLineNo,
                                                            "qty" to quantityList[index].toInt(),
                                                            "remove" to removedItemsStatus[index]
                                                        )
                                                        add(newOrderItem)
                                                    }
                                                }

                                                val reorder: Map<String, Any> = mapOf(
                                                    "orderId" to order.orderId,
                                                    "deliveryAddress" to if(!deliveryAddressEdit.equals("")) deliveryAddressEdit else order.deliveryAddress,
                                                    "orderLines" to tempOrderItems
                                                )


                                                val response = productRepository.updateOrder(context, reorder, order.orderId)

                                                if(response) {
                                                    alertTitle = "Success"
                                                    alertMessage = "Your Order has been updated successfully"
                                                    showAlert = true
                                                    reorderProgress = false
                                                } else {
                                                    reorderProgress = false
                                                }
                                            }
                                        }
                                    } else {
                                        reorderProgress = true
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                val tempOrderItems = mutableListOf<Map<String, Any>>().apply {
                                                    for(orderItem in order.orderLines) {
                                                        val newOrderItem = mapOf(
                                                            "orderLineNo" to "",
                                                            "productNo" to orderItem.productNo,
                                                            "orderNo" to "",
                                                            "vendorNo" to orderItem.vendorNo,
                                                            "status" to "Pending",
                                                            "qty" to orderItem.qty.toInt(),
                                                            "unitPrice" to orderItem.unitPrice.toFloat(),
                                                            "total" to orderItem.total.toFloat()
                                                        )
                                                        add(newOrderItem)
                                                    }
                                                }

                                                val reorder: Map<String, Any> = mapOf(
                                                    "orderNo" to "",
                                                    "customerNo" to order.customerNo,
                                                    "deliveryAddress" to order.deliveryAddress,
                                                    "orderDate" to getCurrentFormattedDate(),
                                                    "status" to "Pending",
                                                    "orderLines" to tempOrderItems
                                                )


                                                val response = productRepository.createOrder(context, reorder)

                                                if(response) {
                                                    alertTitle = "Success"
                                                    alertMessage = "Your Order has been placed successfully"
                                                    showAlert = true
                                                    reorderProgress = false
                                                } else {
                                                    reorderProgress = false
                                                }
                                            }
                                        }
                                    }
                                }
                        ) {
                            if(reorderProgress) {
                                CircularProgressIndicator(color = Color.Black)
                            } else {
                                if(!orderUpdated) {
                                    Text("Reorder", fontWeight = FontWeight.Bold, color = Color.Black)
                                } else {
                                    Text("Update", fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }

                        Text("One Click Away to Order Again", fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp, bottom = 30.dp).fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xffa7a7a7))

                    }



                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderViewActivityPreview() {
    OrderViewActivityLayout({})
}
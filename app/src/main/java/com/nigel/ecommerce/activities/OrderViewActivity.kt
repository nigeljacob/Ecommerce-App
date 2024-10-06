package com.nigel.ecommerce.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme
import com.nigel.ecommerce.models.Order
import com.nigel.ecommerce.models.OrderItem
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.models.Review
import com.nigel.ecommerce.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OrderViewActivityLayout(onClose: () -> Unit) {

    var context = LocalContext.current

    var totalPrice by remember { mutableStateOf(200.00) }

    val intent = (context as? Activity)?.intent

    var order by remember { mutableStateOf(
        intent?.getSerializableExtra("order") as Order
    ) }

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
    
    LaunchedEffect(Unit, refresh) {
        if(refresh) {
            products.clear()
            review.clear()
            withContext(Dispatchers.IO) {
                for(product in order.orderLines) {
                    val productFromDB = productRepository.getProductById(context, product.productNo)
                    products.add(productFromDB)

                    if(productFromDB == null) {
                        options.add(product.productName)
                    } else {
                        options.add(productFromDB.title)
                    }
                }

                val customerReviews = productRepository.getCustomerReviews(context, customerId!!)
                review.addAll(customerReviews)

                refresh = false
            }
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    val scope = rememberCoroutineScope()

    EcommerceTheme {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
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
                                textStyle = TextStyle(fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimary),
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

                                                val response = productRepository.addReview(context, newReview, order)
                                                if(response) {
                                                    refresh = true
                                                    progress = false
                                                } else {
                                                    // show alert
                                                    progress = false
                                                }
                                            }
                                        }
                                    }
                            ) {
                                Text("Add Review", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp), color = Color.Black)
                            }
                        }
                    }

                }
            },
            sheetPeekHeight = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
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
                                    onClose()
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
                            Text(order.orderDate, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                            repeat(order.orderLines.size) { index ->

                                var orderItem = order.orderLines[index]

                                if(products.size > index) {
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
                                                    Text(orderItem.qty.toInt().toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Column(
                                                verticalArrangement = Arrangement.Bottom,
                                                horizontalAlignment = Alignment.End,
                                                modifier = Modifier.fillMaxHeight()
                                            ) {
                                                Text("$" + orderItem.total.toInt().toString(), textAlign = TextAlign.End, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp).fillMaxWidth())
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
                            Text(order.deliveryAddress, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Payment Status: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                            Text("Paid", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Text("Your Review", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            var indexList = mutableListOf<Int>()

                            repeat(review.size) { index ->
                                val reviewItem = review[index]
                                var available = false

                                for(product in products) {
                                    if(product?.id.equals(reviewItem.productId)) {
                                        available = true
                                        indexList.add(products.indexOf(product))
                                    } else {
                                        indexList.add(-1)
                                    }
                                }

                                if(!available) {
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
                                                        reviewMessage = reviewItem.message
                                                        reviewRating = reviewItem.rating.toFloat()
                                                        selectedIndex = indexList[index]
                                                        selectedItem = if(selectedIndex > -1) options[index] else "Select Product"
                                                        scaffoldState.bottomSheetState.expand()
                                                    }
                                                }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                                }

                                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

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

                                }
                        ) {
                            Text("Reorder", fontWeight = FontWeight.Bold, color = Color.Black)
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
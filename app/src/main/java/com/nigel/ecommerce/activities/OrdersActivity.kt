package com.nigel.ecommerce.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme
import com.nigel.ecommerce.models.Order
import com.nigel.ecommerce.models.OrderItem
import com.nigel.ecommerce.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface() {
                    OrdersPreview({closeActivity()})
                }
            }
        }
    }

    private fun closeActivity() {
        finish()
    }
}

private fun openActivity(context: Context, order: Order, id: String) {
    val intent: Intent = Intent(context, OrderViewActivity::class.java)
    intent.putExtra("order", order)
    intent.putExtra("userID", id)
    context.startActivity(intent)
}


@Composable
fun OrdersPreview(onClose: () -> Unit) {

    var context = LocalContext.current

    val intent = (context as? Activity)?.intent

    var selected by remember { mutableStateOf(intent?.getStringExtra("type") ?: "Pending") }

    var orders = remember {
        mutableStateListOf<Order>()
    }
    
    var userId by remember { mutableStateOf(intent?.getStringExtra("userID") ?: "") }
    
    val productRepository = ProductRepository(context)

    var gettingOrders by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            gettingOrders = true
            val ordersFromDB = productRepository.getOrderHistory(context, userId)
            orders.addAll(ordersFromDB)
            gettingOrders = false
        }
    }

    LaunchedEffect(isRefreshing) {
        if(isRefreshing) {
            withContext(Dispatchers.IO) {
                gettingOrders = true
                val ordersFromDB = productRepository.getOrderHistory(context, userId)
                orders.clear()
                orders.addAll(ordersFromDB)
                gettingOrders = false
                isRefreshing = false
            }
        }
    }

    var scrollState = rememberLazyListState()

    EcommerceTheme {
        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing),
            onRefresh = {
                isRefreshing = true
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 40.dp, end = 20.dp)
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
                                modifier = Modifier.width(80.dp)
                            )
                        }

                    }

                    Text("My Orders", fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if(selected.equals("Pending")) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                .clickable {
                                    selected = "Pending"
                                }
                        ) {
                            Text("Pending", modifier = Modifier.padding(5.dp), fontSize = if(selected.equals("Pending")) 15.sp else 13.sp, fontWeight = if(selected.equals("Pending")) FontWeight.Bold else FontWeight.Normal)
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if(selected.equals("Processing")) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                .clickable {
                                    selected = "Processing"
                                }
                        ) {
                            Text("Partially Delivered", textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp), fontSize = if(selected.equals("Processing")) 15.sp else 13.sp, fontWeight = if(selected.equals("Processing")) FontWeight.Bold else FontWeight.Normal)
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if(selected.equals("Delivered")) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                .clickable {
                                    selected = "Delivered"
                                }
                        ) {
                            Text("Delivered", modifier = Modifier.padding(5.dp), fontSize =  if(selected.equals("Delivered")) 15.sp else 13.sp, fontWeight = if(selected.equals("Delivered")) FontWeight.Bold else FontWeight.Normal)
                        }
                    }

                    if(orders.isEmpty()) {
                        if(gettingOrders) {
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
                            Text("No Order History", fontSize = 13.sp, textAlign = TextAlign.Center, color = Color(0xffa7a7a7), modifier = Modifier.fillMaxWidth().padding(20.dp))
                        }
                    } else {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            items(orders.size) { index ->
                                val order = orders[index]
                                if(selected.equals(order.status.replace("Partially Delivered", "Processing"))){

                                    var totalPrice = 0.00
                                    var totalQuantity = 0.00

                                    for(orderItem in order.orderLines) {
                                        totalPrice += orderItem.total
                                        totalQuantity += orderItem.qty
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp)
                                            ) {
                                                Text("Order No: " + order.orderNo, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                                Text(order.orderDate.split("T")[0], fontSize = 13.sp, color = Color(0xffa7a7a7))
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                                            ) {
                                                Text("Products: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                                                Text(order.orderLines.size.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                                                ) {
                                                    Text("Quantity: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                                                    Text(totalQuantity.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                                                ) {
                                                    Text("Total Amount: ", fontSize = 13.sp, color = Color(0xffa7a7a7))
                                                    Text("$"+totalPrice.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier
                                                            .padding(15.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(MaterialTheme.colorScheme.background)
                                                            .clickable {
                                                                openActivity(context, order, userId)
                                                            }
                                                    ) {
                                                        if(selected.equals("Delivered")) {
                                                            Text("Details", fontSize = 13.sp, modifier = Modifier
                                                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp))
                                                        } else {
                                                            Text("Track", fontSize = 13.sp, modifier = Modifier
                                                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp))
                                                        }
                                                    }
                                                }

                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.End,
                                                        modifier = Modifier
                                                            .padding(15.dp)
                                                            .fillMaxWidth()
                                                    ) {
                                                        Text(order.status, fontWeight = FontWeight.Bold ,fontSize = 13.sp, color = if(order.status.equals("Pending")) Color(0xFF96d1c7) else if(order.status.equals("Partially Delivered")) Color(0xffffb343) else Color(0xff4bb543))
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderPreview() {
    OrdersPreview({})
}
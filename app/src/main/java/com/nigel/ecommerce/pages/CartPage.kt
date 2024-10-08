package com.nigel.ecommerce.pages

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nigel.ecommerce.activities.CheckoutActivity
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.ui.theme.EcommerceTheme

private fun updateCart(context: Context, products: MutableList<Product>, quantities: MutableList<Int>) {
    val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()

    // Convert the updated lists to JSON
    val productsJson = gson.toJson(products)
    val quantitiesJson = gson.toJson(quantities)

    // Save the updated lists back to shared preferences
    editor.putString("cartProducts", productsJson)
    editor.putString("cartQuantities", quantitiesJson)
    editor.apply()
}

private fun getCartItemsProducts(context: Context): List<Product> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    val productsJson = sharedPreferences.getString("cartProducts", null)

    return if (productsJson != null) {
        val gson = Gson()
        val type = object : TypeToken<List<Product>>() {}.type
        gson.fromJson(productsJson, type) ?: emptyList()
    } else {
        emptyList()
    }
}

private fun getCartItemsQuantities(context: Context): List<Int> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    val quantitiesJson = sharedPreferences.getString("cartQuantities", null)

    return if (quantitiesJson != null) {
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        gson.fromJson(quantitiesJson, type) ?: emptyList()
    } else {
        emptyList()
    }
}

private fun openActivity(context: Context, products: MutableList<Product>, quantities: MutableList<Int>, deliveryAddress: String, total: Double, userId: String, selected: MutableList<Boolean>) {
    val intent: Intent = Intent(context, CheckoutActivity::class.java)
    intent.putExtra("deliveryAddress", deliveryAddress)
    intent.putExtra("total", total)
    intent.putExtra("products", ArrayList(products))
    intent.putExtra("quantity", ArrayList(quantities))
    intent.putExtra("userID", userId)
    intent.putExtra("selected", ArrayList(selected))
    context.startActivity(intent)
}

@Composable
fun CartPage(modifier: Modifier = Modifier, id: String) {

    var context = LocalContext.current

    val cartItems = remember {
        mutableStateListOf<Product>().apply {
            addAll(getCartItemsProducts(context))
        }
    }

    var cartItemsQuantity = remember {
        mutableStateListOf<Int>().apply {
            addAll(getCartItemsQuantities(context))
        }
    }

    var cartItemsSelected = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(cartItems.size) {
                add(false)
            }
        }
    }
    
    var deliveryAddress by remember { mutableStateOf("") }

    var selectAll by rememberSaveable { mutableStateOf(false) }

    var atleastOneSelected by rememberSaveable { mutableStateOf(false) }

    var scrollState = rememberLazyListState()

    var cartTotal by rememberSaveable { mutableStateOf(0.00) }

    LaunchedEffect (cartItems, cartItemsQuantity.map { it }, cartItemsSelected.map { it }) {
        cartTotal = cartItems.sumOf {
            if(cartItemsSelected.get(cartItems.indexOf(it))) {
                it.price * cartItemsQuantity[cartItems.indexOf(it)]
            } else {
                0.00
            }
        }
    }

    val builder = AlertDialog.Builder(context)
    builder.setTitle("Are you sure ?")
    builder.setMessage("Are you sure you want to remove items from the cart ?")

    LaunchedEffect(cartItemsSelected.map { it }) {
        var available = false
        var atleastOne = false

        for(i in 0 until cartItemsSelected.size) {
            if(!cartItemsSelected[i]) {
                available = true
            }

            if(cartItemsSelected[i]) {
                atleastOne = true
            }
        }

        if(!available) {
            selectAll = true
        } else {
            selectAll = false
        }

        if(!atleastOne) {
            atleastOneSelected = false
        }
    }

    LaunchedEffect(Unit) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
        deliveryAddress = sharedPreferences.getString("deliveryAddress", null) ?: "Not Set Yet"
    }

    EcommerceTheme {
        Box(
            Modifier.focusModifier().fillMaxWidth().fillMaxHeight()
        ) {
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
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
                    ) {
                        Text("Cart", fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(start = 10.dp, end = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Icon",
                                    tint = Color(0xffa7a7a7),
                                    modifier = Modifier.width(20.dp)
                                )

                                Text(deliveryAddress, fontSize = 14.sp, modifier = Modifier.padding(start = 5.dp).weight(1f))

                                

                                Spacer(modifier = Modifier.width(5.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ){
                        Checkbox(
                            checked = selectAll,
                            onCheckedChange = {
                                selectAll = it

                                if(it) {
                                    cartItemsSelected.replaceAll { true }
                                    atleastOneSelected = true
                                } else {
                                    cartItemsSelected.replaceAll { false }
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF96d1c7)
                            )
                        )

                        Text("Select all", fontSize = 14.sp, modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Icon",
                            tint = if(atleastOneSelected) Color(0xffff0000) else Color(0xffa7a7a7),
                            modifier = Modifier
                                .width(20.dp)
                                .clickable {

                                    builder.setPositiveButton("Yes") { dialog, which ->
                                        var deleteIndex = mutableListOf<Int>()
                                        if(atleastOneSelected) {
                                            for(index in 0 until cartItems.size) {
                                                if(cartItemsSelected[index]) {
                                                    deleteIndex.add(0, index)
                                                }
                                            }

                                            for(index in 0 until deleteIndex.size) {
                                                cartItems.removeAt(deleteIndex[index])
                                                cartItemsSelected.removeAt(deleteIndex[index])
                                                cartItemsQuantity.removeAt(deleteIndex[index])
                                            }

                                            atleastOneSelected = false
                                            selectAll = false
                                            Toast.makeText(context, deleteIndex.size.toString() + " items deleted from cart", Toast.LENGTH_SHORT).show()
                                        }
                                        val products = mutableListOf<Product>().apply {
                                            addAll(cartItems)
                                        }

                                        val quantities = mutableListOf<Int>().apply {
                                            addAll(cartItemsQuantity)
                                        }
                                        updateCart(context, products, quantities)
                                        dialog.dismiss()
                                    }

                                    builder.setNegativeButton("No") { dialog, which ->
                                        dialog.dismiss()
                                    }

                                    val alertDialog: AlertDialog = builder.create()

                                    alertDialog.show()

                                }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                    }

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(cartItems.size) { index ->

                            var cartItem = cartItems[index]

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp)
                            ) {
                                Checkbox(
                                    checked = cartItemsSelected[index],
                                    onCheckedChange = {
                                        cartItemsSelected[index] = it
                                        if(it) {
                                            atleastOneSelected = true
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF96d1c7)
                                    )
                                )

                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    AsyncImage(
                                        model = cartItem.imageURL[0],
                                        contentDescription = "Image from URL",
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f).padding(start = 10.dp, end = 10.dp).height(70.dp)
                                ) {

                                    Text(cartItem.title, fontSize = 14.sp, modifier = Modifier.weight(1f))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {

                                        var price = cartItem.price * cartItemsQuantity[index]

                                        Text("$" + String.format("%.2f", price), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(30.dp)
                                                .height(30.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .clickable {
                                                    cartItemsQuantity[index]--
                                                    val products = mutableListOf<Product>().apply {
                                                        addAll(cartItems)
                                                    }

                                                    val quantities = mutableListOf<Int>().apply {
                                                        addAll(cartItemsQuantity)
                                                    }
                                                    updateCart(context, products, quantities)
                                                }
                                        ) {
                                            Text("-", fontSize = 16.sp)
                                        }

                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(30.dp)
                                                .height(30.dp)
                                                .clip(CircleShape)
                                        ) {
                                            Text(cartItemsQuantity[index].toString(), fontSize = 16.sp)
                                        }

                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(30.dp)
                                                .height(30.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .clickable {
                                                    cartItemsQuantity[index]++
                                                    val products = mutableListOf<Product>().apply {
                                                        addAll(cartItems)
                                                    }

                                                    val quantities = mutableListOf<Int>().apply {
                                                        addAll(cartItemsQuantity)
                                                    }
                                                    updateCart(context, products, quantities)
                                                }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = "Icon",
                                                modifier = Modifier.width(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp, bottom = 110.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if(atleastOneSelected) Color(0xFFc3e703) else Color(0x77c3e703))
                        .clickable {
                            if(atleastOneSelected) {
                                var cartItemsArray = mutableListOf<Product>()
                                var cartQuantity = mutableListOf<Int>()
                                var selectedArray = mutableListOf<Boolean>()
                                for(selected in 0 until cartItemsSelected.size) {
                                    if(cartItemsSelected[selected]) {
                                        cartItemsArray.add(cartItems[selected])
                                        cartQuantity.add(cartItemsQuantity[selected])
                                        selectedArray.add(cartItemsSelected[selected])
                                    }
                                }
                                openActivity(context, cartItemsArray, cartQuantity, deliveryAddress, cartTotal, id, selectedArray)
                            }
                        }
                ) {
                    Text("Checkout - $" + String.format("%.2f", cartTotal) , fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun cartPreview() {
    CartPage(Modifier, "")
}
package com.nigel.ecommerce.pages

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nigel.ecommerce.SeeAllActivity
import com.nigel.ecommerce.activities.NotificationActivity
import com.nigel.ecommerce.activities.ProductViewActivity
import com.nigel.ecommerce.components.ProductCard
import com.nigel.ecommerce.models.Category
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.models.Review
import com.nigel.ecommerce.repository.ProductRepository
import com.nigel.ecommerce.ui.theme.EcommerceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private fun openActivity(context: Context, product: Product) {
    val intent: Intent = Intent(context, ProductViewActivity::class.java)
    intent.putExtra("product", product)
    context.startActivity(intent)
}

private fun openShowAll(context: Context, products: MutableList<Product>, type: String, title: String) {
    val intent: Intent = Intent(context, SeeAllActivity::class.java)
    intent.putExtra("title", title)
    intent.putExtra("products", ArrayList(products))
    intent.putExtra("category", type)
    context.startActivity(intent)
}

private fun writeAddress(context: Context, address: String) {
    val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("deliveryAddress", address)
    editor.apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(modifier: Modifier = Modifier, products: MutableList<Product>, categories: MutableList<Category>, search: (text: String) -> Unit) {

    var context = LocalContext.current

    var searchText by rememberSaveable { mutableStateOf("") }

    val searchBarHeight = remember { 56.dp }

    val scrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val itemWidth = 150.dp
    val columns = (screenWidth / itemWidth).toInt()

    val gridScrollState = rememberLazyGridState()

    val focusManager = LocalFocusManager.current

    var isFocused by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState()

    var deliveryAddressEdit by remember { mutableStateOf("") }

    var deliveryAddress by remember { mutableStateOf("") }

    var addressUpdated by remember { mutableStateOf(true) }

    var scope = rememberCoroutineScope()

    val isDarkMode = isSystemInDarkTheme()

    var textColor by remember { mutableStateOf(Color(0xff000000)) }

    LaunchedEffect(isDarkMode) {
        if (isDarkMode) {
            textColor = Color(0xffffffff)
        } else {
            textColor = Color(0xff000000)
        }
    }

    LaunchedEffect(Unit, addressUpdated) {
        if(addressUpdated) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
            deliveryAddress = sharedPreferences.getString("deliveryAddress", null) ?: "Not Set Yet"
            addressUpdated = false
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
                                writeAddress(context, deliveryAddressEdit)
                                addressUpdated = true
                                Toast.makeText(context, "Address Updated", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Text("Update Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp), color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                }
            },
            sheetPeekHeight = 0.dp,
        ) {
            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer).clickable(
                onClick = {
                    focusManager
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )) {
                Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)).background(MaterialTheme.colorScheme.background)) {
                    Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(55.dp)
                                .height(55.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFc3e703))
                        ) {
                            // logo
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                        if(!deliveryAddress.equals("Not Set Yet")) {
                                            deliveryAddressEdit = deliveryAddress
                                        }
                                    }
                                }
                        ) {
                            Text("Delivery Address", fontSize = 14.sp, color = Color(0xffa7a7a7))
                            Text(deliveryAddress, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(55.dp)
                                .height(55.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .clickable {
                                    val intent: Intent = Intent(context, NotificationActivity::class.java)
                                    context.startActivity(intent)
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Icon",
                                modifier = Modifier.width(20.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().height(searchBarHeight - (scrollState.value / 4).dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.secondaryContainer).padding(start = 10.dp, end = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {

                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Icon",
                                    tint = Color(0xffa7a7a7),
                                    modifier = Modifier.width(20.dp)
                                )

                                Spacer(modifier.width(5.dp))

                                Box() {
                                    BasicTextField(
                                        value = searchText,
                                        onValueChange = {
                                            searchText = it.trim()
                                        },
                                        textStyle = TextStyle(
                                            fontSize = 15.sp,
                                            color = textColor,
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Text,
                                            imeAction = ImeAction.Search
                                        ),
                                        keyboardActions = KeyboardActions (
                                            onSearch = {
                                                search(searchText)
                                            }
                                        ),
                                        modifier = Modifier
                                            .onFocusChanged { focusState ->
                                                isFocused = focusState.isFocused
                                            }
                                    )
                                    if(searchText == ""  && !isFocused) {
                                        Text("Search", color = Color(0xffA7A7A7), fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollState)

                ) {
                    Row(
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Text("Categories", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between items
                        ) {
                            items(categories.size) { index ->

                                val category = categories[index]

                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                                        .clickable (
                                            onClick = {
                                                val categoryProduct = mutableListOf<Product>()

                                                for(product in products) {

                                                    if(product.category.equals(category.id)) {
                                                        categoryProduct.add(product)
                                                    }

                                                }

                                                openShowAll(context, categoryProduct, "Category", category.name + "s")
                                            },
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        )
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(55.dp)
                                            .height(55.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                    ) {
                                        AsyncImage(
                                            model = category.imageUrl,
                                            contentDescription = "Image from URL",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(55.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Text(category.name, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Text("New Arivals", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
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
                                        openShowAll(context, products, "Product", "All Products")
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

                    Column() {
                        val rows = products.chunked(2)
                        for (row in rows) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                for (product in row) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 4.dp).weight(1f).clickable {
                                            for(product in products) {
                                                val categoryId = product.category
                                                println(categoryId)
                                                for(category in categories) {
                                                    println(category.id)
                                                    if(category.id.equals(categoryId)) {
                                                        product.category = category.name
                                                        break;
                                                    }
                                                }
                                            }
                                            openActivity(context, product)
                                        }
                                    ) {
                                        ProductCard(
                                            product = product
                                        )
                                    }
                                }

                                if (row.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomePage(Modifier, mutableListOf<Product>(), mutableListOf<Category>(), {})
}
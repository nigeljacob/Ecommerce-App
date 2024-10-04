package com.nigel.ecommerce.pages

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nigel.ecommerce.activities.ProductViewActivity
import com.nigel.ecommerce.components.ProductCard
import com.nigel.ecommerce.models.Category
import com.nigel.ecommerce.models.Product
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

@Composable
fun HomePage(modifier: Modifier = Modifier, products: MutableList<Product>, categories: MutableList<Category>) {

    var context = LocalContext.current

    var searchText by rememberSaveable { mutableStateOf("") }

    val searchBarHeight = remember { 56.dp }

    val scrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val itemWidth = 150.dp
    val columns = (screenWidth / itemWidth).toInt()

    val gridScrollState = rememberLazyGridState()

    EcommerceTheme {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer)) {
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
                    ) {
                        Text("Delivery Address", fontSize = 14.sp, color = Color(0xffa7a7a7))
                        Text("92 High Street, London", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(55.dp)
                            .height(55.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
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
                                        searchText = it
                                    },
                                    textStyle = TextStyle(fontSize = 15.sp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email
                                    )
                                )
                                if(searchText == "") {
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
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.width(20.dp)
                            )
                        }
                    }
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
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
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

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomePage(Modifier, mutableListOf<Product>(), mutableListOf<Category>())
}
package com.nigel.ecommerce.pages

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
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
import com.nigel.ecommerce.R
import com.nigel.ecommerce.activities.ProductViewActivity
import com.nigel.ecommerce.components.ProductCard
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.ui.theme.EcommerceTheme

fun getProducts(searchQuery: String, products: MutableList<Product>): SnapshotStateList<Product> {

    var results = mutableStateListOf<Product>()

    for(product in products) {
        if(product.title.uppercase().contains(searchQuery.uppercase())) {
            results.add(product)
        } else if (searchQuery.uppercase().contains(product.title.uppercase())) {
            results.add(product)
        } else if(searchQuery.uppercase().contains(product.category.uppercase())) {
            results.add(product)
        } else if (product.category.uppercase().contains(searchQuery.uppercase())) {
            results.add(product)
        }
    }

    return results

}

private fun writeToSharedPreference(searchQuery: String, context: Context) {
    val previousValues = getFromSharedPreference("searchQuery", context)

    if(previousValues != null) {
        val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("searchQuery", searchQuery + ";;" + previousValues)
        editor.apply()
    } else {
        val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("searchQuery", searchQuery)
        editor.apply()
    }
}

private fun getFromSharedPreference(key: String, context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, null)
}

private fun openActivity(context: Context, product: Product) {
    val intent: Intent = Intent(context, ProductViewActivity::class.java)
    intent.putExtra("product", product)
    context.startActivity(intent)
}

@Composable
fun SearchPage(modifier: Modifier = Modifier, products: MutableList<Product>, searchText: String) {

    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf(searchText) }

    val focusManager = LocalFocusManager.current

    var isFocused by remember { mutableStateOf(false) }

    var searchResults = remember { mutableStateListOf<Product>(

    ) }

    var previousSearch by remember { mutableStateOf<String>(
        getFromSharedPreference("searchQuery", context) ?: ""
    )}

    var scrollState = rememberScrollState()

    var horizontalScrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val itemWidth = 150.dp
    val columns = (screenWidth / itemWidth).toInt()

    var containerHeight by remember { mutableStateOf(0) }

    var searchConducted by remember { mutableStateOf(false) }

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
        if(!searchText.equals("")) {
            searchConducted = false
            searchResults.clear()
            focusManager.clearFocus()
            val results = getProducts(searchQuery, products)
            searchResults.addAll(results)
            if(!previousSearch.split(";;")[0].equals(searchQuery)) {
                writeToSharedPreference(searchQuery, context)
                previousSearch = searchQuery + ";;" + previousSearch
            }
            searchConducted = true
        }
    }

    EcommerceTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(
                    onClick = {
                        focusManager.clearFocus()
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
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
                    Text("Search", fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
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
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it.trim()
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
                                            if(!searchQuery.isEmpty()) {
                                                if(!searchQuery.equals(" ")) {
                                                    searchConducted = false
                                                    searchResults.clear()
                                                    focusManager.clearFocus()
                                                    val results = getProducts(searchQuery, products)
                                                    searchResults.addAll(results)
                                                    if(!previousSearch.split(";;")[0].equals(searchQuery)) {
                                                        writeToSharedPreference(searchQuery, context)
                                                        previousSearch = searchQuery + ";;" + previousSearch
                                                    }
                                                    searchConducted = true
                                                }
                                            }
                                        }
                                    ),
                                    modifier = Modifier
                                        .onFocusChanged { focusState ->
                                            isFocused = focusState.isFocused
                                        }
                                )
                                if(searchQuery == "" && !isFocused) {
                                    Text("Search", color = Color(0xffA7A7A7), fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }

                if(previousSearch != "") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        if(previousSearch.split(";;").size > 6) {
                            for (i in 0 until 6) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(5.dp)
                                        .clickable {
                                            searchConducted = false
                                            searchResults.clear()
                                            searchQuery = previousSearch.split(";;")[i]
                                            focusManager.clearFocus()
                                            val results = getProducts(searchQuery, products)
                                            searchResults.addAll(results)
                                            if(!previousSearch.split(";;")[0].equals(searchQuery)) {
                                                writeToSharedPreference(searchQuery, context)
                                                previousSearch = searchQuery + ";;" + previousSearch
                                            }
                                            searchConducted = true
                                        }
                                ) {
                                    Text(previousSearch.split(";;")[i], fontSize = 13.sp, color = Color(0xffa7a7a7))
                                }
                            }
                        } else {
                            for (i in 0 until previousSearch.split(";;").size) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(5.dp)
                                        .clickable {
                                            searchConducted = false
                                            searchResults.clear()
                                            searchQuery = previousSearch.split(";;")[i]
                                            focusManager.clearFocus()
                                            val results = getProducts(searchQuery, products)
                                            searchResults.addAll(results)
                                            if(!previousSearch.split(";;")[0].equals(searchQuery)) {
                                                writeToSharedPreference(searchQuery, context)
                                                previousSearch = searchQuery + ";;" + previousSearch
                                            }
                                            searchConducted = true
                                        }
                                ) {
                                    Text(previousSearch.split(";;")[i], fontSize = 13.sp, color = Color(0xffa7a7a7))
                                }
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                }
            }

            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        containerHeight = it.size.height
                    }
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
            ) {
                if(searchResults.isEmpty()) {
                    if(searchConducted) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((containerHeight/4.5).dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "My Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = ContentScale.Fit
                            )

                            Text("Nothing Found", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                        }
                    }
                } else {
                    Column(

                    ) {

                        Text(searchResults.size.toString() + " Results Found", fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp))

                        val rows = searchResults.chunked(2)
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

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    SearchPage(Modifier, mutableListOf<Product>(), "")
}
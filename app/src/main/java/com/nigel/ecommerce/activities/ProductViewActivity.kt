package com.nigel.ecommerce.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources.Theme
import android.os.Bundle
import android.widget.Space
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.models.Review
import com.nigel.ecommerce.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class ProductViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface {
                    ProductViewActivityLayout { closeActivity() }
                }
            }
        }
    }

    private fun closeActivity() {
        finish()
    }
}

private fun checkForProduct(product: Product, products: MutableList<Product>): Int {
    for(i in products) {
        if(i.id.equals(product.id)) {
            return products.indexOf(i)
        }
    }

    return -1
}

private fun addToCart(context: Context, product: Product) {
    val sharedPreferences = context.getSharedPreferences("EcommercePreference", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()

    // Retrieve previous cart items
    val products = getCartItemsProducts(context).toMutableList()
    val quantities = getCartItemsQuantities(context).toMutableList()

    if(products.isEmpty()){
        products.add(product)
        quantities.add(1)
    } else {
        val index = checkForProduct(product, products)

        if( index != -1 ) {
            quantities[index]++
        } else {
            products.add(product)
            quantities.add(1)
        }
    }

    // Convert the updated lists to JSON
    val productsJson = gson.toJson(products)
    val quantitiesJson = gson.toJson(quantities)

    // Save the updated lists back to shared preferences
    editor.putString("cartProducts", productsJson)
    editor.putString("cartQuantities", quantitiesJson)
    editor.apply()

    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()

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

@Composable
fun ProductViewActivityLayout(onClose: () -> Unit) {

    val context = LocalContext.current

    val intent = (context as? Activity)?.intent

    val product = intent?.let {
        intent.getSerializableExtra("product") as Product
    } ?: Product("", "", listOf(""), "", "", 0.00, "", true, 10.00, "", 10.00, false, "")

    var imageContainerHeight by remember { mutableStateOf(0) }

    var scrollstate = rememberScrollState()

    val imageWeight by remember {
        derivedStateOf {
            val weight = 1f - (scrollstate.value / 1000f)
            max(0.2f, min(1f, weight))
        }
    }

    var viewFullDescription by rememberSaveable { mutableStateOf(false) }

    val review = remember { mutableStateListOf<Review>() }

    var refresh by remember { mutableStateOf(true) }

    var reviewRating by remember { mutableStateOf(0.00) }

    val annotatedString = buildAnnotatedString {
        // Base text without glow effect
        withStyle(
            style = SpanStyle(color = Color(0xffa7a7a7))
        ) {
            if(product.description.length > 222) {
                append(product!!.description.substring(0, 222))
            } else {
                append(product!!.description)
            }
        }

        append(". ")

        withStyle(
            style = SpanStyle(
                shadow = Shadow(
                    color = MaterialTheme.colorScheme.background,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                ),
                fontWeight = FontWeight.Bold,
            )
        ) {
            append("Read More")
        }
    }

    val productRepository = ProductRepository(context)

    LaunchedEffect(Unit, refresh) {
        withContext(Dispatchers.IO) {
            if(refresh) {
                review.clear()
                val customerReviews = productRepository.getProductReviews(context, product.id)
                review.addAll(customerReviews)
                reviewRating = productRepository.getAverageReview(context, product.id)
                refresh = false
            }
        }
    }

    EcommerceTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(imageWeight)
                    .onSizeChanged { size ->
                        imageContainerHeight = size.height
                    }
            ) {
                AsyncImage(
                    model = product!!.imageURL[0],
                    contentDescription = "Image from URL",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageContainerHeight.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Fit
                )

            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollstate)
                        .padding(20.dp)

                ) {
                    Text(product!!.title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Row(

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(start = 20.dp, end = 20.dp)
                                .height(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Icon",
                                tint = Color(0xFF96d1c7),
                                modifier = Modifier.width(20.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(Math.round(reviewRating).toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp, start = 10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(start = 20.dp, end = 20.dp)
                                .height(30.dp)
                        ) {
                            Text(product.category, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .height(50.dp)
                            .padding(start = 10.dp)
                    ) {
                        Text("$ " + product.price.toString(), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Column(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .clickable {
                                viewFullDescription = !viewFullDescription
                            }
                    ) {
                        if(product.description.length > 222 && !viewFullDescription) {
                            Text(annotatedString, fontSize = 14.sp)
                        } else {
                            Text(product.description, fontSize = 14.sp, color = Color(0xffA7A7A7))
                        }
                    }

                    Column(
                    ) {
                        Text("All Reviews", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            if(refresh) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                                ) {
                                    CircularProgressIndicator(color = Color(0xFFc3e703))
                                }
                            } else {
                                if(review.size > 0) {
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
                                            }
                                        }

                                        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                                        if(refresh) {
                                            CircularProgressIndicator(color = Color(0xFFc3e703))
                                        }

                                    }
                                } else {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                                    ) {
                                        Text("No Reviews Yet", color = Color(0xffa7a7a7), fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp).fillMaxWidth())
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(start = 20.dp, end = 20.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFc3e703))
                            .clickable {
                                addToCart(context, product)
                            }
                    ) {
                        Text("Add to Cart", fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Text("Delivery in 3 Days", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp, bottom = 30.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductViewActivityLayoutPreview() {
    ProductViewActivityLayout(onClose = {})
}
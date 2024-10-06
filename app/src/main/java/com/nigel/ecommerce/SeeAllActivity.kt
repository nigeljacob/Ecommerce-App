package com.nigel.ecommerce

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nigel.ecommerce.activities.ProductViewActivity
import com.nigel.ecommerce.components.ProductCard
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.ui.theme.EcommerceTheme

class SeeAllActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface() {
                    SeeAllLayout({finish()})
                }
            }
        }
    }
}

private fun openActivity(context: Context, product: Product) {
    val intent: Intent = Intent(context, ProductViewActivity::class.java)
    intent.putExtra("product", product)
    context.startActivity(intent)
}

@Composable
fun SeeAllLayout(onClose: () -> Unit) {

    val context = LocalContext.current

    val intent = (context as? Activity)?.intent

    var title by remember { mutableStateOf(intent?.getStringExtra("title") ?: "") }

    var products by remember { mutableStateOf(intent?.getSerializableExtra("products") as MutableList<Product> ?: mutableListOf<Product>()) }

    val scrollState = rememberLazyGridState()

    EcommerceTheme {
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
                Spacer(modifier = Modifier.height(20.dp))

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

                Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp))

                Spacer(modifier = Modifier.height(20.dp))

                if(!products.isEmpty()) {
                    LazyVerticalGrid(
                        state = scrollState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(products.size) {index ->
                            Column(
                                modifier = Modifier.padding(vertical = 4.dp).weight(1f).clickable {
                                    openActivity(context, products[index])
                                }
                            ) {
                                ProductCard(
                                    product = products[index]
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.offset(y = -50.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "My Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = ContentScale.Fit
                            )

                            Text("Nothing Found", fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp).fillMaxWidth())
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeeAllPreview() {
    SeeAllLayout({})
}
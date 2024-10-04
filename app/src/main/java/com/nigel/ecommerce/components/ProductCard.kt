package com.nigel.ecommerce.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.nigel.ecommerce.models.Product


@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {

    var cardWidth by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .padding(10.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    cardWidth = size.width
                }
                .height(150.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            AsyncImage(
                model = product.imageURL[0],
                contentDescription = "Image from URL",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 10.dp)
        ) {
            Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = "$${product.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
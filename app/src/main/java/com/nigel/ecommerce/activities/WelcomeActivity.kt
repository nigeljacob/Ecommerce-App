package com.nigel.ecommerce.activities

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nigel.ecommerce.R
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface() {
                    WelcomeActivityLayout()
                }
            }
        }
    }
}

private fun openActivity(context: Context, type: String) {
    if(type.equals("Login")) {
        val intent: Intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    } else {
        val intent: Intent = Intent(context, RegisterActivity::class.java)
        context.startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeActivityLayout() {

    var context = LocalContext.current

    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var containerHeight by remember { mutableStateOf(0) }

    EcommerceTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .zIndex(1f)
                    .onSizeChanged { size ->
                        containerHeight = size.height
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.welcome),
                        contentDescription = "My Image",
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {

                Text("Welcome to Ecommerce App", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 25.sp, modifier = Modifier.padding(bottom = 10.dp, top = 40.dp).fillMaxWidth())
                Text("We've got the best shopping experience for you", fontSize = 13.sp, color = Color(0xffa7a7a7), modifier = Modifier.padding(bottom = 10.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable { 
                                openActivity(context, "Login")
                            }
                    ) {
                        Text("Login", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFc3e703))
                            .clickable {
                                openActivity(context, "Register")
                            }
                    ) {
                        Text("Sign Up", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
            }
        }
    }
}
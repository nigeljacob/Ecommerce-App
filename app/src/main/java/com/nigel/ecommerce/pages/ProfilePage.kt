package com.nigel.ecommerce.pages

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nigel.ecommerce.R
import com.nigel.ecommerce.activities.LoginActivity
import com.nigel.ecommerce.activities.RegisterActivity
import com.nigel.ecommerce.ui.theme.EcommerceTheme

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
fun ProfilePage(modifier: Modifier = Modifier) {

    var context = LocalContext.current

    var loggedIn by remember { mutableStateOf(false) }


    EcommerceTheme {
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
                    Text("Profile", fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }

                if(loggedIn) {

                } else {

                    Text(
                        text = "You are not logged in. Please log in to view your profile.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xffa7a7a7),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 30.dp, end = 30.dp, bottom = 20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp)
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
                                Text("Login" , fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp)
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
                                Text("Sign Up" , fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

            if(loggedIn) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {

                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nothing),
                        contentDescription = "My Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .offset(y = -100.dp),
                        contentScale = ContentScale.Fit
                    )

                }
            }
        }
    }
}
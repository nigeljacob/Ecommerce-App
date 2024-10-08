package com.nigel.ecommerce.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nigel.ecommerce.R
import com.nigel.ecommerce.activities.ui.theme.EcommerceTheme
import com.nigel.ecommerce.models.Notification

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface() {
                    NotificationLayout()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationLayout() {

    val notifications = remember { mutableStateListOf<Notification>(
        Notification(
            "001",
            "Welcome",
            "Welcome to our store",
            true,
            "001",
            "2024",
        )
    ) }

    val isDarkMode = isSystemInDarkTheme()

    var textColor by remember { mutableStateOf(Color(0xff000000)) }

    LaunchedEffect(isDarkMode) {
        if (isDarkMode) {
            textColor = Color(0xffffffff)
        } else {
            textColor = Color(0xff000000)
        }
    }

    val scrollState = rememberLazyListState()

    EcommerceTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable {

                            }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Icon",
                            modifier = Modifier.width(80.dp),
                            tint = textColor
                        )
                    }

                }

                Text("Notifications", fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp), color = textColor)

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(notifications.size) { index ->
                        val notification = notifications[index]

                        Column (
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xffffffff))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.store),
                                            contentDescription = "My Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(notification.title, fontWeight = FontWeight.Bold, color = textColor)
                                        Text(notification.message, color = Color(0xffa7a7a7), fontSize = 13.sp)
                                    }

                                    if(!notification.IsRead) {
                                        Column(
                                            modifier = Modifier.width(15.dp).height(15.dp).offset(y = 8.dp).clip(
                                                CircleShape).background(Color(0xFFc3e703))
                                        ) {
                                        }
                                    }

                                }

                                Text(notification.createdOn, color = Color(0xffa7a7a7), fontSize = 12.sp, modifier = Modifier.align(
                                    Alignment.TopEnd))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                    }
                }

            }
        }
    }
}
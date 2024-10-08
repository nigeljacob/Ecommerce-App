package com.nigel.ecommerce

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nigel.ecommerce.activities.WelcomeActivity
import com.nigel.ecommerce.models.Category
import com.nigel.ecommerce.models.NavItem
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.models.User
import com.nigel.ecommerce.pages.CartPage
import com.nigel.ecommerce.pages.HomePage
import com.nigel.ecommerce.pages.ProfilePage
import com.nigel.ecommerce.pages.SearchPage
import com.nigel.ecommerce.repository.AuthRepository
import com.nigel.ecommerce.repository.ProductRepository
import com.nigel.ecommerce.ui.theme.EcommerceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
        // check login

        val authRepository = AuthRepository(this)
        val loggedIn = authRepository.checkLogin()

        if(!loggedIn) {
            val intent: Intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceTheme {
                Surface {
                    MainLayout({closeActivity()}, {finish()})
                }
            }
        }
    }

    private fun closeActivity() {
        finish()
    }
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

@Composable
fun MainLayout(onClose: () -> Unit, onDestroy: () -> Unit) {

    // get cart count from shared preference

    var context = LocalContext.current

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, 0),
        NavItem("Search", Icons.Default.Search, 0),
        NavItem("Cart", Icons.Default.ShoppingCart, 0),
        NavItem("Profile", Icons.Default.AccountCircle, 0)
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val primaryColor = Color(0xFFc3e703)
    val grayColor = Color.Gray

    val categories = remember {
        mutableStateListOf<Category>()
    }

    // dummy data for testing
    val products = remember {
        mutableStateListOf<Product>()
    }

    var userDetails by remember { mutableStateOf<User?>(null) }

    val productRepository = ProductRepository(context)

    val authRepository = AuthRepository(context)

    var isRefreshing by remember { mutableStateOf(true) }

    var searchText by remember { mutableStateOf("") }

    var error by remember { mutableStateOf(false) }

    var alertTitle by remember { mutableStateOf("") }

    var alertMessage by remember { mutableStateOf("") }

    var showAlert by remember { mutableStateOf(false) }

    val builder = AlertDialog.Builder(context)

    LaunchedEffect(showAlert) {
        if(showAlert) {
            builder.setTitle(alertTitle)
            builder.setMessage(alertMessage)
            builder.setPositiveButton("Close App") { dialog, _ ->
                showAlert = false
                dialog.dismiss()
                onDestroy()
            }
            builder.create().show()
        }
    }

    LaunchedEffect(Unit, isRefreshing) {
        if(isRefreshing) {
            withContext(Dispatchers.IO) {

                try{
                    categories.clear()
                    products.clear()
                    userDetails = authRepository.getUserDetails()
                    println(userDetails?.name)
                    val categoriesFromDB = productRepository.getAllCategories(context)
                    categories.addAll(categoriesFromDB)
                    val productsFromDB = productRepository.getAllProducts(context)
                    products.addAll(productsFromDB)
                    isRefreshing = false
                } catch (exeption: Exception) {
                    alertTitle = "Ooops!"
                    alertMessage = "The app is unable to connect to it's Server. Try again later"
                    showAlert = true
                }
            }
        }
    }

    EcommerceTheme {
        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing),
            onRefresh = {
                isRefreshing = true
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        navItems.forEachIndexed { index, navItem ->
                            NavigationBarItem(
                                selected = selectedIndex == index,
                                onClick = {
                                    selectedIndex = index
                                },
                                icon = {
                                    BadgedBox(badge = {
                                        if (navItem.badgeCount > 0)
                                            Badge {
                                                Text(text = navItem.badgeCount.toString())
                                            }
                                    }) {
                                        Icon(
                                            imageVector = navItem.icon,
                                            contentDescription = "Icon",
                                            tint = if (selectedIndex == index) primaryColor else grayColor
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = navItem.label,
                                        color = if (selectedIndex == index) primaryColor else grayColor
                                    )
                                },
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                ContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex, products, categories, userDetails, onClose, searchText, { text ->
                    selectedIndex = 1
                    searchText = text
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainLayoutPreview() {
    MainLayout({}, {})
}


@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int, products: MutableList<Product>, categores: MutableList<Category>, userDetails: User?, onClose: () -> Unit, searchText: String, search: (text: String) -> Unit) {

    when(selectedIndex){
        0-> HomePage(modifier, products, categores, search)
        1-> SearchPage(modifier, products, searchText)
        2-> CartPage(modifier, userDetails!!.id!!)
        3-> ProfilePage(modifier, userDetails, onClose)
    }
}
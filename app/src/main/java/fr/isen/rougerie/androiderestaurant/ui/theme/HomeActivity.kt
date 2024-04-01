package fr.isen.rougerie.androiderestaurant.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import fr.isen.rougerie.androiderestaurant.R


class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val totalItemsInCart = getTotalCartItems(this)
        setContent {
            val cartItemCount = remember { mutableStateOf(totalItemsInCart) }
            AndroidERestaurantTheme {
                Scaffold(
                    topBar = {  MyTopAppBar(cartItemCount) } //TopBar personnalisée
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues), //padding
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val context = LocalContext.current
                        MenuScreen(context = context)
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity", "L'activité Home est détruite.")
    }
}

@Composable
fun MenuScreen(context: Context) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "Bienvenue chez\n\nTom Tom Restaurant",
            textAlign = TextAlign.Center,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFAF0101),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.tomtomrestau2),
            contentDescription = "Image Restaurant",
            modifier = Modifier.size(400.dp)
        )

        // catégories
        val categories = listOf("Entrées", "Plats", "Desserts")
        categories.forEach { category ->
            CategoryItem(name = category, onClick = { categoryName ->
                val intent = Intent(context, CategoryActivity::class.java).apply {
                    putExtra("categoryName", categoryName)
                }
                context.startActivity(intent)
            })
            Divider(
                color = Color(0xFFAF0101),
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
    }
}

@Composable
fun CategoryItem(name: String, onClick: (String) -> Unit) {
    Text(
        text = name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(name) }
            .padding(4.dp),
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )
}


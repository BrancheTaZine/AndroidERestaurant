package fr.isen.rougerie.androiderestaurant.ui.theme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import coil.compose.rememberImagePainter
import fr.isen.rougerie.androiderestaurant.R



class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra("categoryName") ?: "Catégorie"
        val totalItemsInCart = getTotalCartItems(this)
        setContent {
            val isLoading = remember { mutableStateOf(true) } // chargement
            val menuItems = remember { mutableStateOf<List<MenuItem>>(listOf()) }
            val context = LocalContext.current
            val cartItemCount = remember { mutableStateOf(totalItemsInCart) }
            AndroidERestaurantTheme {
                Scaffold(
                    topBar = { MyTopAppBar(cartItemCount) } // topbarre
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier.padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        } else {
                            MenuScreen(categoryName = categoryName, items = menuItems.value, onClick = { menuItem ->
                                val intent = Intent(context, DishDetailActivity::class.java).apply {
                                    val gson = Gson()
                                    val menuItemJson = gson.toJson(menuItem)
                                    putExtra("menuItem", menuItemJson)
                                }
                                context.startActivity(intent)
                            })
                        }
                    }
                }
            }

            fetchMenuItems(categoryName) { items ->
                menuItems.value = items
                isLoading.value = false
            }
        }
    }


    private fun fetchMenuItems(categoryName: String, onResult: (List<MenuItem>) -> Unit) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val params = JSONObject()
        params.put("id_shop", "1")

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                Log.d("CategoryActivity", "Réponse de l'API: $response")
                try {
                    val gson = Gson()
                    val menuResponse = gson.fromJson(response.toString(), MenuResponse::class.java)
                    val filteredItems =
                        menuResponse.data.firstOrNull { it.name_fr == categoryName }?.items
                            ?: emptyList()
                    onResult(filteredItems)
                } catch (e: Exception) {
                    Log.e("CategoryActivity", "Parsing error", e)
                    onResult(emptyList()) // En cas d'erreur : liste vide
                }
            },
            { error ->
                error.printStackTrace()
                Log.e("CategoryActivity", "Volley error: ${error.message}")
                runOnUiThread {
                    Toast.makeText(this, "Failed to load data: ${error.message}", Toast.LENGTH_LONG).show()
                }
                onResult(emptyList()) // erreur de réseau: liste vide
            })

        queue.add(jsonObjectRequest)
    }


}

@Composable
fun MenuScreen(categoryName: String, items: List<MenuItem>, onClick: (MenuItem) -> Unit) {
    Column {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Crée 2 colonnes
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            items(items) { item ->
                MenuItemComposable(item = item, onClick = onClick)
            }
        }
    }
}

@Composable
fun MenuItemComposable(item: MenuItem, onClick: (MenuItem) -> Unit) {
    Column(modifier = Modifier
        .padding(8.dp)
        .clickable { onClick(item) }) {
        if (item.images.isNotEmpty()) {
            ImageFromUrls(urls = item.images)
        }
        Text(
            text = item.name_fr,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = item.getFirstPriceFormatted(),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
fun ImageFromUrls(urls: List<String>, imageDefault: Int = R.drawable.sablier) {
    var currentUrlIndex by remember { mutableStateOf(0) }

    val painter = rememberImagePainter(
        data = urls.getOrNull(currentUrlIndex) ?: "",
        builder = {
            crossfade(true)
            error(imageDefault) //image par défaut
            listener(onError = { _, _ ->
                // Logique en cas d'erreur pour une URL spécifique
                if (currentUrlIndex < urls.size - 1) {
                    currentUrlIndex++
                }
            })
        }
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}



// réponse et données
data class MenuResponse(
    val data: List<Category>
)

data class Category(
    val name_fr: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val id: String,
    val name_fr: String,
    val id_category: String,
    val categ_name_fr: String,
    val images: List<String>,
    val ingredients: List<Ingredient>,
    val prices: List<Price>
) {
    //le premier prix disponible
    // Supposons que le prix soit stocké sous forme de chaîne de caractères et qu'il représente un prix en euros
    fun getFirstPriceFormatted(): String {
        return if (prices.isNotEmpty()) {
            "${prices.first().price}€"
        } else {
            "N/A"
        }
    }
}

data class Ingredient(
    val id: String, // Identifiant du produit
    val id_shop: String, // Identifiant du magasin/shop
    val name_fr: String, // Nom français de l'ingrédient
    val create_date: String, // Date de création de l'ingrédient
    val update_date: String, // Date de mise à jour de l'ingrédient
    val id_pizza: String? // Identifiant de la pizza (si applicable, peut ne pas être présent pour tous les ingrédients, donc nullable)
)

data class Price(
    val id: String, // Identifiant du prix
    val id_pizza: String, // Identifiant de la pizza
    val id_size: String, // Identifiant de la taille
    val price: String, // Valeur du prix
    val create_date: String, // Date de création du prix
    val update_date: String, // Date de mise à jour du prix
    val size: String // Taille correspondante au prix
)


package fr.isen.rougerie.androiderestaurant.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import java.io.FileNotFoundException
import androidx.compose.material3.*
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext


class DishDetailActivity : ComponentActivity() {
    private lateinit var cartItemCount: MutableState<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menuItemJson = intent.getStringExtra("menuItem")
        val menuItem = Gson().fromJson(menuItemJson, MenuItem::class.java)

        setContent {
            cartItemCount = remember { mutableStateOf(getTotalCartItems(this@DishDetailActivity)) }
            AndroidERestaurantTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    DishDetailScreen(menuItem = menuItem, cartItemCount = cartItemCount)
                }
            }
        }
    }

    fun updateCartItemCount() {
        // Utilisez runOnUiThread si cette méthode est appelée à partir d'un thread non UI
        runOnUiThread {
            cartItemCount.value = getTotalCartItems(this)
        }
    }
}

@Composable
fun DishDetailScreen(menuItem: MenuItem, cartItemCount: MutableState<Int>) {
    Scaffold(
        topBar = { MyTopAppBar(cartItemCount) },
        content = { innerPadding ->
            DishContent(menuItem = menuItem, paddingValues = innerPadding)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(cartItemCount: MutableState<Int>) {
    val context = LocalContext.current

    TopAppBar(
        title = { Text("Tom Tom Restaurant", color = Color.White) },
        actions = {
            Spacer(modifier = Modifier.weight(1f, true))
            IconButton(
                onClick = {
                    val intent = Intent(context, CartActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.sizeIn(minWidth = 72.dp, minHeight = 48.dp)
            ) {
                BadgedBox(
                    badge = { Badge { Text(text = cartItemCount.value.toString()) } }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Shopping Cart"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFAF0101))
    )
    Log.d("CartActivity", "Setting title")
}



@Composable
fun DishContent(menuItem: MenuItem, paddingValues: PaddingValues) {
    var quantity by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(menuItem.images.isNotEmpty()) }
    val pricePerUnit = menuItem.prices.firstOrNull()?.price?.toDouble() ?: 0.0
    val totalPrice = quantity * pricePerUnit
    val view = LocalView.current

    LaunchedEffect(key1 = menuItem.images) {
        if (menuItem.images.isNotEmpty()) {
            delay(3000)
            isLoading = false
            Log.d("DishDetail", "Images")
        }
    }

    Column(modifier = Modifier.padding(paddingValues)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(25.dp))
        } else {
            ImageCarousel(imageUrls = menuItem.images)

            Text(
                text = menuItem.name_fr,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Prix unitaire: ${menuItem.getFirstPriceFormatted()}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )

            IngredientGrid(ingredients = menuItem.ingredients)
            QuantitySelector(
                menuItem = menuItem,
                quantity = quantity,
                totalPrice = totalPrice,
                onQuantityChanged = { newQuantity -> quantity = newQuantity },
                view = view
            )
        }
    }
}


@Composable
fun IngredientGrid(ingredients: List<Ingredient>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Utilisez 2 colonnes, par exemple.
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(ingredients) { ingredient ->
            Text(
                text = ingredient.name_fr,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}


@Composable
fun QuantitySelector(
    menuItem: MenuItem, // Ajout de menuItem comme paramètre
    quantity: Int,
    totalPrice: Double,
    onQuantityChanged: (Int) -> Unit,
    view: View // Passer view comme paramètre au lieu de l'obtenir localement
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(32.dp))
            Text(text = quantity.toString())
            Spacer(modifier = Modifier.width(32.dp))
            Button(onClick = { onQuantityChanged(quantity + 1) }) {
                Text("+")
            }
        }
        Button(
            onClick = { addToCart(menuItem, quantity, totalPrice, view) },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = String.format("Total: %.2f€", totalPrice))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(imageUrls: List<String>, defaultImage: Int = R.drawable.sablier, onImageLoaded: () -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    HorizontalPager(state = pagerState) { page ->
        val painter = rememberImagePainter(
            data = imageUrls[page],
            builder = {
                crossfade(true)
                placeholder(defaultImage)
                error(defaultImage)
                listener(onError = { _, _ ->
                    Log.e("ImageCarousel", "Erreur lors du chargement de l'image à l'index $page")
                }, onSuccess = { _, _ ->
                    Log.d("ImageCarousel", "Image chargée avec succès à l'index $page")
                    onImageLoaded()
                })
            }
        )

        Image(
            painter = painter,
            contentDescription = "Image Carousel",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Assurez-vous que toutes les images ont un aspect ratio carré
        )
    }
}



fun addToCart(menuItem: MenuItem, quantity: Int, totalPrice: Double, view: View) {
    val filename = "cart.json"
    val gson = Gson()
    val context = view.context
    var cart: MutableList<CartItem> = mutableListOf()

    Log.d("addToCart", "Début de la fonction addToCart")

    // Essayer de lire le fichier du panier existant
    try {
        context.openFileInput(filename).use { inputStream ->
            val existingCartJson = inputStream.bufferedReader().readText()
            Log.d("addToCart", "Fichier existant lu avec succès")
            try {
                val cartType = object : TypeToken<List<CartItem>>() {}.type
                cart = gson.fromJson(existingCartJson, cartType)
                Log.d("addToCart", "Désérialisation réussie")
            } catch (jsonException: JsonSyntaxException) {
                // Si le fichier ne contient pas une liste, initialiser cart comme une liste vide
                Log.e("addToCart", "Erreur de format JSON, initialisation d'un nouveau panier", jsonException)
                cart = mutableListOf()
            }
        }
    } catch (e: FileNotFoundException) {
        Log.e("addToCart", "Fichier non trouvé, création d'un nouveau fichier", e)
    }

    // Ajouter le nouvel article au panier
    val newCartItem = CartItem(menuItem, quantity, totalPrice)
    cart.add(newCartItem)
    Log.d("addToCart", "Article ajouté au panier")

    // Sauvegarder le panier mis à jour
    try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
            val updatedCartJson = gson.toJson(cart)
            outputStream.write(updatedCartJson.toByteArray())
            Log.d("addToCart", "Panier sauvegardé avec succès")

            // Mise à jour de la pastille de notification
            if (context is DishDetailActivity) {
                (context).updateCartItemCount()
            }
        }
    } catch (e: Exception) {
        Log.e("addToCart", "Erreur lors de la sauvegarde du panier", e)
    }

    // Afficher une Snackbar pour informer l'utilisateur
    Snackbar.make(view, "Ajouté au panier", Snackbar.LENGTH_LONG).show()
    Log.d("addToCart", "Snackbar affichée")

}

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int,
    val totalPrice: Double
)
fun getTotalCartItems(context: Context): Int {
    val filename = "cart.json"
    val gson = Gson()
    var totalItems = 0

    try {
        context.openFileInput(filename).use { inputStream ->
            val existingCartJson = inputStream.bufferedReader().readText()
            val cartType = object : TypeToken<List<CartItem>>() {}.type
            val cartItems: List<CartItem> = gson.fromJson(existingCartJson, cartType)
            totalItems = cartItems.sumOf { it.quantity }
        }
    } catch (e: FileNotFoundException) {
        Log.e("getTotalCartItems", "Fichier non trouvé, supposant que le panier est vide", e)
    } catch (e: Exception) {
        Log.e("getTotalCartItems", "Erreur lors de la lecture du fichier du panier", e)
    }

    return totalItems
}

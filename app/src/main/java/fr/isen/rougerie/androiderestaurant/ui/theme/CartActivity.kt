package fr.isen.rougerie.androiderestaurant.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import java.io.FileNotFoundException

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CartScreen()
                }
            }
        }
    }

    // Fonction pour lire le contenu du panier à partir du fichier
    fun readCartItems(): List<CartItem> {
        val gson = Gson()
        val filename = "cart.json"
        var cartItems: List<CartItem> = listOf()

        try {
            openFileInput(filename).use { inputStream ->
                val cartJson = inputStream.bufferedReader().readText()
                val cartType = object : com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken<List<CartItem>>() {}.type
                cartItems = gson.fromJson(cartJson, cartType)
            }
        } catch (e: FileNotFoundException) {
            Log.e("fr.isen.rougerie.androiderestaurant.ui.theme.CartActivity", "Fichier non trouvé: $filename", e)
        } catch (e: Exception) {
            Log.e("fr.isen.rougerie.androiderestaurant.ui.theme.CartActivity", "Erreur lors de la lecture du fichier du panier", e)
        }
        return cartItems
    }

    // Fonction pour mettre à jour le fichier cart.json après suppression d'un item
    fun updateCartFile(cartItems: List<CartItem>) {
        val gson = Gson()
        val filename = "cart.json"
        val cartJson = gson.toJson(cartItems)

        openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
            outputStream.write(cartJson.toByteArray())
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {
    val context = LocalContext.current
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }

    // Charger les articles du panier au démarrage
    LaunchedEffect(key1 = true) {
        cartItems = (context as CartActivity).readCartItems()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mon Panier") })
        },
        content = {
            LazyColumn {
                items(cartItems, itemContent = { cartItem ->
                    CartItemRow(cartItem = cartItem, onRemove = {
                        cartItems = cartItems.toMutableList().also { it.remove(cartItem) }
                        (context as CartActivity).updateCartFile(cartItems)
                    })
                })
            }
            Button(
                onClick = { /* Logique pour passer la commande */ },
                // modifier = Modifier.padding(16.dp)
            ) {
                Text("Passer la commande")
            }
        }
    )
}

@Composable
fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "${cartItem.menuItem.name_fr} x ${cartItem.quantity}")
        //  Spacer(Modifier.weight(1f))
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
        }
    }
}

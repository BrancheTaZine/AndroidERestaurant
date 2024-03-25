package fr.isen.rougerie.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background // Import nécessaire pour utiliser la fonction background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import fr.isen.rougerie.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.rougerie.androiderestaurant.PlatsActivity // Assurez-vous d'importer la classe PlatsActivity
import fr.isen.rougerie.androiderestaurant.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bienvenue chez TomTom Restaurant",
                            style = TextStyle(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )

                        TabButtons(this@MainActivity)
                    }
                }
            }
        }
    }
}

@Composable
fun TabButtons(activity: ComponentActivity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp)) // Ajoute une bande orange en haut
        TabButton(name = "Entrées", onClick = {})
        Spacer(modifier = Modifier.height(16.dp)) // Ajoute un espace entre les boutons
        TabButton(name = "Plats") {
            // Passer à PlatsActivity lors du clic sur le bouton "Plats"
            activity.startActivity(Intent(activity, PlatsActivity::class.java))
        }
        Spacer(modifier = Modifier.height(16.dp)) // Ajoute un espace entre les boutons
        TabButton(name = "Desserts", onClick = {})
        Spacer(modifier = Modifier.height(32.dp)) // Ajoute une bande orange en bas
        Image(
            painter = painterResource(id = R.drawable.tomtomrestau2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Hauteur de l'image
        )
    }
}

@Composable
fun TabButton(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Hauteur des boutons réduite
            .padding(horizontal = 24.dp) // Marge horizontale plus grande
            .background(color = Color(0xFFDC1F01), shape = RoundedCornerShape(16.dp)) // Fond orange avec forme ovale
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.align(Alignment.Center),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White
            )
        ) {
            Text(text = name, fontSize = 25.sp, fontWeight = FontWeight.Bold) // Taille et graisse du texte modifiées
        }
    }
}
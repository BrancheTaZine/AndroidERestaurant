package fr.isen.rougerie.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.rougerie.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight

class PlatsActivity : ComponentActivity() {
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
                        PlatButton(name = "Plat 1")
                        Spacer(modifier = Modifier.height(16.dp))
                        PlatButton(name = "Plat 2")
                        Spacer(modifier = Modifier.height(16.dp))
                        PlatButton(name = "Plat 3")
                    }
                }
            }
        }
    }
}

@Composable
fun PlatButton(name: String) {
    Button(
        onClick = { /* Action à effectuer lors du clic */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Hauteur des boutons réduite
            .padding(horizontal = 24.dp) // Marge horizontale plus grande
            .background(color = Color(0xFFDC1F01), shape = RoundedCornerShape(16.dp)) // Fond orange avec forme ovale
    ) {
        Text(
            text = name,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White // Couleur du texte en blanc
        )
    }
}

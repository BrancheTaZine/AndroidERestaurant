package fr.isen.rougerie.androiderestaurant.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ButtonLayout()
                }
            }
        }
    }
}

@Composable
fun ButtonLayout() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { /* Entrées button clicked */ },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Text(text = "Entrées")
        }

        Button(
            onClick = { /* Plats button clicked */ },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Text(text = "Plats")
        }

        Button(
            onClick = { /* Desserts button clicked */ },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Text(text = "Desserts")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonLayoutPreview() {
    AndroidERestaurantTheme {
        ButtonLayout()
    }
}

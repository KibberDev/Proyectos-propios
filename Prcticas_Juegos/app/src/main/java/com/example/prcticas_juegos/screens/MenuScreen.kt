package com.example.prcticas_juegos.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prcticas_juegos.R
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip


@Composable
fun MenuScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E8ADB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 80.dp)
                    .size(300.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(40.dp), clip = true)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de la APP",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(40.dp))
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 👉 Aquí usamos directamente los botones
            Button(
                onClick = { navController.navigate("tres_en_raya") },
                modifier = Modifier
                    .width(250.dp)
                    .padding(8.dp)
                    .height(50.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("3 en Raya")
            }

            Button(
                onClick = { /* Acción para Juego 2 */ },
                modifier = Modifier
                    .width(250.dp)
                    .padding(8.dp)
                    .height(50.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Juego 2")
            }

            Button(
                onClick = { /* Acción para Juego 3 */ },
                modifier = Modifier
                    .width(250.dp)
                    .padding(8.dp)
                    .height(50.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Juego 3")
            }
        }
    }
}

package com.example.prcticas_juegos.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.nio.file.WatchEvent

@Composable
fun TresEnRayaScreen(navController: NavController) {
    val tablero = remember { mutableStateListOf("", "", "", "", "", "", "", "", "") }
    val turnoJugador = remember { mutableStateOf(true) }
    val ganador = remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(10.dp, Color.Black))
    ) {

        Text(
            text = when (ganador.value) {
                "X" -> "¡Has ganado!"
                "O" -> "¡La máquina gana!"
                "Empate" -> "¡Empate!"
                else -> if (turnoJugador.value) "Tu turno" else "Turno de la máquina"
            },
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        for (fila in 0..2) {
            Row {
                for (col in 0..2) {
                    val index = fila * 3 + col
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RectangleShape
                            )
                            .clickable {
                                if (tablero[index].isEmpty() && ganador.value.isEmpty()) {
                                    tablero[index] = "X"
                                    comprobarGanador(tablero, ganador)
                                    if (ganador.value.isEmpty()) {
                                        turnoJugador.value = false
                                        turnoMaquina(tablero, ganador)
                                        turnoJugador.value = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = tablero[index], fontSize = 32.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable{
                        for (i in tablero.indices) {
                            tablero[i] = ""
                        }
                        ganador.value = ""
                        turnoJugador.value = true
                    }
                    .border(2.dp, Color.Black)
                    .padding(12.dp)
            ) {
                Text(text = "Reiniciar", fontSize = 18.sp)
            }

            Box(
                modifier = Modifier
                    .clickable{
                        navController.navigate("Menu") {
                            popUpTo("Menu") { inclusive = true}
                        }
                    }
                    .border(2.dp, Color.Black)
                    .padding(12.dp)
            ) {
                Text(text = "Volver al menú", fontSize = 18.sp)
            }
        }
    }
}

fun comprobarGanador(tablero: List<String>, ganador: MutableState<String>) {
    val combinacionesGanadoras = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (combinacion in combinacionesGanadoras) {
        val (a, b, c) = combinacion
        if (tablero[a].isNotEmpty() && tablero[a] == tablero[b] && tablero[a] == tablero[c]) {
            ganador.value = tablero[a]
            return
        }
    }
    if (tablero.all { it.isNotEmpty() }) {
        ganador.value = "Empate"
    }
}

fun turnoMaquina(tablero: MutableList<String>, ganador: MutableState<String>) {
    val casillasVacias = tablero.mapIndexed { index, value -> if (value.isEmpty()) index else null }.filterNotNull()
    if (casillasVacias.isNotEmpty()) {
        val jugada = casillasVacias.random()
        tablero[jugada] = "O"
        comprobarGanador(tablero, ganador)
    }
}


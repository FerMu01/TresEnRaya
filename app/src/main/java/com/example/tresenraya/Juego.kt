package com.example.tresenraya

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Juego : AppCompatActivity() {
    private lateinit var board: Array<Array<Button>>
    private var currentPlayer = "X"
    private val boardStatus = Array(3) { Array(3) { "" } } // Estado del tablero

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Obtener el jugador inicial desde el intent
        currentPlayer = intent.getStringExtra("jugadorInicial") ?: "X"

        // Inicializar la matriz de botones
        board = arrayOf(
            arrayOf(findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3)),
            arrayOf(findViewById(R.id.button4), findViewById(R.id.button5), findViewById(R.id.button6)),
            arrayOf(findViewById(R.id.button7), findViewById(R.id.button8), findViewById(R.id.button9))
        )

        // Configurar listeners
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j].setOnClickListener { onCellClicked(i, j) }
            }
        }
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (boardStatus[row][col].isNotEmpty()) return // No permite sobreescribir

        board[row][col].text = currentPlayer
        boardStatus[row][col] = currentPlayer

        if (checkWinner()) {
            Toast.makeText(this, "¡$currentPlayer ha ganado!", Toast.LENGTH_LONG).show()
            resetBoard()
            return
        }

        currentPlayer = if (currentPlayer == "X") "O" else "X" // Cambiar de jugador
    }

    private fun checkWinner(): Boolean {
        // Verificar filas y columnas
        for (i in 0..2) {
            if (boardStatus[i][0] == currentPlayer && boardStatus[i][1] == currentPlayer && boardStatus[i][2] == currentPlayer) return true
            if (boardStatus[0][i] == currentPlayer && boardStatus[1][i] == currentPlayer && boardStatus[2][i] == currentPlayer) return true
        }
        // Verificar diagonales
        if (boardStatus[0][0] == currentPlayer && boardStatus[1][1] == currentPlayer && boardStatus[2][2] == currentPlayer) return true
        if (boardStatus[0][2] == currentPlayer && boardStatus[1][1] == currentPlayer && boardStatus[2][0] == currentPlayer) return true

        return false
    }

    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j].text = ""
                boardStatus[i][j] = ""
            }
        }
        currentPlayer = intent.getStringExtra("jugadorInicial") ?: "X" // Restaurar al jugador inicial
    }
}

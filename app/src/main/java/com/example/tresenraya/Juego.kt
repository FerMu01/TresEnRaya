package com.example.tresenraya

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class Juego : AppCompatActivity() {
    private lateinit var board: Array<Array<Button>>
    private lateinit var txtTurno: TextView
    private lateinit var btnReinicio: Button

    private var currentPlayer = "X"
    private val boardStatus = Array(3) { Array(3) { "" } } // Estado del tablero
    private var gameMode = "Facil" // Solo implementamos Fácil en este ejemplo
    private var inicio = "Al Azar" // Quién inicia, obtenido del intent
    private lateinit var userIcon: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Asignar el TextView para mostrar el turno y el botón de reinicio
        txtTurno = findViewById(R.id.txtTurno)
        btnReinicio = findViewById(R.id.btnReinicio)
        btnReinicio.isEnabled = false  // Inicialmente deshabilitado

        // Obtener configuración del intent
        userIcon = intent.getStringExtra("jugadorInicial") ?: "X"
        gameMode = intent.getStringExtra("dificultad") ?: "Facil"
        inicio = intent.getStringExtra("inicio") ?: "Al Azar"

        // Determinar quién inicia según la opción seleccionada
        currentPlayer = when (inicio) {
            "CPU" -> if (userIcon == "X") "O" else "X"
            "Al Azar" -> if (Random.nextBoolean()) userIcon else if (userIcon == "X") "O" else "X"
            else -> userIcon  // "Yo": el usuario empieza
        }

        // Inicializar la matriz de botones (las celdas del tablero)
        board = arrayOf(
            arrayOf(findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3)),
            arrayOf(findViewById(R.id.button4), findViewById(R.id.button5), findViewById(R.id.button6)),
            arrayOf(findViewById(R.id.button7), findViewById(R.id.button8), findViewById(R.id.button9))
        )

        // Configurar listeners para cada celda
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j].setOnClickListener { onCellClicked(i, j) }
            }
        }

        // Mostrar el turno inicial en txtTurno
        if (currentPlayer == userIcon) {
            txtTurno.text = "Es Tu Turno"
        } else {
            txtTurno.text = "Turno De La CPU"
            if (gameMode == "Facil") {
                delayedCpuMove()
            }
        }

        // Configurar el botón de reinicio para volver a jugar
        btnReinicio.setOnClickListener {
            resetBoard()
            btnReinicio.isEnabled = false
            // Actualizar mensaje según quién empieza después del reinicio
            if (currentPlayer == userIcon) {
                txtTurno.text = "Es Tu Turno"
            } else {
                txtTurno.text = "Turno De La CPU"
                if (gameMode == "Facil") {
                    delayedCpuMove()
                }
            }
        }
    }

    private fun onCellClicked(row: Int, col: Int) {
        // No permite sobreescribir o que el usuario juegue si no es su turno
        if (boardStatus[row][col].isNotEmpty() || currentPlayer != userIcon) return

        // El usuario realiza su movimiento
        board[row][col].text = currentPlayer
        boardStatus[row][col] = currentPlayer

        // Verificar si hay un ganador
        if (checkWinner()) {
            txtTurno.text = "Ganaste"
            btnReinicio.isEnabled = true
            return
        }
        // Verificar empate
        if (isBoardFull()) {
            txtTurno.text = "Empate"
            btnReinicio.isEnabled = true
            return
        }

        // Cambiar turno
        currentPlayer = if (currentPlayer == "X") "O" else "X"

        // Actualizar mensaje de turno
        if (currentPlayer == userIcon) {
            txtTurno.text = "Es Tu Turno"
        } else {
            txtTurno.text = "Turno De La CPU"
            if (gameMode == "Facil") {
                delayedCpuMove()
            }
        }
    }

    private fun delayedCpuMove() {
        // Retardo de 1 segundo antes de que la CPU juegue
        Handler(Looper.getMainLooper()).postDelayed({ cpuMove() }, 1000)
    }

    private fun cpuMove() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (boardStatus[i][j].isEmpty()) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }
        if (emptyCells.isNotEmpty()) {
            val (row, col) = emptyCells.random()
            board[row][col].text = currentPlayer
            boardStatus[row][col] = currentPlayer

            if (checkWinner()) {
                txtTurno.text = "Gano La CPU"
                btnReinicio.isEnabled = true
                return
            }
            if (isBoardFull()) {
                txtTurno.text = "Empate"
                btnReinicio.isEnabled = true
                return
            }
            // Cambiar turno nuevamente al usuario
            currentPlayer = userIcon
            txtTurno.text = "Es Tu Turno"
        }
    }

    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (boardStatus[i][j].isEmpty()) return false
            }
        }
        return true
    }

    private fun checkWinner(): Boolean {
        // Verificar filas y columnas
        for (i in 0..2) {
            if (boardStatus[i][0] == currentPlayer &&
                boardStatus[i][1] == currentPlayer &&
                boardStatus[i][2] == currentPlayer) return true
            if (boardStatus[0][i] == currentPlayer &&
                boardStatus[1][i] == currentPlayer &&
                boardStatus[2][i] == currentPlayer) return true
        }
        // Verificar diagonales
        if (boardStatus[0][0] == currentPlayer &&
            boardStatus[1][1] == currentPlayer &&
            boardStatus[2][2] == currentPlayer) return true
        if (boardStatus[0][2] == currentPlayer &&
            boardStatus[1][1] == currentPlayer &&
            boardStatus[2][0] == currentPlayer) return true

        return false
    }

    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j].text = ""
                boardStatus[i][j] = ""
            }
        }
        // Reiniciar turno según la configuración original
        currentPlayer = when (inicio) {
            "CPU" -> if (userIcon == "X") "O" else "X"
            "Al Azar" -> if (Random.nextBoolean()) userIcon else if (userIcon == "X") "O" else "X"
            else -> userIcon
        }
    }
}

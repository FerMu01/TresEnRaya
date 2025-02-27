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
    private var gameMode = "Facil" // Se espera "Facil", "Medio" o "Dificil"
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
            delayedCpuMove()
        }

        // Configurar el botón de reinicio para volver a jugar
        btnReinicio.setOnClickListener {
            resetBoard()
            btnReinicio.isEnabled = false
            if (currentPlayer == userIcon) {
                txtTurno.text = "Es Tu Turno"
            } else {
                txtTurno.text = "Turno De La CPU"
                delayedCpuMove()
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
        if (checkWinnerFor(userIcon)) {
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

        // Cambiar turno a la CPU
        currentPlayer = getCpuMarker()
        txtTurno.text = "Turno De La CPU"
        delayedCpuMove()
    }

    private fun delayedCpuMove() {
        Handler(Looper.getMainLooper()).postDelayed({ cpuMove() }, 1000)
    }

    private fun cpuMove() {
        if (gameMode == "Facil") {
            randomCpuMove()
        } else if (gameMode == "Medio") {
            if (!medioCpuMove()) {
                randomCpuMove()
            }
        } else {
            // Para "Dificil" se implementaría minimax; aquí se puede usar la estrategia medio como fallback
            if (!medioCpuMove()) {
                randomCpuMove()
            }
        }
    }

    // Modo Fácil: movimiento aleatorio
    private fun randomCpuMove() {
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
            makeCpuMove(row, col)
        }
    }

    // Modo Medio: intenta ganar o bloquear; si no se cumple, usa heurísticas (centro y esquinas)
    private fun medioCpuMove(): Boolean {
        val cpuMarker = getCpuMarker()
        // 1. Si la CPU puede ganar, hazlo.
        val winMove = findWinningMove(cpuMarker)
        if (winMove != null) {
            makeCpuMove(winMove.first, winMove.second)
            return true
        }
        // 2. Si el usuario puede ganar en el siguiente movimiento, bloquea.
        val blockMove = findWinningMove(userIcon)
        if (blockMove != null) {
            makeCpuMove(blockMove.first, blockMove.second)
            return true
        }
        // 3. Solo a veces tomar el centro si está libre (por ejemplo, 50% de probabilidad)
        if (boardStatus[1][1].isEmpty() && Random.nextFloat() < 0.5f) {
            makeCpuMove(1, 1)
            return true
        }
        // 4. Tomar alguna esquina libre.
        val corners = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        val availableCorners = corners.filter { boardStatus[it.first][it.second].isEmpty() }
        if (availableCorners.isNotEmpty()) {
            val corner = availableCorners.random()
            makeCpuMove(corner.first, corner.second)
            return true
        }
        return false
    }

    // Realiza el movimiento de la CPU en la celda (row, col)
    private fun makeCpuMove(row: Int, col: Int) {
        board[row][col].text = currentPlayer
        boardStatus[row][col] = currentPlayer
        if (checkWinnerFor(getCpuMarker())) {
            txtTurno.text = "Gano La CPU"
            btnReinicio.isEnabled = true
            return
        }
        if (isBoardFull()) {
            txtTurno.text = "Empate"
            btnReinicio.isEnabled = true
            return
        }
        currentPlayer = userIcon
        txtTurno.text = "Es Tu Turno"
    }

    // Busca un movimiento ganador para el marcador dado y lo retorna; de lo contrario, null.
    private fun findWinningMove(marker: String): Pair<Int, Int>? {
        for (i in 0..2) {
            for (j in 0..2) {
                if (boardStatus[i][j].isEmpty()) {
                    boardStatus[i][j] = marker
                    if (checkWinFor(marker)) {
                        boardStatus[i][j] = ""
                        return Pair(i, j)
                    }
                    boardStatus[i][j] = ""
                }
            }
        }
        return null
    }

    // Función auxiliar que verifica si un marcador gana en el tablero actual.
    private fun checkWinFor(marker: String): Boolean {
        for (i in 0..2) {
            if (boardStatus[i][0] == marker && boardStatus[i][1] == marker && boardStatus[i][2] == marker) return true
            if (boardStatus[0][i] == marker && boardStatus[1][i] == marker && boardStatus[2][i] == marker) return true
        }
        if (boardStatus[0][0] == marker && boardStatus[1][1] == marker && boardStatus[2][2] == marker) return true
        if (boardStatus[0][2] == marker && boardStatus[1][1] == marker && boardStatus[2][0] == marker) return true
        return false
    }

    // Verifica si el tablero está lleno.
    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (boardStatus[i][j].isEmpty()) return false
            }
        }
        return true
    }

    // Utiliza checkWinFor para el marcador indicado.
    private fun checkWinnerFor(marker: String): Boolean {
        return checkWinFor(marker)
    }

    // Devuelve el marcador que usa la CPU (opuesto al del usuario).
    private fun getCpuMarker(): String {
        return if (userIcon == "X") "O" else "X"
    }

    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j].text = ""
                boardStatus[i][j] = ""
            }
        }
        // Reiniciar turno según la configuración original.
        currentPlayer = when (inicio) {
            "CPU" -> getCpuMarker()
            "Al Azar" -> if (Random.nextBoolean()) userIcon else getCpuMarker()
            else -> userIcon
        }
    }
}

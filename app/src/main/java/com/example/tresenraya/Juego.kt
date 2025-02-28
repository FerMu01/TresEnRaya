package com.example.tresenraya

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Juego : AppCompatActivity() {
    private lateinit var board: Array<Array<Button>>
    private lateinit var txtTurno: TextView
    private lateinit var btnReinicio: Button
    private lateinit var txtDificultad: TextView

    private var currentPlayer = "X"
    private val boardStatus = Array(3) { Array(3) { "" } } // Estado del tablero
    private var gameMode = "Facil" // Se espera "Facil", "Medio" o "Dificil"
    private var inicio = "Al Azar" // Quién inicia, obtenido del intent
    private lateinit var userIcon: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)
        window.statusBarColor = android.graphics.Color.parseColor("#0D0D0D")
        // Asignar los TextView y botón de reinicio
        txtTurno = findViewById(R.id.txtTurno)
        btnReinicio = findViewById(R.id.btnReinicio)
        txtDificultad = findViewById(R.id.txtDificultad)
        btnReinicio.isEnabled = false

        // Obtener configuración del intent
        userIcon = intent.getStringExtra("jugadorInicial") ?: "X"
        gameMode = intent.getStringExtra("dificultad") ?: "Facil"
        inicio = intent.getStringExtra("inicio") ?: "Al Azar"

        // Mostrar la dificultad seleccionada
        txtDificultad.text = "Dificultad: $gameMode"

        // Determinar quién inicia
        currentPlayer = when (inicio) {
            "CPU" -> if (userIcon == "X") "O" else "X"
            "Al Azar" -> if (Random.nextBoolean()) userIcon else if (userIcon == "X") "O" else "X"
            else -> userIcon
        }

        // Inicializar la matriz de botones
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

        // Mostrar turno inicial
        if (currentPlayer == userIcon) {
            txtTurno.text = "Es Tu Turno"
        } else {
            txtTurno.text = "Turno De La CPU"
            delayedCpuMove()
        }

        // Configurar botón de reinicio
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
        if (boardStatus[row][col].isNotEmpty() || currentPlayer != userIcon) return

        // Asignar la imagen correspondiente y eliminar cualquier tint
        if (currentPlayer == "X") {
            board[row][col].setBackgroundResource(R.drawable.equis)
        } else {
            board[row][col].setBackgroundResource(R.drawable.redondo)
        }
        board[row][col].backgroundTintList = null

        boardStatus[row][col] = currentPlayer

        if (checkWinnerFor(userIcon)) {
            txtTurno.text = "Ganaste"
            btnReinicio.isEnabled = true
            return
        }
        if (isBoardFull()) {
            txtTurno.text = "Empate"
            btnReinicio.isEnabled = true
            return
        }

        currentPlayer = getCpuMarker()
        txtTurno.text = "Turno De La CPU"
        delayedCpuMove()
    }

    private fun delayedCpuMove() {
        Handler(Looper.getMainLooper()).postDelayed({ cpuMove() }, 1000)
    }

    private fun cpuMove() {
        when (gameMode) {
            "Facil" -> randomCpuMove()
            "Medio" -> {
                if (!medioCpuMove()) {
                    randomCpuMove()
                }
            }
            "Dificil" -> {
                val move = bestMove()
                if (move != null) {
                    makeCpuMove(move.first, move.second)
                }
            }
            else -> randomCpuMove()
        }
    }

    // Modo Fácil: movimiento aleatorio
    private fun randomCpuMove() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2)
            for (j in 0..2)
                if (boardStatus[i][j].isEmpty()) emptyCells.add(Pair(i, j))
        if (emptyCells.isNotEmpty()) {
            val (row, col) = emptyCells.random()
            makeCpuMove(row, col)
        }
    }

    // Modo Medio: intenta ganar o bloquear; si no, heurística (centro con probabilidad, esquinas)
    private fun medioCpuMove(): Boolean {
        val cpuMarker = getCpuMarker()
        val winMove = findWinningMove(cpuMarker)
        if (winMove != null) {
            makeCpuMove(winMove.first, winMove.second)
            return true
        }
        val blockMove = findWinningMove(userIcon)
        if (blockMove != null) {
            makeCpuMove(blockMove.first, blockMove.second)
            return true
        }
        if (boardStatus[1][1].isEmpty() && Random.nextFloat() < 0.5f) {
            makeCpuMove(1, 1)
            return true
        }
        val corners = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        val availableCorners = corners.filter { boardStatus[it.first][it.second].isEmpty() }
        if (availableCorners.isNotEmpty()) {
            val chosenCorner = availableCorners.random()
            makeCpuMove(chosenCorner.first, chosenCorner.second)
            return true
        }
        return false
    }

    // Dificultad Dificil: Algoritmo Minimax
    private fun bestMove(): Pair<Int, Int>? {
        var bestScore = -1000
        var move: Pair<Int, Int>? = null
        for (i in 0..2) {
            for (j in 0..2) {
                if (boardStatus[i][j].isEmpty()) {
                    boardStatus[i][j] = getCpuMarker()
                    val score = minimax(boardStatus, 0, false)
                    boardStatus[i][j] = ""
                    if (score > bestScore) {
                        bestScore = score
                        move = Pair(i, j)
                    }
                }
            }
        }
        return move
    }

    private fun minimax(board: Array<Array<String>>, depth: Int, isMaximizing: Boolean): Int {
        if (checkWinFor(getCpuMarker(), board)) return 10 - depth
        if (checkWinFor(userIcon, board)) return depth - 10
        if (isBoardFull(board)) return 0

        return if (isMaximizing) {
            var bestScore = -1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j].isEmpty()) {
                        board[i][j] = getCpuMarker()
                        bestScore = max(bestScore, minimax(board, depth + 1, false))
                        board[i][j] = ""
                    }
                }
            }
            bestScore
        } else {
            var bestScore = 1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j].isEmpty()) {
                        board[i][j] = userIcon
                        bestScore = min(bestScore, minimax(board, depth + 1, true))
                        board[i][j] = ""
                    }
                }
            }
            bestScore
        }
    }

    private fun max(a: Int, b: Int): Int = if (a > b) a else b
    private fun min(a: Int, b: Int): Int = if (a < b) a else b

    private fun makeCpuMove(row: Int, col: Int) {
        // Asignar la imagen correspondiente y eliminar el tint para que se respeten los colores originales
        if (currentPlayer == "X") {
            board[row][col].setBackgroundResource(R.drawable.equis)
        } else {
            board[row][col].setBackgroundResource(R.drawable.redondo)
        }
        board[row][col].backgroundTintList = null

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

    private fun findWinningMove(marker: String): Pair<Int, Int>? {
        for (i in 0..2) {
            for (j in 0..2) {
                if (boardStatus[i][j].isEmpty()) {
                    boardStatus[i][j] = marker
                    if (checkWinFor(marker, boardStatus)) {
                        boardStatus[i][j] = ""
                        return Pair(i, j)
                    }
                    boardStatus[i][j] = ""
                }
            }
        }
        return null
    }

    private fun checkWinFor(marker: String, board: Array<Array<String>>): Boolean {
        for (i in 0..2) {
            if (board[i][0] == marker && board[i][1] == marker && board[i][2] == marker) return true
            if (board[0][i] == marker && board[1][i] == marker && board[2][i] == marker) return true
        }
        if (board[0][0] == marker && board[1][1] == marker && board[2][2] == marker) return true
        if (board[0][2] == marker && board[1][1] == marker && board[2][0] == marker) return true
        return false
    }

    private fun checkWinFor(marker: String): Boolean {
        return checkWinFor(marker, boardStatus)
    }

    private fun isBoardFull(): Boolean {
        return isBoardFull(boardStatus)
    }

    private fun isBoardFull(board: Array<Array<String>>): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) return false
            }
        }
        return true
    }

    private fun checkWinnerFor(marker: String): Boolean {
        return checkWinFor(marker)
    }

    private fun getCpuMarker(): String {
        return if (userIcon == "X") "O" else "X"
    }

    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                // Limpiamos la imagen de fondo (dejándolo sin recurso)
                board[i][j].setBackgroundResource(0)
                boardStatus[i][j] = ""
            }
        }
        currentPlayer = when (inicio) {
            "CPU" -> getCpuMarker()
            "Al Azar" -> if (Random.nextBoolean()) userIcon else getCpuMarker()
            else -> userIcon
        }
    }
}

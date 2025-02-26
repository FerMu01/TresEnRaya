package com.example.tresenraya

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnFacil: Button
    private lateinit var btnMedio: Button
    private lateinit var btnDificil: Button

    private lateinit var btnYo: Button
    private lateinit var btnCpu: Button
    private lateinit var btnAzar: Button

    private lateinit var btnEquis: Button
    private lateinit var btnRedondo: Button

    private var jugadorInicial = "X" // Valor por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar botones
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)

        btnYo = findViewById(R.id.btnYo)
        btnCpu = findViewById(R.id.btnCpu)
        btnAzar = findViewById(R.id.btnAzar)

        btnEquis = findViewById(R.id.btnEquis)
        btnRedondo = findViewById(R.id.btnRedondo)

        // Seleccionar por defecto "Medio", "Al Azar" y "X"
        setSelectedButton(btnMedio, listOf(btnFacil, btnMedio, btnDificil))
        setSelectedButton(btnAzar, listOf(btnYo, btnCpu, btnAzar))
        setSelectedButton(btnEquis, listOf(btnEquis, btnRedondo))

        // Configurar los listeners para los botones de dificultad
        btnFacil.setOnClickListener { setSelectedButton(btnFacil, listOf(btnFacil, btnMedio, btnDificil)) }
        btnMedio.setOnClickListener { setSelectedButton(btnMedio, listOf(btnFacil, btnMedio, btnDificil)) }
        btnDificil.setOnClickListener { setSelectedButton(btnDificil, listOf(btnFacil, btnMedio, btnDificil)) }

        // Configurar los listeners para los botones de inicio de juego
        btnYo.setOnClickListener { setSelectedButton(btnYo, listOf(btnYo, btnCpu, btnAzar)) }
        btnCpu.setOnClickListener { setSelectedButton(btnCpu, listOf(btnYo, btnCpu, btnAzar)) }
        btnAzar.setOnClickListener { setSelectedButton(btnAzar, listOf(btnYo, btnCpu, btnAzar)) }

        // Configurar los listeners para los botones de icono (X y O)
        btnEquis.setOnClickListener {
            setSelectedButton(btnEquis, listOf(btnEquis, btnRedondo))
            jugadorInicial = "X"
        }

        btnRedondo.setOnClickListener {
            setSelectedButton(btnRedondo, listOf(btnEquis, btnRedondo))
            jugadorInicial = "O"
        }

        // Configurar el botón "Jugar" para abrir la actividad Juego y pasar el jugador inicial
        val jugarButton: Button = findViewById(R.id.button9)
        jugarButton.setOnClickListener {
            val intent = Intent(this, Juego::class.java)
            intent.putExtra("jugadorInicial", jugadorInicial) // Enviar el jugador inicial
            startActivity(intent)
        }
    }

    private fun setSelectedButton(selectedButton: Button, buttonGroup: List<Button>) {
        // Restaurar todos los botones del grupo al color de no seleccionado
        for (button in buttonGroup) {
            button.setBackgroundColor(getColor(R.color.unselected))
        }
        // Resaltar el botón seleccionado
        selectedButton.setBackgroundColor(getColor(R.color.selected))
    }
}

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

    private var jugadorInicial = "X" // Valor por defecto para el icono (X o O)
    private var inicio = "Al Azar"    // Quién empieza: "Yo", "CPU" o "Al Azar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ajustar padding según las insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar botones de dificultad
        btnFacil = findViewById(R.id.btnFacil)
        btnMedio = findViewById(R.id.btnMedio)
        btnDificil = findViewById(R.id.btnDificil)

        // Inicializar botones de quién inicia
        btnYo = findViewById(R.id.btnYo)
        btnCpu = findViewById(R.id.btnCpu)
        btnAzar = findViewById(R.id.btnAzar)

        // Inicializar botones de icono
        btnEquis = findViewById(R.id.btnEquis)
        btnRedondo = findViewById(R.id.btnRedondo)

        // Seleccionar por defecto "Medio", "Al Azar" y "X"
        setSelectedButton(btnMedio, listOf(btnFacil, btnMedio, btnDificil))
        setSelectedButton(btnAzar, listOf(btnYo, btnCpu, btnAzar))
        setSelectedButton(btnEquis, listOf(btnEquis, btnRedondo))

        // Listeners para dificultad (en este ejemplo solo se implementa "Facil")
        btnFacil.setOnClickListener { setSelectedButton(btnFacil, listOf(btnFacil, btnMedio, btnDificil)) }
        btnMedio.setOnClickListener { setSelectedButton(btnMedio, listOf(btnFacil, btnMedio, btnDificil)) }
        btnDificil.setOnClickListener { setSelectedButton(btnDificil, listOf(btnFacil, btnMedio, btnDificil)) }

        // Listeners para quién inicia
        btnYo.setOnClickListener {
            setSelectedButton(btnYo, listOf(btnYo, btnCpu, btnAzar))
            inicio = "Yo"
        }
        btnCpu.setOnClickListener {
            setSelectedButton(btnCpu, listOf(btnYo, btnCpu, btnAzar))
            inicio = "CPU"
        }
        btnAzar.setOnClickListener {
            setSelectedButton(btnAzar, listOf(btnYo, btnCpu, btnAzar))
            inicio = "Al Azar"
        }

        // Listeners para el icono (X o O)
        btnEquis.setOnClickListener {
            setSelectedButton(btnEquis, listOf(btnEquis, btnRedondo))
            jugadorInicial = "X"
        }
        btnRedondo.setOnClickListener {
            setSelectedButton(btnRedondo, listOf(btnEquis, btnRedondo))
            jugadorInicial = "O"
        }

        // Configurar el botón "Jugar" para iniciar la actividad Juego y pasar la configuración
        val jugarButton: Button = findViewById(R.id.button9)
        jugarButton.setOnClickListener {
            val intent = Intent(this, Juego::class.java)
            intent.putExtra("jugadorInicial", jugadorInicial) // Icono del jugador (X o O)
            intent.putExtra("dificultad", "Facil")             // Modo: en este caso, Fácil
            intent.putExtra("inicio", inicio)                   // Quién empieza: "Yo", "CPU" o "Al Azar"
            startActivity(intent)
        }
    }

    private fun setSelectedButton(selectedButton: Button, buttonGroup: List<Button>) {
        // Restaurar color de todos los botones del grupo
        for (button in buttonGroup) {
            button.setBackgroundColor(getColor(R.color.unselected))
        }
        // Resaltar el botón seleccionado
        selectedButton.setBackgroundColor(getColor(R.color.selected))
    }
}

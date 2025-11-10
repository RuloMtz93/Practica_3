package com.example.practica_3

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        // ==============================
        // 1) Aplicar apariencia (claro/oscuro/sistema)
        // ==============================
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val apariencia = prefs.getString("apariencia", "sistema")
        when (apariencia) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        // ==============================
        // 2) Aplicar paleta (guinda/azul) ANTES de inflar vistas
        // ==============================
        val colorElegido = prefs.getString("color_tema", "guinda")
        when (colorElegido) {
            "azul" -> setTheme(R.style.Theme_Practica3_Azul)
            else -> setTheme(R.style.Theme_Practica3_Guinda)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FABs
        val btnTheme = findViewById<FloatingActionButton>(R.id.btnTheme)
        val btnMode = findViewById<FloatingActionButton>(R.id.btnMode)

        // Configurar estilos de FABs con la MISMA paleta
        configurarBotonTema(btnTheme, colorElegido)
        configurarBotonApariencia(btnMode, colorElegido) // 🔥 ahora usa la misma paleta

        // Diálogo de paleta (Guinda/Azul)
        btnTheme.setOnClickListener {
            val opciones = arrayOf("Tema Guinda (IPN)", "Tema Azul (ESCOM)")
            AlertDialog.Builder(this)
                .setTitle("Selecciona una paleta de color")
                .setItems(opciones) { _, which ->
                    val nuevoTema = if (which == 0) "guinda" else "azul"
                    prefs.edit().putString("color_tema", nuevoTema).apply()
                    Toast.makeText(this, "Color cambiado a ${opciones[which]}", Toast.LENGTH_SHORT).show()
                    recreate()
                }.show()
        }

        // Diálogo de apariencia (Claro/Oscuro/Sistema)
        btnMode.setOnClickListener {
            val opciones = arrayOf("Claro", "Oscuro", "Seguir sistema")
            AlertDialog.Builder(this)
                .setTitle("Selecciona apariencia")
                .setItems(opciones) { _, which ->
                    val nuevaApariencia = when (which) {
                        0 -> "claro"
                        1 -> "oscuro"
                        else -> "sistema"
                    }
                    prefs.edit().putString("apariencia", nuevaApariencia).apply()

                    when (nuevaApariencia) {
                        "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }

                    Toast.makeText(this, "Apariencia: ${opciones[which]}", Toast.LENGTH_SHORT).show()
                    recreate()
                }.show()
        }

        // Permisos
        checkStoragePermissions()
    }

    // ========= Helpers de color =========

    /** Devuelve el color primario correcto según la paleta y si está en modo oscuro. */
    private fun primaryColorRes(theme: String?): Int {
        val isDark = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        return when {
            theme == "azul" && isDark -> R.color.azul_escom_oscuro
            theme == "azul" && !isDark -> R.color.azul_escom
            theme == "guinda" && isDark -> R.color.guinda_ipn_oscuro
            else -> R.color.guinda_ipn
        }
    }

    private fun configurarBotonTema(btnTheme: FloatingActionButton, theme: String?) {
        btnTheme.backgroundTintList =
            ContextCompat.getColorStateList(this, primaryColorRes(theme))
        btnTheme.imageTintList =
            ContextCompat.getColorStateList(this, android.R.color.white)
    }

    private fun configurarBotonApariencia(btnMode: FloatingActionButton, theme: String?) {
        // 🔄 Mismo color que el FAB de paleta
        btnMode.backgroundTintList =
            ContextCompat.getColorStateList(this, primaryColorRes(theme))
        btnMode.imageTintList =
            ContextCompat.getColorStateList(this, android.R.color.white)
    }

    // ========= Permisos =========
    private fun checkStoragePermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (!Environment.isExternalStorageManager()) {
                    showManageAllFilesDialog()
                } else {
                    iniciarExplorador()
                }
            }
            else -> {
                val readGranted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                val writeGranted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (readGranted && writeGranted) {
                    iniciarExplorador()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        STORAGE_PERMISSION_CODE
                    )
                }
            }
        }
    }

    private fun showManageAllFilesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de acceso total requerido")
            .setMessage("Para que el explorador funcione correctamente, necesita permiso de acceso total a los archivos del dispositivo.")
            .setPositiveButton("Conceder") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(
                    this,
                    "No se concedió el permiso. La app no podrá explorar archivos externos.",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    // ========= Navegación =========
    private fun iniciarExplorador() {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FileExplorerFragment())
                .commitNow()
        }
        Toast.makeText(this, "Explorador iniciado correctamente.", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            val allGranted = grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                iniciarExplorador()
            } else {
                Toast.makeText(
                    this,
                    "Permisos denegados. No se podrá acceder a los archivos.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                iniciarExplorador()
            }
        }
    }
}

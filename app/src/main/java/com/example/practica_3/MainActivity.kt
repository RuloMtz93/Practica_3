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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔹 Leer el color guardado desde SharedPreferences
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val colorElegido = prefs.getString("color_tema", "guinda") // usa "color_tema"

        // 🔹 Aplicar el tema correspondiente antes de cargar la vista
        when (colorElegido) {
            "azul" -> setTheme(R.style.Theme_Practica3_Azul)
            else -> setTheme(R.style.Theme_Practica3_Guinda)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🎨 Botón flotante para cambiar tema
        val btnTheme = findViewById<FloatingActionButton>(R.id.btnTheme)

        configurarBotonTema(btnTheme, colorElegido)

        btnTheme.setOnClickListener {
            val opciones = arrayOf("Tema Guinda (IPN)", "Tema Azul (ESCOM)")
            AlertDialog.Builder(this)
                .setTitle("Selecciona un tema")
                .setItems(opciones) { _, which ->
                    val nuevoTema = if (which == 0) "guinda" else "azul"
                    prefs.edit().putString("color_tema", nuevoTema).apply() // clave corregida

                    Toast.makeText(
                        this,
                        "Tema cambiado a ${opciones[which]}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 🔄 Recargar activity con el nuevo tema
                    btnTheme.postDelayed({
                        recreate()
                    }, 300)
                }
                .show()
        }

    // 🔐 Verificación de permisos antes de iniciar explorador
        checkStoragePermissions()
    }

    // ===========================================================
    // 🎨 Configurar color dinámico del FAB según el tema activo
    // ===========================================================
    private fun configurarBotonTema(btnTheme: FloatingActionButton, theme: String?) {
        if (theme == "azul") {
            // Tema ESCOM → fondo azul, ícono blanco
            btnTheme.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.azul_escom)
            btnTheme.imageTintList =
                ContextCompat.getColorStateList(this, android.R.color.white)
        } else {
            // Tema IPN → fondo guinda, ícono blanco
            btnTheme.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.guinda_ipn)
            btnTheme.imageTintList =
                ContextCompat.getColorStateList(this, android.R.color.white)
        }
    }

    // ===========================================================
    // 🔐 Verificación y solicitud de permisos de almacenamiento
    // ===========================================================
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

    // ⚙️ Mostrar diálogo para conceder MANAGE_EXTERNAL_STORAGE
    private fun showManageAllFilesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de acceso total requerido")
            .setMessage(
                "Para que el explorador funcione correctamente, necesita permiso de acceso total a los archivos del dispositivo."
            )
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

    // ===========================================================
    // 🚀 Inicializar explorador
    // ===========================================================
    private fun iniciarExplorador() {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FileExplorerFragment())
                .commitNow()
        }
        Toast.makeText(this, "Explorador iniciado correctamente.", Toast.LENGTH_SHORT).show()
    }




    // ===========================================================
    // 📲 Resultado de permisos
    // ===========================================================
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

    // ===========================================================
    // 🔄 Reanudar actividad (por si el usuario dio permiso manualmente)
    // ===========================================================
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                iniciarExplorador()
            }
        }
    }
}

# 📁 Explorador de Archivos Android

Aplicación desarrollada en **Kotlin (Android Studio)** como parte de la práctica de almacenamiento y exploración de archivos.  
Permite navegar entre carpetas internas y externas del dispositivo, crear, eliminar, copiar, mover, renombrar y visualizar archivos comunes con una interfaz moderna basada en **Material Design 3** y soporte para **tema dinámico (claro/oscuro)**.

---

## 🚀 Características principales

✅ **Exploración completa del sistema de archivos**
- Navegación jerárquica entre carpetas internas y externas.
- Compatible con Android 10, 11, 12 y 13 (permiso *All files access* incluido).
- Visualización de nombre, tamaño, tipo y fecha de modificación.

✅ **Gestión de archivos**
- Crear nuevas carpetas.
- Eliminar, copiar, pegar y mover archivos o directorios.
- Renombrar archivos o carpetas con validación básica.
- Abrir archivos con aplicaciones del sistema según su tipo (imágenes, PDF, texto, audio, etc.).

✅ **Interfaz adaptativa**
- Diseño moderno con `Material3`.
- Botones flotantes con animaciones suaves.
- Soporte de **modo claro / oscuro / seguir sistema**.
- Dos **paletas de color personalizadas**:
  - 🎨 **Guinda IPN**
  - 💙 **Azul ESCOM**

✅ **Permisos inteligentes**
- Detección automática del tipo de almacenamiento disponible.
- Solicitud de permisos `MANAGE_EXTERNAL_STORAGE` en Android 11+.
- Alternancia rápida entre almacenamiento **interno** y **externo**.

---

⚙️ Requisitos


Android Studio Giraffe o posterior
SDK mínimo: 30 (Android 11)
Lenguaje: Kotlin
Librerías usadas:
com.google.android.material:material:1.12.0
androidx.appcompat:appcompat
androidx.recyclerview:recyclerview
androidx.coordinatorlayout:coordinatorlayout

---

🧭 Cómo usar

Instala la app o ejecútala desde Android Studio en modo Debug.
Concede permisos de almacenamiento cuando sean solicitados.
Usa los botones flotantes:
➕ Crear carpeta
⋮ Opciones avanzadas: Copiar, Pegar, Mover, Renombrar, Eliminar
🎨 Cambiar color del tema (Guinda / Azul)
🌓 Cambiar modo (Claro / Oscuro / Sistema)
Toca una carpeta para abrirla o un archivo para visualizarlo.

---

Capturas de pantalla

![explorador3](https://github.com/user-attachments/assets/14f39f28-8a89-4cc7-a06e-944baecd2960)
![explorador4](https://github.com/user-attachments/assets/07833bde-68c2-439b-a2b8-d0c0fe5da5a3)
![explorador1](https://github.com/user-attachments/assets/4811e457-995d-48f4-82a1-b374fab71c4e)
![explorador2](https://github.com/user-attachments/assets/0fa78a21-718d-4e5d-addb-7590c9cac8f3)


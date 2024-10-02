package sv.edu.ufg.fis.amb.permisoss_parcial2_ns100221


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var txtCoordinates: TextView
    private lateinit var btnLocation: Button
    private lateinit var btnUploadImage: Button
    private lateinit var btnNotify: Button
    private lateinit var imageView: ImageView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Registro de permisos para la galería
    private val requestImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Aquí se solicita el permiso para acceder a imágenes solo después de seleccionar la imagen
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                imageView.setImageURI(it)
                Toast.makeText(this, "Imagen seleccionada.", Toast.LENGTH_SHORT).show()
            } else {
                // Si el permiso no está concedido, lo solicitamos
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), IMAGE_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializando las variables de la interfaz
        txtCoordinates = findViewById(R.id.txtCoordinates)
        btnLocation = findViewById(R.id.btnLocation)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnNotify = findViewById(R.id.btnNotify)
        imageView = findViewById(R.id.imageView)

        // Inicializando el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configurando el botón de ubicación
        btnLocation.setOnClickListener {
            // Verificamos si los permisos de ubicación están concedidos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion()
            } else {
                // Solicitamos el permiso de ubicación
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
        }

        // Configurando el botón de subir imagen
        btnUploadImage.setOnClickListener {
            // Abrimos la galería para seleccionar una imagen
            requestImageLauncher.launch("image/*")
        }

        // Configurando el botón de notificaciones
        btnNotify.setOnClickListener {
            // Verificamos si los permisos de notificaciones están concedidos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido.", Toast.LENGTH_SHORT).show()
            } else {
                // Solicitamos el permiso de notificaciones
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    // Método para obtener la ubicación actual
    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener<Location?> { location ->
            // Si la ubicación no es nula, mostramos las coordenadas
            if (location != null) {
                txtCoordinates.text = "Coordenadas: ${location.latitude}, ${location.longitude}"
            } else {
                txtCoordinates.text = "No se pudo obtener la ubicación."
            }
        })
    }

    // Manejo de resultados de permisos solicitados
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obtenerUbicacion()
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
                }
            }
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de notificaciones concedido.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permiso de notificaciones denegado.", Toast.LENGTH_SHORT).show()
                }
            }
            IMAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Volvemos a intentar acceder a la imagen seleccionada
                    requestImageLauncher.launch("image/*")
                } else {
                    Toast.makeText(this, "Se requiere permiso para acceder a las imágenes.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Códigos de permisos
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
        private const val IMAGE_PERMISSION_REQUEST_CODE = 1002
    }
}

package mx.edu.ittepic.ladm_u1_p2_dealbaperez

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                            Manifest.permission.READ_EXTERNAL_STORAGE), 0)

        btnGuardar.setOnClickListener{
            if(validarCampos1()){
                if (radioInterna.isChecked)
                    guardarArchivoInterno()
                else
                    guardarArchivoSD()
            }
        }

        btnAbrir.setOnClickListener{
            if(validarCampos2()){
                if (radioInterna.isChecked)
                    leerArchivoInterno()
                else
                    leerArchivoSD()
            }
        }
    }

    private fun guardarArchivoInterno(){
        try{
            var flujoSalida = OutputStreamWriter(openFileOutput(nombreArchivo.text.toString(), Context.MODE_PRIVATE))
            var data = textoArchivo.text.toString().trim()
            flujoSalida.write(data)
            flujoSalida.flush()
            flujoSalida.close()
            mensaje("¡El archivo ha sido creado con éxito!")
            textoArchivo.setText("")
            nombreArchivo.setText("")
        }catch(error: IOException){
            mensaje("ERROR: "+error.message.toString())
        }
    }

    private fun guardarArchivoSD(){
        if(noSD()){
            mensaje("No hay memoria externa disponible :(")
            return
        }
        try {
            val rutaSD = Environment.getExternalStorageDirectory()
            var data = textoArchivo.text.toString().trim()
            val flujo = File(rutaSD.absolutePath, nombreArchivo.text.toString())
            val flujoSalida = OutputStreamWriter(FileOutputStream(flujo))
            flujoSalida.write(data)
            flujoSalida.close()
            mensaje("¡El archivo ha sido creado con éxito!")
            textoArchivo.setText("")
            nombreArchivo.setText("")
        } catch (error: Exception) {
            mensaje("ERROR: "+error.message.toString())
        }
    }

    private fun leerArchivoInterno(){
        try{
            var flujoEntrada = BufferedReader(InputStreamReader(openFileInput(nombreArchivo.text.toString())))
            var data = flujoEntrada.readLine()
            textoArchivo.setText(data)
        }catch(error: IOException){
            mensaje("ERROR: "+error.message.toString())
        }
    }

    private fun leerArchivoSD(){
        if(noSD()){
            mensaje("No hay memoria externa disponible :(")
            return
        }
        try {
            val rutaSD = Environment.getExternalStorageDirectory()
            val flujo = File(rutaSD.absolutePath, nombreArchivo.text.toString())
            val flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(flujo)))
            val data = flujoEntrada.readLine()
            textoArchivo.setText(data)
            flujoEntrada.close()
        } catch (error: Exception) {
            mensaje("ERROR: "+error.message.toString())
        }
    }

    private fun validarCampos1(): Boolean {
        if (textoArchivo.text.toString().isEmpty()) {
            mensaje("Falta introducir una frase")
            textoArchivo.requestFocus()
            return false
        }
        if (!(radioInterna.isChecked || radioSD.isChecked)) {
            mensaje("Elija si desea guardarlo en la SD o en la memoria interna")
            return false
        }
        if (nombreArchivo.text.toString().isEmpty()) {
            mensaje("Falta introducir el nombre del archivo a guardar")
            nombreArchivo.requestFocus()
            return false
        }
        if (nombreArchivo.text.toString().contains(" ")) {
            mensaje("El nombre del archivo no puede contener espacios, favor de eliminarlos")
            nombreArchivo.requestFocus()
            return false
        }

        return true
    }

    private fun validarCampos2(): Boolean{
        if(!(radioInterna.isChecked  || radioSD.isChecked)){
            mensaje("Elija si desea abrir un archivo de la SD o de la memoria interna")
            return false
        }
        if(nombreArchivo.text.toString().isEmpty()){
            mensaje("Falta introducir el nombre del archivo a abrir")
            nombreArchivo.requestFocus()
            return false
        }
        if(nombreArchivo.text.toString().contains(" ")){
            mensaje("El nombre del archivo no puede contener espacios, favor de eliminarlos")
            nombreArchivo.requestFocus()
            return false
        }

        return true
    }

    private fun mensaje(m: String){
        AlertDialog.Builder(this).
            setTitle("ATENCIÓN").
            setMessage(m).
            setPositiveButton("ACEPTAR"){d, i->}.
            show()
    }

    private fun noSD() : Boolean{
        return Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED
    }
}

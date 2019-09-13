package id.co.bytestream_downloader

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this@MainActivity, PERMISSIONS, 112)
        btn_download.setOnClickListener {
            download()
        }

        btn_convert.setOnClickListener {
            convertFileToByteSteam()
        }
    }

    private fun convertFileToByteSteam(): ByteArray? {
        val assetManager = assets
        val inputStream = assetManager.open("test.pdf")

        try {
            return inputStream.readBytes()
        } catch (e: IOException) {
            Log.e("IRFAN", "Error: ${e.message}")
        }
        return null
    }

    private fun download() {
        if (!hasPermissions(this@MainActivity, PERMISSIONS)) {
            val t = Toast.makeText(
                applicationContext,
                "You don't have write access !",
                Toast.LENGTH_LONG
            )
            t.show()

        } else {
            writeResponseBodyToDisk(ByteArrayInputStream(convertFileToByteSteam()))
        }
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            permissions.forEach {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun writeResponseBodyToDisk(body: ByteArrayInputStream): Boolean {
        try {
            val fileName =
                "File name" + "_from" + GregorianCalendar().time + ".pdf"
            val futureStudioIconFile = File(
                "${Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$fileName"
            )
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                var fileSizeDownloaded: Long = 0

                inputStream = body
                outputStream = FileOutputStream(futureStudioIconFile)

                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.e("IRFAN", "file download: $fileSizeDownloaded of")
                }

                outputStream.flush()

                return true
            } catch (e: IOException) {
                Log.e("IRFAN", "${e.message}: ");
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            Log.e("IRFAN", "${e.message}: ");
            return false
        }

    }
}

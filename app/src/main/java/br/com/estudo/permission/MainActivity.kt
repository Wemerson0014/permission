package br.com.estudo.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

const val REQUEST_READ_CONTACTS = 0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //permissão negada
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                val snackbar = Snackbar.make(
                    main,
                    "Usuário, por favor ative este recurso",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction("OK") {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        REQUEST_READ_CONTACTS
                    )
                }
                snackbar.show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS
                )
            }
        } else {
            //permissão concedida
            showList()
        }

    }

    private fun showList() {
        val list = arrayListOf<String>()
        val projection = arrayOf(ContactsContract.Data.DISPLAY_NAME)
        val cursor =
            contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null)

        cursor?.let { c ->
            c.moveToFirst()
            do {
                list.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor?.close()

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = MyAdapter(list)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showList()
                } else {
                    Toast.makeText(this, "Você negou a permissão", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private class MyAdapter(private val list: List<String>) :
        RecyclerView.Adapter<MyAdapter.MyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            return MyHolder(
                LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            val item = list[position]
            (holder.itemView as TextView).text = item
        }

        private class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }

    }
}

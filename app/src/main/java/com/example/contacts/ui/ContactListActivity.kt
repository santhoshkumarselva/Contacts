package com.example.contacts.ui

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.adapter.ContactAdapter
import com.example.contacts.model.ContactModel

class ContactListActivity : AppCompatActivity() {

    private val REQUEST_CODE_READ_CONTACTS = 101
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private var contactList = ArrayList<ContactModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        recyclerView = findViewById(R.id.recycler_view_contacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(contactList)
        recyclerView.adapter = adapter

        // Check for read contacts permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        } else {
            // Permission already granted, load contacts
            loadContacts()
        }
    }

    // Load contacts from the device
    private fun loadContacts() {
        val contentResolver: ContentResolver = contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.let {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val contact = ContactModel(id, name)
                    contactList.add(contact)
                }
                it.close()
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load contacts
                loadContacts()
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied. Can't load contacts.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}

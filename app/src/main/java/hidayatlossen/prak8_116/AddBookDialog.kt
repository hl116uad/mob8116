package hidayatlossen.prak8_116

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import hidayatlossen.prak8_116.databinding.UploadDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Class ini butuh Context (untuk menampilkan dialog) dan DatabaseReference (untuk simpan data)
class AddBookDialog(
    private val context: Context,
    private val booksRef: DatabaseReference
) {

    fun show() {
        // 1. Inflate Layout (Gunakan LayoutInflater.from(context))
        val dialogBinding = UploadDialogBinding.inflate(LayoutInflater.from(context))

        // 2. Setup DatePicker
        dialogBinding.editTextRelease.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context, // Gunakan 'context' yang dipassing dari constructor
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                    dialogBinding.editTextRelease.setText(dateFormat.format(selectedCalendar.time))
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // 3. Buat Dialog
        MaterialAlertDialogBuilder(context)
            .setTitle("Tambah Buku")
            .setView(dialogBinding.root)
            .setPositiveButton("Tambah") { dialog, _ ->

                val title = dialogBinding.editTextTitleBook.text.toString()
                val release = dialogBinding.editTextRelease.text.toString()

                if (title.isEmpty() || release.isEmpty()) {
                    Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                } else {
                    saveDataToFirebase(title, release)
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Kita buat fungsi private biar rapi
    private fun saveDataToFirebase(title: String, release: String) {
        val id = booksRef.push().key
        val newBook = Book(title, release)

        id?.let {
            booksRef.child(it).setValue(newBook)
                .addOnSuccessListener {
                    Toast.makeText(context, "Data berhasil ditambah!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
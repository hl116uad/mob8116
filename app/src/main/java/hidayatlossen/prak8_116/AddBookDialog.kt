package hidayatlossen.prak8_116

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import hidayatlossen.prak8_116.databinding.UploadDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddBookDialog(
    private val context: Context,
    private val booksRef: DatabaseReference
) {

    fun show() {
        val dialogBinding = UploadDialogBinding.inflate(LayoutInflater.from(context))

        dialogBinding.editTextRelease.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


                    dialogBinding.editTextRelease.setText(dateFormat.format(selectedCalendar.time))
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        val title = SpannableString("Tambah Tugas Baru")
        title.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton("Tambah") { dialog, _ ->

                val title = dialogBinding.editTextTitleBook.text.toString()
                val release = dialogBinding.editTextRelease.text.toString()
                val description = dialogBinding.editTextDescriptionBook.text.toString()

                if (title.isEmpty() || release.isEmpty()) {
                    Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                } else {
                    saveDataToFirebase(title, release, description)
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveDataToFirebase(title: String, release: String, description: String) {
        val id = booksRef.push().key
        val newBook = Book(title, release, id, description, false)

        id?.let {
            booksRef.child(id).setValue(newBook)
                .addOnSuccessListener {
                    Toast.makeText(context, "Data berhasil ditambah!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
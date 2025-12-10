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

class EditBookDialog(
    private val context: Context,
    private val booksRef: DatabaseReference
) {

    fun show(book: Book) {
        val binding = UploadDialogBinding.inflate(LayoutInflater.from(context))

        binding.editTextTitleBook.setText(book.title)
        binding.editTextRelease.setText(book.date)
        binding.editTextDescriptionBook.setText(book.description)

        binding.editTextRelease.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                context,
                { _, y, m, d ->
                    val cal = Calendar.getInstance()
                    cal.set(y, m, d)
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.editTextRelease.setText(format.format(cal.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
        val title = SpannableString("Edit Tugas")
        title.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(binding.root)
            .setPositiveButton("Simpan") { _, _ ->

                val newTitle = binding.editTextTitleBook.text.toString()
                val newDate = binding.editTextRelease.text.toString()
                val newDesc = binding.editTextDescriptionBook.text.toString()

                if (newTitle.isEmpty() || newDate.isEmpty()) {
                    Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                } else {
                    updateBook(book.id!!, newTitle, newDate, newDesc, book.completed)

                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    private fun updateBook(id: String, title: String, date: String, description: String, completed: Boolean?) {
        val updatedBook = Book(title = title, date = date, id = id, description = description, completed = completed
        )

        booksRef.child(id).setValue(updatedBook)
            .addOnSuccessListener {
                Toast.makeText(context, "Tugas diperbarui", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}

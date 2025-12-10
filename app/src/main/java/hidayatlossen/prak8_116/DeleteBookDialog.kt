package hidayatlossen.prak8_116

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DatabaseReference

class DeleteBookDialog(
    private val context: Context,
    private val booksRef: DatabaseReference
) {
    fun delete(bookId: String?) {
        if (bookId == null) {
            Toast.makeText(context, "ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        booksRef.child(bookId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Tugas dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}

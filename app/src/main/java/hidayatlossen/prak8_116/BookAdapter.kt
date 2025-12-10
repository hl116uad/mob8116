package hidayatlossen.prak8_116

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import hidayatlossen.prak8_116.databinding.ItemBookBinding

class BookAdapter(
    val books: MutableList<Book>,
    private val booksRef: DatabaseReference
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val booksRef: DatabaseReference
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book, adapter: BookAdapter) {

            binding.tvTitle.text = book.title
            binding.tvRelease.text = book.date
            binding.tvDescription.text = book.description

            // 1. Matikan listener
            binding.checkComplete.setOnCheckedChangeListener(null)

            // 2. Set checkbox tanpa trigger
            binding.checkComplete.isChecked = book.completed == true

            if (book.completed == true) {
                binding.tvTitle.paintFlags =
                    binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.root.alpha = 0.5f
            } else {
                binding.tvTitle.paintFlags =
                    binding.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.root.alpha = 1f
            }

            binding.checkComplete.setOnCheckedChangeListener { _, isChecked ->

                // Update Firebase
                book.id?.let { id ->
                    booksRef.child(id).child("completed").setValue(isChecked)
                }

                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnCheckedChangeListener

                // Jika completed = true → pindahkan ke bawah
                if (isChecked) {
                    val movedItem = adapter.books.removeAt(position)
                    adapter.books.add(movedItem)
                    adapter.notifyItemMoved(position, adapter.books.size - 1)
                }
            }
        }

        fun setDeleteAction(onDelete: () -> Unit) {
            binding.btnDelete.setOnClickListener { onDelete() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding, booksRef)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book, this)

        // Klik card → Edit
        holder.itemView.setOnClickListener {
            val dialog = EditBookDialog(holder.itemView.context, booksRef)
            dialog.show(book)
        }

        // Klik delete
        holder.setDeleteAction {
            val deleteDialog = DeleteBookDialog(holder.itemView.context, booksRef)
            deleteDialog.delete(book.id)
        }
    }

    override fun getItemCount() = books.size
}

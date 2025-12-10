package hidayatlossen.prak8_116

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import hidayatlossen.prak8_116.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var booksRef: DatabaseReference
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: BookAdapter
    private val listBooks = mutableListOf<Book>()   // ← list tetap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        booksRef = FirebaseDatabase.getInstance().getReference("books")

        // Pasang adapter hanya 1x
        adapter = BookAdapter(listBooks, booksRef)
        binding.rvBooks.layoutManager = LinearLayoutManager(this)
        binding.rvBooks.adapter = adapter

        fetchData()
        setupAddButton()
    }

    private fun fetchData() {
        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                for (data in snapshot.children) {
                    val book = data.getValue(Book::class.java)
                    book?.let { books.add(it) }
                }

                // Sortir data: completed = false di atas, completed = true di bawah
                val sortedBooks = books.sortedBy { it.completed == true }

                // Cek jika adapter sudah ada
                if (binding.rvBooks.adapter == null) {
                    binding.rvBooks.adapter = BookAdapter(sortedBooks.toMutableList(), booksRef)
                } else {
                    val adapter = binding.rvBooks.adapter as BookAdapter
                    adapter.books.clear()
                    adapter.books.addAll(sortedBooks)
                    adapter.notifyDataSetChanged()
                }

                if (books.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.rvBooks.visibility = View.GONE
                } else {
                    binding.emptyLayout.visibility = View.GONE
                    binding.rvBooks.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAddButton() {
        binding.fabAddBooks.setOnClickListener {
            AddBookDialog(this, booksRef).show()
        }
    }
}

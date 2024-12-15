package com.example.tugasroom.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.tugasroom.database.Note
import com.example.tugasroom.database.NoteDao
import com.example.tugasroom.database.NoteRoomDatabase
import com.example.tugasroom.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private var updateId: Int = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        with(binding) {
            btnAdd.setOnClickListener(View.OnClickListener {
                insert(
                    Note(
                        title = edtTitle.text.toString(),
                        description = edtDesc.text.toString(),
                        date = edtDate.text.toString()
                    )
                )
                setEmptyField()
            })
            listView.setOnItemClickListener { adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i) as Note
                val intent = Intent(this@MainActivity, UpdateActivity::class.java).apply {
                    putExtra("NOTE_ID", item.id)
                    putExtra("NOTE_TITLE", item.title)
                    putExtra("NOTE_DESC", item.description)
                    putExtra("NOTE_DATE", item.date)
                }
                startActivity(intent)
            }
            listView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { adapterView, _, i, _ ->
                    val item = adapterView.adapter.getItem(i) as Note
                    delete(item)
                    true
                }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

    private fun getAllNotes() {
        mNotesDao.allNotes.observe(this) { notes ->
            val adapter: ArrayAdapter<Note> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, notes
            )
            binding.listView.adapter = adapter
        }
    }

    private fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }

    private fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }

    private fun update(note: Note) {
        executorService.execute { mNotesDao.update(note) }
    }

    private fun setEmptyField() {
        with(binding) {
            edtTitle.setText("")
            edtDesc.setText("")
            edtDate.setText("")
        }
    }
}
package com.example.tugasroom.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tugasroom.database.Note
import com.example.tugasroom.database.NoteDao
import com.example.tugasroom.database.NoteRoomDatabase
import com.example.tugasroom.databinding.ActivityUpdateBinding
import java.util.concurrent.Executors

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var mNotesDao: NoteDao
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        val noteId = intent.getIntExtra("NOTE_ID", 0)
        val noteTitle = intent.getStringExtra("NOTE_TITLE")
        val noteDesc = intent.getStringExtra("NOTE_DESC")
        val noteDate = intent.getStringExtra("NOTE_DATE")

        binding.edtUpdateTitle.setText(noteTitle)
        binding.edtUpdateDesc.setText(noteDesc)
        binding.edtUpdateDate.setText(noteDate)

        binding.btnUpdateNote.setOnClickListener {
            val updatedNote = Note(
                id = noteId,
                title = binding.edtUpdateTitle.text.toString(),
                description = binding.edtUpdateDesc.text.toString(),
                date = binding.edtUpdateDate.text.toString()
            )
            updateNoteInDatabase(updatedNote)
        }
    }

    private fun updateNoteInDatabase(note: Note) {
        executorService.execute {
            mNotesDao.update(note)
            runOnUiThread {
                Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
package com.example.noteapp.feature_note.domain.uses_case

import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.repository.NoteRepository

class AddNotesUseCase(
    private val repository: NoteRepository
) {
    @Throws(Note.InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        if (note.title.isBlank()) {
         throw Note.InvalidNoteException("The title of the note is empty")
        }
        if (note.content.isBlank()) {
            throw Note.InvalidNoteException("The content of the note is empty")
        }
        repository.insertNote(note)

    }
}
package com.example.noteapp.feature_note.domain.uses_case

import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.repository.NoteRepository

class deleteNotesUseCase(
    private val Repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        Repository.deleteNote(note)
    }

}
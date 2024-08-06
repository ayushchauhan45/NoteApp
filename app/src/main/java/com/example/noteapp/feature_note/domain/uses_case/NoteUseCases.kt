package com.example.noteapp.feature_note.domain.uses_case

data class NoteUseCases(
    val getNotes: getNotesUseCase,
    val deleteNote: deleteNotesUseCase,
    val addNote: AddNotesUseCase,
    val getNoteById: GetNoteById
)

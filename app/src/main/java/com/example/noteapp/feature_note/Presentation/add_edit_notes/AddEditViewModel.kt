package com.example.noteapp.feature_note.Presentation.add_edit_notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.feature_note.Presentation.add_edit_notes.components.AddEditNoteEvent
import com.example.noteapp.feature_note.Presentation.add_edit_notes.components.NoteTextFieldState
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.uses_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor
    (
    private val noteUseCases: NoteUseCases,
     savedStateHandle: SavedStateHandle
    ) : ViewModel()
{
  private val _noteTitle = mutableStateOf(NoteTextFieldState(hint = "Enter the Title....."))
    val noteTitle: State<NoteTextFieldState> = _noteTitle

  private val _noteContent = mutableStateOf(NoteTextFieldState(hint = "Enter the note....."))
    val noteContent: State<NoteTextFieldState> = _noteContent

  private val _noteColor = mutableStateOf(Note.noteColor.random().toArgb())
    val noteColor: State<Int> = _noteColor

  private val _eventFlow = MutableSharedFlow<UiEvent>()
  val eventFlow = _eventFlow.asSharedFlow()

 private var currentNoteId: Int? = null
    init {
        savedStateHandle.get<Int>("noteId")?.let {noteId->
            if(noteId != -1){
                viewModelScope.launch {
                    noteUseCases.getNoteById(noteId)?.also {note->
                        currentNoteId = note.id
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = note.content,
                             isHintVisible = false
                        )
                        _noteColor.value = note.color
                    }
                }
            }
        }
    }

  fun onEvent(event: AddEditNoteEvent) {
      when(event) {
          is AddEditNoteEvent.EnteredTitle -> {
          _noteTitle.value= noteTitle.value.copy(
              text = event.value
          )
          }

          is AddEditNoteEvent.ChangeTitleFocus -> {
           _noteTitle.value = noteTitle.value.copy(
               isHintVisible = !event.focusState.isFocused&&
               noteTitle.value.text.isBlank()
           )
          }

          is AddEditNoteEvent.EnteredContent -> {
         _noteContent.value = noteContent.value.copy(
             text = event.value
         )
          }

          is AddEditNoteEvent.ChangeContentFocus -> {
          _noteContent.value = noteContent.value.copy(
              isHintVisible = !event.focusState.isFocused&&
              noteContent.value.text.isBlank()
          )
          }

          is AddEditNoteEvent.ChangeColor -> {
          _noteColor.value = event.color
          }

          is AddEditNoteEvent.SaveNote -> {
          viewModelScope.launch {
              try {
              noteUseCases.addNote(
                  Note(
                      title = noteTitle.value.text,
                      content = noteContent.value.text,
                      timestamp = System.currentTimeMillis(),
                      color = noteColor.value,
                      id = currentNoteId
                  )
              )
                  _eventFlow.emit(UiEvent.SaveNote)
              }catch (e:Note.InvalidNoteException){
                   _eventFlow.emit(
                       UiEvent.ShowSnackBar(
                           message = e.message?: "Couldn't save note"
                       )
                   )
              }
          }
          }
      }
  }

    sealed class UiEvent{
        data class ShowSnackBar(val message: String): UiEvent()
        data object SaveNote:UiEvent()
  }
}
package com.example.noteapp.feature_note.Presentation.notes

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.SnackbarResult
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.noteapp.feature_note.Presentation.notes.components.NoteItem
import com.example.noteapp.feature_note.Presentation.notes.components.OrderSection
import com.example.noteapp.feature_note.Presentation.util.Screen
import com.example.noteapp.feature_note.domain.model.Note
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
){
  val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    androidx.compose.material.Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Notes") },
            backgroundColor = Color.Black)
                 },
        backgroundColor = Color.DarkGray,
        floatingActionButton = {
            androidx.compose.material.FloatingActionButton(
                onClick = {
                    navController.navigate(
                        Screen.AddEditNoteScreen.route
                    )
            },
            backgroundColor = MaterialTheme.colorScheme.primary)
            {
              Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        scaffoldState = scaffoldState
    ) {
         Column(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(16.dp)
         ) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
              ) {
                  Text(text = "Your Note",
                      style = MaterialTheme.typography.headlineMedium,
                      color = Color.White)

                  IconButton(onClick = {
                      viewModel.onEvent(NoteEvent.ToggleOrderSection)
                  }) {
                      Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Sort"
                      )

                  }
              }
             AnimatedVisibility(visible = state.isOrderSectionVisible,
                 enter = fadeIn() + slideInVertically() ,
                 exit = fadeOut() + slideOutVertically()
             ) {
                 OrderSection (
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(vertical = 16.dp),
                     noteOrder = state.noteOrder,
                     onOrderChange = {
                         viewModel.onEvent(NoteEvent.Order(it))
                     }
                 )
             }
             Spacer(modifier = Modifier.height(16.dp))
             LazyColumn(
                 modifier = Modifier.fillMaxWidth()
             ) {
               items(state.notes){ note ->
                   NoteItem(note = note,
                           modifier = Modifier
                               .fillMaxWidth()
                               .clickable {
                                   navController.navigate(
                                       Screen.AddEditNoteScreen.route +
                                               "?noteId=${note.id}&noteColor= ${note.color}"
                                   )
                               },
                       onDeleteClick = {
                           viewModel.onEvent(NoteEvent.DeleteNote(note))
                           scope.launch {
                               val result = scaffoldState.snackbarHostState.showSnackbar(
                                   message = "Note Deleted",
                                   actionLabel = "Undo"
                               )
                               if (result == SnackbarResult.ActionPerformed){
                                   viewModel.onEvent(NoteEvent.RestoreNote)
                               }
                           }
                       }

                   )
                    Spacer(modifier = Modifier.height(16.dp))

               }
             }
         }
    }
}
package com.example.noteapp.di
import android.app.Application
import androidx.room.Room
import com.example.noteapp.feature_note.Data.data_source.NoteDataBase
import com.example.noteapp.feature_note.Data.repository.NoteRepositoryImpl
import com.example.noteapp.feature_note.domain.repository.NoteRepository
import com.example.noteapp.feature_note.domain.uses_case.AddNotesUseCase
import com.example.noteapp.feature_note.domain.uses_case.GetNoteById
import com.example.noteapp.feature_note.domain.uses_case.NoteUseCases
import com.example.noteapp.feature_note.domain.uses_case.deleteNotesUseCase
import com.example.noteapp.feature_note.domain.uses_case.getNotesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNoteDatabase(app:Application):NoteDataBase{
        return Room.databaseBuilder(
            app,
            NoteDataBase::class.java,
            NoteDataBase.DATABASE_NAME
        ).build()
    }


    @Provides
    @Singleton
    fun provideNoteRepository(db:NoteDataBase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }


    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository):NoteUseCases{
        return NoteUseCases(
            getNotes = getNotesUseCase(repository) ,
            deleteNote = deleteNotesUseCase(repository),
            addNote = AddNotesUseCase(repository),
            getNoteById = GetNoteById(repository)

        )
    }
}
package ru.appkode.base.repository.books

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.movies.details.MovieDetailsUM
import ru.appkode.base.ui.core.core.LceState

interface BooksNetworkRepository {
    fun getBookDetails(bookId: Long, localRepository: BooksLocalRepository): Observable<LceState<BookDetailsUM>>
    fun getBookSearch(
        text: String,
        localRepository: BooksLocalRepository,
        page: Int = 1
    ): Observable<List<BookListItemUM>>

    fun getMovieDetails(id: Long, localRepository: BooksLocalRepository): Observable<MovieDetailsUM>

    fun getMovieSearch(
        text: String,
        localRepository: BooksLocalRepository,
        page: Int = 1
    ): Observable<List<BookListItemUM>>
}
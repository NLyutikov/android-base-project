package ru.appkode.base.entities.core.books.search

import ru.appkode.base.entities.core.books.lists.BookListItemUM

fun BookSearchNM.toUiModel(): List<BookListItemUM> {
    return this.work.orEmpty().map { it.toUiModel() }
}

fun SearchResultNM.toUiModel(): BookListItemUM {
    return BookListItemUM(
        id = id,
        title = title,
        averageRating = average_rating,
        imagePath = image_url,
        isInWishList = false,
        isInHistory = false,
        order = null
    )
}

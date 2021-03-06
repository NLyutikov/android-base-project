package ru.appkode.base.ui.books

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.ui.books.lists.history.HistoryController
import ru.appkode.base.ui.books.lists.search.books.BooksSearchController
import ru.appkode.base.ui.books.lists.search.movies.MoviesSearchController
import ru.appkode.base.ui.books.lists.wish.WishListController
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.*

sealed class ScreenAction

data class ShowList(val controllerTag: String) : ScreenAction()
object ShowSearchList : ScreenAction()
object ShowMovieSearchList : ScreenAction()
object ShowBookSearchList : ScreenAction()
object DialogCanceled : ScreenAction()

class BooksMainPresenter(
    schedulers: AppSchedulers,
    val navigationRouter: Router,
    val parentRouter: Router
) : BasePresenter<BooksMainScreen.View, BooksMainScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(BooksMainScreen.View::showListIntent)
                .map { itemId -> ShowList(itemId) },
            intent(BooksMainScreen.View::showSearchList)
                .map { ShowSearchList },
            intent { Observable.just(WISH_LIST_CONTROLLER_TAG) }
                .map { itemId -> ShowList(itemId) },
            intent(BooksMainScreen.View::showBookSearchListIntent)
                .map { ShowBookSearchList },
            intent(BooksMainScreen.View::showMovieSearchListIntent)
                .map { ShowMovieSearchList },
            intent(BooksMainScreen.View::dialogCanceledIntent)
                .map { DialogCanceled }
        )
    }

    override fun reduceViewState(
        previousState: BooksMainScreen.ViewState,
        action: ScreenAction
    ): Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when(action) {
            is ShowList -> processShowList(previousState, action)
            is ShowSearchList -> processShowSearchList(previousState)
            is ShowBookSearchList -> processShowBookSearchList(previousState)
            is ShowMovieSearchList -> processShowMovieSearchList(previousState)
            is DialogCanceled -> processDialogCanceled(previousState)
        }
    }

    private fun processShowSearchList(
        previousState: BooksMainScreen.ViewState
    ) :  Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(showDialog = true) to null
    }

    private fun processShowBookSearchList(
        previousState: BooksMainScreen.ViewState
    ) :  Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(showDialog = false) to command {
            parentRouter.pushController(BooksSearchController().obtainVerticalTransaction())
        }
    }

    private fun processShowMovieSearchList(
        previousState: BooksMainScreen.ViewState
    ) :  Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(showDialog = false) to command {
            parentRouter.pushController(MoviesSearchController().obtainVerticalTransaction())
        }
    }

    private fun processDialogCanceled(
        previousState: BooksMainScreen.ViewState
    ) :  Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(showDialog = false) to null
    }

    /**
     * Удаляет все контроллеры из backstack кроме контроллеров за отображение которых отвечает mainController
     * Затем отображет требуемый LIST_CONTROLLER
     * Если этот контроллер не в backstack, то пушим его
     * Иначе достаем из backstack
     */
    private fun processShowList(
        previousState: BooksMainScreen.ViewState,
        action: ShowList
    ) : Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(currentViewTag = action.controllerTag) to command {
            navigationRouter.deleteAllControllersFromBackstackExcept(
                WISH_LIST_CONTROLLER_TAG, HISTORY_CONTROLLER_TAG,
                changeHandler = getFadeChangeHandler()
            )
            if (!navigationRouter.isControllersWithTagsInBackstack(action.controllerTag))
                when(action.controllerTag) {
                    WISH_LIST_CONTROLLER_TAG -> navigationRouter.pushController(
                        WishListController().obtainFadeTransactionWithTag(WISH_LIST_CONTROLLER_TAG)
                    )
                    HISTORY_CONTROLLER_TAG -> navigationRouter.pushController(
                        HistoryController().obtainFadeTransactionWithTag(HISTORY_CONTROLLER_TAG)
                    )
                }
            else {
                navigationRouter.displayControllerFromBackstackByTag(action.controllerTag)
            }
        }
    }

    override fun createInitialState(): BooksMainScreen.ViewState {
        return BooksMainScreen.ViewState(WISH_LIST_CONTROLLER_TAG, false)
    }
}
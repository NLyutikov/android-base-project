package ru.appkode.base.ui.books.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_item.view.*
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents

class CommonListAdapter(
    private val fromLocalDataSource: Boolean = false,
    private val draggable: Boolean = false
) : RecyclerView.Adapter<CommonListAdapter.ViewHolder>() {

    var data = emptyList<BookListItemUM>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val eventRelay: PublishRelay< Pair<Int, Int> > = PublishRelay.create()

    val itemClicked: Observable<Int> = eventRelay.filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED)

    val wishListIconClicked: Observable<Int> = eventRelay
        .filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_WISH_LIST_ICON_CLICKED)

    val historyIconClicked: Observable<Int> = eventRelay
        .filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_HISTORY_ICON_CLICKED)

    val deleteIconClicked: Observable<Int> = eventRelay
        .filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_DELETE_ICON_CLICKED)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.books_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(view.context)
                .load(data[position].imagePath)
                .onlyRetrieveFromCache(fromLocalDataSource)
                .into(image)
            title.text = data[position].title
            rating.text = data[position].averageRating.toString()
            wishListIcon.isVisible = !data[position].isInWishList
            historyIcon.isVisible = !data[position].isInHistory
            dragIcon.isVisible = draggable
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.books_list_item_image
        val title = view.books_list_item_name
        val rating = view.books_list_item_rating_text
        val wishListIcon = view.books_list_item_wish_list
        val historyIcon = view.books_list_item_history
        val deleteIcon = view.books_list_item_delete
        val dragIcon = view.books_list_item_drag_img

        init {
            view.setOnClickListener { view ->
                    if (view !is ImageView)
                        eventRelay.accept(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED to adapterPosition)
            }
            wishListIcon.setOnClickListener {
                eventRelay.accept(
                    COMMON_LIST_ADAPTER_EVENT_ID_WISH_LIST_ICON_CLICKED to adapterPosition
                )
            }
            historyIcon.setOnClickListener {
                eventRelay.accept(
                    COMMON_LIST_ADAPTER_EVENT_ID_HISTORY_ICON_CLICKED to adapterPosition
                )
            }
            deleteIcon.setOnClickListener {
                eventRelay.accept(
                    COMMON_LIST_ADAPTER_EVENT_ID_DELETE_ICON_CLICKED to adapterPosition
                )
            }
        }
    }
}

const val COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED = 12
const val COMMON_LIST_ADAPTER_EVENT_ID_WISH_LIST_ICON_CLICKED = 13
const val COMMON_LIST_ADAPTER_EVENT_ID_HISTORY_ICON_CLICKED = 14
const val COMMON_LIST_ADAPTER_EVENT_ID_DELETE_ICON_CLICKED = 15
package com.vtsb.hipago.presentation.view.adapter

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import androidx.databinding.DataBindingUtil
import com.google.common.collect.BiMap
import com.vtsb.hipago.databinding.ItemSearchSuggestionBinding
import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.entity.TagType
import java.util.*


class SearchCursorAdapter constructor(
    private val searchResultGetter: SearchResultGetter,
    private val stringType: BiMap<String, TagType>,
    context: Context,
    cursor: Cursor,
) : CursorAdapter(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return ItemSearchSuggestionBinding
            .inflate(LayoutInflater.from(context), parent, false).root
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val tag = cursor.getString(1)
        val tagType = cursor.getString(2)
        val amount = cursor.getInt(3)

        val binding: ItemSearchSuggestionBinding = DataBindingUtil.getBinding(view)!!
        binding.suggestion = Suggestion(tag, stringType[tagType]!!, amount)
    }

    override fun runQueryOnBackgroundThread(constraint: CharSequence?): Cursor? {
        //constraint = searchView.getQuery();
        if (constraint == null) {
            Log.e(this.javaClass.simpleName, "runQueryOnBackgroundThread constraint null")
            return null
        }
        val suggestions = searchResultGetter.getSuggestions(constraint.toString())
        val columns = arrayOf(BaseColumns._ID, "tag", "type", "count")
        val cursor = MatrixCursor(columns)
        for (i in suggestions.indices) {
            val sug = suggestions[i]
            cursor.addRow(arrayOf(i, sug.tag, sug.tagType.otherName, sug.amount))
        }
        return cursor
    }

    interface SearchResultGetter {
        fun getSuggestions(query: String?): ArrayList<Suggestion>
    }

}
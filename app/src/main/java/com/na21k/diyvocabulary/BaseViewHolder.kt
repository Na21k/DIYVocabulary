package com.na21k.diyvocabulary

import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.core.view.MenuCompat
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(
    itemView: View,
    @MenuRes val contextMenuRes: Int,
    @StringRes val contextMenuHeaderRes: Int
) : RecyclerView.ViewHolder(itemView),
    View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    init {
        itemView.setOnCreateContextMenuListener(this)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        if (menu == null || v == null) {
            return
        }

        if (contextMenuRes != 0) {
            val menuInflater = MenuInflater(v.context)
            menuInflater.inflate(contextMenuRes, menu)
            MenuCompat.setGroupDividerEnabled(menu, true)

            if (contextMenuHeaderRes != 0) {
                menu.setHeaderTitle(contextMenuHeaderRes)
            }

            menu.forEach { item ->
                item.setOnMenuItemClickListener(this)
            }
        }
    }
}

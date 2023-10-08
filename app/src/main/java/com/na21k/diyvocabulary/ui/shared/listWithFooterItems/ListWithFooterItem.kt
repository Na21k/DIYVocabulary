package com.na21k.diyvocabulary.ui.shared.listWithFooterItems

const val NORMAL_ITEM = 111
const val FOOTER_ITEM = 222

abstract class ListWithFooterItem {
    abstract fun getType(): Int
}

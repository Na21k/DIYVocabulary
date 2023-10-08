package com.na21k.diyvocabulary.ui.shared.listWithFooterItems

class NormalListItem<T>(val model: T) : ListWithFooterItem() {
    override fun getType(): Int = NORMAL_ITEM
}

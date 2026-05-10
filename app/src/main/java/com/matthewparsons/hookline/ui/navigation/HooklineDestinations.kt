package com.matthewparsons.hookline.ui.navigation

object HooklineDestinations {
    const val HOME = "home"
    const val NEW_PATTERN = "newPattern"

    const val PATTERN_DETAIL_ROUTE = "patternDetail/{id}"
    const val PATTERN_DETAIL_ARG_ID = "id"

    fun patternDetail(id: String): String = "patternDetail/$id"
}

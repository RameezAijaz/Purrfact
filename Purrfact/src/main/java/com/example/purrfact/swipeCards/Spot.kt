package com.purrfact.swipeCards

data class Spot(
    val id: Long = counter++,
    val fact: String?,
    val url: String?
) {
    companion object {
        private var counter = 0L
    }
}

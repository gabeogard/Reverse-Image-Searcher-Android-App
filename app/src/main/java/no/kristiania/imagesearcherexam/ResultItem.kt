package no.kristiania.imagesearcherexam

import android.graphics.Bitmap

data class ResultItem(
    val searchedItem : String,
    val resultOne : Bitmap?,
    val resultTwo : Bitmap?,
    val resultThree : Bitmap?
)

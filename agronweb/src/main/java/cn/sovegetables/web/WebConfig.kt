package cn.sovegetables.web

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WebConfig(var enableAutoTitle : Boolean = true,
                     var title: String? = null,
                     var isCenterTitle: Boolean = true,
                     var withCloseIconAndClosePage: Boolean = false,
                     var statusColorInt: Int? = null,
                     var url: String? = null
) : Parcelable {
}
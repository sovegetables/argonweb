package cn.sovegetables.web

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WebConfig(var enableAutoTitle : Boolean = true,
                     var shareType: Int = TYPE_SHARE_NOT,
                     var title: String? = null,
                     var isCenterTitle: Boolean = true,
                     var withCloseIconAndClosePage: Boolean = false,
                     var statusColorInt: Int? = null,
                     var url: String? = null
) : Parcelable {

    internal var realUrl: String? = null

    companion object{
        const val TYPE_SHARE_NOT = 0
        const val TYPE_SHARE_DEFAULT = 1
    }

}
package info.juanmendez.geovolumecore.ui.form

import android.app.Activity
import android.support.annotation.StringRes
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by @juanmendezinfo on 2/3/18.
 */
interface CoreFormView {
    fun browseLocation(lat: Double, lon: Double)
}
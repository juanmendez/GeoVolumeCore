package info.juanmendez.geovolumecore.types.fence

import android.os.Build
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*

/**
 *
 */
fun Long._toUTC(): DateTime {
    return DateTime(this, DateTimeZone.UTC)
}

fun Long._toDateTime(): DateTime {
    return DateTime( this, DateTimeZone.forTimeZone(TimeZone.getDefault()) )
}

fun Long._toLocalTime( includeMeridien:Boolean = false ):String{
    return this._toDateTime().toString(if( includeMeridien )"hh:mm a" else "hh:mm")
}

fun Long._toMeridien():String{
    return this._toDateTime().toString( "a")
}

@Suppress("DEPRECATION")
fun TextView._textAppearance(style_id:Int ){

    if (Build.VERSION.SDK_INT >= 23) {
        this.setTextAppearance( style_id )
    } else {
        this.setTextAppearance( this.context, id)
    }
}
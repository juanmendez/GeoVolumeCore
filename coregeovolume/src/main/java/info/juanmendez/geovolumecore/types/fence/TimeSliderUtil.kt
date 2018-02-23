package info.juanmendez.geovolumecore.types.fence

import android.os.Build
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by juan on 2/11/18.
 */
class TimeSliderUtil {
    companion object {
        var MAXHOURS = 12
        var MAXTIME = TimeUnit.HOURS.toMillis(MAXHOURS.toLong())
        val STEP_UNIT = 10
        var TIME_UNIT = TimeUnit.MINUTES.toMillis(15)
        val SLIDER_MAX = 4 * MAXHOURS * STEP_UNIT

        fun displayTime(rulerValue:Long ):String{
            return when {
                rulerValue < TimeUnit.HOURS.toMillis(1) ->  "${TimeUnit.MILLISECONDS.toMinutes(rulerValue).toString()} mins"
                rulerValue == TimeUnit.HOURS.toMillis(1) -> "1 hour"
                else ->  "${TimeUnit.MILLISECONDS.toHours(rulerValue)} hours"
            }
        }

        fun displayTime( sliderValue:Int ):String{
            return displayTime( toRuler(sliderValue) )
        }

        fun toRuler(sliderValue:Int ):Long{
            var steps:Int = sliderValue/ STEP_UNIT
            return steps * TIME_UNIT
        }

        fun toSlider(rulerValue:Long ):Int{
            var steps = rulerValue / TIME_UNIT

            return steps.toInt() * STEP_UNIT
        }

        fun toNextFifteenMod( theirTime:Long ):Long{

            var now = theirTime

            var extra = now % TIME_UNIT

            var toFifteen = TIME_UNIT - extra

            if( TimeUnit.MILLISECONDS.toMinutes( extra ) > 4 ){
                toFifteen += TIME_UNIT
            }

            return now + toFifteen
        }
    }
}

class GeoSliderUtil {
    companion object {

        val MAX_RADIUS = 500
        val MIN_RADIUS = 20
        val UNIT = 10
        val STEPS = (MAX_RADIUS - MIN_RADIUS)/ UNIT
        val SLIDER_MAX = STEPS * UNIT

        const val meterToFeet = 3.28084

        fun trimDistance( meters:Int ):Int{
            var _meters = Math.max( meters, MIN_RADIUS )
            _meters = Math.min( _meters, MAX_RADIUS )

            return _meters
        }

        /**
         * feet are rounded, to reduce text content
         */
        fun toFeet(meters:Int):Long{
            var result = trimDistance(meters) * meterToFeet
            return Math.round( result )
        }

        fun toSlider( meters:Int ):Int{
            return trimDistance(meters) - MIN_RADIUS
        }

        fun toRuler( sliderValue:Int ):Int{
            return (sliderValue) + MIN_RADIUS
        }
    }
}

/**
 *
 */
fun Long._toUTC(): DateTime {
    return DateTime(this, DateTimeZone.UTC)
}

fun Long._toDateTime():DateTime{
    return DateTime( this, DateTimeZone.forTimeZone(TimeZone.getDefault()) )
}

fun Long._toLocalDateTime():String{
    return this._toDateTime().toString("hh:mm")
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
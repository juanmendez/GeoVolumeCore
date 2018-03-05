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
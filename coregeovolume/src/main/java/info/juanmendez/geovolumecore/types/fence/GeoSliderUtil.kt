package info.juanmendez.geovolumecore.types.fence

/**
 * Created by juan on 2/28/18.
 */
class GeoSliderUtil {
    companion object {

        val MAX_RADIUS = 750
        val MIN_RADIUS = 50
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
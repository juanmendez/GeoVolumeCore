package info.juanmendez.geovolumecore

import info.juanmendez.geovolumecore.types.fence.FenceUtils
import info.juanmendez.geovolumecore.types.fence.models.GeoFence
import info.juanmendez.geovolumecore.types.fence.models.HeadphoneFence
import info.juanmendez.geovolumecore.types.fence.models.TimeFence
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import info.juanmendez.geovolumecore.types.fence._toDateTime
import org.joda.time.DateTime
import org.joda.time.Period
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by juan on 2/6/18.
 * TDD for fenceKey models. Here we are just doing test development for creating the fenceKey objects.
 * The test class doesn't mock Google AwarenessConst.
 */
class FenceModelingTest {

    var volFence = VolumeFence(false)

    /**
     * This test made the code to validate fences, including VolFence
     */
    @Test
    fun startingWithVolFence(){
        //can we startFence a fenceKey if members are valid?
        val geoFence: GeoFence = volFence.geoFence

        assertFalse( FenceUtils.isValid(geoFence) )

        geoFence.isActive = true
        geoFence.radius = 10
        geoFence.location.lat = 1.0
        geoFence.location.lon = 1.0

        assertTrue( FenceUtils.isValid(geoFence))


        val timeFence:TimeFence = volFence.timeFence
        timeFence.interval = 1L
        timeFence.expiration = 1L
        assertFalse( FenceUtils.isValid(timeFence ))

        timeFence.isActive = true
        assertTrue( FenceUtils.isValid( timeFence ))

        assertFalse( FenceUtils.isValid( volFence.headphoneFence ) )
        assertTrue( FenceUtils.isValid( volFence.headphoneFence.apply { this.isActive = true } ) )

        val fences = volFence.asList()
        val ifAnyFenceValid = fences.any { it !is HeadphoneFence && FenceUtils.isValid(it) }

        assertTrue( ifAnyFenceValid )

        //volumeFence can be valid even if it's inactive
        /*assertTrue( FenceUtils.isValid( volFence ))
        assertTrue( FenceUtils.isValid( volFence.apply { this.isActive = true } ))*/
    }

    /**
     * we need to collect fences once validating
     */
    @Test
    fun collectFences(){
        val geoFence: GeoFence = volFence.geoFence
        assertFalse( FenceUtils.getActiveFences( volFence ).isNotEmpty() )

        geoFence.isActive = true
        geoFence.radius = 10
        geoFence.location.lat = 1.0
        geoFence.location.lon = 1.0

        val timeFence:TimeFence = volFence.timeFence
        timeFence.isActive = true
        timeFence.interval = 1
        timeFence.expiration = 1

        assertFalse( FenceUtils.getActiveFences( volFence ).isEmpty() )

        volFence.headphoneFence.isActive = true
        assertEquals( FenceUtils.getActiveFences( volFence ).size, volFence.asList().size )
    }

    /**
     * lets build code to store utc timeExpiration and local as well
     */
    @Test
    fun inRegardsToTime(){
        val timeFence:TimeFence = volFence.timeFence
        timeFence.interval = 40 * 60 * 1000
        timeFence.expiration = DateTime.now().plus( Period.millis( timeFence.interval.toInt()) ).millis

        println( timeFence.expiration._toDateTime() )
    }
}
package info.juanmendez.geovolumecore

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.fence.models.FenceEvent
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import info.juanmendez.geovolumecore.types.volume.VolumeManager
import info.juanmendez.geovolumecore.types.volume.CoreVolumeAdapter
import info.juanmendez.geovolumecore.types.volume.CoreVolumeStorage
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

/**
 * Created by juan on 2/1/18.
 */
class VolumeUnitTest {
    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var storage: CoreVolumeStorage

    @Mock
    lateinit var adapter: CoreVolumeAdapter

    @Mock
    lateinit var aws: CoreAwarenessService
    lateinit var twistAwarenessService:TwistAwarenessService

    lateinit var volumeManager: VolumeManager
    lateinit var twistVolume: TwistVolume

    @Before
    fun before(){
        twistAwarenessService = TwistAwarenessService(aws)
        volumeManager = VolumeManager(adapter, storage, aws)
        twistVolume = TwistVolume(adapter, storage)

    }

    @Test
    fun toggleVolumeTest(){
        assertNotNull( volumeManager.adapter )
        assertNotNull( volumeManager.storage )

        assertNotNull( storage.levels )

        val hashMap = HashMap<Int, Int>()
        hashMap[1] = 1
        hashMap[2] = 1
        hashMap[3] = 1
        hashMap[4] = 1
        adapter.levels = hashMap

        assertEquals(adapter.levels.size, hashMap.size )

        //initially volume is not muted!
        assertFalse( storage.mute )

        //initially not muted
        volumeManager.toggleMute()

        assertTrue( storage.mute )
        assertEquals( adapter.levels.get(1), 0 )


        //ok, phone rebooted, and everything is stored.. so we won't have any problems in here
        volumeManager.toggleMute()
        assertFalse( storage.mute )
        assertEquals( adapter.levels.get(1), 1 )

        assertNotSame( storage.levels, volumeManager.getStoredLevels() )


        //back to muted
        volumeManager.toggleMute()

        //user updated the volumes while it is on mute mode...
        //when user unmutes, then those values set should remain
        adapter.levels = HashMap<Int, Int>().apply {
            this[1] = 2
            this[2] = 0
            this[3] = 3
            this[4] = 1
        }

        volumeManager.toggleMute()

        //it should have 2 instead of 1
        assertEquals( adapter.levels.get(1), 2 )

    }

    /**
     * Through this test I discovered JSON is converting HashMap into set<String,Double>
     * instead of <Int,Int>, then this test helped to fix it that with Type
     */
    @Test
    fun jsonToHashmap(){
        var h:HashMap<Int,Int> = HashMap()
        h[1] = 1
        h[2] = 2

        val json = Gson().toJson( h )
        assertFalse( json.isEmpty() )

        val type = object:TypeToken<HashMap<Int, Int>>() {}.type
        var strH:HashMap<Int,Int> = Gson().fromJson( json, type )
        assertNotNull( strH )
        assertNotNull( strH[1] )
        assertTrue( strH is HashMap<Int,Int> )

        var h1:HashMap<Int,Int>? = Gson().fromJson( "", type )
        assertNotNull( h1?:HashMap<Int,Int>() )
        assertNull( h1 )
    }

    /**
     * user's device is muted, then it is rebooted, and the fence is running again.
     * The problem is VolumeManager might attempt to mute a muted device. Collecting
     * current levels which are all level 0, and keeping them to restore the volume levels
     * at a later time
     */
    @Test
    fun muteWhatsMuted(){

        val hashMap = HashMap<Int, Int>()
        hashMap[1] = 1
        hashMap[2] = 1
        hashMap[3] = 1
        hashMap[4] = 1
        adapter.levels = hashMap

        twistAwarenessService.fence = VolumeFence( true, "hello")
        //fence value is false, meaning mute the phone!
        twistAwarenessService.subject.onNext( FenceEvent(FenceEvent.FENCE_UPDATED, "hello", true ))

        assertTrue( twistVolume.storage.levels.all { it.value==1 } )

        //phone is rebooted, now what? there is an attempt to mute the phone based on Fence conditions
        twistAwarenessService.subject.onNext( FenceEvent(FenceEvent.FENCE_UPDATED, "hello", true ))

        //this should remain true, despite doing it twice, but we get an error
        //but now we ensure not to update new levels if device is already been muted
        assertTrue( twistVolume.storage.levels.all { it.value==1 } )
        assertTrue( twistVolume.adapter.levels.all { it.value == 0 })

        //ok we want to unmute the device
        twistAwarenessService.subject.onNext( FenceEvent(FenceEvent.FENCE_UPDATED, "hello", false ))
        assertTrue( twistVolume.adapter.levels.all { it.value == 1 })
        assertTrue( twistVolume.storage.levels.all { it.value==1 } ) // they are just kept on hold

        //lets do this also if phone is rebooted
        twistAwarenessService.subject.onNext( FenceEvent(FenceEvent.FENCE_UPDATED, "hello", false ))
        assertTrue( twistVolume.adapter.levels.all { it.value == 1 })

        //so TDD has helped greatly to fix my issue which dealt with rebooting the phone, and canceling the fence
        //the volume levels were all zero
    }
}
package info.juanmendez.geovolumecore

import info.juanmendez.geovolumecore.types.awareness.AwarenessService
import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessBuilder
import info.juanmendez.geovolumecore.types.awareness.CoreFenceStorage
import info.juanmendez.geovolumecore.types.fence.FenceUtils
import info.juanmendez.geovolumecore.types.fence.models.FenceEvent
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import info.juanmendez.geovolumecore.types.volume.CoreVolumeAdapter
import info.juanmendez.geovolumecore.types.volume.CoreVolumeStorage
import info.juanmendez.geovolumecore.types.volume.VolumeManager
import io.reactivex.observers.TestObserver
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.TimeUnit

/**
 * Created by juan on 2/25/18.
 */
class AwarenessTest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var volumeStorage: CoreVolumeStorage

    @Mock
    lateinit var volumeAdapter: CoreVolumeAdapter


    @Mock
    lateinit var awarenessBuilder:CoreAwarenessBuilder

    @Mock
    lateinit var fenceStorage:CoreFenceStorage

    lateinit var volumeManager: VolumeManager
    lateinit var twistVolume: TwistVolume
    lateinit var aws:AwarenessService


    private lateinit var twistStorage: TwistFenceStorage
    private lateinit var twistBuilder: TwistAwarenessBuilder
    private lateinit var testObserver:TestObserver<FenceEvent>

    @Before
    fun before(){

        twistStorage = TwistFenceStorage( fenceStorage )
        twistBuilder = TwistAwarenessBuilder( awarenessBuilder )
        twistVolume = TwistVolume( volumeAdapter, volumeStorage )

        aws = AwarenessService(awarenessBuilder, fenceStorage)
        volumeManager = VolumeManager( volumeAdapter, volumeStorage, aws )

        testObserver = TestObserver.create()
        aws.listen().subscribe(testObserver)
    }


    @Test
    fun testCanceling(){
        twistBuilder.areHeadphonesOn = true
        var volumeFence = VolumeFence(false, "hello")
        volumeFence.headphoneFence.isActive = false

        aws.startFence( volumeFence )
        aws.stopCurrentFence()

        assertFalse( fenceStorage.cancellationPending )


        volumeFence.headphoneFence.isActive = true
        aws.startFence( volumeFence )
        aws.stopCurrentFence()
        assertTrue( fenceStorage.cancellationPending )

        aws.onFenceEvent(true)
        assertTrue( fenceStorage.cancellationPending )

        twistBuilder.areHeadphonesOn = false
        aws.onFenceEvent(true)
        assertFalse( fenceStorage.cancellationPending )
    }

    /**
     * So a fence with an expiration must be canceleled.
     * As it is expected to run until it expires
     */
    @Test
    fun testCancelWhenFenceExpires(){
        var now = DateTime.now()

        var volumeFence = VolumeFence( false, "hello")
        with( volumeFence.timeFence ){
            isActive = true
            interval = 1000
            expiration = now.millis
        }

        aws.startFence( volumeFence )

        aws.onFenceEvent( false )

        //so the time for expiration is now..
        //lets see if this has occured
        assertTrue( FenceUtils.isTimeExpired( volumeFence) )
        assertFalse( volumeFence.isActive )
    }
}
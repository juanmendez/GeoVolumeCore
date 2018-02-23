package info.juanmendez.geovolumecore

import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.awareness.CoreFenceTable
import info.juanmendez.geovolumecore.types.fence.models.FenceEvent
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import info.juanmendez.geovolumecore.types.volume.CoreVolumeAdapter
import info.juanmendez.geovolumecore.types.volume.CoreVolumeStorage
import info.juanmendez.geovolumecore.types.volume.VolumeManager
import io.reactivex.observers.TestObserver
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

/**
 * Created by juan on 2/10/18.
 */
class AwarenessConstApiTest {
    @get:Rule val mockitoRule = MockitoJUnit.rule()

    @Mock lateinit var table: CoreFenceTable
    lateinit var twistTable: TwistTable

    @Mock lateinit var aws: CoreAwarenessService
    lateinit var twistAwarenessService:TwistAwarenessService


    @Mock lateinit var volumeStorage: CoreVolumeStorage
    @Mock lateinit var volumeAdapter: CoreVolumeAdapter
    lateinit var volumeManager: VolumeManager
    lateinit var twistVolume: TwistVolume


    @Before
    fun before(){
        twistTable = TwistTable( table )
        twistAwarenessService = TwistAwarenessService(aws)

        twistVolume = TwistVolume(volumeAdapter, volumeStorage)
        volumeManager = VolumeManager(volumeAdapter, volumeStorage, aws )

        val hashMap = HashMap<Int, Int>()
        hashMap[1] = 1
        hashMap[2] = 1
        hashMap[3] = 1
        hashMap[4] = 1
        volumeAdapter.levels = hashMap
    }


    @Test
    fun mockingTable(){
        val fence = VolumeFence(true)
        table.addOrUpdate( fence )
        assertTrue( table.getFences().isNotEmpty() )

        table.remove( fence )
        assertTrue( table.getFences().isEmpty() )
    }

    @Test
    fun mockingAwarenessService(){
        val fence = VolumeFence(false, "xyz")
        val observer = TestObserver<FenceEvent>()
        aws.listen().subscribe( observer )

        aws.startFence( fence )
        assertNotNull( aws.getLastFence() )
        assertTrue( aws.getLastFence()?.isActive ?: false )
        observer.assertOf { FenceEvent( FenceEvent.FENCE_STARTED, fence.key) }

        var event = FenceEvent(FenceEvent.FENCE_UPDATED, fence.key, true)
        twistAwarenessService.subject.onNext( event )
        observer.assertOf {  event }

        event = FenceEvent(FenceEvent.FENCE_UPDATED, fence.key, false)
        twistAwarenessService.subject.onNext( event )
        observer.assertOf {  event }

        aws.stopCurrentFence()
        assertFalse( aws.getLastFence()?.isActive ?: true )
        observer.assertOf {  FenceEvent( FenceEvent.FENCE_CANCELED, fence.key) }
    }

    /**
     * We need to ensure volumeManager is also grabbing changes, and modifying volume levels
     */
    @Test
    fun testVolumeManagerUponAWS(){

        val fence = VolumeFence(false, "xyz")

        aws.startFence( fence )
        lateinit var event: FenceEvent

        event = FenceEvent(FenceEvent.FENCE_UPDATED, fence.key, true)
        twistAwarenessService.subject.onNext( event )
        assertTrue( volumeAdapter.levels.all { it.value > 0 } && volumeAdapter.levels.isNotEmpty() )

        event = FenceEvent(FenceEvent.FENCE_UPDATED, fence.key, false)
        twistAwarenessService.subject.onNext( event )
        assertTrue( volumeAdapter.levels.all { it.value == 0 } && volumeAdapter.levels.isNotEmpty() )

        event = FenceEvent(FenceEvent.FENCE_UPDATED, fence.key, true)
        twistAwarenessService.subject.onNext( event )
        assertTrue( volumeAdapter.levels.all { it.value > 0 } && volumeAdapter.levels.isNotEmpty() )
    }
}
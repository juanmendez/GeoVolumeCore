package info.juanmendez.geovolumecore

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessBuilder
import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.awareness.CoreFenceStorage
import info.juanmendez.geovolumecore.types.awareness.CoreFenceTable
import info.juanmendez.geovolumecore.types.fence.models.FenceEvent
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by juan on 2/10/18.
 */
class TwistTable(table: CoreFenceTable){

    var fences = mutableListOf<VolumeFence>()

    init {
        doAnswer {
            fences.add(it.getArgument(0))
        }.`when`(table).addOrUpdate( any() )

        doAnswer {
            fences.remove( it.getArgument(0))
        }.`when`(table).remove( any() )

        doAnswer {
            fences.toList()
        }.`when`(table).getFences()
    }
}


class TwistAwarenessService( service: CoreAwarenessService){
    var fence:VolumeFence? = null
    val subject = BehaviorSubject.create<FenceEvent>()

    init {
        doAnswer {
            fence = it.getArgument(0)
            fence?.isActive = true
            subject.onNext( FenceEvent( FenceEvent.FENCE_STARTED, fence?.key ?: "" ))
        }.`when`(service).startFence( any() )


        doAnswer {
            fence = it.getArgument(0)
            fence?.isActive = false
            subject.onNext( FenceEvent( FenceEvent.FENCE_CANCELED, fence?.key ?: "" ))
        }.`when`(service).stopCurrentFence()

        doAnswer {
            fence
        }.`when`(service).getLastFence()

        doReturn( subject ).`when`(service).listen()
    }

    var headphonesOn = false
}

class TwistFenceStorage(fenceStorage:CoreFenceStorage){
    private var fence = VolumeFence(false)
    private var cancellationPending = false

    init {
        doAnswer {
            fence
        }.`when`(fenceStorage).lastFence


        doAnswer {
            fence = it.getArgument(0)
        }.`when`(fenceStorage).lastFence = any()


        doAnswer {
            cancellationPending
        }.`when`(fenceStorage).cancellationPending


        doAnswer {
            cancellationPending = it.getArgument(0)
        }.`when`(fenceStorage).cancellationPending = any()
    }
}


class TwistAwarenessBuilder( builder:CoreAwarenessBuilder){

    var areHeadphonesOn = false

    init {
        doAnswer {
            var fn = it.getArgument<(Boolean)->Unit>(0)
            fn( areHeadphonesOn )
        }.`when`(builder).getHeadphoneState( any() )
    }
}
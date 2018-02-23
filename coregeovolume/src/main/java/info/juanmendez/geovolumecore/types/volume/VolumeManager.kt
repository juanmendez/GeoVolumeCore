package info.juanmendez.geovolumecore.types.volume

import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.fence.models.FenceEvent

/**
 * Created by @juanmendezinfo on 2/1/18.
 * its job is to turn on, and off CoreVolumeAdapter
 */
class VolumeManager (val adapter: CoreVolumeAdapter, val storage: CoreVolumeStorage, val awarenessService: CoreAwarenessService){

    init {

        /**
         * Upon having fenceKey deactivated, lets resume the volume
         */
        awarenessService.listen().subscribe({
                if( it.status == FenceEvent.FENCE_UPDATED){
                    if( it.fenceValue){
                        restoreThem()
                    }else{
                        muteThem()
                    }
                }else if( it.status == FenceEvent.FENCE_CANCELED){
                   restoreThem()
                }})
    }

    /**
     * Upon toggling, we need to turn on or place volume levels down
     */
    fun toggleMute(){

        if( storage.mute ){
            restoreThem()
        }else{
            muteThem()
        }

        storage.mute = !storage.mute
    }

    fun getStoredLevels()=storage.levels.clone()

    private fun restoreThem(){
        //reset volume levels
        var unmutedSet = HashMap<Int,Int>()
        var latestValue:Int

        for( entry in storage.levels ){

            /**
             * if the device while muted has levels changed, then
             * instead keep those values instead of previous levels
             */
            latestValue = adapter.levels.get(entry.key )?.let {
                if( it > 0) it
                else entry.value
            }?:entry.value

            unmutedSet.set( entry.key, latestValue )
        }

        /**
         * storage.levels could be empty, meaning it has never stored any volumene-levels.
         * In this case is best to skip assignment to adapter.levels
         */
        if( unmutedSet.isNotEmpty() )
            adapter.levels = unmutedSet
    }

    private fun muteThem(){
        /**
         * back up volume levels, and mute adapter
         */
        storage.levels = adapter.levels
        adapter.muteLevels()
    }
}
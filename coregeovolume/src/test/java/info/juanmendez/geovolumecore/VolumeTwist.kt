package info.juanmendez.geovolumecore

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import info.juanmendez.geovolumecore.types.volume.CoreVolumeAdapter
import info.juanmendez.geovolumecore.types.volume.CoreVolumeStorage


/**
 * Created by juan on 2/10/18.
 */
class TwistVolume(val adapter:CoreVolumeAdapter, val storage:CoreVolumeStorage ){

    var storageMap = HashMap<String, Int >()
    var adapterMap = HashMap<Int, Int>()


    init{
        //<editor-fold desc="volumeStorage">

        doAnswer { storageMap = it.getArgument(0) }.`when`(storage).levels= any<HashMap<Int, Int>>()
        doAnswer { storageMap.clone()  }.`when`(storage).levels

        var mute = false
        doAnswer { mute=it.getArgument(0) }.`when`(storage).mute= any()
        doAnswer { mute }.`when`(storage).mute
        //</editor-fold>

        //<editor-fold desc="volumeAdapter">
        doAnswer {  adapterMap = it.getArgument(0) }.`when`(adapter).levels = any()
        doAnswer {  adapterMap.clone()  }.`when`(adapter).levels

        doAnswer {  invocationOnMock -> run {
            adapterMap = HashMap<Int, Int>()
            adapterMap[1] = 0
            adapterMap[2] = 0
            adapterMap[3] = 0
            adapterMap[4] = 0

        } }.`when`(adapter).muteLevels()
        //</editor-fold>
    }
}
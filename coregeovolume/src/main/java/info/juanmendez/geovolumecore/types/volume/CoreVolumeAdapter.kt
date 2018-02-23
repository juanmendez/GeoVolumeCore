package info.juanmendez.geovolumecore.types.volume

/**
 * Created by @juanmendezinfo on 2/1/18.
 */
interface CoreVolumeAdapter {
    var levels:HashMap<Int,Int>
    fun muteLevels()
}
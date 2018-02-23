package info.juanmendez.geovolumecore.ui.form

/**
 * Created by @juanmendezinfo on 2/3/18.
 */
interface Presenter<V, VM> {
    fun getVM():VM
    fun start()
    fun stop()
}
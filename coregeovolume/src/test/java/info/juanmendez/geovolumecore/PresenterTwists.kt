package info.juanmendez.geovolumecore

import com.nhaarman.mockito_kotlin.doAnswer
import info.juanmendez.geovolumecore.types.location.CoreLocationFinder
import info.juanmendez.geovolumecore.types.location.CoreNetworkService
import info.juanmendez.geovolumecore.types.location.models.Location
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by juan on 2/6/18.
 * A twist is a placeholder for a mock object. So if we have network and we want it to reply with false
 * when isOnline(), we just go to the twist object and change its own variable which then modifies the
 * return value of isOnline().
 *
 * Twists can also be a good way to define the current state of a mock object. such as shown in
 * TwistFinder.isRunning. That way assertions can be based on twists states,
 * which tell what is the state of the mock object
 */


class TwistNetwork(network: CoreNetworkService){

    private var online:Boolean = false
    private val subject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    var isOnline:Boolean
        get() = online
        set(value) {
            online = value
            subject.onNext( value )
        }

    init {
        doAnswer { isOnline }.`when`( network ).isOnline()
        doAnswer { subject }.`when`( network ).listening()
    }
}

class TwistPermit( finder: CoreLocationFinder){

    private var allow:Boolean = false
    val subject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    var grant:Boolean
        get() = allow
        set(value) {
            allow = value
            subject.onNext( value )
        }

    init {
        doAnswer { allow }.`when`( finder ).hasPermission()
    }
}

class TwistFinder( finder: CoreLocationFinder){

    private val mSubject: BehaviorSubject<Location> = BehaviorSubject.create()
    var isRunning = false

    init{
        doAnswer {
            isRunning = true
            mSubject.onNext( Location() )
            mSubject
        }.`when`(finder).startTracking()

        doAnswer {
            isRunning = false }.`when`(finder).stopTracking()
    }
}
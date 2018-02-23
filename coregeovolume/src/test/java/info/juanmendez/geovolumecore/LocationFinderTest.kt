package info.juanmendez.geovolumecore

import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.location.CoreLocationFinder
import info.juanmendez.geovolumecore.types.location.CoreNetworkService
import info.juanmendez.geovolumecore.ui.form.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

/**
 * Created by juan on 2/3/18.
 * The work from its presenter is now delegated here
 * It takes care of testing tracking addresses
 */
class LocationFinderTest {
    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var finder: CoreLocationFinder

    @Mock
    lateinit var network: CoreNetworkService

    lateinit var twistPermit:TwistPermit
    lateinit var twistFinder:TwistFinder
    lateinit var twistNetwork:TwistNetwork

    @Mock
    lateinit var view:CoreFormView
    lateinit var presenter:FormPresenter

    @Mock
    lateinit var session: CoreFenceSession

    @Mock lateinit var aws: CoreAwarenessService
    lateinit var twistAwarenessService:TwistAwarenessService

    @Before
    fun before(){
        twistNetwork = TwistNetwork( network )
        twistFinder = TwistFinder( finder )
        twistPermit = TwistPermit( finder )
        twistAwarenessService = TwistAwarenessService(aws)
        presenter = FormPresenter( view, FormModule( network, finder, FenceSession(aws), aws ).apply { this.locationPermit = twistPermit.subject } )
    }

    @Test
    fun testTrackingAddress(){

        presenter.start()

        twistNetwork.isOnline = true

        //we are not given location permission, we shouldn't be tracking
        twistPermit.grant = false
        presenter.startTrackingLocation()
        assertFalse( twistFinder.isRunning )

        //ideally we are online, and we are granted location permission
        twistPermit.grant = true

        presenter.startTrackingLocation()
        assertTrue( twistFinder.isRunning )

        //so now network goes offline, then
        //finder should stopCurrentFence
        twistNetwork.isOnline = false
        assertFalse( twistFinder.isRunning )

        //can we startFence tracking if not online
        presenter.startTrackingLocation()
        assertFalse( twistFinder.isRunning )

        presenter.stop()
    }

    @Test
    fun testVM(){
        presenter.start()

        //vm should have an observable related to network
        twistNetwork.isOnline = true

        var vm = presenter.getVM()
        assertTrue( vm.isOnline.get() )

        /**
         * hasLocationPermission only gets an update upon rotate
         */
        assertFalse( vm.hasLocationPermission.get() )

        //between rotate change permission
        presenter.stop()
        twistPermit.grant = true
        presenter.start()
        vm = presenter.getVM()

        assertTrue( vm.hasLocationPermission.get() )
        presenter.stop()
    }
}
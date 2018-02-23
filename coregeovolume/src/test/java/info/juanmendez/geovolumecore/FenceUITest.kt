package info.juanmendez.geovolumecore

import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.fence.*
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import info.juanmendez.geovolumecore.types.location.CoreLocationFinder
import info.juanmendez.geovolumecore.types.location.CoreNetworkService
import info.juanmendez.geovolumecore.types.location.models.Location
import info.juanmendez.geovolumecore.ui.form.*
import org.joda.time.LocalTime
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.TimeUnit

/**
 * Created by juan on 2/6/18.
 */
class FenceUITest {
    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var finder: CoreLocationFinder

    @Mock
    lateinit var network: CoreNetworkService

    lateinit var twistPermit:TwistPermit
    lateinit var twistFinder:TwistFinder
    lateinit var twistNetwork:TwistNetwork
    lateinit var module:FormModule

    @Mock
    lateinit var view: CoreFormView
    lateinit var presenter: FormPresenter
    lateinit var vm:FormViewModel
    lateinit var volumeFence: VolumeFence

    @Mock lateinit var aws: CoreAwarenessService
    lateinit var twistAwarenessService:TwistAwarenessService

    @Before
    fun before(){
        twistNetwork = TwistNetwork( network )
        twistFinder = TwistFinder( finder )
        twistPermit = TwistPermit( finder )
        twistAwarenessService = TwistAwarenessService(aws)
        module = FormModule( network, finder, FenceSession( aws), aws )
        volumeFence = module.session.fence

        presenter = FormPresenter( view, module.apply { this.locationPermit = twistPermit.subject } )
    }


    @Test
    fun testTimeSlider(){
        var now = LocalTime.parse( "13:00")
        var nowAsFifteenth = TimeSliderUtil.toNextFifteenMod( now.millisOfDay.toLong() )
        now = LocalTime.fromMillisOfDay( nowAsFifteenth )
        println( now.toString() )
        assertEquals( now.minuteOfHour, 15)

        now = LocalTime.parse( "13:57" )
        nowAsFifteenth = TimeSliderUtil.toNextFifteenMod( now.millisOfDay.toLong() )
        now = LocalTime.fromMillisOfDay( nowAsFifteenth )
        assertEquals( now.minuteOfHour, 15 )
        println( now.toString() )


        now = LocalTime.parse( "13:45" )
        nowAsFifteenth = TimeSliderUtil.toNextFifteenMod( now.millisOfDay.toLong() )
        now = LocalTime.fromMillisOfDay( nowAsFifteenth )
        assertEquals( now.minuteOfHour, 0 )
        println( now.toString() )

        var step = 2
        var sliderValue = step * TimeSliderUtil.STEP_UNIT

        var rulerValue:Long = TimeSliderUtil.toRuler( sliderValue )
        assertEquals( TimeUnit.MILLISECONDS.toMinutes(rulerValue), 30 )


        nowAsFifteenth = TimeUnit.MINUTES.toMillis( 15 )
        sliderValue = TimeSliderUtil.toSlider( nowAsFifteenth )

        //we move 15 minutes!
        assertEquals( sliderValue, 1 * TimeSliderUtil.STEP_UNIT )


        nowAsFifteenth = TimeUnit.MINUTES.toMillis( 45 )
        sliderValue = TimeSliderUtil.toSlider( nowAsFifteenth )

        //we move 15 minutes!
        assertEquals( sliderValue, 3 * TimeSliderUtil.STEP_UNIT )
    }

    @Test
    fun testDistanceSlider(){
        //meters to feet
        //we have 10 meters.. what is in the slider?
        assertEquals( GeoSliderUtil.toSlider(10), 0 )

        assertEquals( GeoSliderUtil.toRuler(GeoSliderUtil.SLIDER_MAX), GeoSliderUtil.MAX_RADIUS )
        assertEquals( GeoSliderUtil.toRuler(0), GeoSliderUtil.MIN_RADIUS )
    }

    @Test
    fun testFenceActions(){
        volumeFence.isActive = true
        presenter.start()
       /* vm = presenter.getVM()
        assertTrue( vm.isVolumeFenceActive.get() )

        vm.fenceAction.set( AwarenessConst.START_FENCE )
        verify( aws ).startFence( any() )

        vm.fenceAction.set( AwarenessConst.STOP_FENCE )
        verify( aws ).stopCurrentFence( any() )

        vm.fenceAction.set( AwarenessConst.RESET_FENCE )
        verify( aws ).resetFence( any() )*/
    }

    /**
     * so now we need to startFence working with the viewModel. We need to interact with user input
     * and notify when there is a chance to startFence running a fenceKey.
     * Things we will look up to:
     * - User provides a given Location having lat/lon
     * - User provides radius
     * - User provides a timeExpiration interval... hmm smells like teen Joda spirit!!
     * - We update volumeFence only through the viewModel.
     */
    @Test
    fun interactingWithLocation(){

        volumeFence.isActive = true
        val circumfence = volumeFence.geoFence
        val timeFence = volumeFence.timeFence

        presenter.start()
        vm = presenter.getVM()
//        assertEquals( vm.timeExpiration.get(), DateTime.now().toString("h:mm a")  )

        vm.timeInterval = TimeUnit.MINUTES.toMillis( 30 ).toInt()
        assertFalse( vm.timeExpiration.get() == 0L )

        //location is one way binding.. so lets try it now
        var vmAddress = Location( "hello", "00 N State", "Chicago, IL 60600" )
        vmAddress.lat = 5.0
        vmAddress.lon = 6.0

        vm.location = vmAddress
        vm.radius = 10

        assertEquals( circumfence.location.name, "hello")
        assertTrue( circumfence.location.lat == 5.0 )

        vm.isGeoFenceActive.set( true )
        assertTrue( circumfence.isActive )

        vm.isTimeFenceActive.set(true)
        assertTrue( timeFence.isActive )
        assertTrue( FenceUtils.isValid( volumeFence ) )
        assertTrue( vm.isVolumeFenceValid.get() )

        vmAddress.lat = 0.0
        vm.notifyPropertyChanged(BR.location)

        vm.isGeoFenceActive.set( false )
        vm.isTimeFenceActive.set( false )

        //there are no valid child fences, so volumeFence is invalid
        assertFalse( FenceUtils.isValid( circumfence))
        assertFalse( FenceUtils.isValid( volumeFence ))
        assertFalse( vm.isVolumeFenceValid.get() )

        //ok, then lets make timefence valid
        vm.isTimeFenceActive.set( true)

        assertTrue(  FenceUtils.isValid( timeFence ))
        assertTrue( FenceUtils.isValid( volumeFence ) )


        //so now isVolumeFenceValid should be true
        assertTrue( vm.isVolumeFenceValid.get() )
    }


    @Test
    fun interactingWithRadius(){
        presenter.start()
        vm = presenter.getVM()

        vm.radius = 10 //10 meters
        assertEquals( vm.radius, volumeFence.geoFence.radius.toInt() )
    }

    /**
     * Lets do a bold move. User is required to replace current fenceKey.
     */
    @Test
    fun rebuildFence(){
        var newFence = VolumeFence(true).apply {
            with( this.geoFence){
                isActive = true
                location = Location("New Home", "00 N. Magnolia", "Chicago, IL", 3.0, 4.0)
                radius = 10
            }
        }

        twistPermit.grant = true
        twistNetwork.isOnline = true

        presenter.start()
        assertTrue( presenter.getVM().hasLocationPermission.get() )
        assertTrue( presenter.getVM().isOnline.get() )
        var vm = presenter.getVM()
        var theirRadius = vm.radius

        //now we want to update fenceKey!
        module.session.fence = newFence
        presenter.refresh()
        assertNotSame( vm, presenter.getVM())
        assertNotEquals( theirRadius, presenter.getVM().radius )
        assertTrue( presenter.getVM().hasLocationPermission.get() )
        assertTrue( presenter.getVM().isOnline.get() )

        presenter.stop()
    }


    /**
     * Our fences need some graphical way to add them to volumeFence
     * So for now lets think of checkboxes. We need to ensure our fenceKey
     * is able to get updates from the viewmodel, and also be active
     * which means it passes
     */
    @Test
    fun validateFences(){

        var circumfence = volumeFence.geoFence
        circumfence.isActive = true

        var headphoneFence = volumeFence.headphoneFence
        headphoneFence.isActive = true

        var timeFence = volumeFence.timeFence
        timeFence.isActive = true

        presenter.start()
        var vm = presenter.getVM()
        assertTrue( vm.isGeoFenceActive.get() )

        vm.isGeoFenceActive.set( false )
        assertFalse( circumfence.isActive )

        assertTrue( vm.isHeadphoneFenceActive.get() )

        vm.isHeadphoneFenceActive.set( false )
        assertFalse( headphoneFence.isActive )

        assertTrue( vm.isTimeFenceActive.get() )
        vm.isTimeFenceActive.set( false )
        assertFalse( timeFence.isActive )

    }

}
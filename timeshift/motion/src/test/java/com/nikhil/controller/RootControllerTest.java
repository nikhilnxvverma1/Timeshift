package com.nikhil.controller;

import com.nikhil.common.PulseListener;
import com.nikhil.common.PulseListenerContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

/**
 * Created by NikhilVerma on 11/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RootControllerTest {

//    private RootController rootController;
//    private Circle circle1;
//    private Circle circle2;
//    private Circle circle3;
//    private CircleController circleController1;
//    private CircleController circleController2;
//    private CircleController circleController3;

    @Mock
    private CompositionController mockedCompositionController;
    @Mock
    private PulseListener mockedPulseListener;

    @Before
    public void setUp() throws Exception {
        System.out.println("setting up data");
//        rootController=new RootController();
//
//        CompositionController compositionController1 = new CompositionController(new Timeline(null));
//        rootController.addCompositionController(compositionController1);
//        circle1 = new Circle(23, 45, 0, 360);
//        circleController1 = new CircleController(circle1);
//        compositionController1.addItemController(circleController1);
//
//        CompositionController compositionController2 = new CompositionController(new Timeline(null));
//        rootController.addCompositionController(compositionController2);
//        circle2 = new Circle(23, 45, 0, 360);
//        circleController2 = new CircleController(circle2);
//        compositionController2.addItemController(circleController2);
//
//        CompositionController compositionController3 = new CompositionController(new Timeline(null));
//        rootController.addCompositionController(compositionController3);
//        circle3 = new Circle(23, 45, 0, 360);
//        circleController3 = new CircleController(circle3);
//        compositionController3.addItemController(circleController3);

//        initMocks();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("tearing down");
    }

    @Test
    public void testStep() throws Exception {
        System.out.println("step method");
//        rootController.step(0.016);
        RootController rootController=new RootController(mockedCompositionController);
        rootController.registerPulseListener(mockedPulseListener);
        rootController.step(0.016,0 );
        verify(mockedCompositionController, times(1)).step(0.016,0 );
        verify(mockedPulseListener, times(1)).step(0.016,0 );
    }

    @Test
    public void testRegisterPulseListener() throws Exception {
        System.out.println("register pulse listener");
        RootController rootController=new RootController(mockedCompositionController);
        rootController.registerPulseListener(mockedPulseListener);
        PulseListenerContainer pulseListenerContainer=rootController.findContainerFor(mockedPulseListener);
        assertTrue(pulseListenerContainer.getPulseListener()==mockedPulseListener);

        //adding the same pulse listener again
        rootController.registerPulseListener(mockedPulseListener);
        PulseListenerContainer anotherPulseListenerContainer=rootController.findContainerFor(mockedPulseListener);
        assertEquals(anotherPulseListenerContainer,pulseListenerContainer);
    }

    @Test
    public void testDeregisterPulseListener() throws Exception {
        System.out.println("deregister pulse listener");
        RootController rootController=new RootController(mockedCompositionController);
        rootController.registerPulseListener(mockedPulseListener);
        rootController.deregisterPulseListener(mockedPulseListener);
        PulseListenerContainer pulseListenerContainer=rootController.findContainerFor(mockedPulseListener);
        assertNull(pulseListenerContainer);
    }

    @Test
    public void testAddCompositionController() throws Exception {
        System.out.println("add composition controller");
        RootController rootController=new RootController(mockedCompositionController);
        CompositionController secondCompositionController=mock(CompositionController.class);
        rootController.addCompositionController(secondCompositionController);
        assertEquals(mockedCompositionController.getNext(),secondCompositionController);
    }
    @Test
    public void testFindContainerFor() throws Exception{
        System.out.println("testing find container for");
        RootController rootController=new RootController(mockedCompositionController);
        //negative test
        PulseListenerContainer pulseListenerContainer =rootController.findContainerFor(mockedPulseListener);
        assertNull(pulseListenerContainer);

        //positive test
        rootController.registerPulseListener(mockedPulseListener);
        pulseListenerContainer=rootController.findContainerFor(mockedPulseListener);
        assertTrue(pulseListenerContainer.getPulseListener()==mockedPulseListener);
        assertNotNull(pulseListenerContainer);

    }

}
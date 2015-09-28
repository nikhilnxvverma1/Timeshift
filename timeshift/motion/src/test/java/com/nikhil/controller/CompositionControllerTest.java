package com.nikhil.controller;

import com.nikhil.controller.item.CircleModelController;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.playback.TimelinePlayer;
import com.nikhil.timeline.Timeline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

/**
 * Created by NikhilVerma on 17/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositionControllerTest {

    private CompositionController compositionController;
    private TimelinePlayer mockedTimelinePlayer=mock(TimelinePlayer.class);

    @Before
    public void setUp() throws Exception{

        Timeline mockedTimeline=mock(Timeline.class);
        compositionController=new CompositionController(mockedTimeline,mockedTimelinePlayer);

        //3 mocked item controllers
        ItemModelController mockedItem1=mock(CircleModelController.class);

        compositionController.setItemModelControllerStart(mockedItem1);
    }

    @After
    public void tearDown() throws Exception{

    }

    @Test
    public void testStep(){

        when(mockedTimelinePlayer.shouldRecieveTimestep(0.016,0 )).thenReturn(false);
        compositionController.step(0.016, 0);

        verify(compositionController.getItemModelControllerStart(),times(0)).step(0.016, 0,compositionController);

        when(mockedTimelinePlayer.shouldRecieveTimestep(0.016,0 )).thenReturn(true);
        when(mockedTimelinePlayer.getRevisedDeltaTimestep(0.016)).thenReturn(0.016);
        compositionController.step(0.016, 0);

        verify(compositionController.getItemModelControllerStart(),times(1)).step(0.016,0,compositionController );

    }

    @Test
    public void testTimelineReachedTerminal(){
        compositionController.timelineReachedTerminal(compositionController.getTimeline());
        verify(mockedTimelinePlayer,times(1)).didReachEnd();
    }
}
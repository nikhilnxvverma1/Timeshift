package com.nikhil.model.pathsystem.rupture;

import com.nikhil.model.pathsystem.TravelPath;
import com.nikhil.model.pathsystem.traveller.Traveller;
import com.nikhil.model.pathsystem.traveller.TravellerConfiguration;

public class RuptureMover implements Traveller {

	private RupturePoint parent;

	@Override
	public TravellerConfiguration getTravellerConfiguration() {
		return null;
	}

	@Override
	public void movedTo(TravelPath movingOn, double distanceOnPath) {

	}

	@Override
	public void transitioned(TravelPath fromTravelPath, TravelPath toTravelPath) {

	}

	@Override
	public void reachedTerminalPath(TravelPath terminatingPath, boolean movingForward) {

	}

	@Override
	public void collidedWith(Traveller other) {

	}
}

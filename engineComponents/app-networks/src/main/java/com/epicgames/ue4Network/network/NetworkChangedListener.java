package com.epicgames.ue4Network.network;

import android.net.Network;

public interface NetworkChangedListener {
	
	void onNetworkAvailable(Network network);

	void onNetworkLost(Network network);
}

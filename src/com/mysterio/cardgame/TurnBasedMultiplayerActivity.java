package com.mysterio.cardgame;

import java.util.List;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameActivity;

public class TurnBasedMultiplayerActivity extends BaseGameActivity implements OnInvitationReceivedListener, RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener{

	private static final String TAG = "TurnBasedCardGame";
	private String mIncomingInvitationId;
	
	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		Log.i(TAG, "Login Succeeded");
        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(getApiClient(), this);		
	}

	@Override
	public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
		Log.i(TAG, "invitation received : "+invitation);
        mIncomingInvitationId = invitation.getInvitationId();
        Toast.makeText(getApplicationContext(), mIncomingInvitationId + " is inviting you to play.", Toast.LENGTH_LONG).show();
     // if we received an invite via notification, accept it; otherwise, go to main screen
        if (getInvitationId() != null) {
            acceptInviteToRoom(getInvitationId());
            return;
        }        
	}
	
	// Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        //switchToScreen(R.id.screen_wait);
        keepScreenOn();
        //resetGameVars();
        Games.RealTimeMultiplayer.join(getApiClient(), roomConfigBuilder.build());
    }
    
    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }    

	@Override
	public void onInvitationRemoved(String invitationId) {
		if (mIncomingInvitationId.equals(invitationId)) {
			Log.i(TAG, "Invitation removed");
            mIncomingInvitationId = null;
        }
		
	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeftRoom(int statusCode, String roomId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomCreated(int statusCode, Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectedToRoom(Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnectedFromRoom(Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PConnected(String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PDisconnected(String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerDeclined(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerJoined(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerLeft(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeersConnected(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeersDisconnected(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomAutoMatching(Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomConnecting(Room room) {
		// TODO Auto-generated method stub
		
	}

}

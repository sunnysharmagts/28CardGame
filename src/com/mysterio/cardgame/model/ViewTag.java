package com.mysterio.cardgame.model;

import com.mysterio.cardgame.model.Card.Player;

public class ViewTag {
	
	private final Player mPlayer;
	private final int mDrawable;
	
	public ViewTag(final Player player, final int drawable) {
		mPlayer = player;
		mDrawable = drawable;
	}

	/**
	 * @return the mPlayer
	 */
	public Player getPlayer() {
		return mPlayer;
	}

	/**
	 * @return the mDrawable
	 */
	public int getDrawable() {
		return mDrawable;
	}
}

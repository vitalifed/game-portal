package org.bol.game.portal.dto;

/**
 * 
 * 
 * @author VF85400
 *
 * @param <ActualUser>
 */
public class Game<ActualUser extends User> {

	private ActualUser currentUser;

	public ActualUser getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(ActualUser currentUser) {
		this.currentUser = currentUser;
	}
	
}

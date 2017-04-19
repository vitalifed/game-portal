package org.steelrat.game.portal.dto;

/**
 * 
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
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

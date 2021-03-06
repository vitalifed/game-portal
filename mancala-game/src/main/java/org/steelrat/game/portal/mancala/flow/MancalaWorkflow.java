package org.steelrat.game.portal.mancala.flow;

import org.steelrat.game.portal.LauncherBuilder;
import org.steelrat.game.portal.dto.Game;
import org.steelrat.game.portal.dto.User;
import org.steelrat.game.portal.flow.Workflow;

/**
 * The interface extends generic {@link Workflow} interface with a single method
 * state, where the state method presumably updates the state of a game and
 * creates a {@link LauncherBuilder}
 * 
 * @author <a href="mailto:vitali.fedosenko@gmail.com">Vitali Fedasenka</a>
 *
 * @param <ActualUser>
 *            Actual implementation of {@link User}
 * @param <ActualGame>
 *            Actual implementation of {@link Game}
 */
public interface MancalaWorkflow<ActualUser extends User, ActualGame extends Game<ActualUser>>
		extends Workflow<ActualUser, ActualGame> {

	LauncherBuilder<?> state(String room, ActualUser user, Integer idx);

}

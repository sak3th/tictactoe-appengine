package tictactoe.model;

import tictactoe.model.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "playerendpoint")
public class PlayerEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public static CollectionResponse<Player> list(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Player> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Player as Player");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Player>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Player obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Player> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param email the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	public static Player get(@Named("email") String email) {
		EntityManager mgr = getEntityManager();
		Player player = null;
		try {
			player = mgr.find(Player.class, email);
		} finally {
			mgr.close();
		}
		return player;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param player the entity to be inserted.
	 * @return The inserted entity.
	 */
	public static Player insert(Player player) {
		EntityManager mgr = getEntityManager();
		try {
			if (contains(player)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(player);
		} finally {
			mgr.close();
		}
		return player;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param player the entity to be updated.
	 * @return The updated entity.
	 */
	public static Player update(Player player) {
		EntityManager mgr = getEntityManager();
		try {
			if (!contains(player)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.merge(player);
		} finally {
			mgr.close();
		}
		return player;
	}

	/**
	 * This method removes the entity with primary key email.
	 * It uses HTTP DELETE method.
	 *
	 * @param email the primary key of the entity to be deleted.
	 * @return The deleted entity.
	 */
	public static Player remove(@Named("email") String email) {
		EntityManager mgr = getEntityManager();
		Player player = null;
		try {
			player = mgr.find(Player.class, email);
			mgr.remove(player);
		} finally {
			mgr.close();
		}
		return player;
	}

	private static boolean contains(Player player) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Player item = mgr.find(Player.class, player.getEmail());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
package pt.teamloan.utils;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.jboss.logmanager.Level;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import pt.teamloan.exception.GenericException;

@ApplicationScoped
public class UUIDMapper {

	private static final Logger LOGGER = Logger.getLogger(UUIDMapper.class.getName());

	@Inject
	EntityManager em;

	@CacheResult(cacheName = "map-uuid-cache")
	@Transactional
	public Integer mapToId(UUID uuid, Class entityClass) throws GenericException {
		try {
			long time = System.currentTimeMillis();
			TypedQuery<Integer> mapToIdQuery = em
					.createQuery("SELECT e.id FROM " + entityClass.getSimpleName() + " e WHERE e.uuid = :uuid", Integer.class);
			mapToIdQuery.setParameter("uuid", uuid);
			Integer id = mapToIdQuery.getSingleResult();
			return id;
		} catch (Exception e) {
			String errorMessage = formatMessageForLog("Error mapping UUID to ID. Entity: {0}; UUID: {1}",
					entityClass.getSimpleName(), uuid);
			LOGGER.log(Level.ERROR, errorMessage, e);
			throw new GenericException(errorMessage, e);
		}
	}
	public Integer mapToId(String uuid, Class entityClass) throws GenericException {
		return mapToId(UUID.fromString(uuid), entityClass);
	}
	
	@CacheInvalidateAll(cacheName = "map-uuid-cache")
	public void invalidateCache() {
		LOGGER.info("Invalidated cache 'map-uuid-cache'");
	}
	
	private String formatMessageForLog(String message, Object... parameters) {
		return MessageFormat.format(message, parameters);
	}

}

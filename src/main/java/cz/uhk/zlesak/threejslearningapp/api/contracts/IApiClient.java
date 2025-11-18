package cz.uhk.zlesak.threejslearningapp.api.contracts;

import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import org.springframework.data.domain.PageRequest;

/**
 * Interface for generic API Client
 * Defines CRUD operations and listing for entities of type T and returns entities of type S in paginated results.
 *
 * @param <T> Entity type that this API client works with
 * @param <S> Entity type that is returned in paginated results
 */
public interface IApiClient <T, S, F> {
    static String getBaseUrl() {
        boolean isHotswap = java.lang.management.ManagementFactory.getRuntimeMXBean()
                .getInputArguments().stream()
                .anyMatch(arg -> arg.contains("hotswap-agent.jar"));
        if (isHotswap) {
            return "http://localhost:8080/api/";
        }
        return "http://kotlin-backend:8080/api/";
    }

    static  String getLocalBaseBeUrl() {
        return "http://localhost:8080/api/";
    }

    /**
     * Creates a new entity.
     *
     * @param entity Entity to create
     * @return Created entity
     * @throws Exception if creation fails
     */
    T create(T entity) throws Exception;

    /**
     * Reads an entity by its ID.
     *
     * @param id ID of the entity to read
     * @return Read entity
     * @throws Exception if reading fails
     */
    T read(String id) throws Exception;

    /**
     * Reads entities in a paginated manner.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @return PageResult of entities of type S
     * @throws Exception if reading fails
     */
    PageResult<S> readEntities(PageRequest pageRequest) throws Exception;

    /**
     * Reads entities in a paginated manner with filtering.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @param filter Filter object of type F
     * @return PageResult of entities of type S
     * @throws Exception if reading fails
     */
    PageResult<S> readEntitiesFiltered(PageRequest pageRequest, F filter) throws Exception;

    /**
     * Updates an existing entity by its ID.
     *
     * @param id ID of the entity to update
     * @param entity Updated entity
     * @return Updated entity
     * @throws Exception if updating fails
     */
    T update(String id, T entity) throws Exception;

    /**
     * Deletes an entity by its ID.
     *
     * @param id ID of the entity to delete
     * @return True if deletion was successful, false otherwise
     * @throws Exception if deletion fails
     */
    boolean delete(String id) throws Exception;

}


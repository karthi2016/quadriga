package edu.asu.spring.quadriga.qstore.async;

import edu.asu.spring.quadriga.exceptions.QStoreStorageException;

public interface IQStoreAsyncTaskManager {

    /**
     * This method will start executing the query to load public networks
     * asynchronously
     * 
     * @return
     * @throws QStoreStorageException
     */
    String startLoadingPublicNetworks() throws QStoreStorageException;

    /**
     * This method will return all the public networks that are connected to
     * popular terms
     * 
     * @return
     * @throws QStoreStorageException
     */
    String getNetworkWithPopularTerms() throws QStoreStorageException;

    /**
     * This method tells if the asynchronous query has loaded the public
     * networks or not
     * 
     * @return
     */
    boolean getPublicNetworkStatus();

}

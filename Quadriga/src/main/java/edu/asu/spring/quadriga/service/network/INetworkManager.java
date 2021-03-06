package edu.asu.spring.quadriga.service.network;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import edu.asu.spring.quadriga.domain.IUser;
import edu.asu.spring.quadriga.domain.enums.ETextAccessibility;
import edu.asu.spring.quadriga.domain.network.INetwork;
import edu.asu.spring.quadriga.domain.network.INetworkNodeInfo;
import edu.asu.spring.quadriga.domain.network.impl.CreationEvent;
import edu.asu.spring.quadriga.domain.network.impl.ElementEventsType;
import edu.asu.spring.quadriga.domain.network.impl.RelationEventType;
import edu.asu.spring.quadriga.domain.workbench.IProject;
import edu.asu.spring.quadriga.domain.workspace.IWorkspace;
import edu.asu.spring.quadriga.exceptions.QStoreStorageException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.network.domain.impl.TextOccurance;
import edu.asu.spring.quadriga.web.network.INetworkStatus;

/**
 * This interface would have all the methods required to work on storing,
 * displaying or manipulating {@link INetwork}
 * 
 * @author Lohith Dwaraka
 *
 */
public interface INetworkManager {

    // Constants to mention type of event in the QStore XML
    public static String RELATIONEVENT = "RE";
    public static String APPELLATIONEVENT = "AE";

    // Latest Version number for Network
    public static int VERSION_ZERO = 0;

    // Top node in Network
    public static String TOPNODE = "1";

    // Non Top node in Network
    public static String NONTOPNODE = "0";

    // DSpace error
    public static String DSPACEERROR = "DSPACEERROR";

    // New network
    public static String NEWNETWORK = "NEW";
    public static String UPDATENETWORK = "UPDATE";

    /**
     * Get {@link INetwork} object for a given networkId and {@link IUser}. We
     * could access workspace, project details, network status and other
     * information through this method param networkID {@link INetwork} ID of
     * type {@link String} parameter
     * 
     * @return {@link INetwork} object is returned
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    public abstract INetwork getNetwork(String networkId) throws QuadrigaStorageException;

    /**
     * Get a {@link List} of {@link INetwork} objects of a given {@link IUser}.
     * We could use this method incase we need to show {@link List} of
     * {@link INetwork} in GUI or any other manipulation.
     * 
     * @param user
     *            {@link IUser} object
     * @return {@link List} of {@link INetwork} objects
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    public abstract List<INetwork> getNetworkList(IUser user) throws QuadrigaStorageException;

    /**
     * Get all approved {@link INetwork} objects
     * 
     * @return
     * @throws QuadrigaStorageException
     */
    public abstract List<INetwork> getApprovedNetworkList() throws QuadrigaStorageException;

    /**
     * Get only the Top Nodes of the {@link INetworkNodeInfo}. In a XML received
     * from QStore we could have a list of Appellation Events and Relation
     * Events. Relation Events would recursively have Relation Events as a part
     * of Subject and Object of the part of relation. We would store only the
     * top Relation Event ID, as we have facilities from QStore to fetch the XML
     * of Relation Events. We have isTop field in network statement table to
     * mention whether a statement is top or not.
     * 
     * @param networkId
     *            {@link INetwork} ID of type {@link String}
     * @return Returns {@link List} of {@link INetworkNodeInfo}
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    public abstract List<INetworkNodeInfo> getNetworkTopNodes(String networkId) throws QuadrigaStorageException;

    /**
     * This method should help in getting the network's previous version network
     * statements. We could use this to view different versions of the networks.
     * 
     * @param networkId
     *            {@link INetwork} ID of type {@link String}
     * @param versionNo
     *            Version number of network.
     * @return Returns the {@link List} of {@link INetworkNodeInfo}
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    public abstract List<INetworkNodeInfo> getNetworkTopNodesByVersion(String networkId, int versionNo)
            throws QuadrigaStorageException;

    /**
     * This method should help in getting {@link List} of {@link INetwork} in
     * the {@link IProject}. We could use this to list Networks belonging to a
     * {@link IProject}.
     * 
     * @param projectid {@link IProject} ID of type {@link String}
     * @param status Status of requested networks, use {@link INetworkStatus}
     * 
     * @return Returns {@link List} of {@link INetwork}
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    public abstract List<INetwork> getNetworksInProject(String projectid, String status)
            throws QuadrigaStorageException;

    /**
     * This method should help in renaming the {@link INetwork}. We could use
     * this while we have a network been rejected and we add a new
     * {@link INetwork} based on editor annotation. We could prefer to change
     * the name of the {@link INetwork} while reuploading the {@link INetwork}
     * through clients.
     * 
     * @param networkId
     *            {@link INetwork} ID of type {@link String}
     * @param networkName
     *            New {@link INetwork} name of type {@link String}
     * @return Returns success or error message in form of {@link String}.
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    public abstract String updateNetworkName(String networkId, String networkName) throws QuadrigaStorageException;

    /**
     * This method should help in getting the {@link ElementEventsType} object
     * using a {@link RelationEventType} ID. Usually the source of the data for
     * {@link RelationEventType} is QStore, We could get the XML from QStore and
     * Marshall it into a {@link ElementEventsType} object.
     * 
     * @param relationEventId
     *            {@link RelationEventType} ID in form of {@link String}
     * @return Returns the {@link ElementEventsType} object for a particular
     *         {@link RelationEventType} ID
     * @throws JAXBException
     *             Throws JAXB exception in case we have issues while
     *             unmarshalling.
     * @throws QStoreStorageException
     *             Database storage exception thrown
     */
    public abstract ElementEventsType getElementEventTypeFromCreationEventTypeID(String relationEventId)
            throws JAXBException, QStoreStorageException;

    /**
     * This method should help to store the Network XML from clients into QStore
     * We should store all the nodes ID ( RelationEvent ID, Appellation ID ) in
     * the network XML. Also check if the text files mentioned in the XML is
     * present in the workspace the networks is added to.
     * 
     * @param xml
     *            Network XML of type {@link String}
     * @param user
     *            {@link IUser} object
     * @param networkName
     *            {@link INetwork} name of type {@link String}
     * @param workspaceId
     *            {@link IWorkspace} ID of type {@link String}
     * @param uploadStatus
     *            Upload status of the network - NEW or UPDATE
     * @param networkId
     *            {@link INetwork} ID of type {@link String}
     * @return Returns the Network ID or Error message
     * @throws JAXBException
     *             JAXB exception for any XML to object unmarshalling.
     */
    public abstract String storeNetworkDetails(String xml, IUser user, String networkName, String workspaceId,
            String uploadStatus, String networkId, int version, String networkStatus, String externalUserId)
                    throws JAXBException;

    /**
     * This method should help in getting all the version of a {@link INetwork}
     * 
     * @author Sayalee
     * @param networkid
     *            {@link INetwork} ID of type String
     * @return Returns a {@link List} of {@link INetwork}
     * @throws QuadrigaStorageException
     *             Throws a Storage exception if there is any issue accessing
     *             Database or table content.
     */
    public abstract List<INetwork> getAllNetworkVersions(String networkid) throws QuadrigaStorageException;

    /**
     * This method should help in getting Xml of the Network from Qstore or any
     * source using the list of statements belonging to the Network.
     * 
     * @param networkId
     *            {@link INetwork} ID of type String
     * @return Returns XML of the Network
     * @throws Exception
     *             Exception in parsing the xml or rendering the request to the
     *             source of network xml.
     */
    public abstract String getNetworkXML(String networkId) throws Exception;

    /**
     * This method should get the latest version number of the network
     * 
     * @param networkID
     *            {@link INetwork} ID of type String
     * @return Returns the verison number of the {@link INetwork}
     * @throws QuadrigaStorageException
     *             Throws a Storage exception if there is any issue accessing
     *             Database or table content.
     */
    public abstract int getLatestVersionOfNetwork(String networkID) throws QuadrigaStorageException;

    /**
     * This method should get {@link List} of {@link INetwork} object belonging
     * to an owner {@link IUser}
     * 
     * @param user
     *            {@link IUser} object
     * @return returns the {@link List} of {@link INetwork} of the {@link IUser}
     * @throws QuadrigaStorageException
     *             Throws a Storage exception if there is any issue accessing
     *             Database or table content.
     */
    public abstract List<INetwork> getNetworksOfOwner(IUser user) throws QuadrigaStorageException;

    /**
     * This method should get the network status code for any status of the
     * INetwork. Network code for the different status of the {@link INetwork}
     * would be found in {@link INetworkStatus} PENDING = 0; ASSIGNED = 1;
     * APPROVED= 2; REJECTED = 3; UNKNOWN = -1;
     * 
     * @param status
     *            Status of the network, can take PENDING, ASSIGNED, APPROVED,
     *            REJECTED
     * @return Returns the network status code.
     */
    public abstract int getNetworkStatusCode(String status);

    public List<INetwork> editNetworkStatusCode(List<INetwork> networkList);

    public String storeNetworks(String xml) throws QStoreStorageException;

    Set<TextOccurance> getTextsForConceptId(String conceptId, ETextAccessibility access) throws Exception;

    /**
     * Get Top Level Creation Events
     * 
     * @param xml
     * @param topNodeIDStream
     * @return
     * @throws JAXBException
     */
    List<CreationEvent> getTopElementEvents(String xml, Stream<String> topNodeIDStream) throws JAXBException;

}
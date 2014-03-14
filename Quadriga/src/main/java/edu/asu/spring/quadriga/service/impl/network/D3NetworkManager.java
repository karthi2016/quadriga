package edu.asu.spring.quadriga.service.impl.network;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.spring.quadriga.domain.INetworkNodeInfo;
import edu.asu.spring.quadriga.domain.impl.networks.AppellationEventType;
import edu.asu.spring.quadriga.domain.impl.networks.CreationEvent;
import edu.asu.spring.quadriga.domain.impl.networks.ElementEventsType;
import edu.asu.spring.quadriga.domain.impl.networks.PredicateType;
import edu.asu.spring.quadriga.domain.impl.networks.RelationEventType;
import edu.asu.spring.quadriga.domain.impl.networks.RelationType;
import edu.asu.spring.quadriga.domain.impl.networks.SubjectObjectType;
import edu.asu.spring.quadriga.domain.impl.networks.TermType;
import edu.asu.spring.quadriga.domain.impl.networks.jsonobject.AppellationEventObject;
import edu.asu.spring.quadriga.domain.impl.networks.jsonobject.JsonObject;
import edu.asu.spring.quadriga.domain.impl.networks.jsonobject.NodeObject;
import edu.asu.spring.quadriga.domain.impl.networks.jsonobject.ObjectTypeObject;
import edu.asu.spring.quadriga.domain.impl.networks.jsonobject.PredicateObject;
import edu.asu.spring.quadriga.domain.impl.networks.jsonobject.RelationEventObject;
import edu.asu.spring.quadriga.domain.impl.networks.jsonobject.SubjectObject;
import edu.asu.spring.quadriga.exceptions.QStoreStorageException;
import edu.asu.spring.quadriga.service.INetworkManager;
import edu.asu.spring.quadriga.service.conceptcollection.IConceptCollectionManager;

@Service
public class D3NetworkManager {

	@Autowired
	private INetworkManager networkManager;

	@Autowired
	IConceptCollectionManager conceptCollectionManager;

	private static final Logger logger = LoggerFactory
			.getLogger(D3NetworkManager.class);




	public String generateJSonForD3Jquery(List<INetworkNodeInfo> networkTopNodesList){



		return null;
	}

	public String parseNetwork(List<INetworkNodeInfo> networkTopNodesList){
		Iterator <INetworkNodeInfo> topNodeIterator = networkTopNodesList.iterator();
		List<List<Object>> relationEventPredicateMapping = new ArrayList<List<Object>>();
		while(topNodeIterator.hasNext()){
			INetworkNodeInfo networkNodeInfo = topNodeIterator.next();
			logger.debug("Node id "+networkNodeInfo.getId());
			logger.debug("Node statement type "+networkNodeInfo.getStatementType());
			if(networkNodeInfo.getStatementType().equals("RE")){
				try{
					String statementId = networkNodeInfo.getId();
					parseEachStatement(networkNodeInfo.getId(), networkNodeInfo.getStatementType(),statementId,relationEventPredicateMapping);
				}catch(QStoreStorageException e){
					logger.error("QStore retrieve error",e);
				}catch(JAXBException e){
					logger.error("Issue while parsing the JAXB object",e);
				}
			}
		}

		return null;
	}

	public String parseEachStatement(String relationEventId,String statementType, String statementId, List<List<Object>> relationEventPredicateMapping) throws JAXBException, QStoreStorageException{
		ElementEventsType elementEventType =getElementEventTypeFromRelationEvent(relationEventId);
		List<CreationEvent> creationEventList =elementEventType.getRelationEventOrAppellationEvent();
		Iterator <CreationEvent> creationEventIterator= creationEventList.iterator();
		while(creationEventIterator.hasNext()){
			CreationEvent creationEvent = creationEventIterator.next();
			// Check if event is Appellation event
			if(creationEvent instanceof AppellationEventType)
			{
				// Do nothing, we no need to display appellation events on UI.
			}
			// Check if event is Relation event
			if(creationEvent instanceof RelationEventType){
				// Trying to get a list of objects in the relations event type object
				// First get PredicateType
				// Then go recursively to subject and object
				JsonObject jsonObject = new JsonObject();
				RelationEventType relationEventType = (RelationEventType) creationEvent;
				jsonObject.setIsRelationEventObject(true);
				jsonObject.setRelationEventObject(parseThroughRelationEvent(relationEventType,new RelationEventObject(),relationEventPredicateMapping));
				
				List<D3NetworkManager.NodeObjectWithStatement> nodeObjectWithStatementList = new ArrayList<D3NetworkManager.NodeObjectWithStatement>();

				// This would help us in forming the json string as per requirement.
				nodeObjectWithStatementList = prepareNodeObjectContent(jsonObject.getRelationEventObject(),nodeObjectWithStatementList,statementId);

			}
		}
		return null;
	}


	public ElementEventsType getElementEventTypeFromRelationEvent(String relationEventId) throws JAXBException, QStoreStorageException{
		String xml = networkManager.getNodeXmlStringFromQstore(relationEventId);
		ElementEventsType elementEventType = null;
		if(xml ==null){
			throw new QStoreStorageException("Some issue retriving data from Qstore, Please check the logs related to Qstore");
		}else{

			// Initialize ElementEventsType object for relation event
			elementEventType = unMarshalXmlToElementEventsType(xml);
		}	
		return elementEventType;
	}

	public ElementEventsType unMarshalXmlToElementEventsType(String xml) throws JAXBException{
		ElementEventsType elementEventType = null;

		// Try to unmarshall the XML got from QStore to an ElementEventsType object
		JAXBContext context = JAXBContext.newInstance(ElementEventsType.class);
		Unmarshaller unmarshaller1 = context.createUnmarshaller();
		unmarshaller1.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		InputStream is = new ByteArrayInputStream(xml.getBytes());
		JAXBElement<ElementEventsType> response1 =  unmarshaller1.unmarshal(new StreamSource(is), ElementEventsType.class);
		elementEventType = response1.getValue();

		return elementEventType;
	}

	public RelationEventObject parseThroughRelationEvent(RelationEventType relationEventType,RelationEventObject relationEventObject,List<List<Object>> relationEventPredicateMapping){

		// Get RelationType of the RelationEventType
		RelationType relationType = relationEventType.getRelation(relationEventType);

		// Handle Predicate of the RelationType
		PredicateType predicateType = relationType.getPredicateType(relationType);
		relationEventObject.setPredicateObject(parseThroughPredicate(relationEventType, predicateType, relationEventPredicateMapping));
		
		// Handle Subject of the RelationType
		SubjectObjectType subjectType = relationType.getSubjectType(relationType);
		SubjectObject subjectObject = parseThroughSubject(relationEventType, subjectType, relationEventPredicateMapping);
		relationEventObject.setSubjectObject(subjectObject);		
		
		// Handle Object of the RelationType
		SubjectObjectType objectType = relationType.getObjectType(relationType);
		ObjectTypeObject objectTypeObject = parseThroughObject(relationEventType, objectType, relationEventPredicateMapping);
		relationEventObject.setObjectTypeObject(objectTypeObject);
		
		
		return relationEventObject;
	}

	
	public String stackRelationEventPredicateAppellationObject(String relationEventId,String predicateName,AppellationEventObject appellationEventObject,List<List<Object>> relationEventPredicateMapping){
		Iterator<List<Object>> relationEventPredicateMappingIterator = relationEventPredicateMapping.iterator();

		while(relationEventPredicateMappingIterator.hasNext()){
			List<Object> objectList = relationEventPredicateMappingIterator.next();
			Iterator<Object> objectIterator = objectList.iterator();
			while(objectIterator.hasNext()){
				Object object = objectIterator.next();
				if(object instanceof String[]){
					String pairs[] = (String [])object;
					if(pairs[0].equals(relationEventId)){
						String predicateNameLocal = pairs[1];
						return predicateNameLocal;
					}
				}
			}
		}
		String[] pairs = new String [2];
		pairs[0]=(relationEventId);
		pairs[1]=(predicateName);
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(pairs);
		objectList.add(appellationEventObject);
		relationEventPredicateMapping.add(objectList);

		return null;

	}
	
	public PredicateObject parseThroughPredicate(RelationEventType relationEventType, PredicateType predicateType,List<List<Object>> relationEventPredicateMapping){
		//	Predicate has only appellation event, so get appellation event inside the predicate
		AppellationEventType appellationEvent = predicateType.getAppellationEvent();
		PredicateObject predicateObject = null;
		List<TermType> termTypeList= appellationEvent.getTerms(appellationEvent);
		Iterator <TermType> termTypeIterator = termTypeList.iterator();
		while(termTypeIterator.hasNext()){
			TermType tt = termTypeIterator.next();
			AppellationEventObject appellationEventObject = new AppellationEventObject();
			appellationEventObject.setNode(conceptCollectionManager.getConceptLemmaFromConceptId(tt.getTermInterpertation(tt))+"_"+networkManager.shortUUID());
			appellationEventObject.setTermId(tt.getTermID(tt)+"_"+networkManager.shortUUID());
			predicateObject = new PredicateObject();
			predicateObject.setAppellationEventObject(appellationEventObject);

			predicateObject.setRelationEventID(relationEventType.getRelationEventId(relationEventType));
			stackRelationEventPredicateAppellationObject(relationEventType.getRelationEventId(relationEventType),predicateObject.getAppellationEventObject().getNode(),appellationEventObject, relationEventPredicateMapping);
			return predicateObject;
		}
		return predicateObject;
	}
	
	
	/**
	 * Get Appellation associated to relation event
	 * @param relationEventId
	 * @return
	 */
	public AppellationEventObject checkRelationEventInStack(String relationEventId,List<List<Object>> relationEventPredicateMapping){

		Iterator<List<Object>> I = relationEventPredicateMapping.iterator();
		int flag=0;
		AppellationEventObject appellationEventObject=null;
		while(I.hasNext()){
			List<Object> objectList = I.next();
			Iterator<Object> I1 = objectList.iterator();

			while(I1.hasNext()){
				Object object = I1.next();

				if(object instanceof String[]){
					String pairs[] = (String [])object;
					if(pairs[0].equals(relationEventId)){
						String predicateNameLocal = pairs[1];
						logger.debug(" relationEventId  :" +relationEventId +" id : "+pairs[0]+"predicate Name"+predicateNameLocal );
						flag=1;
					}
				}
				if(object instanceof AppellationEventObject){
					appellationEventObject=(AppellationEventObject)object;
				}
			}
			if(flag==1){
				return appellationEventObject;
			}
		}

		return null;
	}
	
	public SubjectObject parseThroughSubject(RelationEventType relationEventType, SubjectObjectType subjectObjectType,List<List<Object>> relationEventPredicateMapping){

		//	Check for relation event inside subject and add if any
		RelationEventType subjectRelationEventType = subjectObjectType.getRelationEvent();

		SubjectObject subjectObject = new SubjectObject();

		if(subjectRelationEventType == null){
			subjectObject.setIsRelationEventObject(false);
		}else{
			AppellationEventObject temp = checkRelationEventInStack(subjectRelationEventType.getRelationEventId(subjectRelationEventType),relationEventPredicateMapping);
			/*
			 * I am trying to fool subject as Appellation event
			 * when we find a existing relation event been referred here
			 * I will give appellation event with predicate of referred relation event
			 */
			if(!(temp == null)){
				subjectObject.setIsRelationEventObject(false);
				subjectObject.setAppellationEventObject(temp);
			}else{
				subjectObject.setIsRelationEventObject(true);
				RelationEventObject relationEventObject   = new RelationEventObject();
				checkRelationEventInStack(subjectRelationEventType.getRelationEventId(subjectRelationEventType),relationEventPredicateMapping);
				relationEventObject=parseThroughRelationEvent(subjectRelationEventType,relationEventObject,relationEventPredicateMapping);
				subjectObject.setRelationEventObject(relationEventObject);
			}
		}
		//	Check for Appellation event inside subject and add if any
		AppellationEventType appellationEventType = subjectObjectType.getAppellationEvent();
		if(appellationEventType == null){
			
		}else{
			List<TermType> termTypeList= appellationEventType.getTerms(appellationEventType);
			Iterator <TermType> termTypeIterator = termTypeList.iterator();
			while(termTypeIterator.hasNext()){
				TermType tt = termTypeIterator.next();
				AppellationEventObject appellationEventObject = new AppellationEventObject();
				appellationEventObject.setNode(conceptCollectionManager.getConceptLemmaFromConceptId(tt.getTermInterpertation(tt)));
				appellationEventObject.setTermId(tt.getTermID(tt)+"_"+networkManager.shortUUID());
				subjectObject.setAppellationEventObject(appellationEventObject);
				logger.debug("subjectType Term : "+tt.getTermInterpertation(tt));
			}
		}
		return subjectObject;
		
	}
	
	
	public ObjectTypeObject parseThroughObject(RelationEventType relationEventType, SubjectObjectType subjectObjectType,List<List<Object>> relationEventPredicateMapping){

		//	Check for relation event inside subject and add if any
		RelationEventType objectRelationEventType = subjectObjectType.getRelationEvent();

		ObjectTypeObject objectTypeObject = new ObjectTypeObject();

		if(objectRelationEventType == null){
			objectTypeObject.setIsRelationEventObject(false);
		}else{
			AppellationEventObject temp = checkRelationEventInStack(objectRelationEventType.getRelationEventId(objectRelationEventType),relationEventPredicateMapping);
			/*
			 * I am trying to fool subject as Appellation event
			 * when we find a existing relation event been referred here
			 * I will give appellation event with predicate of referred relation event
			 */
			if(!(temp == null)){
				objectTypeObject.setIsRelationEventObject(false);
				objectTypeObject.setAppellationEventObject(temp);
			}else{
				objectTypeObject.setIsRelationEventObject(true);
				RelationEventObject relationEventObject   = new RelationEventObject();
				checkRelationEventInStack(objectRelationEventType.getRelationEventId(objectRelationEventType),relationEventPredicateMapping);
				relationEventObject=parseThroughRelationEvent(objectRelationEventType,relationEventObject,relationEventPredicateMapping);
				objectTypeObject.setRelationEventObject(relationEventObject);
			}
		}
		//	Check for Appellation event inside subject and add if any
		AppellationEventType appellationEventType = subjectObjectType.getAppellationEvent();
		if(appellationEventType == null){
			
		}else{
			List<TermType> termTypeList= appellationEventType.getTerms(appellationEventType);
			Iterator <TermType> termTypeIterator = termTypeList.iterator();
			while(termTypeIterator.hasNext()){
				TermType tt = termTypeIterator.next();
				AppellationEventObject appellationEventObject = new AppellationEventObject();
				appellationEventObject.setNode(conceptCollectionManager.getConceptLemmaFromConceptId(tt.getTermInterpertation(tt)));
				appellationEventObject.setTermId(tt.getTermID(tt)+"_"+networkManager.shortUUID());
				objectTypeObject.setAppellationEventObject(appellationEventObject);
				logger.debug("subjectType Term : "+tt.getTermInterpertation(tt));
			}
		}
		return objectTypeObject;
		
	}
	
	
	/**
	 * Use temp structure to allow our json builder work easily.
	 * {@link NodeObject} is used to build this temp structure. {@link NodeObject} has a predicate, subject object structure in it.
	 * I am recursively filling the {@link NodeObject}.
	 * @param relationEventObject
	 */
	public List<D3NetworkManager.NodeObjectWithStatement> prepareNodeObjectContent(RelationEventObject relationEventObject,List<D3NetworkManager.NodeObjectWithStatement> nodeObjectWithStatementList, String statementId){
		
		// Get predicate Object structure
		PredicateObject predicateObject = relationEventObject.getPredicateObject();
		NodeObject nodeObject = getPredicateNodeObjectContent(predicateObject,new NodeObject());


		// Get Subject Object into temp structure 
		SubjectObject subjectObject = relationEventObject.getSubjectObject();
		ObjectTypeObject objectTypeObject = relationEventObject.getObjectTypeObject();
		if(subjectObject.getIsRelationEventObject()){
			nodeObject.setSubject(subjectObject.getSubjectRelationPredictionAppellation(subjectObject));
			nodeObject.setSubjectId(subjectObject.getSubjectRelationPredictionAppellationTermId(subjectObject));
			logger.debug("Subject Predicate node : "+subjectObject.getSubjectRelationPredictionAppellation(subjectObject));
			// Get Object into temp structure 
			if(objectTypeObject.getIsRelationEventObject()){
				nodeObject.setObject(objectTypeObject.getObjectRelationPredictionAppellation(objectTypeObject));
				nodeObject.setObjectId(objectTypeObject.getObjectRelationPredictionAppellationTermId(objectTypeObject));
				nodeObjectWithStatementList.add(new NodeObjectWithStatement(nodeObject,statementId));
				logger.debug("Object Predicate node : "+objectTypeObject.getObjectRelationPredictionAppellation(objectTypeObject));
			}else{

				AppellationEventObject appellationEventObject1 = objectTypeObject.getAppellationEventObject();
				nodeObject.setObject(appellationEventObject1.getNode());
				nodeObject.setObjectId(appellationEventObject1.getTermId());
				nodeObjectWithStatementList.add(new NodeObjectWithStatement(nodeObject,statementId));
				logger.debug("Object Predicate : "+appellationEventObject1.getNode() );
			}
			nodeObjectWithStatementList = prepareNodeObjectContent(subjectObject.getRelationEventObject(),nodeObjectWithStatementList,statementId);
		}else{

			AppellationEventObject appellationEventObject1 = subjectObject.getAppellationEventObject();
			nodeObject.setSubject(appellationEventObject1.getNode());
			nodeObject.setSubjectId(appellationEventObject1.getTermId());
			logger.debug("Subject Predicate : "+appellationEventObject1.getNode() );
		}
		// Get Object into temp structure 
		if(objectTypeObject.getIsRelationEventObject()){
			nodeObjectWithStatementList = prepareNodeObjectContent(objectTypeObject.getRelationEventObject(),nodeObjectWithStatementList,statementId);
		}else{
			AppellationEventObject appellationEventObject1 = objectTypeObject.getAppellationEventObject();
			nodeObject.setObject(appellationEventObject1.getNode());
			nodeObject.setObjectId(appellationEventObject1.getTermId());
			nodeObjectWithStatementList.add(new NodeObjectWithStatement(nodeObject,statementId));
			logger.debug("Object Predicate : "+appellationEventObject1.getNode() );
		}
		return nodeObjectWithStatementList;
	}
	
	
	
	/**
	 * prepare Predicate Object content for 
	 * @param predicateObject
	 * @param nodeObject
	 * @return
	 */
	public NodeObject getPredicateNodeObjectContent (PredicateObject predicateObject, NodeObject nodeObject){
		AppellationEventObject appellationEventObject = predicateObject.getAppellationEventObject();
		// Store predicate detail in our temporary structure
		nodeObject.setRelationEventId(predicateObject.getRelationEventID());

		nodeObject.setPredicate(appellationEventObject.getNode());
		nodeObject.setPredicateId(appellationEventObject.getTermId());
		logger.debug("Predicate : "+appellationEventObject.getNode() );
		
		return nodeObject;
	}

	/**
	 * Method to add nodes and links into List, which can be used to make a JSON object later.
	 * @param nodeObject
	 */
//	public void updateNodeLinkForD3JSON(NodeObject nodeObject){
//
//		String predicateNameId = nodeObject.getPredicate();
//
//		String subjectNodeId=nodeObject.getSubject();
//		String objectNodeId = nodeObject.getObject();
//		String stmtId = statementId;
//
//
//		// Check for reference to relation
//		String temp=checkRelationEventRepeatation(nodeObject.getRelationEventId(),nodeObject.getPredicate());
//		String predicateName = predicateNameId.substring(0,predicateNameId.lastIndexOf('_'));
//		if(!(temp.equals(""))){
//			predicateNameId = temp;
//		}
//
//		// Add Nodes for D3 JSon
//		{
//			// Adding Subject into node list 
//			if(!d3NodeIdMap.containsKey(subjectNodeId)){
//				ID3Node d3NodeSubject = d3NodeFactory.createD3NodeObject();
//				//List<String> stmtList = d3NodeSubject.getStatementIdList();
//				//stmtList.add(stmtId);
//				//d3NodeSubject.setStatementIdList(stmtList);
//				d3NodeSubject.setNodeName(nodeObject.getSubject());
//				d3NodeSubject.setNodeId(subjectNodeId);
//				d3NodeSubject.setGroupId(ID3Constant.RELATION_EVENT_SUBJECT_TERM);
//				d3NodeList.add(d3NodeSubject);
//				d3NodeIdMap.put(subjectNodeId,nodeIndex);
//				nodeIndex++;
//			}
//
//			// Adding Object into node list
//			if(!d3NodeIdMap.containsKey(objectNodeId)){
//				ID3Node d3NodeObject = d3NodeFactory.createD3NodeObject();
//				//List<String> stmtList = d3NodeObject.getStatementIdList();
//				//stmtList.add(stmtId);
//				//d3NodeObject.setStatementIdList(stmtList);
//				d3NodeObject.setNodeName(nodeObject.getObject());
//				d3NodeObject.setNodeId(objectNodeId);
//				d3NodeObject.setGroupId(ID3Constant.RELATION_EVENT_OBJECT_TERM);
//				d3NodeList.add(d3NodeObject);
//				d3NodeIdMap.put(objectNodeId,nodeIndex);
//				nodeIndex++;
//			}
//
//			// Adding Subject into node list 
//
//			if(!d3NodeIdMap.containsKey(predicateNameId)){
//				ID3Node d3NodePredicate = d3NodeFactory.createD3NodeObject();
//				List<String> stmtList = d3NodePredicate.getStatementIdList();
//				stmtList.add(stmtId);
//				d3NodePredicate.setStatementIdList(stmtList);
//				d3NodePredicate.setNodeName(predicateName);
//				d3NodePredicate.setNodeId(predicateNameId);
//				d3NodePredicate.setGroupId(ID3Constant.RELATION_EVENT_PREDICATE_TERM);
//				d3NodeList.add(d3NodePredicate);
//				d3NodeIdMap.put(predicateNameId,nodeIndex);
//				nodeIndex++;
//			}else{
//				int index= d3NodeIdMap.get(predicateNameId);
//				ID3Node d3NodePredicate = d3NodeList.get(index);
//				List<String> stmtList = d3NodePredicate.getStatementIdList();
//				boolean flag=false;
//				for(String stmt: stmtList){
//					if(stmt.equals(stmtId)){
//						flag=true;
//					}
//				}
//				if(!flag){
//					stmtList.add(stmtId);
//				}
//			}
//
//		}
//
//		// Add Links for D3 JSon
//		{
//
//			ID3Link d3LinkSubject = d3LinkFactory.createD3LinkObject();
//			d3LinkSubject.setSource(d3NodeIdMap.get(predicateNameId));
//			d3LinkSubject.setTarget(d3NodeIdMap.get(subjectNodeId));
//			d3LinkList.add(d3LinkSubject);
//
//			ID3Link d3LinkObject = d3LinkFactory.createD3LinkObject();
//			d3LinkObject.setSource(d3NodeIdMap.get(predicateNameId));
//			d3LinkObject.setTarget(d3NodeIdMap.get(objectNodeId));
//			d3LinkList.add(d3LinkObject);
//		}
//
//	}
	
	/**
	 * Inner class to store {@link NodeObject} with statement ID it belongs to.
	 * @author Lohith Dwaraka
	 *
	 */
	class NodeObjectWithStatement{
		private NodeObject nodeObject;
		private String statementId;
		
		public NodeObjectWithStatement(NodeObject nodeObject,String statementId) {
			this.nodeObject = nodeObject;
			this.statementId = statementId;
		}
		
		public NodeObject getNodeObject() {
			return nodeObject;
		}
		public void setNodeObject(NodeObject nodeObject) {
			this.nodeObject = nodeObject;
		}
		public String getStatementId() {
			return statementId;
		}
		public void setStatementId(String statementId) {
			this.statementId = statementId;
		}		
	}
}
package edu.asu.spring.quadriga.service.network.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import edu.asu.spring.quadriga.domain.impl.networks.AppellationEventType;
import edu.asu.spring.quadriga.domain.impl.networks.CreationEvent;
import edu.asu.spring.quadriga.domain.impl.networks.ElementEventsType;
import edu.asu.spring.quadriga.domain.impl.networks.PredicateType;
import edu.asu.spring.quadriga.domain.impl.networks.RelationEventType;
import edu.asu.spring.quadriga.domain.impl.networks.RelationType;
import edu.asu.spring.quadriga.domain.impl.networks.TermType;
import edu.asu.spring.quadriga.exceptions.QStoreStorageException;
import edu.asu.spring.quadriga.qstore.IMarshallingService;
import edu.asu.spring.quadriga.qstore.IQStoreConnector;
import edu.asu.spring.quadriga.service.conceptcollection.IConceptCollectionManager;
import edu.asu.spring.quadriga.transform.Link;
import edu.asu.spring.quadriga.transform.Node;
import edu.asu.spring.quadriga.transform.PredicateNode;

/**
 * Class for parsing Appellation/Relation Event networks into S-O-P networks.
 * @author jdamerow
 *
 */
@PropertySource(value = "classpath:/user.properties")
@Service
public class EventParser {
    
    private static final Logger logger = LoggerFactory.getLogger(EventParser.class);
    
    @Autowired
    private IQStoreConnector qstoreConnector;

    @Autowired
    @Qualifier("jaxbMarshaller")
    private Jaxb2Marshaller jaxbMarshaller;
    
    @Autowired
    private Environment env;
    
    @Autowired
    private IConceptCollectionManager conceptCollectionManager;
    
    @Autowired
    private IMarshallingService marshallingService;

    
    public void parseStatement(String relationEventId,
                               ElementEventsType elementEventType,
                               Map<String, Node> nodes,
                               List<Link> links) {
        List<CreationEvent> creationEventList = elementEventType.getRelationEventOrAppellationEvent();
        Iterator<CreationEvent> creationEventIterator = creationEventList.iterator();
         
        while (creationEventIterator.hasNext()) {
            CreationEvent event = creationEventIterator.next();
            parseSubjectOrObjectEvent(event, relationEventId, nodes, links);
        }
        
    }
    
    private Node parseSubjectOrObjectEvent(CreationEvent event, String statementId, Map<String, Node> leafNodes, List<Link> links) {
        if (event == null) {
            return null;
        }
        
        if (event instanceof AppellationEventType) {
           List<TermType> terms = ((AppellationEventType) event).getTerms();
           if (terms.size() > 0) {
               String conceptId = terms.get(0).getTermID();
               if (leafNodes.containsKey(conceptId)) {
                   leafNodes.get(conceptId).getStatementIds().add(statementId);
               }
               else {
                   Node node = new Node();
                   parseNode((AppellationEventType) event, node, statementId);
                   leafNodes.put(conceptId, node);
               }
               return leafNodes.get(conceptId);
           }
           return null;
        }
        else if (event instanceof RelationEventType) {
            RelationType relation = ((RelationEventType) event).getRelation();

            // create node for predicate
            PredicateType pred = relation.getPredicateType();
            PredicateNode predNode = parsePredicateEvent(pred.getAppellationEvent(), statementId);
            leafNodes.put(predNode.getId(), predNode);
            
            Node subjectNode = parseSubjectOrObjectEvent(relation.getSubjectType().getAppellationEvent(), statementId, leafNodes, links);
            if (subjectNode == null) {
                subjectNode = parseSubjectOrObjectEvent(relation.getSubjectType().getRelationEvent(), statementId, leafNodes, links);
            }
            
            Node objectNode = parseSubjectOrObjectEvent(relation.getObjectType(relation).getAppellationEvent(), statementId, leafNodes, links);
            if (objectNode == null) {
                objectNode = parseSubjectOrObjectEvent(relation.getObjectType(relation).getRelationEvent(), statementId, leafNodes, links);
            }

            // source reference from relation type
            String sourceReference = relation.getSourceReference();

            if (subjectNode != null) {
                Link link = new Link();
                // add the statement id to the link
                link.setStatementId(statementId);
                link.setSubject(predNode);
                link.setObject(subjectNode);
                link.setLabel("has subject");
                links.add(link);
                // set the source reference to the link
                link.setSourceReference(sourceReference);
            }
            
            if (objectNode != null) {
                Link link = new Link();
                // add the statement id to the link
                link.setStatementId(statementId);
                link.setSubject(predNode);
                link.setObject(objectNode);
                link.setLabel("has object");
                links.add(link);
                // set the source reference to the link
                link.setSourceReference(sourceReference);
            }
            
            return predNode;
        }
        
        return null;
    }
    
    private PredicateNode parsePredicateEvent(AppellationEventType appellationEvent, String statementId) {
        PredicateNode predNode = new PredicateNode();
        
        parseNode(appellationEvent, predNode, statementId);
        
        predNode.setId(UUID.randomUUID().toString());
        
        return predNode;
    }
    
    private void parseNode(AppellationEventType event, Node node, String statementId) {
        StringBuffer label = new StringBuffer();
        for (TermType type : event.getTerms()) {
            label.append(type.getTermInterpertation());
            label.append(" ");
        }
        node.setId(event.getAppellationEventID());
        node.setConceptId(label.toString().trim());
        // set the source reference
        node.setSourceReference(event.getSourceReference());
        
        if (node.getConceptId() != null) {
            node.setLabel(conceptCollectionManager.getConceptLemmaFromConceptId(node.getConceptId()));
        }
        
        node.getStatementIds().add(statementId);
    }
    
}

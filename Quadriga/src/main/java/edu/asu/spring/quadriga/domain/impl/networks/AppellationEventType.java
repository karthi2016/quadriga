//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.08.01 at 10:33:36 AM MST 
//


package edu.asu.spring.quadriga.domain.impl.networks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for appellationEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="appellationEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://digitalhps.org/creationEvents-model}creationEvent">
 *       &lt;choice maxOccurs="2">
 *         &lt;element name="term" type="{http://digitalhps.org/creationEvents-model}termType"/>
 *         &lt;element name="external_refId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "appellationEventType", propOrder = {
    "termOrExternalRefId"
})
public class AppellationEventType
    extends CreationEvent {

    @XmlElements({
        @XmlElement(name = "term", type = TermType.class),
        @XmlElement(name = "external_refId", type = String.class)
    })
    protected List<Object> termOrExternalRefId;

    /**
     * Gets the value of the termOrExternalRefId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the termOrExternalRefId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTermOrExternalRefId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TermType }
     * {@link String }
     * 
     * 
     */
    public List<Object> getTermOrExternalRefId() {
        if (termOrExternalRefId == null) {
            termOrExternalRefId = new ArrayList<Object>();
        }
        return this.termOrExternalRefId;
    }
    
    /**
     * Returns the list of terms in an Appellation
     * @author Lohith Dwaraka
     * @param aet  : AppellationEventType
     * @return
     */
    public List<TermType> getTerms(){
    	List<Object> objectList = getTermOrExternalRefId();
    	List<TermType> termTypeList = new ArrayList<TermType>();
    	Iterator <Object> I3 = objectList.iterator();
		while(I3.hasNext()){
			Object o = I3.next();
			if(o instanceof TermType){
				
				TermType tt = (TermType) o;
				termTypeList.add(tt);
				
			}
		}    
		return termTypeList;
    }
    
    public String getAppellationEventID(){
		List<JAXBElement<?>> elementsList = this.getIdOrCreatorOrCreationDate();
		Iterator <JAXBElement<?>> elementsIterator = elementsList.iterator();
		while(elementsIterator.hasNext()){
			JAXBElement<?> element = (JAXBElement<?>) elementsIterator.next();
			if(element.getName().toString().contains("id")){
				return element.getValue().toString();
			}
		}
		return null;
    }
}

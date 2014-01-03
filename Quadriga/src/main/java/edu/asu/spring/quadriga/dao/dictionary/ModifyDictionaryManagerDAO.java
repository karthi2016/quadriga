package edu.asu.spring.quadriga.dao.dictionary;

import java.util.Date;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.asu.spring.quadriga.dao.DAOConnectionManager;
import edu.asu.spring.quadriga.db.dictionary.IDBConnectionModifyDictionaryManager;
import edu.asu.spring.quadriga.domain.IDictionary;
import edu.asu.spring.quadriga.dto.DictionaryCollaboratorDTO;
import edu.asu.spring.quadriga.dto.DictionaryCollaboratorDTOPK;
import edu.asu.spring.quadriga.dto.DictionaryDTO;
import edu.asu.spring.quadriga.dto.QuadrigaUserDTO;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;

@Repository
public class ModifyDictionaryManagerDAO extends DAOConnectionManager implements IDBConnectionModifyDictionaryManager
{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final Logger logger = LoggerFactory.getLogger(ModifyDictionaryManagerDAO.class);
	
	/**
	 * Update the dictionary details
	 * @param dictionary object and userName
	 * @return void
	 * @throws QuadrigaStorageException
	 */
	@Override
	public void updateDictionaryRequest(IDictionary dictionary, String userName) throws QuadrigaStorageException 
	{
		try
		{
			DictionaryDTO dictionaryDTO = (DictionaryDTO) sessionFactory.getCurrentSession().get(DictionaryDTO.class, dictionary.getId());
			dictionaryDTO.setDictionaryname(dictionary.getName());
			dictionaryDTO.setDescription(dictionary.getDescription());
			dictionaryDTO.setAccessibility(Boolean.FALSE);
			dictionaryDTO.setUpdatedby(userName);
			dictionaryDTO.setUpdateddate(new Date());
			sessionFactory.getCurrentSession().update(dictionaryDTO);
		}
		catch(Exception e)
		{
			logger.info("updateDictionaryRequest ::"+e.getMessage());	
			throw new QuadrigaStorageException(e);
		}
	}

	/**
	 * Transfer dictionary owner
	 * @param dictionaryId, old owner, new owner and collaborator role
	 * @return void
	 * @throws QuadrigaStorageException
	 */
	
	@Override
	public void transferDictionaryOwner(String dictionaryId, String oldOwner,String newOwner, String collabRole) throws QuadrigaStorageException {
		try
		{
			DictionaryDTO dictionaryDTO = (DictionaryDTO) sessionFactory.getCurrentSession().get(DictionaryDTO.class, dictionaryId);
			dictionaryDTO.setDictionaryowner(getUserDTO(newOwner));
			dictionaryDTO.setUpdatedby(oldOwner);
			dictionaryDTO.setUpdateddate(new Date());
			
			Iterator<DictionaryCollaboratorDTO> dictCollabItr = dictionaryDTO.getDictionaryCollaboratorDTOList().iterator();
			while(dictCollabItr.hasNext())
			{
				DictionaryCollaboratorDTO dictCollaboratorDTO = dictCollabItr.next();
				if(dictCollaboratorDTO.getQuadrigaUserDTO().getUsername().equals(newOwner))
				{
					dictCollabItr.remove();
				}
			}
			
			DictionaryCollaboratorDTO dictCollaboratorDTO = new DictionaryCollaboratorDTO();
			dictCollaboratorDTO.setDictionaryDTO(dictionaryDTO);
			dictCollaboratorDTO.setDictionaryCollaboratorDTOPK(new DictionaryCollaboratorDTOPK(dictionaryId,oldOwner,collabRole));
			dictCollaboratorDTO.setQuadrigaUserDTO(new QuadrigaUserDTO(oldOwner));
			dictCollaboratorDTO.setCreatedby(oldOwner);
			dictCollaboratorDTO.setCreateddate(new Date());
			dictCollaboratorDTO.setUpdatedby(oldOwner);
			dictCollaboratorDTO.setUpdateddate(new Date());
			dictionaryDTO.getDictionaryCollaboratorDTOList().add(dictCollaboratorDTO);
			
			sessionFactory.getCurrentSession().update(dictionaryDTO);
		}
		catch(HibernateException e)
		{
			logger.error("transferDictionaryOwner method :",e);
        	throw new QuadrigaStorageException();
		}
		
	}

}
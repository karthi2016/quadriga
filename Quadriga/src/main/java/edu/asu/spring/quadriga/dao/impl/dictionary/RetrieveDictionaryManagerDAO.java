package edu.asu.spring.quadriga.dao.impl.dictionary;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.asu.spring.quadriga.dao.dictionary.IDBConnectionRetrieveDictionaryManager;
import edu.asu.spring.quadriga.dao.impl.BaseDAO;
import edu.asu.spring.quadriga.domain.dictionary.IDictionary;
import edu.asu.spring.quadriga.dto.DictionaryDTO;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.mapper.DictionaryDTOMapper;
import edu.asu.spring.quadriga.service.dictionary.mapper.IDictionaryDeepMapper;

@Repository
public class RetrieveDictionaryManagerDAO extends BaseDAO implements IDBConnectionRetrieveDictionaryManager
{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private DictionaryDTOMapper dictionaryDTOMapper;
	
	@Autowired
	private IDictionaryDeepMapper dictionaryDeepMapper;
	
	private static final Logger logger = LoggerFactory.getLogger(RetrieveDictionaryManagerDAO.class);

	/**
	 * This method retrieves the dictionary details for the specified dictionaryid.
	 * @param dictionaryId
	 * @return IDictinary object
	 * @throws QuadrigaStorageException
	 */
	@Override
	public DictionaryDTO getDictionaryDetails(String dictionaryId) throws QuadrigaStorageException 
	{
		DictionaryDTO dictionaryDTO = null;
		try
		{
			dictionaryDTO = (DictionaryDTO) sessionFactory.getCurrentSession().get(DictionaryDTO.class, dictionaryId);
			//dictionary = dictionaryDTOMapper.getDictionary(dictionaryDTO);
		} 
		catch (HibernateException e) 
		{
			logger.error("getDictionaryDetails method :",e);
			throw new QuadrigaStorageException();
		}
		return dictionaryDTO;
	}
	

	@Override
	public DictionaryDTO getDictionaryDTO(String dictionaryId) throws QuadrigaStorageException 
	{
		DictionaryDTO dictionaryDTO = null;
		try
		{
			dictionaryDTO = (DictionaryDTO) sessionFactory.getCurrentSession().get(DictionaryDTO.class, dictionaryId);
		} 
		catch (HibernateException e) 
		{
			logger.error("getDictionaryDetails method :",e);
			throw new QuadrigaStorageException();
		}
		return dictionaryDTO;
	}
	
	@Override
	public DictionaryDTO getDictionaryDTO(String dictionaryId, String userName) throws QuadrigaStorageException 
	{
		DictionaryDTO dictionaryDTO = null;
		try
		{
			
			Query query = sessionFactory.getCurrentSession().createQuery("SELECT d FROM DictionaryDTO d WHERE d.dictionaryid = :dictionaryid and d.dictionaryowner.username =:username "); 
			query.setParameter("username", userName);
			query.setParameter("dictionaryid", dictionaryId);
			List<DictionaryDTO> dictionaryDTOList = query.list();
			
			if(dictionaryDTOList != null && dictionaryDTOList.size() > 0){
				dictionaryDTO = dictionaryDTOList.get(0);
			}
		} 
		catch (HibernateException e) 
		{
			logger.error("getDictionaryDetails method :",e);
			throw new QuadrigaStorageException();
		}
		return dictionaryDTO;
	}

	
	
	
}
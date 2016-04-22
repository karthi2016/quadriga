package edu.asu.spring.quadriga.dao.impl.transform;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.asu.spring.quadriga.dao.transform.ITransformFilesDAO;
import edu.asu.spring.quadriga.dao.impl.BaseDAO;
import edu.asu.spring.quadriga.dto.TransformFilesDTO;

/**
 * DAO class to  save or get data from tbl_transfomationfiles_metadata
 * 
 * @author JayaVenkat
 *
 */
@SuppressWarnings("rawtypes")
@Repository
public class TransformFilesDAO extends BaseDAO implements
		ITransformFilesDAO {

	/**
	 * This method will return list of DTO objects and each DTO object corresponds to on transformation
	 */
	@Override
	public List<TransformFilesDTO> getAllTransformations(){
		@SuppressWarnings("unchecked")
		List<TransformFilesDTO> transformList = sessionFactory.getCurrentSession().createCriteria(TransformFilesDTO.class).list();
		return transformList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getDTO(String id) {		
		return getDTO(TransformFilesDTO.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveTransformDTO(TransformFilesDTO tranformDTO) {
		saveNewDTO(tranformDTO);				
	}
		
}

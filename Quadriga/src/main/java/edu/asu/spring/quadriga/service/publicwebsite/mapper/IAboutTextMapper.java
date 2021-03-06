package edu.asu.spring.quadriga.service.publicwebsite.mapper;

import edu.asu.spring.quadriga.domain.settings.IAboutText;
import edu.asu.spring.quadriga.dto.AboutTextDTO;

/**
 * @author Nischal Samji
 * 
 *         Interface to implement IAboutText Mapper functions
 *
 */
public interface IAboutTextMapper {

    /**
     * This method takes in {@link IAboutText} as an argument and returns a
     * mapped {@link AboutTextDTO} Object.
     * 
     * @param abtDTO
     *            {@link AboutTextDTO} that needs to be mapped to a
     *            {@link IAboutText}
     * @return Returns a {@link IAboutText} Object that is mapped from a
     *         {@link AboutTextDTO}
     */
    public IAboutText aboutTextDTOtoBean(AboutTextDTO abtDTO);

    /**
     * This method takes in {@link AboutTextDTO} as an argument and returns a
     * mapped {@link IAboutText} Object.
     * 
     * @param abtText
     *            Domain Object that needs to be mapped to a
     *            {@link AboutTextDTO}.
     * @return Return a {@link AboutTextDTO} object that is mapped from a Domain
     *         object.
     */
    public AboutTextDTO aboutTextBeanToDTO(IAboutText abtText);

}

package edu.asu.spring.quadriga.service.textfile;

import java.io.IOException;

import org.springframework.stereotype.Service;

import edu.asu.spring.quadriga.domain.workspace.ITextFile;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;

public interface ITextFileService {

    /**
     * @param txtfile
     *          Text File object from web controller to handle text operations.
     * @return
     *          Returns true if file save is successful else returns false. 
     * @throws QuadrigaStorageException
     * @throws IOException
     */
    boolean saveTextFile(ITextFile txtfile) throws QuadrigaStorageException, IOException;

}

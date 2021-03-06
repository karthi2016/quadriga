package edu.asu.spring.quadriga.web.transformation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.spring.quadriga.domain.network.tranform.ITransformation;
import edu.asu.spring.quadriga.exceptions.FileStorageException;
import edu.asu.spring.quadriga.exceptions.QuadrigaGeneratorException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.transformation.ITransformationManager;
import edu.asu.spring.quadriga.service.transformation.ITransformationProcessor;

@Controller
@Scope("session")
public class TransformationController {

    private String result;

    private Future<String> future;

    @Autowired
    private ITransformationManager transformationManager;

    @Autowired
    private ITransformationProcessor transformProcessor;

    /**
     * Gets transformation-request,networks from the User and returns
     * transformed graph Extensions: Obtain Mapping and Pattern files from the
     * user.
     * 
     * @param model
     * @param principal
     * @return
     * @throws QuadrigaStorageException
     * @throws FileStorageException
     */

    @RequestMapping(value = "auth/transformation/start", method = RequestMethod.POST)
    public String generateTransform(@RequestParam("transformation") String transformationIds,
            @RequestParam("sanitized_network_id") String networkIds, Model model)
            throws QuadrigaStorageException, IOException, QuadrigaGeneratorException, FileStorageException {

        String[] transformationIdsList = transformationIds.split(",");
        List<ITransformation> transformations = transformationManager.getTransformations(transformationIdsList);
        List<String> networkIdList = Arrays.asList(networkIds.split("\\s*,\\s*"));

        future = transformProcessor.runTransformationAsync(transformations, networkIdList);

        return "auth/transformation/statusTransformation";
    }

    /**
     * Checks the status of future event, if it's done directs to result page
     * and if it's not done directs to status page
     * 
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping(value = "auth/transformation/result/status", method = RequestMethod.GET)
    public String showStatus() throws InterruptedException, ExecutionException {

        if (future.isDone()) {
            result = future.get();
            
            if (result == null) {
                return "auth/transformation/emptyTransformation";
            }
            return "auth/transformation/successTransformation";
        }
        return "auth/transformation/statusTransformation";
    }

    /**
     * response is built with generated graphstring and downloaded as a file
     * 
     * @param response
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping(value = "auth/transformation/result/download")
    public String downloadTransformation(HttpServletResponse response) throws IOException {

        if (result == null) {
            return "auth/transformation/failureTransformation";
        }
        InputStream inputStream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
        response.setContentLength(result.length());
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=" + "TransformationResult.graphml"));
        FileCopyUtils.copy(inputStream, response.getOutputStream());

        return null;
    }
}

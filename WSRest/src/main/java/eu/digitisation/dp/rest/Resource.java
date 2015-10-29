package eu.digitisation.dp.rest;

import eu.digitisation.dp.rest.model.services.ServiceBean;
import eu.digitisation.dp.rest.model.services.soap.SoapOperationBean;
import eu.digitisation.dp.rest.model.services.soap.SoapOperationInputBean;
import eu.digitisation.dp.rest.model.services.soap.SoapOperationOutputBean;
import eu.digitisation.dp.rest.model.services.myexperiment.MyExperimentBean;
import eu.digitisation.dp.utils.Configuration;
import eu.digitisation.dp.utils.XmlService;
import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowBean;
import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowOutputPortBean;
import eu.digitisation.dp.workflows.taverna.WorkflowParser;
import eu.digitisation.dp.workflows.taverna.WorkflowRunner;
import eu.digitisation.dp.workflows.taverna.t2flowrender.T2FlowRenderer;
import eu.impact_project.iif.ws.generic.SoapInput;
import eu.impact_project.iif.ws.generic.SoapOutput;
import eu.impact_project.iif.ws.generic.SoapService;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Exposes some operations via rest:
 * - soap services
 *  list
 *  run
 * - taverna workflows
 *  parse
 *  run
 */
@Path("/")
public class Resource {
    private static final Logger log = Logger.getLogger(Resource.class.getName());

    /**
     * Get list of services from url
     *
     * @return List of services
     * @throws Exception
     */
    @GET
    @Path("/services")
    @Produces("application/json")
    public List<ServiceBean> getServices() throws Exception {
        List<XmlService> services = Configuration.getInstance().loadServices();
        List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();

        for (XmlService xmlService : services) {
            ServiceBean serviceBean = new ServiceBean(xmlService);

            serviceBeans.add(serviceBean);
        }

        return serviceBeans;
    }

    /**
     * Detailed service description, operations with its inputs
     *
     * @param id number of a service
     * @return Object bean with description of a service
     * @throws Exception
     */
    @GET
    @Path("/services/{id}")
    @Produces("application/json")
    public ServiceBean getService(@PathParam("id") String id) throws Exception {
        List<XmlService> services = Configuration.getInstance().loadServices();
        ServiceBean serviceBean = new ServiceBean(services.get(Integer.valueOf(id)));

        return serviceBean;
    }

    /**
     * Get the inputs from a specific operation
     *
     * @param id number of a service
     * @return Description of a service
     * @throws Exception
     */
    @GET
    @Path("/services/{id}/{operation}")
    @Produces("application/json")
    public List<SoapOperationInputBean> inputExample(@PathParam("id") String id,
                                                     @PathParam("operation") String operation) throws Exception {
        List<XmlService> services = Configuration.getInstance().getServices();
        ServiceBean serviceBean = new ServiceBean(services.get(Integer.valueOf(id)));
        List<SoapOperationInputBean> soapOperationInputBeans = new ArrayList<>();
        SoapOperationBean soapOperationBean = serviceBean.getSoapServiceBean().getOperation(operation);

        soapOperationInputBeans.addAll(soapOperationBean.getInformationInputs());

        return soapOperationInputBeans;
    }

    /**
     * Run a service
     *
     * @param id                      Number of a service
     * @param operation               Name of a operation
     * @param soapOperationInputsBean List of inputs from a specific operation in a service
     * @return
     * @throws IOException
     */
    @POST
    @Path("/services/{id}/{operation}")
    @Consumes("*/*")
    @Produces("application/json")
    public List<SoapOperationOutputBean> executeService(@PathParam("id") String id,
                                                        @PathParam("operation") String operation,
                                                        List<SoapOperationInputBean> soapOperationInputsBean) throws IOException {
        List<SoapOperationOutputBean> soapOperationOutputsBean = new ArrayList<SoapOperationOutputBean>();
        List<XmlService> services = Configuration.getInstance().getServices();
        Map<String, SoapOperationInputBean> map = list2Map(soapOperationInputsBean);

        ServiceBean serviceBean = new ServiceBean(services.get(Integer.valueOf(id)));
        SoapService soapService = new SoapService(serviceBean.getWsdl());

        for (SoapInput soapInput : soapService.getOperation(operation).getInputs()) {
            if (map.get(soapInput.getName()) != null)
                soapInput.setValue(map.get(soapInput.getName()).getValues().get(0)); //TODO list of values?
        }

        //TODO Implement security
        soapService.getOperation(operation).execute("", "");

        soapService.getOperation(operation).getResponse();
        if (!soapService.getOperations().isEmpty()) {
            List<SoapOutput> soapOutputs = soapService.getOperation(operation).getOutputs();

            for (SoapOutput so : soapOutputs) {
                SoapOperationOutputBean soapOperationOutputBean = new SoapOperationOutputBean(so);
                soapOperationOutputsBean.add(soapOperationOutputBean);
            }
        }

        return soapOperationOutputsBean;
    }

    /**
     * Auxiliary function to convert a List to Map
     *
     * @param soapOperationInputsBean List of soapoperation
     * @return Map of inputs
     */
    private Map<String, SoapOperationInputBean> list2Map(List<SoapOperationInputBean> soapOperationInputsBean) {
        Map<String, SoapOperationInputBean> out = new HashMap<String, SoapOperationInputBean>();

        for (SoapOperationInputBean sinput : soapOperationInputsBean)
            out.put(sinput.getName(), sinput);

        return out;
    }

    /**
     * Upload a file via POST
     *
     * @param fileName
     * @param input
     * @return
     * @throws IOException
     */
    @POST
    @Path("/files/{fileName}")
    @Consumes("multipart/form-data")
    public Response uploadFile(@PathParam("fileName") String fileName, MultipartFormDataInput input) throws IOException
    {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(Configuration.getInstance().getFormUploadFileFieldsName());
        String filePath = Configuration.getInstance().getFilesUploadPath() + fileName;

        for (InputPart inputPart : inputParts)
        {
            try
            {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                writeFile(bytes, filePath);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return Response.status(200)
                .entity(fileName).build();
    }

    /**
     * Auxiliary function to write a file
     *
     * @param content
     * @param filename
     * @throws IOException
     */
    private void writeFile(byte[] content, String filename) throws IOException
    {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }

    /**
     * Access point to download a file from a upload folder
     *
     * @param fileName
     * @return
     */
    @GET
    @Path("/files/{fileName}")
    @Produces("*/*")
    public Response getFile(@PathParam("fileName") String fileName)
    {
        if(fileName == null || fileName.isEmpty())
        {
            Response.ResponseBuilder response = Response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }
        File file = new File(Configuration.getInstance().getFilesUploadPath() + fileName);
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName + "\"");

        return response.build();
    }

    /**
     * Parse a tarverna workflow string file in a bean object
     *
     * @param form
     * @return json file with taverna workflow inputs and preview
     * @throws JDOMException
     * @throws IOException
     */
    @POST
    @Path("/tavernaworkflows/workflowparser")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    public TavernaWorkflowBean workflowParser(@MultipartForm MultipartFormDataInput form) throws JDOMException, IOException {
        Map<String, List<InputPart>> formMap = form.getFormDataMap();
        String filename = formMap.get(Configuration.getInstance().getFormTavernaWorkflowparserFieldsFilename()).get(0).getBodyAsString();
        String workflowString = formMap.get(Configuration.getInstance().getFormTavernaWorkflowparserFieldsWorkflowFile()).get(0).getBodyAsString();
        TavernaWorkflowBean currentWorkflow = WorkflowParser.parseWorkflow(workflowString);
        T2FlowRenderer t2FlowRenderer = new T2FlowRenderer();

        currentWorkflow.setName(filename);
        currentWorkflow.setWorkflowPreview(t2FlowRenderer.renderToHtml(workflowString, null, 300));

        return currentWorkflow;
    }

    /**
     * MyExperiment login autentication
     *
     * @param loginForm
     * @return
     * @throws JDOMException
     * @throws IOException
     * @throws JAXBException
     */
    @POST
    @Path("/tavernaworkflows/myexperiment/")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    public MyExperimentBean login(@MultipartForm MultipartFormDataInput loginForm) throws JDOMException, IOException, JAXBException {
        Map<String, List<InputPart>> formMap = loginForm.getFormDataMap();
        String myExperimentUser = formMap.get(Configuration.getInstance().getFormTavernaMyexperimentFieldsUsername()).get(0).getBodyAsString();
        String myExperimentPassword = formMap.get(Configuration.getInstance().getFormTavernaMyexperimentFieldsPassword()).get(0).getBodyAsString();
        MyExperimentBean myExperimentBean = new MyExperimentBean(myExperimentUser, myExperimentPassword);

        return myExperimentBean;
    }

    /**
     * Send a taverna workflow with user inputs to taverna server
     *
     * @param form
     * @return
     * @throws JDOMException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws URISyntaxException
     */
    @POST
    @Path("/tavernaworkflows/workflowrunner")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    public List<TavernaWorkflowOutputPortBean> workflowRunner(@MultipartForm MultipartFormDataInput form) throws JDOMException, IOException, ParserConfigurationException, SAXException, URISyntaxException {
        Map<String, List<InputPart>> formMap = form.getFormDataMap();
        Map<String, String> workflowInputs = formMapToWorkflowInputs(formMap.get(Configuration.getInstance().getFormTavernaWorkflowrunnerFieldsInputs()).get(0));
        String workflowString = formMap.get(Configuration.getInstance().getFormTavernaWorkflowrunnerFieldsWorkflowFile()).get(0).getBodyAsString();

        TavernaWorkflowBean currentWorkflow = WorkflowParser.parseWorkflow(workflowString);
        List<TavernaWorkflowOutputPortBean> tavernaWorkflowOutputPortBean = WorkflowRunner.run(currentWorkflow, workflowInputs);

        return tavernaWorkflowOutputPortBean;
    }

    /**
     * Auxiliary function to convert a form in a Map
     *
     * @param inputs
     * @return
     * @throws IOException
     */
    private Map<String, String> formMapToWorkflowInputs(InputPart inputs) throws IOException {
        Map<String, String> workflowInputs = new HashMap<>();
        String json = inputs.getBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<SoapOperationInputBean> navigation = mapper.readValue(json,
                                                                        mapper.getTypeFactory()
                                                                                .constructCollectionType(List.class, SoapOperationInputBean.class));
        for (SoapOperationInputBean soapOperationInputBean: navigation)
            workflowInputs.put(soapOperationInputBean.getName(), soapOperationInputBean.getStringValues());

        return workflowInputs;
    }
}

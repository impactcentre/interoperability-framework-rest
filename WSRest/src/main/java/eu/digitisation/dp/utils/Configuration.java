package eu.digitisation.dp.utils;

import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Created by emolla on 4/09/15.
 */
public class Configuration {
    private static Configuration ourInstance = new Configuration();
    private List<XmlService> services;

    private String filesUploadPath = "tmp";
    private String servicesXmlList = null;
    private Integer serviceReadTimeout = 5000;
    private URI tavernaServerUrl = null;
    private String tavernaServerCred = null;

    // name form fields
    private String formUploadFileFieldsName = "attachment";

    private String formTavernaWorkflowparserFieldsWorkflowFile = "workflowFile";
    private String formTavernaWorkflowparserFieldsFilename = "filename";

    private String formTavernaWorkflowrunnerFieldsWorkflowFile = "workflowFile";
    private String formTavernaWorkflowrunnerFieldsInputs = "inputs";

    private String formTavernaMyexperimentFieldsUsername = "myExperimentUser";
    private String formTavernaMyexperimentFieldsPassword = "myExperimentPassword";

    private Configuration() {
        String configUserPath = "/config.properties";
        Properties props = new Properties();
        InputStream stream = null;

        try {
            stream = Configuration.class.getResourceAsStream(configUserPath);
            props.load(stream);

            this.filesUploadPath = props.getProperty("files.uploadPath") + '/';
            this.servicesXmlList = props.getProperty("services.xmlList");
            this.serviceReadTimeout = Integer.valueOf(props.getProperty("services.readTimeout"));
            this.tavernaServerUrl = new URI(props.getProperty("taverna.server.url"));
            this.tavernaServerCred = props.getProperty("taverna.server.cred");

            this.formUploadFileFieldsName = props.getProperty("form.uploadFile.fields.name");

            this.formTavernaWorkflowparserFieldsWorkflowFile = props.getProperty("form.taverna.workflowparser.fields.workflowFile");
            this.formTavernaWorkflowparserFieldsFilename = props.getProperty("form.taverna.workflowparser.fields.filename");

            this.formTavernaWorkflowrunnerFieldsWorkflowFile = props.getProperty("form.taverna.workflowrunner.fields.workflowFile");
            this.formTavernaWorkflowrunnerFieldsInputs = props.getProperty("form.taverna.workflowrunner.fields.inputs");

            this.formTavernaMyexperimentFieldsUsername = props.getProperty("form.taverna.myexperiment.fields.username");
            this.formTavernaMyexperimentFieldsPassword = props.getProperty("form.taverna.myexperiment.fields.password");

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance() {
        return ourInstance;
    }

    public List<XmlService> loadServices() {
        URL serviceListUrl = null;
        XmlServiceProvider sp;

        try {
            serviceListUrl = new URL(this.servicesXmlList);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            sp = new XmlServiceProvider(serviceListUrl);
            this.services = sp.getServiceList();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return services;
    }

    public Integer getServiceReadTimeout() {
        return serviceReadTimeout;
    }

    public URI getTavernaServerUrl() {
        return tavernaServerUrl;
    }

    public String getTavernaServerCred() {
        return tavernaServerCred;
    }

    public List<XmlService> getServices() {
        return services;
    }

    public String getFilesUploadPath() {
        return filesUploadPath;
    }

    public String getFormUploadFileFieldsName() {
        return formUploadFileFieldsName;
    }

    public String getFormTavernaWorkflowparserFieldsWorkflowFile() {
        return formTavernaWorkflowparserFieldsWorkflowFile;
    }

    public String getFormTavernaWorkflowparserFieldsFilename() {
        return formTavernaWorkflowparserFieldsFilename;
    }

    public String getFormTavernaWorkflowrunnerFieldsWorkflowFile() {
        return formTavernaWorkflowrunnerFieldsWorkflowFile;
    }

    public String getFormTavernaWorkflowrunnerFieldsInputs() {
        return formTavernaWorkflowrunnerFieldsInputs;
    }

    public String getFormTavernaMyexperimentFieldsUsername() {
        return formTavernaMyexperimentFieldsUsername;
    }

    public String getFormTavernaMyexperimentFieldsPassword() {
        return formTavernaMyexperimentFieldsPassword;
    }
}

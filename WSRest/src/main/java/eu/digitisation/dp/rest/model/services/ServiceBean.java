package eu.digitisation.dp.rest.model.services;

import eu.digitisation.dp.rest.model.services.soap.SoapServiceBean;
import eu.digitisation.dp.utils.XmlService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.net.URL;

@XmlRootElement(name = "service")
public class ServiceBean {
    private int id;
    private String name;
    private String description;
    private URL wsdl;
    private SoapServiceBean soapServiceBean;

    public ServiceBean() {
    }

    public ServiceBean(XmlService xmlService) throws IOException {
        this.id = xmlService.getId();
        this.name = xmlService.getTitle();
        this.description = xmlService.getDescription();
        this.wsdl = xmlService.getURL();
        this.soapServiceBean = xmlService.getSoapServiceBean();
    }

    public int getId() {
        return id - 1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public URL getWsdl() {
        return wsdl;
    }

    public void setWsdl(URL wsdl) {
        this.wsdl = wsdl;
    }

    @XmlElement(name = "soapService")
    public SoapServiceBean getSoapServiceBean() {
        return soapServiceBean;
    }

    public void setSoapServiceBean(SoapServiceBean soapServiceBean) {
        this.soapServiceBean = soapServiceBean;
    }
}

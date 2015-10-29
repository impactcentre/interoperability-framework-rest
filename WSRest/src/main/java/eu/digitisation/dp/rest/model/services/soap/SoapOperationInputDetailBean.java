package eu.digitisation.dp.rest.model.services.soap;

import eu.impact_project.iif.ws.generic.SoapInput;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.List;

/**
 * Created by emolla on 8/09/15.
 */

@XmlRootElement(name = "SoapOperationInput")
public class SoapOperationInputDetailBean {
    private String name;
    private String defaultValue;
    private boolean binary = false;
    private String documentation = null;
    private boolean isMultivaluated = false;
    private List<String> posibleValues;

    public SoapOperationInputDetailBean() {
    }

    public SoapOperationInputDetailBean(SoapInput soapInput) throws IOException {
        setSoapInput(soapInput);
    }

    public void setSoapInput(SoapInput soapInput) throws IOException {
        this.name = soapInput.getName();
        this.defaultValue = soapInput.getDefaultValue();
        this.binary = soapInput.isBinary();
        this.documentation = soapInput.getDocumentation();
        this.posibleValues = soapInput.getPossibleValues();
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "defaultValue")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlElement(name = "isBinary")
    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    @XmlElement(name = "documentation")
    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    @XmlElement(name = "posibleValues")
    public List<String> getPosibleValues() {
        return posibleValues;
    }

    public void setPosibleValues(List<String> posibleValues) {
        this.posibleValues = posibleValues;
    }

    @XmlElement(name = "isMultievaluated")
    public boolean isMultivaluated() {
        return isMultivaluated;
    }

    public void setIsMultivaluated(boolean isMultivaluated) {
        this.isMultivaluated = isMultivaluated;
    }
}

package eu.digitisation.dp.rest.model.services.soap;

import eu.impact_project.iif.ws.generic.SoapOutput;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by emolla on 8/09/15.
 */

@XmlRootElement(name = "soapOperationOutput")
public class SoapOperationOutputBean {
    private String name;
    private String response;
    private String value = "";

    public SoapOperationOutputBean() {
    }

    public SoapOperationOutputBean(SoapOutput soapOutput) {
        //this.response = soapOutput.get
        this.name = soapOutput.getName();
        this.value = soapOutput.getValue();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

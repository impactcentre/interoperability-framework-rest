package eu.digitisation.dp.rest.model.services.soap;

import eu.impact_project.iif.ws.generic.SoapInput;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emolla on 8/09/15.
 */
@XmlRootElement
public class SoapOperationBean {
    private String name;
    private String documentation;
    private List<SoapOperationInputDetailBean> inputs = new ArrayList<SoapOperationInputDetailBean>();

    public SoapOperationBean() {
    }

    public SoapOperationBean(String name, String documentation, List<SoapInput> soapInputs) throws IOException {
        this.name = name;
        this.documentation = documentation;
        this.setInputsFromSOAP(soapInputs);
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "documentation")
    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    @XmlElement(name = "inputs")
    public List<SoapOperationInputDetailBean> getInputs() {
        return inputs;
    }

    public void setInputs(List<SoapOperationInputDetailBean> inputs) {
        this.inputs = inputs;
    }

    public List<SoapOperationInputBean> getInformationInputs()
    {
        List<SoapOperationInputBean> soapOperationInputBeans = new ArrayList<>();

        for (SoapOperationInputDetailBean inputDetailBean: inputs)
        {
            soapOperationInputBeans.add(new SoapOperationInputBean(inputDetailBean));
        }

        return soapOperationInputBeans;
    }

    public void setInputsFromSOAP(List<SoapInput> soapInputs) throws IOException {

        for (SoapInput soapInput : soapInputs) {
            SoapOperationInputDetailBean soapOperationInputDetailBean = new SoapOperationInputDetailBean(soapInput);

            inputs.add(soapOperationInputDetailBean);
        }
    }
}

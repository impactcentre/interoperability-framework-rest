package eu.digitisation.dp.rest.model.services.soap;

import eu.impact_project.iif.ws.generic.SoapInput;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emolla on 8/09/15.
 */

@XmlRootElement
public class SoapOperationInputBean {
    private String name;
    private List<String> values;

    public SoapOperationInputBean() {
    }

    public SoapOperationInputBean(SoapInput soapInput) throws IOException {
        setSoapInput(soapInput);
    }

    public SoapOperationInputBean(SoapOperationInputDetailBean soapOperationInputDetailBean){
        this.name = soapOperationInputDetailBean.getName();
        this.values = soapOperationInputDetailBean.getPosibleValues();
        if (this.values.size() == 0) {
            this.values = new ArrayList<>();
            this.values.add(soapOperationInputDetailBean.getDefaultValue());
        }
    }

    public void setSoapInput(SoapInput soapInput) throws IOException {
        this.name = soapInput.getName();
        this.values = soapInput.getValues();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public String getStringValues() {
        String stringValues = "";

        for (String value: values)
            stringValues += value;

        return stringValues;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String toString() {
        return "name: " + name + " Values: " + values.toString();
    }
}

package eu.digitisation.dp.rest.model.services.soap;

import eu.impact_project.iif.ws.generic.SoapOperation;
import eu.impact_project.iif.ws.generic.SoapService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emolla on 8/09/15.
 */

@XmlRootElement(name = "soapService")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class SoapServiceBean {
    private String documentation;
    private List<SoapOperationBean> operations;

    public SoapServiceBean() {
    }

    public SoapServiceBean(SoapService soapService) throws IOException {
        this.operations = new ArrayList<>();
        if (soapService != null) {
            this.documentation = soapService.getDocumentation();

            for (SoapOperation operation : soapService.getOperations()) {
                SoapOperationBean operationBean = new SoapOperationBean(operation.getName(), operation.getDocumentation(), operation.getInputs());

                operations.add(operationBean);
            }
        }
    }

    @XmlElement(name = "operations")
    public List<SoapOperationBean> getOperations() {
        return operations;
    }

    public SoapOperationBean getOperation(String operationName)
    {

        for(SoapOperationBean soapOperationBean: operations)
        {
            if (soapOperationBean.getName().equalsIgnoreCase(operationName))
                return soapOperationBean;
        }

        return null;
    }

    public void setOperations(List<SoapOperationBean> operations) {
        this.operations = operations;
    }

    @XmlElement(name = "documentation")
    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}

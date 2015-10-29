package eu.digitisation.dp.rest.model.services.myexperiment;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by emolla on 8/10/15.
 */

@XmlRootElement(name = "group")
//@XmlAccessorType(XmlAccessType.FIELD)
public class MyExperimentUserGroupBean {

    private String id;

    private String name;

    private String uri;

    private String resource;

    public MyExperimentUserGroupBean() {
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @XmlAttribute(name = "uri")
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @XmlAttribute(name = "resource")
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }


}

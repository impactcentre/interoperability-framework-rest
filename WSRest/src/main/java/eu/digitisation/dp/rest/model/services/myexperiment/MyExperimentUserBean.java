package eu.digitisation.dp.rest.model.services.myexperiment;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by emolla on 8/10/15.
 */

@XmlRootElement(name = "user")
//@XmlAccessorType(XmlAccessType.FIELD)
public class MyExperimentUserBean {
    private String id;
    private String uri;
    private String resource;
    private String name;
    private String email;
    private String avatar;

    private String website;

    private Set<MyExperimentUserGroupBean> groups = new HashSet<MyExperimentUserGroupBean>();

    public MyExperimentUserBean() {
    }
    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @XmlElement
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @XmlElement
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    @XmlElement
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
    @XmlElement(name = "groups")
    public Set<MyExperimentUserGroupBean> getGroups() {
        return groups;
    }

    public void setGroups(Set<MyExperimentUserGroupBean> groups) {
        this.groups = groups;
    }
}

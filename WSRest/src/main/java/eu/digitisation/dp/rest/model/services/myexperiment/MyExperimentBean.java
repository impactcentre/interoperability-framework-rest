package eu.digitisation.dp.rest.model.services.myexperiment;

import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by emolla on 8/10/15.
 */
@XmlRootElement(name = "MyExperimentBean")
@XmlAccessorType(XmlAccessType.NONE)
public class MyExperimentBean {
    private Boolean logged = false;
    //private String parameters = "elements=id,created,updated,name,description,email,avatar,city,country,website,friends,groups,favourites,favourited,workflows,files,packs,tags,taggings,experiments,jobs,runners";
    private String parameters = "elements=id,name,description,email,avatar,city,country,website,groups";
    private String protocol = "http://";
    private String url = "www.myexperiment.org";
    @XmlElement(name="baseurl")
    private String baseurl = protocol + url;
    private String whoamiurl = baseurl + "/whoami.xml";
    private String workflowsurl = baseurl + "/workflow.xml?elements=content";
    private HttpClient httpclient = null;
    @XmlElement(name="myExperimentUser")
    private MyExperimentUserBean myExperimentUserBean = new MyExperimentUserBean();
    private List<TavernaWorkflowBean> tavernaWorkflows = null;

    public MyExperimentBean() {
    }

    public MyExperimentBean(String user, String password) throws IOException, JAXBException {
        this.httpclient = new HttpClient();
        this.httpclient.getParams().setAuthenticationPreemptive(true);
        this.httpclient.getState().setCredentials(new AuthScope(url, -1), new UsernamePasswordCredentials(user, password));

        getUserInformation();
    }

    private void getUserInformation() throws IOException, JAXBException {
        GetMethod getWhoAmI = new GetMethod(this.whoamiurl);

        getWhoAmI.setDoAuthentication(true);
        int status = httpclient.executeMethod(getWhoAmI);

        if (status == 200) {
            this.logged = true;
            InputStream responseUserInfo = getWhoAmI.getResponseBodyAsStream();
            JAXBContext jcMEUserBean = JAXBContext.newInstance(MyExperimentUserBean.class);
            Unmarshaller umMEUserBean = jcMEUserBean.createUnmarshaller();

            //this.myExperimentUserBean = (MyExperimentUserBean) umMEUserBean.unmarshal(responseUserInfo);
            //System.out.print("User uri: " + myExperimentUserBean.getUri());
            //GetMethod getGroups = new GetMethod(this.myExperimentUserBean.getUri() + "&" + this.parameters);

            //status = httpclient.executeMethod(getGroups);
            //InputStream responseUserGroups = getGroups.getResponseBodyAsStream();
            MyExperimentUserBean tempUserBean = (MyExperimentUserBean) umMEUserBean.unmarshal(new File("/tmp/sample.xml"));

            System.out.println(" NumGroups: " + tempUserBean.getGroups().size() + " groups");

            for (MyExperimentUserGroupBean group: tempUserBean.getGroups())
                System.out.println("Group Name: " + group.getName());
        }
    }

    public InputStream LoadWorkflowFromWeb(String workflowName) throws IOException {
        GetMethod get = new GetMethod(this.workflowsurl + "&id=" + workflowName);

        get.setDoAuthentication(true);
        this.httpclient.executeMethod(get);
        // get the xml
        return get.getResponseBodyAsStream();
    }

    public HttpClient getHttpclient() {
        return httpclient;
    }

    public void setHttpclient(HttpClient httpclient) {
        this.httpclient = httpclient;
    }
}

/*
    
    Copyright 2011 The IMPACT Project
    
    @author Dennis
    @version 0.1

    Licensed under the Apache License, Version 2.0 (the "License"); 
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 */

package eu.digitisation.dp.rest.model.workflows;

import eu.digitisation.dp.workflows.taverna.Wsdl;
import org.xml.sax.SAXException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Bean representing a Taverna workflow
 * 
 * @author dennis
 * 
 */
@XmlRootElement(name = "tavernaworkflow")
public class TavernaWorkflowBean {
    private String name;
    private String workflowPreview;
    private String stringVersion;
    private List<TavernaWorkflowInputBean> inputs = new ArrayList<TavernaWorkflowInputBean>();
    private List<String> urls = new ArrayList<String>();
    private List<Wsdl> wsdls = new ArrayList<Wsdl>();

    public TavernaWorkflowBean() {
    }

    public TavernaWorkflowBean(String stringVersion) {
        this.stringVersion = stringVersion;
    }

    public TavernaWorkflowBean(String name, TavernaWorkflowBean currentWorkflow) {
        this.name = name;
        this.stringVersion = currentWorkflow.getStringVersion();

        for (TavernaWorkflowInputBean input: currentWorkflow.getInputs())
        {
            TavernaWorkflowInputBean tavernaWorkflowInputBean = new TavernaWorkflowInputBean(input);

            this.inputs.add(tavernaWorkflowInputBean);
        }
        this.urls = currentWorkflow.getUrls();
        this.wsdls = currentWorkflow.getWsdls();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringVersion() {
        return stringVersion;
    }

    public void setStringVersion(String stringVersion) {
        this.stringVersion = stringVersion;
    }

    @XmlElement(name = "inputs")
    public List<TavernaWorkflowInputBean> getInputs() {
        return inputs;
    }

    public void setInputs(List<TavernaWorkflowInputBean> inputs) {
        this.inputs = inputs;
    }

    public void setUrls(String inputsData) throws ParserConfigurationException, SAXException, IOException {
            String patronUrl = "(http(s)?://)[^ ]*";
            Pattern pattern = Pattern.compile(patronUrl);
            java.util.regex.Matcher matcher = pattern.matcher(inputsData);
            
            while (matcher.find()) {  
                String url = matcher.group();
                if (!this.urls.contains(url)) this.urls.add(url);
            }
    }
    
    public void setWsdls(String workflow) throws ParserConfigurationException, SAXException, IOException {

            String patronUrl = "(http(s)?://)[^<]*";
            Pattern pattern = Pattern.compile("<wsdl>" + patronUrl + "</wsdl>");
            java.util.regex.Matcher matcher = pattern.matcher(workflow);
            List<String> auxWsdls = new ArrayList<String>();
            
            while (matcher.find()) {  
                String wsdlTAG = matcher.group();
                String urlWsdl = wsdlTAG.substring("<wsdl>".length(), wsdlTAG.length()-"</wsdl>".length());
                
                //System.out.println("WSDL: " + urlWsdl);
                if (!auxWsdls.contains(urlWsdl)) {
                    Wsdl wsdl = new Wsdl(urlWsdl);
                    
                    auxWsdls.add(urlWsdl);
                    this.wsdls.add(wsdl);
                }
            }
    }
    
    public boolean testUrl(String url)
    {
        boolean valid = true;
        
        try {
          HttpURLConnection.setFollowRedirects(true);
          HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
          con.setRequestMethod("GET");
          con.connect(); 
          if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
              valid = false;
              System.out.println("Url not avaliable(" + con.getResponseCode() + "): " + url);
          }
        }
        catch (Exception e) {
            valid = false;
            System.out.println("Catch Url not avaliable: " + url);
            e.printStackTrace();
        }
        return valid;
    }
    
    public List<String> testUrls(List<String> list)
    {
        List<String> failedUrls = new ArrayList<String>();
        
        for (String currentUrl : list) {
            if (!testUrl(currentUrl))
                failedUrls.add(currentUrl);
        }
        
        return failedUrls;
    }

    public List<String> getUrls() {
        return urls;
    }
    
    public List<Wsdl> getWsdls() {
        return wsdls;
    }
    
    public boolean getSecurity(String wsdl) {
        boolean security = true;
        return security;
    }

    public String getWorkflowPreview() {
        return workflowPreview;
    }

    public void setWorkflowPreview(String workflowPreview) {
        this.workflowPreview = workflowPreview;
    }
}

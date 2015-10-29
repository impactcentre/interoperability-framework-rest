package eu.digitisation.dp.utils;

import eu.digitisation.dp.rest.model.services.soap.SoapServiceBean;
import eu.impact_project.iif.ws.generic.SoapService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by emolla on 8/09/15.
 */
public class XmlService implements Comparable<XmlService> {
    int id;
    String title;
    String description;
    URL url;
    SoapService soapService;

    public XmlService() {
    }

    public XmlService(int id, String title, String description, URL url) throws IOException {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        if (isWsdlAlive(url))
            this.soapService = new SoapService(url);
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public URL getURL() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getDocumentation() {
        return soapService.getDocumentation();
    }

    public SoapServiceBean getSoapServiceBean() throws IOException {
        SoapServiceBean soapServiceBean = new SoapServiceBean(soapService);

        return soapServiceBean;
    }

    public int getIdentifier() {
        return id;
    }

    public int compareTo(XmlService arg0) {
        return this.id - arg0.getIdentifier();
    }
    public boolean isWsdlAlive(URL targetUrl)
    {
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) targetUrl.openConnection();
            httpUrlConn.setRequestMethod("HEAD");

            // Set timeouts in milliseconds
            httpUrlConn.setConnectTimeout(Configuration.getInstance().getServiceReadTimeout());
            httpUrlConn.setReadTimeout(Configuration.getInstance().getServiceReadTimeout());

            return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + " produced by " + targetUrl.toString());
            return false;
        }
    }


}
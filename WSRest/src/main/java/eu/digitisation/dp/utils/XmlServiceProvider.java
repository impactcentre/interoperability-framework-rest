package eu.digitisation.dp.utils;/*

	Copyright 2011 The IMPACT Project
	
	@author Dennis Neumann

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

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class XmlServiceProvider {

    final static Logger logger = LoggerFactory
            .getLogger(XmlServiceProvider.class);
    private static final String NO_NAMESPACE = "";
    private List<XmlService> services = new ArrayList<XmlService>();
    private URL xmlUrl;

    public XmlServiceProvider() {
    }

    public XmlServiceProvider(URL url) throws ConfigurationException, IOException {
        xmlUrl = url;
        loadServices();
    }

    private void loadServices() throws IOException {
        InputStream xmlFile = getRemoteFile(xmlUrl);

        if (xmlFile == null)
            throw new RuntimeException("The file " + xmlUrl.toString() + " not reachable or the server certificate is invalid");
        Document xmlDoc = convertToDoc(xmlFile);
        NodeList serviceNodes = applyXPath(xmlDoc, "//service", NO_NAMESPACE);

        SortedSet<XmlService> sortedSet = new TreeSet<XmlService>();
        for (int i = 0; i < serviceNodes.getLength(); i++) {
            Node srv = serviceNodes.item(i);

            XmlService service = createServiceObjectFor(srv);
            if (service != null)
                sortedSet.add(service);
        }
        services = new ArrayList<XmlService>(sortedSet);
    }

    private InputStream getRemoteFile(URL url) throws IOException {
        InputStream is = url.openStream();

        return is;
    }

    private Document convertToDoc(InputStream xmlFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private NodeList applyXPath(Document doc, String xpathExpression,
                                String namespace) {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        if (!namespace.equals(NO_NAMESPACE))
            xpath.setNamespaceContext(new WSDLNamespaceContext());

        XPathExpression expr;

        Object result = null;
        try {
            expr = xpath.compile(xpathExpression);
            result = expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return (NodeList) result;

    }

    private XmlService createServiceObjectFor(Node service) throws IOException {

        int id = Integer.valueOf(service.getAttributes().getNamedItem("id")
                .getTextContent());

        String urlString = "";
        String title = "";
        String description = "";

        NodeList nodes = service.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeName = node.getLocalName();
            if (nodeName != null && nodeName.equals("url"))
                urlString = node.getTextContent();
            else if (nodeName != null && nodeName.equals("title"))
                title = node.getTextContent();
            else if (nodeName != null && nodeName.equals("description"))
                description = node.getTextContent().trim();
        }

        URL wsdlUrl = null;
        XmlService result = null;

        try {
            wsdlUrl = new URL(urlString);
            result = new XmlService(id, title, description, wsdlUrl);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public List<XmlService> getServiceList() {
        return services;
    }

    public URL getUrl(String id) {

        return xmlUrl;
    }

}

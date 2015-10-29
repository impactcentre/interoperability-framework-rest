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

package eu.digitisation.dp.workflows.taverna;

import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowBean;
import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowInputBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Analyzes the uploaded workflow files for input names and input depths
*/

public class WorkflowParser{
    public static TavernaWorkflowBean parseWorkflow(String workflowString) {

        // remove single-line java comments from beanshell scripts since they
        // might produce corrupted java code after serialization
        workflowString = workflowString.replaceAll("[\\s]//[^\n]*", "");

        // <localworkerName> tags are not understood by taverna server
        // 0.2.x
        workflowString = workflowString.replaceAll("<localworkerName>[^<]*</localworkerName>", "");

        TavernaWorkflowBean workflow = new TavernaWorkflowBean(workflowString);

        try {

            SAXBuilder builder = new SAXBuilder();

            InputStream stream = new ByteArrayInputStream(workflowString.getBytes());

            Document doc = builder.build(stream);

            
            // get all input ports from the top dataflow
            XPath xpath = XPath
                    .newInstance("//wf:dataflow[@role='top']/wf:inputPorts/wf:port");
            Namespace ns = Namespace.getNamespace("wf", "http://taverna.sf.net/2008/xml/t2flow");
            xpath.addNamespace(ns);
            List<Element> results = xpath.selectNodes(doc);

            for (Element port : results) {

                // create workflow inputs using data from the port element
                TavernaWorkflowInputBean currentInput = new TavernaWorkflowInputBean("unknown_input");
                currentInput.setName(port.getChild("name", ns).getText());
                currentInput.setDepth(Integer.parseInt(port.getChild("depth",
                        ns).getText()));

                // get all the example input values for the port
                // strangely, taverna does not delete old values if you enter a
                // new one
                XPath xpath2 = XPath
                        .newInstance(".//net.sf.taverna.t2.annotation.AnnotationAssertionImpl[annotationBean/@class='net.sf.taverna.t2.annotation.annotationbeans.ExampleValue']");
                List<Element> results2 = xpath2.selectNodes(port);

                String value = "";
                String date = "";

                // luckily, taverna stores the date+time to each input example
                // so the most current one can be found
                for (Element el : results2) {
                    if (el.getChildText("date").compareTo(date) > 0) {
                        value = el.getChild("annotationBean").getChildText("text");
                        date = el.getChildText("date");
                    }
                }
                currentInput.setExampleValue(value);

                // add the found input to the workflow instance
                workflow.getInputs().add(currentInput);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workflow;
    }
}

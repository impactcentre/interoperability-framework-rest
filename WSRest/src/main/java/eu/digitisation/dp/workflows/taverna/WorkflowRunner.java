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

import eu.digitisation.dp.utils.Configuration;
import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowBean;
import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowInputBean;
import eu.digitisation.dp.rest.model.workflows.TavernaWorkflowOutputPortBean;
import org.xml.sax.SAXException;
import uk.org.taverna.server.client.*;
import uk.org.taverna.server.client.connection.HttpBasicCredentials;
import uk.org.taverna.server.client.connection.UserCredentials;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes the chosen workflow by uploading it to taverna server
 */

public class WorkflowRunner{
    public static List<TavernaWorkflowOutputPortBean> run(TavernaWorkflowBean currentWorkflow, Map<String, String> htmlFormItems) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        // extract all the html form parameters from the request,
        // including files (files are encoded to base64)
        //Map<String, String> htmlFormItems = Helper.parseRequest(request);
        // will contain outputs from all ports of all workflows
        List<TavernaWorkflowOutputPortBean> allOutputs = new ArrayList<TavernaWorkflowOutputPortBean>();
        long duration = 0;
        long startTime = System.currentTimeMillis();

        URI serverURI = Configuration.getInstance().getTavernaServerUrl();
        String cred = Configuration.getInstance().getTavernaServerCred();
        Server tavernaRESTClient = null;
        try {
            tavernaRESTClient = new Server(serverURI);
            System.out.println("Using " + serverURI.toURL().toString());
        }
        catch (ServerException ex)
        {
            System.out.println("Cant connect to taverna server" + ex.getMessage());
        }

        boolean urlFault = false;
        List<Run> runIDs = new ArrayList<Run>();
        List<String> invalidUrls = new ArrayList<String>();
        Run runID = null;
        UserCredentials user = null;

        // String cred = "taverna:taverna";
        user = new HttpBasicCredentials(cred);
        int j = 0;

        // put all the workflows and their inputs onto the server and
        // execute the corresponding jobs
        // upload the workflow to server
        String workflowAsString = currentWorkflow.getStringVersion();
        byte[] currentWorkflowBytes = workflowAsString.getBytes();

        // will contain all the inputs for the current workflow
        Map<String, String> portinputData = new HashMap<String, String>();

        // Use this for a list.
        List<Map<String, String>> inputList = new ArrayList<Map<String, String>>();
        int currentDepth = 0;

        for (TavernaWorkflowInputBean currentInput : currentWorkflow.getInputs()) {
            int counter = 0;

            String currentName = currentInput.getName();
            //String currentNamePrefixed = "workflow" + j + currentName;
            String currentNamePrefixed = currentName;
            currentDepth = currentInput.getDepth();

            // get the current input value
            String currentValue = htmlFormItems.get(currentNamePrefixed);

            // if the inputs are just simple values
            if (currentDepth == 0) {
                // put the value into taverna-specific map
                portinputData.put(currentName, currentValue);
                // if the inputs are a list of values
            } else if (currentDepth > 0) {
                int i = 0;
                portinputData.put(currentName, currentValue);
                inputList.add(i, portinputData);

                while (htmlFormItems.get(currentNamePrefixed + i) != null
                        && !htmlFormItems.get(currentNamePrefixed + i).equals("")) {
                    String additionalValue = htmlFormItems.get(currentNamePrefixed + i);
                    portinputData = new HashMap<String, String>();
                    // valueList.add(new DataValue(additionalValue));
                    i++;
                    portinputData.put(currentName, additionalValue);
                    inputList.add(i, portinputData);
                }
            }
        }
        if (currentDepth == 0) inputList.add(0,portinputData);

        System.out.println("Size: " + inputList.size());
        for (Map<String, String> inputData : inputList) {
            System.out.println("DS: " + inputData.entrySet());

            runID = tavernaRESTClient.createRun(currentWorkflowBytes, user);
            Map<String, InputPort> inputPorts = runID.getInputPorts();

            // convert input values from html form to taverna-specific objects
            for (Map.Entry<String, String> inputWorkflow : inputData.entrySet()) {
                runID.getInputPort(inputWorkflow.getKey()).setValue(inputWorkflow.getValue());
                System.out.println("Setting INPUTs: " +  inputWorkflow.getValue());
            }

            runID.start();
            //System.out.println("Run URL: "+ runID.getURI() );
            runIDs.add(runID);
            //System.err.print("Run UUID: "+ runID.getIdentifier() + " STATUS:" + runID.getStatus() );

            j++;

            // wait until all jobs are done
            for (Run currentRunID : runIDs) {
                while (currentRunID.isRunning()) {
                    try {
                        duration = System.currentTimeMillis() - startTime;
                        System.out.println("Waiting for job [" + currentRunID.getIdentifier()
                            + "] to complete (" + (duration / 1000f) + ")" + " STATUS:" + runID.getStatus());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("HOPELESS");
                    }
                }
            }

            // process the outputs
            int workflowIndex = 0;
            for (Run currentRunID : runIDs) {
                // will contain outputs from all ports of the current workflow
                List<TavernaWorkflowOutputPortBean> workflowOutputPorts = new ArrayList<TavernaWorkflowOutputPortBean>();

                if (currentRunID.isFinished()) {
                    System.out.println("Owner: " + currentRunID.isOwner());
                    // get the outputs of the current job
                    if (currentRunID.isOwner()) {
                        System.out.println("Output state: " + currentRunID.getExitCode());
                        Map<String, OutputPort> outputPorts = null;
                        if (currentRunID.getOutputPorts() != null) outputPorts = currentRunID.getOutputPorts();
                        for (Map.Entry<String, OutputPort> outputPort : outputPorts.entrySet()) {
                            TavernaWorkflowOutputPortBean workflowOutPortCurrent = new TavernaWorkflowOutputPortBean();

                            if (outputPort != null) {
                                if (outputPort.getValue().getDepth() == 0) {
                                    workflowOutPortCurrent.setOutput(outputPort.getValue(),false);
                                    workflowOutputPorts.add(workflowOutPortCurrent);
                                } else {
                                    System.out.println("outputName : " + outputPort.getKey());
                                    workflowOutPortCurrent.setOutput(outputPort.getValue(), currentRunID, outputPort.getKey(), outputPort.getValue().getDepth());
                                    workflowOutputPorts.add(workflowOutPortCurrent);
                                }
                            }
                        }
                        currentRunID.delete();
                    }
                }
                //allOutputs.add(workflowOutputPorts);
                allOutputs = workflowOutputPorts;
                workflowIndex++;
            }
        }

        duration = System.currentTimeMillis() - startTime;
        System.out.println("Jobs took " + (duration / 1000f) + " seconds");

        return allOutputs;
    }
}

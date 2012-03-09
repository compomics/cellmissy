/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Paola
 */
@Service("obsepFileParser")
public class ObsepFileParserImpl implements ObsepFileParser {

    public enum CycleTimeUnit {

        HOURS(1), MINUTES(2), SECONDS(3);
        private int unitValue;

        private CycleTimeUnit(int unitValue) {
            this.unitValue = unitValue;
        }
    }
    private Node loopNode;

    public ObsepFileParserImpl() {
    }

    @Override
    public void parseObsepFile(File obsepFile) {

        // get a document from xml
        // get a document builder
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            // get the dom object
            doc = db.parse(obsepFile);

        } catch (ParserConfigurationException | SAXException | IOException pce) {
            pce.printStackTrace();
        }

        // get root element of xml
        Element element = doc.getDocumentElement();
        // get "net" node: first child node
        Node netNode = element.getFirstChild();

        // get "Loop" node
        loopNode = getChildNodeByAttributeValue(netNode, "Loop");
    }

    @Override
    public Map<ImagingType, String> mapImagingTypetoPositionList() {
        // this Map maps ImagingType (keys) to String (names of PositionList files, values)
        Map<ImagingType, String> imagingTypeToPosListMap = new HashMap<>();
        List<String> posListNames = this.getPosListNames();
        List<ImagingType> imagingInfo = this.getImagingInfo();

        for (int i = 0; i < imagingInfo.size(); i++) {
            imagingTypeToPosListMap.put(imagingInfo.get(i), posListNames.get(i));
        }

        return imagingTypeToPosListMap;
    }

    @Override
    public List<Double> getExperimentInfo() {

        // create new Experiment entity and set class members
        List<Double> experimentInfo = new ArrayList<>();
        NodeList loopChildNodes = loopNode.getChildNodes();

        // get time frame
        NamedNodeMap repeatTimesAttr = loopChildNodes.item(0).getFirstChild().getAttributes();
        for (int i = 0; i < repeatTimesAttr.getLength(); i++) {
            String repeatTimesVal = repeatTimesAttr.item(i).getNodeValue();
            Double timesFrame = Double.parseDouble(repeatTimesVal);
            experimentInfo.add(timesFrame);
        }

        // get interval value
        NamedNodeMap cycleTimeAttr = loopChildNodes.item(1).getFirstChild().getAttributes();
        for (int i = 0; i < cycleTimeAttr.getLength(); i++) {
            String cycleTimeVal = cycleTimeAttr.item(i).getNodeValue();
            Double experimentInterval = Double.parseDouble(cycleTimeVal);
            experimentInfo.add(experimentInterval);
        }

        // get interval unit
        NamedNodeMap cycleTimeUnitAttr = loopChildNodes.item(2).getFirstChild().getAttributes();
        for (int i = 0; i < cycleTimeUnitAttr.getLength(); i++) {
            int cycleTimeUnitValue = Integer.valueOf(cycleTimeUnitAttr.item(i).getNodeValue());
            CycleTimeUnit unit = findCycleTimeUnitByValue(cycleTimeUnitValue);
        }

        // get duration
        Double duration = (experimentInfo.get(0) * experimentInfo.get(1)) / 60;
        experimentInfo.add(duration);

        return experimentInfo;
    }

    
    /**
     * Getting Position List names used in the Experiment
     * @return a List of String (Position List names)
     */
    private List<String> getPosListNames() {
        List<String> posListNames = new ArrayList<>();
        // get "Stage loop" nodes
        List<Node> stageLoopNodes = getChildNodeListByAttributeValue(loopNode, "Stage loop");
        for (int i = 0; i < stageLoopNodes.size(); i++) {
            NamedNodeMap posListAttr = stageLoopNodes.get(i).getFirstChild().getFirstChild().getAttributes();
            for (int j = 0; j < posListAttr.getLength(); j++) {
                String posListName = posListAttr.item(j).getNodeValue();
                posListNames.add(posListName);
            }
        }
        return posListNames;
    }

    /**
     * Getting Imaging Types used in the Experiment
     * @return a List of Imaging Type entities
     */
    private List<ImagingType> getImagingInfo() {
        List<ImagingType> imagingTypeList = new ArrayList<>();

        // get "Stage loop" nodes
        List<Node> stageLoopNodes = getChildNodeListByAttributeValue(loopNode, "Stage loop");
        // get "Image" nodes
        List<Node> imageNodes = new ArrayList<>();
        for (int i = 0; i < stageLoopNodes.size(); i++) {
            Node imageNode = getChildNodeByAttributeValue(stageLoopNodes.get(i), "Image");
            imageNodes.add(imageNode);
        }

        // create new imaging type entitie(s) and set class members
        for (int i = 0; i < imageNodes.size(); i++) {
            ImagingType imagingType = new ImagingType();
            NodeList imageChildNodes = imageNodes.get(i).getChildNodes();

            // set exposure time value
            NamedNodeMap exposureTimeAttr = imageChildNodes.item(0).getFirstChild().getAttributes();
            for (int j = 0; j < exposureTimeAttr.getLength(); j++) {
                String exposureTimeVal = exposureTimeAttr.item(j).getNodeValue();
                imagingType.setExposureTime(Double.parseDouble(exposureTimeVal));
            }

            // get exposure time unit
            NamedNodeMap exposureTimeUnitAttr = imageChildNodes.item(1).getFirstChild().getAttributes();
            for (int j = 0; j < exposureTimeUnitAttr.getLength(); j++) {
            }

            // set name
            NamedNodeMap imageTypeAttr = imageChildNodes.item(4).getFirstChild().getAttributes();
            for (int j = 0; j < imageTypeAttr.getLength(); j++) {
                String imageTypeName = imageTypeAttr.item(j).getNodeValue();
                imagingType.setName(imageTypeName);
            }

            // set light intensity
            Node lampNode = getChildNodeByAttributeValue(loopNode, "ucb.0-lamp_transm.0");
            Node intensityNode = getChildNodeByAttributeValue(lampNode, "intensity");
            NamedNodeMap intensityAttr = intensityNode.getFirstChild().getAttributes();
            for (int j = 0; j < intensityAttr.getLength(); j++) {
                String intensity = intensityAttr.item(j).getNodeValue();
                imagingType.setLightIntensity(Double.parseDouble(intensity) / 10);
            }

            // add imaging type entity to the list
            imagingTypeList.add(imagingType);
        }
        return imagingTypeList;
    }

    private CycleTimeUnit findCycleTimeUnitByValue(int cycleTimeUnitValue) {
        CycleTimeUnit foundUnit = null;
        for (CycleTimeUnit unit : CycleTimeUnit.values()) {
            if (unit.unitValue == cycleTimeUnitValue) {
                foundUnit = unit;
                break;
            }
        }
        return foundUnit;
    }

    /**
     * Get a Child Node of the XML document by Attribute Value
     * @param parentNode
     * @param attributeValue
     * @return a Node
     */
    private Node getChildNodeByAttributeValue(Node parentNode, String attributeValue) {
        NodeList childNodes = parentNode.getChildNodes();
        Node foundNode = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            NamedNodeMap attributeList = childNodes.item(i).getAttributes();
            for (int j = 0; j < attributeList.getLength(); j++) {
                if (attributeList.item(j).getNodeValue().equals(attributeValue)) {
                    foundNode = childNodes.item(i);
                    break;
                }
            }
        }
        return foundNode;
    }

   /**
     * Getting a List of Child Nodes by Attribute Value
     * @param parentNode
     * @param attributeValue
     * @return List of Nodes
     */
    private List<Node> getChildNodeListByAttributeValue(Node parentNode, String attributeValue) {
        NodeList childNodes = parentNode.getChildNodes();
        List<Node> foundNodes = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            NamedNodeMap attributeList = childNodes.item(i).getAttributes();
            for (int j = 0; j < attributeList.getLength(); j++) {
                if (attributeList.item(j).getNodeValue().equals(attributeValue)) {
                    foundNodes.add(childNodes.item(i));
                }
            }
        }
        return foundNodes;
    }
}

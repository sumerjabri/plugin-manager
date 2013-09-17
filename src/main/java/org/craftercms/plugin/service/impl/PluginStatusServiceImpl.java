/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.plugin.service.impl;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.craftercms.plugin.PluginManager.PluginState;
import org.craftercms.plugin.service.api.PluginStatusService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author David Escalante López
 */
public class PluginStatusServiceImpl implements PluginStatusService {


    // <editor-fold defaultstate="collapsed" desc="Constants">

    /**
     * XML Root node name.
     */
    private static final String PLUGIN_STATUS_XML_ROOT = "plugins-status";

    /**
     * Status node name.
     */
    private static final String PLUGIN_STATUS_XML_CHILD = "status";

    /**
     * Id attribute.
     */
    private static final String NODE_ID = "id";

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Attributes">

    private Logger log = LoggerFactory.getLogger(PluginStatusServiceImpl.class);

    /**
     * Path to simple-xml-map.
     */
    private String statusXmlPath;

    /**
     * XPath matcher to search in DOM.
     */
    private XPath xPathMatcher = XPathFactory.newInstance().newXPath();

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public PluginStatusServiceImpl(final String... propertiesPath) {
        loadPropertiesValues(propertiesPath);
        initFile();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="API">

    @Override
    public boolean addOne(final String pluginId, final PluginState state) {

        boolean result = false;

        if (!exists(pluginId)) {

            Document domTree = getDomTree();
            Element statusElement = domTree.createElement(PLUGIN_STATUS_XML_CHILD);
            statusElement.appendChild(domTree.createTextNode(state.toString()));
            Attr statusID = domTree.createAttribute(NODE_ID);
            statusID.setValue(pluginId);
            statusElement.setAttributeNode(statusID);
            Element root = domTree.getDocumentElement();
            root.appendChild(statusElement);

            result = saveDomTree(domTree);
        }

        return result;
    }

    @Override
    public boolean addList(final List<String> pluginList, final PluginState state) {
        boolean result;
        boolean changes = false;

        Document domTree = getDomTree();

        for (String pluginId : pluginList) {
            if (!exists(pluginId)) {
                Element statusElement = domTree.createElement(PLUGIN_STATUS_XML_CHILD);
                statusElement.appendChild(domTree.createTextNode(state.toString()));
                Attr statusID = domTree.createAttribute(NODE_ID);
                statusID.setValue(pluginId);
                statusElement.setAttributeNode(statusID);
                Element root = domTree.getDocumentElement();
                root.appendChild(statusElement);
                changes = true;
            }
        }

        result = changes && saveDomTree(domTree);

        return result;
    }

    @Override
    public PluginState getStatusById(final String pluginId) {

        PluginState result;

        Document domTree = getDomTree();

        Node queryNode = getNodeById(domTree, PLUGIN_STATUS_XML_CHILD, pluginId);

        if (queryNode != null) {
            result = PluginState.valueOf(queryNode.getFirstChild().getNodeValue());
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public List<String> getAllByStatus(final PluginState state) {

        List<String> result = new ArrayList<>();

        Document domTree = getDomTree();

        NodeList resultNodes = getNodesByContent(domTree, PLUGIN_STATUS_XML_CHILD, state.toString());

        for (int i = 0; i < resultNodes.getLength(); i++) {

            Node statusNode = resultNodes.item(i);
            NamedNodeMap attributes = statusNode.getAttributes();
            Node idAttribute = attributes.getNamedItem(NODE_ID);
            result.add(idAttribute.getNodeValue());

        }

        return result;
    }

    @Override
    public List<String> getAll() {
        List<String> result = new ArrayList<>();

        Document domTree = getDomTree();

        NodeList resultNodes = getNodesByName(domTree, PLUGIN_STATUS_XML_CHILD);

        for (int i = 0; i < resultNodes.getLength(); i++) {

            Node statusNode = resultNodes.item(i);
            NamedNodeMap attributes = statusNode.getAttributes();
            Node idAttribute = attributes.getNamedItem(NODE_ID);
            result.add(idAttribute.getNodeValue());
        }

        return result;
    }

    @Override
    public boolean updateOne(final String pluginId, final PluginState state) {
        boolean result = false;

        Document domTree = getDomTree();

        Node statusNode = getNodeById(domTree, PLUGIN_STATUS_XML_CHILD, pluginId);

        if (statusNode != null) {
            statusNode.setTextContent(state.toString());
            result = saveDomTree(domTree);
        }

        return result;
    }

    @Override
    public boolean updateList(final List<String> pluginList, final PluginState state) {
        boolean result;
        boolean changes = false;

        Document domTree = getDomTree();

        for (String pluginId : pluginList) {

            Node statusNode = getNodeById(domTree, PLUGIN_STATUS_XML_CHILD, pluginId);

            if (statusNode != null) {
                statusNode.setTextContent(state.toString());
                changes = true;
            }
        }
        result = changes && saveDomTree(domTree);

        return result;
    }

    @Override
    public boolean updateAll(final PluginState state) {
        boolean result;
        boolean changes = false;

        Document domTree = getDomTree();

        NodeList statusNodes = getNodesByName(domTree, PLUGIN_STATUS_XML_CHILD);

        for (int i = 0; i < statusNodes.getLength(); i++) {
            Node statusNode = statusNodes.item(i);
            statusNode.setTextContent(state.toString());
            changes = true;
        }
        result = changes && saveDomTree(domTree);

        return result;
    }

    @Override
    public boolean delete(final String pluginId) {
        boolean result = false;

        Document domTree = getDomTree();
        Node statusNode = getNodeById(domTree, PLUGIN_STATUS_XML_CHILD, pluginId);
        if (statusNode != null) {
            Element root = domTree.getDocumentElement();
            root.removeChild(statusNode);
            result = saveDomTree(domTree);
        }

        return result;
    }


    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Internal Functionality">

    private boolean exists(final String pluginId) {

        boolean result;

        Document domTree = getDomTree();
        Node queryNode = getNodeById(domTree, PLUGIN_STATUS_XML_CHILD, pluginId);
        result = queryNode != null;

        return result;
    }

    /**
     * Check if XML file exists if not initialize it.
     */
    private void initFile() {
        try {
            File statusXml = new File(getStatusXmlPath());
            if (!statusXml.exists()) {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // root elements
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement(PLUGIN_STATUS_XML_ROOT);
                doc.appendChild(rootElement);

                // write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(getStatusXmlPath()));

                transformer.transform(source, result);

            }
        } catch (ParserConfigurationException pce) {
            log.debug(String.format("Error trying to create initial document builder: %s", pce.getMessage()));
        } catch (TransformerException tfe) {
            log.debug(String.format("Error trying to save initial xml file: :%s", tfe.getMessage()));
        } catch (NullPointerException npe) {
            log.debug(String.format("Error creating the xml file: %s", npe.getMessage()));
        }
    }

    private Document getDomTree() {

        Document doc = null;

        try {

            File fXmlFile = new File(getStatusXmlPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

        } catch (ParserConfigurationException e) {
            log.debug("Error trying to create document builder");
        } catch (IOException e) {
            log.debug("Error trying to load xml file");
        } catch (SAXException e) {
            log.debug("Error trying to parse xml file");
        }

        return doc;
    }

    private boolean saveDomTree(final Document domTree) {

        boolean saved = false;

        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(domTree);
            StreamResult result = new StreamResult(new File(getStatusXmlPath()));

            transformer.transform(source, result);
            saved = true;

        } catch (TransformerException tfe) {
            log.debug("Error trying to save xml file");
        }

        return saved;
    }

    private Node getNodeById(final Document domTree, final String nodeName, final String nodeId) {

        Node result = null;

        try {

            XPathExpression expr = xPathMatcher.compile(String.format("//%s[@id=\"%s\"]", nodeName, nodeId));

            result = (Node)expr.evaluate(domTree, XPathConstants.NODE);


        } catch (XPathExpressionException e) {
            log.debug("Error trying to parse select node by Id XPath expression");
        }

        return result;
    }

    private NodeList getNodesByContent(final Document domTree, final String nodeName, final String nodeContent) {

        NodeList result = null;

        try {

            XPathExpression expr = xPathMatcher.compile(String.format("//%s[contains(., \"%s\")]", nodeName,
                nodeContent));

            result = (NodeList)expr.evaluate(domTree, XPathConstants.NODESET);


        } catch (XPathExpressionException e) {
            log.debug("Error trying to parse get nodes by content XPath expression");
        }

        return result;
    }

    private NodeList getNodesByName(final Document domTree, final String nodeName) {

        NodeList result = null;

        try {

            XPathExpression expr = xPathMatcher.compile(String.format("//%s", nodeName));

            result = (NodeList)expr.evaluate(domTree, XPathConstants.NODESET);


        } catch (XPathExpressionException e) {
            log.debug("Error trying to parse get nodes by name XPath expression");
        }

        return result;
    }

    private void loadPropertiesValues(final String... propertiesPath) {

        Properties properties = new Properties();

        try {
            for (String propertyFile : propertiesPath) {
                properties.load(PluginStatusServiceImpl.class.getClassLoader().getResourceAsStream(propertyFile));
            }
            this.statusXmlPath = properties.getProperty("plugin.status.file");
        } catch (IOException e) {
            log.debug("Error processing the properties map");
        } catch (NullPointerException e) {
            log.debug("Error loading the properties file");
        }

    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters and Getters">

    public String getStatusXmlPath() {
        return statusXmlPath;
    }


    // </editor-fold>

}

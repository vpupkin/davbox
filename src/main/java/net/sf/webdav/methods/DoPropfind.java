/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.webdav.methods;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;

import net.sf.webdav.IMimeTyper;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.LockFailedException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.fromcatalina.URLEncoder;
import net.sf.webdav.fromcatalina.XMLHelper;
import net.sf.webdav.fromcatalina.XMLWriter;
import net.sf.webdav.locking.LockedObject;
import net.sf.webdav.locking.ResourceLocks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DoPropfind extends AbstractMethod {

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(DoPropfind.class);

    /**
     * Array containing the safe characters set.
     */
    protected static URLEncoder URL_ENCODER;

    /**
     * PROPFIND - Specify a property mask.
     */
    private static final int FIND_BY_PROPERTY = 0;

    /**
     * PROPFIND - Display all properties.
     */
    private static final int FIND_ALL_PROP = 1;

    /**
     * PROPFIND - Return property names.
     */
    private static final int FIND_PROPERTY_NAMES = 2;

    private IWebdavStore _store;
    private ResourceLocks _resourceLocks;
    private IMimeTyper _mimeTyper;

    private int _depth;

    public DoPropfind(IWebdavStore store, ResourceLocks resLocks,
            IMimeTyper mimeTyper) {
        _store = store;
        _resourceLocks = resLocks;
        _mimeTyper = mimeTyper;
    }

    public void execute(ITransaction transaction, HttpServletRequest req,
            HttpServletResponse resp) throws IOException, LockFailedException {
        LOG.trace("-- " + this.getClass().getName());

        // Retrieve the resources
        String path = getCleanPath(getRelativePath(req));
        String tempLockOwner = "doPropfind" + System.currentTimeMillis()
                + req.toString();
        _depth = getDepth(req);

        if (_resourceLocks.lock(transaction, path, tempLockOwner, false,
                _depth, TEMP_TIMEOUT, TEMPORARY)) {

            StoredObject so = null;
            try {
                so = _store.getStoredObject(transaction, path);
                if (so == null) {
                    resp.setContentType("text/xml; charset=UTF-8");
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, req
                            .getRequestURI());
                    return;
                }

                Vector<String> properties = null;
                path = getCleanPath(getRelativePath(req));

                int propertyFindType = FIND_ALL_PROP;
                Node propNode = null;

                if (req.getContentLength() != 0) {
                    DocumentBuilder documentBuilder = getDocumentBuilder();
                    try {
                        Document document = documentBuilder
                                .parse(new InputSource(req.getInputStream()));
                        // Get the root element of the document
                        Element rootElement = document.getDocumentElement();

                        propNode = XMLHelper
                                .findSubElement(rootElement, "prop");
                        if (propNode != null) {
                            propertyFindType = FIND_BY_PROPERTY;
                        } else if (XMLHelper.findSubElement(rootElement,
                                "propname") != null) {
                            propertyFindType = FIND_PROPERTY_NAMES;
                        } else if (XMLHelper.findSubElement(rootElement,
                                "allprop") != null) {
                            propertyFindType = FIND_ALL_PROP;
                        }
                    } catch (Exception e) {
                    	e.printStackTrace();
//                        resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
//                        return;
                    }
                } else {
                    // no content, which means it is a allprop request
                    propertyFindType = FIND_ALL_PROP;
                }

                HashMap<String, String> namespaces = new HashMap<String, String>();
                namespaces.put("DAV:", "D");

                if (propertyFindType == FIND_BY_PROPERTY) {
                    propertyFindType = 0;
                    properties = XMLHelper.getPropertiesFromXML(propNode);
                }

                resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
                resp.setContentType("text/xml; charset=UTF-8");

                // Create multistatus object
                XMLWriter generatedXML = new XMLWriter(resp.getWriter(),
                        namespaces);
                generatedXML.writeXMLHeader();
                generatedXML
                        .writeElement("DAV::multistatus", XMLWriter.OPENING);
                if (_depth == 0) {
                    parseProperties(transaction, req, generatedXML, path,
                            propertyFindType, properties, _mimeTyper
                                    .getMimeType(path));
                } else {
                    recursiveParseProperties(transaction, path, req,
                            generatedXML, propertyFindType, properties, _depth,
                            _mimeTyper.getMimeType(path));
                }
                generatedXML
                        .writeElement("DAV::multistatus", XMLWriter.CLOSING);

                generatedXML.sendData();
            } catch (AccessDeniedException e) {
                resp.sendError(WebdavStatus.SC_FORBIDDEN);
            } catch (WebdavException e) {
                LOG.warn("Sending internal error!");
                resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
            } catch (ServletException e) {
                e.printStackTrace(); // To change body of catch statement use
                // File | Settings | File Templates.
            } finally {
                _resourceLocks.unlockTemporaryLockedObjects(transaction, path,
                        tempLockOwner);
            }
        } else {
            Hashtable<String, Integer> errorList = new Hashtable<String, Integer>();
            errorList.put(path, WebdavStatus.SC_LOCKED);
            sendReport(req, resp, errorList);
        }
    }

    /**
     * goes recursive through all folders. used by propfind
     * 
     * @param currentPath
     *      the current path
     * @param req
     *      HttpServletRequest
     * @param generatedXML
     * @param propertyFindType
     * @param properties
     * @param depth
     *      depth of the propfind
     * @throws IOException
     *      if an error in the underlying store occurs
     */
    private void recursiveParseProperties(ITransaction transaction,
            String currentPath, HttpServletRequest req, XMLWriter generatedXML,
            int propertyFindType, Vector<String> properties, int depth,
            String mimeType) throws WebdavException {

        parseProperties(transaction, req, generatedXML, currentPath,
                propertyFindType, properties, mimeType);

        if (depth > 0) {
            // no need to get name if depth is already zero
            String[] names = _store.getChildrenNames(transaction, currentPath);
            names = names == null ? new String[] {} : names;
            String newPath = null;

            for (String name : names) {
                newPath = currentPath;
                if (!(newPath.endsWith("/"))) {
                    newPath += "/";
                }
                newPath += name;
                recursiveParseProperties(transaction, newPath, req,
                        generatedXML, propertyFindType, properties, depth - 1,
                        mimeType);
            }
        }
    }

    /**
     * Propfind helper method.
     * 
     * @param req
     *      The servlet request
     * @param generatedXML
     *      XML response to the Propfind request
     * @param path
     *      Path of the current resource
     * @param type
     *      Propfind type
     * @param propertiesVector
     *      If the propfind type is find properties by name, then this Vector
     *      contains those properties
     */
    private void parseProperties(ITransaction transaction,
            HttpServletRequest req, XMLWriter generatedXML, String path,
            int type, Vector<String> propertiesVector, String mimeType)
            throws WebdavException {

        StoredObject so = _store.getStoredObject(transaction, path);

        boolean isFolder = so.isFolder();
        String creationdate = CREATION_DATE_FORMAT.format(so.getCreationDate());
        String lastModified = LAST_MODIFIED_DATE_FORMAT.format(so
                .getLastModified());
        String resourceLength = String.valueOf(so.getResourceLength());

        // ResourceInfo resourceInfo = new ResourceInfo(path, resources);

        generatedXML.writeElement("DAV::response", XMLWriter.OPENING);
        String status = new String("HTTP/1.1 " + WebdavStatus.SC_OK + " "
                + WebdavStatus.getStatusText(WebdavStatus.SC_OK));

        // Generating href element
        generatedXML.writeElement("DAV::href", XMLWriter.OPENING);

        String href = req.getContextPath();
        String servletPath = req.getServletPath();
        if (servletPath != null) {
            if ((href.endsWith("/")) && (servletPath.startsWith("/")))
                href += servletPath.substring(1);
            else
                href += servletPath;
        }
        if ((href.endsWith("/")) && (path.startsWith("/")))
            href += path.substring(1);
        else
            href += path;
        if ((isFolder) && (!href.endsWith("/")))
            href += "/";

        generatedXML.writeText(rewriteUrl(href));

        generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);

        String resourceName = path;
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash != -1)
            resourceName = resourceName.substring(lastSlash + 1);

        switch (type) {

        case FIND_ALL_PROP:

            generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

            generatedXML.writeProperty("DAV::creationdate", creationdate);
            generatedXML.writeElement("DAV::displayname", XMLWriter.OPENING);
            generatedXML.writeData(resourceName);
            generatedXML.writeElement("DAV::displayname", XMLWriter.CLOSING);
            if (!isFolder) {
                generatedXML
                        .writeProperty("DAV::getlastmodified", lastModified);
                generatedXML.writeProperty("DAV::getcontentlength",
                        resourceLength);
                String contentType = mimeType;
                if (contentType != null) {
                    generatedXML.writeProperty("DAV::getcontenttype",
                            contentType);
                }
                generatedXML.writeProperty("DAV::getetag", getETag(so));
                generatedXML.writeElement("DAV::resourcetype",
                        XMLWriter.NO_CONTENT);
            } else {
                generatedXML.writeElement("DAV::resourcetype",
                        XMLWriter.OPENING);
                generatedXML.writeElement("DAV::collection",
                        XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::resourcetype",
                        XMLWriter.CLOSING);
            }

            writeSupportedLockElements(transaction, generatedXML, path);

            writeLockDiscoveryElements(transaction, generatedXML, path);

            generatedXML.writeProperty("DAV::source", "");
            generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
            generatedXML.writeText(status);
            generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

            break;

        case FIND_PROPERTY_NAMES:

            generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

            generatedXML
                    .writeElement("DAV::creationdate", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::displayname", XMLWriter.NO_CONTENT);
            if (!isFolder) {
                generatedXML.writeElement("DAV::getcontentlanguage",
                        XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getcontentlength",
                        XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getcontenttype",
                        XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getetag", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getlastmodified",
                        XMLWriter.NO_CONTENT);
            }
            generatedXML
                    .writeElement("DAV::resourcetype", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::supportedlock",
                    XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::source", XMLWriter.NO_CONTENT);

            generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
            generatedXML.writeText(status);
            generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

            break;

        case FIND_BY_PROPERTY:

            Vector<String> propertiesNotFound = new Vector<String>();

            // Parse the list of properties

            generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

            Enumeration<String> properties = propertiesVector.elements();

            while (properties.hasMoreElements()) {

                String property = (String) properties.nextElement();

                if (property.equals("DAV::creationdate")) {
                    generatedXML.writeProperty("DAV::creationdate",
                            creationdate);
                } else if (property.equals("DAV::displayname")) {
                    generatedXML.writeElement("DAV::displayname",
                            XMLWriter.OPENING);
                    generatedXML.writeData(resourceName);
                    generatedXML.writeElement("DAV::displayname",
                            XMLWriter.CLOSING);
                } else if (property.equals("DAV::getcontentlanguage")) {
                    if (isFolder) {
                        propertiesNotFound.addElement(property);
                    } else {
                        generatedXML.writeElement("DAV::getcontentlanguage",
                                XMLWriter.NO_CONTENT);
                    }
                } else if (property.equals("DAV::getcontentlength")) {
                    if (isFolder) {
                        propertiesNotFound.addElement(property);
                    } else {
                        generatedXML.writeProperty("DAV::getcontentlength",
                                resourceLength);
                    }
                } else if (property.equals("DAV::getcontenttype")) {
                    if (isFolder) {
                        propertiesNotFound.addElement(property);
                    } else {
                        generatedXML.writeProperty("DAV::getcontenttype",
                                mimeType);
                    }
                } else if (property.equals("DAV::getetag")) {
                    if (isFolder || so.isNullResource()) {
                        propertiesNotFound.addElement(property);
                    } else {
                        generatedXML.writeProperty("DAV::getetag", getETag(so));
                    }
                } else if (property.equals("DAV::getlastmodified")) {
                    if (isFolder) {
                        propertiesNotFound.addElement(property);
                    } else {
                        generatedXML.writeProperty("DAV::getlastmodified",
                                lastModified);
                    }
                } else if (property.equals("DAV::resourcetype")) {
                    if (isFolder) {
                        generatedXML.writeElement("DAV::resourcetype",
                                XMLWriter.OPENING);
                        generatedXML.writeElement("DAV::collection",
                                XMLWriter.NO_CONTENT);
                        generatedXML.writeElement("DAV::resourcetype",
                                XMLWriter.CLOSING);
                    } else {
                        generatedXML.writeElement("DAV::resourcetype",
                                XMLWriter.NO_CONTENT);
                    }
                } else if (property.equals("DAV::source")) {
                    generatedXML.writeProperty("DAV::source", "");
                } else if (property.equals("DAV::supportedlock")) {

                    writeSupportedLockElements(transaction, generatedXML, path);

                } else if (property.equals("DAV::lockdiscovery")) {

                    writeLockDiscoveryElements(transaction, generatedXML, path);

                } else {
                    propertiesNotFound.addElement(property);
                }

            }

            generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
            generatedXML.writeText(status);
            generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

            Enumeration<String> propertiesNotFoundList = propertiesNotFound
                    .elements();

            if (propertiesNotFoundList.hasMoreElements()) {

                status = new String("HTTP/1.1 " + WebdavStatus.SC_NOT_FOUND
                        + " "
                        + WebdavStatus.getStatusText(WebdavStatus.SC_NOT_FOUND));

                generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

                while (propertiesNotFoundList.hasMoreElements()) {
                    generatedXML.writeElement((String) propertiesNotFoundList
                            .nextElement(), XMLWriter.NO_CONTENT);
                }

                generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
                generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
                generatedXML.writeText(status);
                generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
                generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

            }

            break;

        }

        generatedXML.writeElement("DAV::response", XMLWriter.CLOSING);

        so = null;
    }

    private void writeSupportedLockElements(ITransaction transaction,
            XMLWriter generatedXML, String path) {

        LockedObject lo = _resourceLocks.getLockedObjectByPath(transaction,
                path);

        generatedXML.writeElement("DAV::supportedlock", XMLWriter.OPENING);

        if (lo == null) {
            // both locks (shared/exclusive) can be granted
            generatedXML.writeElement("DAV::lockentry", XMLWriter.OPENING);

            generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::exclusive", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::write", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::lockentry", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::lockentry", XMLWriter.OPENING);

            generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::shared", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::write", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::lockentry", XMLWriter.CLOSING);

        } else {
            // LockObject exists, checking lock state
            // if an exclusive lock exists, no further lock is possible
            if (lo.isShared()) {

                generatedXML.writeElement("DAV::lockentry", XMLWriter.OPENING);

                generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::shared", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);

                generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::" + lo.getType(),
                        XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);

                generatedXML.writeElement("DAV::lockentry", XMLWriter.CLOSING);
            }
        }

        generatedXML.writeElement("DAV::supportedlock", XMLWriter.CLOSING);

        lo = null;
    }

    private void writeLockDiscoveryElements(ITransaction transaction,
            XMLWriter generatedXML, String path) {

        LockedObject lo = _resourceLocks.getLockedObjectByPath(transaction,
                path);

        if (lo != null && !lo.hasExpired()) {

            generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::activelock", XMLWriter.OPENING);

            generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
            generatedXML.writeProperty("DAV::" + lo.getType());
            generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
            if (lo.isExclusive()) {
                generatedXML.writeProperty("DAV::exclusive");
            } else {
                generatedXML.writeProperty("DAV::shared");
            }
            generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::depth", XMLWriter.OPENING);
            if (_depth == INFINITY) {
                generatedXML.writeText("Infinity");
            } else {
                generatedXML.writeText(String.valueOf(_depth));
            }
            generatedXML.writeElement("DAV::depth", XMLWriter.CLOSING);

            String[] owners = lo.getOwner();
            if (owners != null) {
                for (int i = 0; i < owners.length; i++) {
                    generatedXML.writeElement("DAV::owner", XMLWriter.OPENING);
                    generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
                    generatedXML.writeText(owners[i]);
                    generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
                    generatedXML.writeElement("DAV::owner", XMLWriter.CLOSING);
                }
            } else {
                generatedXML.writeElement("DAV::owner", XMLWriter.NO_CONTENT);
            }

            int timeout = (int) (lo.getTimeoutMillis() / 1000);
            String timeoutStr = new Integer(timeout).toString();
            generatedXML.writeElement("DAV::timeout", XMLWriter.OPENING);
            generatedXML.writeText("Second-" + timeoutStr);
            generatedXML.writeElement("DAV::timeout", XMLWriter.CLOSING);

            String lockToken = lo.getID();

            generatedXML.writeElement("DAV::locktoken", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
            generatedXML.writeText("opaquelocktoken:" + lockToken);
            generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::locktoken", XMLWriter.CLOSING);

            generatedXML.writeElement("DAV::activelock", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.CLOSING);

        } else {
            generatedXML.writeElement("DAV::lockdiscovery",
                    XMLWriter.NO_CONTENT);
        }

        lo = null;
    }

}

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class XMLParser {

    private Document document;
    private Document backupDocument;

    public void loadDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File("config/config.xml"));
            document.getDocumentElement().normalize();

            // Create a new document for backup
            DocumentBuilder backupBuilder = factory.newDocumentBuilder();
            backupDocument = backupBuilder.newDocument();

            Element apiElement = document.getDocumentElement();
            NodeList childNodes = apiElement.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);

                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNode;
                    String tagName = childElement.getTagName();

                    if (tagName.equals("UNDERTOW")) {
                        // Processing UNDERTOW element. See method below
                        processUndertowNode(childElement);
                    } else if (tagName.equals("DB")) {
                        processDBNode(childElement);
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void processUndertowNode(Element undertowElement) {
        System.out.println("Undertow Display Endpoints: " + undertowElement.getAttribute("DISPLAY_ENDPOINTS"));
        System.out.println("Undertow IO Threadpool: " + undertowElement.getAttribute("IO_THREAD_POOL"));
        System.out.println("Undertow Worker Threadpool: " + undertowElement.getAttribute("WORKER_THREAD_POOL"));

        NodeList childNodes = undertowElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
        }

        System.out.println("=============================");
    }

    private void processDBNode(Element dbElement) {
        NodeList dbChildNodes = dbElement.getChildNodes();

        for (int i = 0; i < dbChildNodes.getLength(); i++) {
            Node dbChildNode = dbChildNodes.item(i);

            if (dbChildNode.getNodeType() == Node.ELEMENT_NODE) {
                Element dbChildElement = (Element) dbChildNode;

                // Getting the different child nodes inside DB
                if (dbChildElement.getTagName().equals("HOST")) {
                    String host = dbChildElement.getTextContent();
                    System.out.println("DB Host: " + host);
                } else if (dbChildElement.getTagName().equals("PORT")) {
                    int port = Integer.parseInt(dbChildElement.getTextContent());
                    System.out.println("DB Port: " + port);
                } else if (dbChildElement.getTagName().equals("DATABASE_NAME")) {
                    String databaseName = dbChildElement.getTextContent();
                    System.out.println("DB Database Name: " + databaseName);
                } else if (dbChildElement.getTagName().equals("USERNAME")) {
                    String username = dbChildElement.getTextContent();
                    System.out.println("DB Username: " + username);
                } else if (dbChildElement.getTagName().equals("PASSWORD")) {
                    String password = dbChildElement.getTextContent();
                    System.out.println("DB Password: " + password);
                } else if (dbChildElement.getTagName().equals("SHOW_SQL")) {
                    String showSqlText = dbChildElement.getTextContent().toLowerCase();
                    boolean showSql = showSqlText.equals("true");
                    System.out.println("DB Show SQL: " + showSql);
                } else if (dbChildElement.getTagName().equals("SLING_RING")) {
                    // Process SLING_RING node
                    processSlingRingNode(dbChildElement);
                }
            }

        }

        // Adds the new element under DB called BACKUPS
        Element backupsElement = document.createElement("BACKUPS");
        dbElement.appendChild(backupsElement);

        Element filePathElement = document.createElement("FILE_PATH");
        backupsElement.appendChild(filePathElement);

        filePathElement.setAttribute("PATH_TYPE", "10");  // 10 was just an example

        saveUpdatedXML(document, "config/config_backup.xml");
    }

    private void processSlingRingNode(Element slingRingElement) {
        // Process SLING_RING node
        NodeList slingRingNodes = slingRingElement.getChildNodes();

        for (int j = 0; j < slingRingNodes.getLength(); j++) {
            Node slingRingNode = slingRingNodes.item(j);

            if (slingRingNode.getNodeType() == Node.ELEMENT_NODE) {
                Element slingRingChildElement = (Element) slingRingNode;

                // Getting the different child nodes inside SLING_RING
                if (slingRingChildElement.getTagName().equals("INITIAL_POOL_SIZE")) {
                    int initialPoolSize = Integer.parseInt(slingRingChildElement.getAttribute("VALUE"));
                    System.out.println("SlingRing Initial Pool Size: " + initialPoolSize);
                } else if (slingRingChildElement.getTagName().equals("MAXIMUM_POOL_SIZE")) {
                    int maximumPoolSize = Integer.parseInt(slingRingChildElement.getAttribute("VALUE"));
                    System.out.println("SlingRing Maximum Pool Size: " + maximumPoolSize);
                } else if (slingRingChildElement.getTagName().equals("EXTRA_CONNS_SIZE")) {
                    int extraConnsSize = Integer.parseInt(slingRingChildElement.getAttribute("VALUE"));
                    System.out.println("SlingRing Extra Conn Size: " + extraConnsSize);
                } else if (slingRingChildElement.getTagName().equals("FIND_FREE_CONN_AFTER")) {
                    int findFreeConnAfter = Integer.parseInt(slingRingChildElement.getAttribute("VALUE"));
                    System.out.println("SlingRing Find Free Conn After: " + findFreeConnAfter);
                } else if (slingRingChildElement.getTagName().equals("DOWNSIZE_AFTER")) {
                    int downsizeAfter = Integer.parseInt(slingRingChildElement.getAttribute("VALUE"));
                    System.out.println("SlingRing Downsize after: " + downsizeAfter);
                } else if (slingRingChildElement.getTagName().equals("PING_AFTER")) {
                    int pingAfter = Integer.parseInt(slingRingChildElement.getAttribute("VALUE"));
                    System.out.println("SlingRing Ping After: " + pingAfter);
                }
            }
        }
    }

    private void saveUpdatedXML(Document document, String backupFilePath) {
        try {
            File originalFile = new File("config/config.xml");
            File backupFile = new File(backupFilePath);
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            try (FileOutputStream fos = new FileOutputStream(originalFile)) {
                javax.xml.transform.TransformerFactory.newInstance()
                        .newTransformer()
                        .transform(new javax.xml.transform.dom.DOMSource(document),
                                new javax.xml.transform.stream.StreamResult(fos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveUpdatedXMLToBackup() {
        saveUpdatedXML(document, "config/config_backup.xml");
    }


    public void updateElement() {
        try {
            Scanner scanner = new Scanner(System.in);

            // Prompt the user for the XPath expression
            System.out.println();
            System.out.print("Enter the XPath expression of the element you'd wish to update: ");
            String xPathExpression = scanner.nextLine();

            // Validating the xpath
            if (!xPathExpression.startsWith("/")) {
                System.out.println("Invalid XPath expression. It must start with '/'.");
                return;
            }

            // Prompt the user for the new value
            System.out.print("Enter the new value: ");
            String newValue = scanner.nextLine();

            // Updates the element value using the provided XPath and new value
            updateElementValueByXPath(xPathExpression, newValue);

            System.out.println("Element updated successfully!");

            saveUpdatedXMLToBackup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateElementValueByXPath(String xPathExpression, String newValue) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.compile(xPathExpression).evaluate(document, XPathConstants.NODE);

            if (node != null) {
                node.setTextContent(newValue);
            } else {
                System.out.println("XPath not found: " + xPathExpression);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}

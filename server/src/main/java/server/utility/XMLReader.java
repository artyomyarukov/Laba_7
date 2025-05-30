package server.utility;

import common.collection.City;
import common.collection.Coordinates;
import common.collection.Government;
import common.collection.Human;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server.ServerApplication;

import java.util.logging.Logger;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;

public class XMLReader {

    private static final Logger logger = Logger.getLogger(XMLReader.class.getName());

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Hashtable<String, City> loadFromXML(String filename) throws IOException {
        Hashtable<String, City> map = new Hashtable<>();
        File file = new File(filename);

        // Если файл не существует, создаем новый пустой файл
        if (!file.exists()) {
            createEmptyXMLFile(file);
            logger.log(Level.INFO, "Файл не существует будет создан новый");
            return map; // Возвращаем пустую коллекцию
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList entryList = document.getElementsByTagName("entry");

            for (int i = 0; i < entryList.getLength(); i++) {
                Node entryNode = entryList.item(i);
                if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element entryElement = (Element) entryNode;

                    String key = getElementText(entryElement, "сity_key");
                    if (key == null || key.isEmpty()) {
                        System.err.println("Элемент entry без ключа, пропускаем");
                        continue;
                    }

                    NodeList cityNodes = entryElement.getElementsByTagName("city_elem");
                    if (cityNodes.getLength() == 0) {
                        System.err.println("Entry без city_elem, пропускаем");
                        continue;
                    }

                    Element cityElement = (Element) cityNodes.item(0);
                    City city = parseCityElement(cityElement);

                    if (city != null && city.validate()) {
                        map.put(key, city);
                    } else {
                        System.err.println("Ошибка: элемент с ключом " + key + " не прошел валидацию");
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Ошибка чтения XML файла: " + filename, e);
        }
        return map;
    }

    /**
     * Создает пустой XML файл с базовой структурой
     */
    private void createEmptyXMLFile(File file) throws IOException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Создаем новый документ
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("cities");
            doc.appendChild(rootElement);

            // Записываем содержимое в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            // Создаем родительские директории, если нужно
            file.getParentFile().mkdirs();
            transformer.transform(source, result);

            System.out.println("Создан новый пустой XML файл: " + file.getAbsolutePath());
        } catch (Exception e) {
            throw new IOException("Ошибка при создании нового XML файла", e);
        }
    }

    // Остальные методы класса остаются без изменений
    private City parseCityElement(Element cityElement) {
        try {
            Long id = Long.parseLong(getElementText(cityElement, "id"));
            String name = getElementText(cityElement, "name");
            Coordinates coordinates = parseCoordinates(cityElement);
            LocalDateTime creationDate = LocalDateTime.parse(getElementText(cityElement, "creationDate"), DATE_TIME_FORMATTER);
            Long area = Long.parseLong(getElementText(cityElement, "area"));
            int population = Integer.parseInt(getElementText(cityElement, "population"));
            Integer metersAboveSeaLevel = parseOptionalInteger(getElementText(cityElement, "metersAboveSeaLevel"));
            Long telephoneCode = parseOptionalLong(getElementText(cityElement, "telephoneCode"));
            long agglomeration = Long.parseLong(getElementText(cityElement, "agglomeration"));
            Government government = Government.valueOf(getElementText(cityElement, "government"));
            Human governor = parseGovernor(cityElement);

            return new City(id, name, coordinates, creationDate, area, population,
                    metersAboveSeaLevel, telephoneCode, agglomeration, government, governor);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга элемента city: " + e.getMessage());
            return null;
        }
    }

    private Coordinates parseCoordinates(Element cityElement) {
        Element coordElement = (Element) cityElement.getElementsByTagName("coordinates").item(0);
        Float x = Float.parseFloat(getElementText(coordElement, "x"));
        float y = Float.parseFloat(getElementText(coordElement, "y"));
        return new Coordinates(x, y);
    }

    private Human parseGovernor(Element cityElement) {
        NodeList governorNodes = cityElement.getElementsByTagName("governor");
        if (governorNodes.getLength() == 0) return null;

        Element governorElement = (Element) governorNodes.item(0);
        String birthdayStr = getElementText(governorElement, "birthday");
        if (birthdayStr == null || birthdayStr.isEmpty()) return null;

        return new Human(LocalDate.parse(birthdayStr, DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return null;
        return nodes.item(0).getTextContent();
    }

    private Integer parseOptionalInteger(String value) {
        return (value == null || value.isEmpty()) ? null : Integer.parseInt(value);
    }

    private Long parseOptionalLong(String value) {
        return (value == null || value.isEmpty()) ? null : Long.parseLong(value);
    }
}
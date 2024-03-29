package juno.http.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import juno.http.HttpRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CacheSource {
    private static final Map<File, CacheSource> INSTANCES = 
            new HashMap<File, CacheSource>();
    
    public final File file;
    private List<CacheModel> _objects;

    private CacheSource(File file) {
        this.file = file;
    }
    
    public static CacheSource getCacheSourceFromFile(File src) {
        CacheSource db = INSTANCES.get(src);
        if (db == null) {
            db = new CacheSource(src);
            INSTANCES.put(src, db);
        }
        return db;
    }
    
    public File getParentFile() {
        final File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    public int indexOf(String uuid, List<CacheModel> list) {
        for (int i = 0; i < list.size(); i++) 
            if (Objects.equals(list.get(i).uuid, uuid)) 
                return i;

        return -1;
    }
    
    public synchronized void writeObjects(List<CacheModel> list) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.newDocument();

            final Element rootElement = doc.createElement("CacheSource");
            doc.appendChild(rootElement);

            for (CacheModel model : list) {
                Element modelElement = model.toXmlElement(doc);
                rootElement.appendChild(modelElement);
            }

            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public synchronized List<CacheModel> readObjects() {
        try {
            if (!file.exists()) {
                return new ArrayList<CacheModel>();
            }
            
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            List<CacheModel> result = new ArrayList<CacheModel>();
            final Element rootElement = doc.getDocumentElement();
            for (int i = 0; i < rootElement.getChildNodes().getLength(); i++) {
                final Node node = rootElement.getChildNodes().item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    result.add(new CacheModel((Element) node));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CacheModel>();
        }
    }
    
    public List<CacheModel> getObjects() {
        if (_objects == null) {
            _objects = readObjects();
        }
        return _objects;
    }
    
    public CacheModel find(final String method, final String url) {
        List<CacheModel> list = getObjects();
        for (int i = 0; i < list.size(); i++) {
            final CacheModel it = list.get(i);
            if (method.equals(it.requestMethod) 
                    && url.equals(it.requestUrl)) {
                return it;
            }
        }
        return null;
    }
    
    public CacheModel find(HttpRequest request) {
        return find(request.getMethod(), request.urlAndParams());
    }

    public int save(CacheModel model) {
        List<CacheModel> list = getObjects();
        int i = indexOf(model.uuid, list);

        if (i == -1) { // Insert
            list.add(model);
            writeObjects(list);
            return 1;
        }

        list.set(i, model); // Update
        writeObjects(list);
        return 2;
    } 
}

package br.ufg.inf.dsp2105;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author cleber
 */
public class StAXDeserializer {

	private String fileName;
	private Object deserializedObject;

	public StAXDeserializer(String fileName) {
		this.fileName = fileName;
		this.deserializedObject = null;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Object getDeserializedObject() {
		return deserializedObject;
	}

	public void deserializeObject() throws IllegalArgumentException,
			IllegalAccessException, FileNotFoundException, XMLStreamException,
			ClassNotFoundException, InstantiationException, NoSuchFieldException {

		InputStream xmlInputStream = new FileInputStream(fileName);
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlInputStream);

		int event = xmlStreamReader.next();

		if (event == XMLStreamConstants.START_ELEMENT) {
			String varName = xmlStreamReader.getLocalName();
			if (!"type".equals(xmlStreamReader.getAttributeLocalName(0))) {
				return;
			}
			String varType = xmlStreamReader.getAttributeValue(0);

			Class c = Class.forName(varType);
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
			}

			xmlStreamReader.next();
			
			deserializedObject = c.newInstance();
			fillObject(deserializedObject, xmlStreamReader);
		}
	}

	private Object fillObject(Object father, XMLStreamReader xmlStreamReader)
			throws XMLStreamException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			InstantiationException, ClassNotFoundException {
		int event = XMLStreamConstants.START_ELEMENT;
		int lastEvent = 0;

		Field currentField = null;
		String currentClassName = null;

		while (!(event == XMLStreamConstants.END_ELEMENT
				&& lastEvent == XMLStreamConstants.END_ELEMENT)) {
			if (event == XMLStreamConstants.START_ELEMENT) {
				if (lastEvent == XMLStreamConstants.START_ELEMENT) {
					Class c = Class.forName(currentClassName);
					Object currentObject = c.newInstance();
					
					currentField.set(father,
							fillObject(currentObject, xmlStreamReader));
				} else {
					if (!"type".equals(xmlStreamReader.getAttributeLocalName(0))) {
						break;
					}
					currentClassName = xmlStreamReader.getAttributeValue(0);
					currentField = father.getClass().getDeclaredField(xmlStreamReader.getLocalName());
					currentField.setAccessible(true);
				}
			} else if (event == XMLStreamConstants.CHARACTERS) {
				if(currentField.getName().equals("serialVersionID")){
					currentField.set(father, xmlStreamReader.getText());
				}
				
			}
			lastEvent = event;
			event = xmlStreamReader.next();
		}

		return father;
	}
}

package br.ufg.inf.dsp2105;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.openehr.rm.RMObject;
import org.openehr.rm.datatypes.quantity.DvOrdered;

/**
 *
 * @author cleber
 */
public class StAXSerializer {

	private String fileName;

	public StAXSerializer(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void serializeObject(Object object) throws 
			FileNotFoundException, XMLStreamException, IOException, 
			IllegalArgumentException, IllegalAccessException {
		if (!(object instanceof DvOrdered)) {
			System.out.println("O objeto fornecido não é uma subclasse de DvOrdered.");
			return;
		}

		OutputStream xmlOutputStream = new FileOutputStream(fileName);
		XMLEventWriter xmlEventWriter = XMLOutputFactory.newInstance().
				createXMLEventWriter(xmlOutputStream);
		XMLEventFactory xmlEventFactory = XMLEventFactory.newFactory();

		XMLEvent event = xmlEventFactory.createStartDocument();
		xmlEventWriter.add(event);

		serializeObject(object, object.getClass().getSimpleName(), xmlOutputStream);

		event = xmlEventFactory.createEndDocument();
		xmlEventWriter.add(event);

		xmlOutputStream.close();
	}

	private void serializeObject(Object object, String name, 
			OutputStream xmlOutputStream) throws XMLStreamException, 
			IOException, IllegalArgumentException, IllegalAccessException {
		
		XMLEventWriter xmlEventWriter = XMLOutputFactory.newInstance().
				createXMLEventWriter(xmlOutputStream);
		XMLEventFactory xmlEventFactory = XMLEventFactory.newFactory();

		XMLEvent event = xmlEventFactory.createStartElement(
				new QName(name), null, null);
		xmlEventWriter.add(event);
		xmlEventWriter.add(xmlEventFactory.createAttribute(new QName("type"),
				object.getClass().getName()));
		xmlEventWriter.add(xmlEventFactory.createDTD(""));

		Field fields[] = object.getClass().getDeclaredFields();
//		System.out.println("This object is from the class " + object.getClass()
//				+ " and has " + fields.length + " fields.");

		for (Field field : fields) {
			field.setAccessible(true);
//			System.out.print(field.getName() + ": ");
			if (field.get(object) == null) {
//				System.out.println("<null>");
				continue;
			}

			StartElement startElement = xmlEventFactory.createStartElement(
					new QName(field.getName()), null, null);

			if (RMObject.class.isAssignableFrom(field.get(object).getClass())) {
				serializeObject(field.get(object), field.getName(), xmlOutputStream);
			} else {
				xmlEventWriter.add(startElement);
				xmlEventWriter.add(xmlEventFactory.createAttribute(new QName("type"),
						field.get(object).getClass().getName()));
				xmlEventWriter.add(xmlEventFactory.createDTD(""));

				event = xmlEventFactory.createCharacters(field.get(object).toString());
				xmlEventWriter.add(event);

				event = xmlEventFactory.createEndElement(
						new QName(field.getName()), null);
				xmlEventWriter.add(event);
			}
		}

		event = xmlEventFactory.createEndElement(new QName(
				object.getClass().getName()), null);
		xmlEventWriter.add(event);

		xmlEventWriter.close();
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.inf.dsp2105;

import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author cleber
 */
public class StAXDeserializerMain {
	public static void main(String[] args) throws IllegalArgumentException, 
			IllegalAccessException, FileNotFoundException, XMLStreamException, 
			ClassNotFoundException, InstantiationException, NoSuchFieldException {
//		if(args.length == 0 || "".equals(args[0])){
//			System.out.println("Informe a URL do arquivo XML que será desserializado.");
//			return;
//		}
		
		StAXDeserializer deserializer = new StAXDeserializer("./dvOrdinal.xml");
		deserializer.deserializeObject();
		Object newObject = deserializer.getDeserializedObject();
	}
}

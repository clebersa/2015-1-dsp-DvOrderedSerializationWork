/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.inf.dsp2105;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;

/**
 *
 * @author cleber
 */
public class StAXSerializerMain {

	public static void main(String[] args) throws IOException, 
			FileNotFoundException, IllegalArgumentException, 
			IllegalAccessException, XMLStreamException {
		if (args.length == 0 || "".equals(args[0])) {
			System.out.println("Informe a URL do arquivo XML que será serializado.");
			return;
		}

		DvOrdinal obj = new DvOrdinal(10, new DvCodedText("value of DvCodedText",
				new CodePhrase("terminologyIDValue", "codeStringValue")));

		StAXSerializer serializer = new StAXSerializer(args[0]);
		serializer.serializeObject(obj);
	}
}

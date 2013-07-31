package pruebas.Util;

import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

public class XMLReader {

	public static String readUnitsProperties() {
		try {
			FileHandle handler = new FileHandle(
					"data/Units/units_properties.xml");
			XmlReader xml = new XmlReader();
			XmlReader.Element xml_element;
			xml_element = xml.parse(handler);
			Iterator iterator_unit = xml_element.getChildrenByName("unit")
					.iterator();
			while (iterator_unit.hasNext()) {
				XmlReader.Element unit_element = (XmlReader.Element) iterator_unit
						.next();
				String lifePoints = unit_element.getAttribute("lifePoints");
				String damage = unit_element.getAttribute("damage");
				String velicity = unit_element.getAttribute("velicity");
				String range = unit_element.getAttribute("range");

				String level_status = unit_element.getAttribute("status");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class KML_Parser extends DefaultHandler {

	private boolean is_folder = false;
	private boolean is_foldername = false;
	private boolean is_placemark = false;
	private boolean is_extendeddata = false;
	private boolean is_schemadata = false;
	private boolean is_simpledata = false;
	private int field = 0;
	private String data;

	private Municipio currMunicipio;
	private DatabaseHandler handler;
	private String filename;
	
	
	public KML_Parser(DatabaseHandler dh, String fn) {
		handler = dh;
		filename = fn;
		currMunicipio = new Municipio();
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {

		if (qName.equals("SimpleData") && is_schemadata) {
			is_simpledata = true;
			data = "";
		} else if (qName.equals("SchemaData") && is_extendeddata) {
			is_schemadata = true;
		} else if (qName.equals("ExtendedData") && is_placemark) {
			is_extendeddata = true;
		} else if (qName.equals("Placemark") && is_folder) {
			if(!is_placemark) {
				is_placemark = true;
				currMunicipio.clear();
			}				
		} else if (qName.equals("name") && !is_placemark) {
			is_foldername = true;
		} else if (qName.equals("Folder")) {
			is_folder = true;
		} 

		if (is_folder && is_placemark && is_extendeddata 
				&& is_schemadata && is_simpledata) {
			String attrName = attrs.getValue("name");

			if (attrName.equals("ID")) {
				field = 1;
			} else if (attrName.equals("CD_GEOCODMU")) {
				field = 2;
			} else if (attrName.equals("NM_LOCALIDADE")) {
				field = 3;
			} else if (attrName.equals("NM_UF")) {
				field = 4;
			} else if (attrName.equals("LONG")) {
				field = 5;
			} else if (attrName.equals("LAT")) {
				field = 6;
			} else if (attrName.equals("ALT")) {
				field = 7;
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		if (qName.equals("SimpleData")) {
			is_simpledata = false;
			field = 0;
		} else if (qName.equals("SchemaData")) {
			is_schemadata = false;
		} else if (qName.equals("ExtendedData")) {
			is_extendeddata = false;
		} else if (qName.equals("Placemark")) {
			if(is_placemark) {
				is_placemark = false;
				handler.insertMunicipio(currMunicipio);
			}
		} else if (qName.equals("name")) {
			is_foldername = false;
		} else if (qName.equals("Folder")) {
			is_folder = false;
		}

	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		String s = new String(ch, start, length);
		
		if(is_folder && is_foldername) {
			if(!s.contains("CIDADE")) {
				is_folder = false;
			}
		}

		try {
			if(is_simpledata) {
				data += s.trim();
				switch(field) {
					case 1: currMunicipio.setId(Integer.valueOf(data));
							break;
					case 2:	currMunicipio.setCd_geocodmu(Integer.valueOf(data));
							break;
					case 3:	currMunicipio.setNm_municipio(data);
							break;
					case 4:	currMunicipio.setNm_uf(data);
							break;
					case 5:	currMunicipio.setLongitude(Double.valueOf(data));
							break;
					case 6: currMunicipio.setLatitude(Double.valueOf(data));
							break;
					case 7: currMunicipio.setAltitude(Double.valueOf(data));
							break;
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}

	public boolean parse() {
		boolean ok = true;
		try {
			// creates and returns new instance of SAX-implementation:
			SAXParserFactory factory = SAXParserFactory.newInstance();
			//factory.setValidating(true);

			// create SAX-parser...
			SAXParser parser = factory.newSAXParser();
			
			parser.parse(filename, this);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			ok = false;
		}
		
		return ok;
	}
}
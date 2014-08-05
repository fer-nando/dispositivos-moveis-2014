
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class KMLCopyToDB extends DefaultHandler {
	
	private static String KML_NAME = "BR_Localidades_2010_v1.kml";

	private boolean is_folder = false;
	private boolean is_foldername = false;
	private boolean is_placemark = false;
	private boolean is_extendeddata = false;
	private boolean is_schemadata = false;
	private boolean is_simpledata = false;
	private boolean is_field_id = false;
	private boolean is_field_geocodmu = false;
	private boolean is_field_municipio = false;
	private boolean is_field_uf = false;
	private boolean is_field_long = false;
	private boolean is_field_lat = false;
	private boolean is_field_alt = false;
	
	private int counter = 0;

	private Municipio currMunicipio;
  private Connection connection;
	private HashMap<String, Integer> estadoToId;
	
	
	public KMLCopyToDB(Connection con, HashMap<String, Integer> map) {
    connection = con;
		estadoToId = map;
		currMunicipio = new Municipio();
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {

		if (qName.equals("SimpleData")) {
			is_simpledata = true;
		} else if (qName.equals("SchemaData")) {
			is_schemadata = true;
		} else if (qName.equals("ExtendedData")) {
			is_extendeddata = true;
		} else if (qName.equals("Placemark")) {
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
				is_field_id = true;
			} else if (attrName.equals("CD_GEOCODMU")) {
				is_field_geocodmu = true;
			} else if (attrName.equals("NM_MUNICIPIO")) {
				is_field_municipio = true;
			} else if (attrName.equals("NM_UF")) {
				is_field_uf = true;
			} else if (attrName.equals("LONG")) {
				is_field_long = true;
			} else if (attrName.equals("LAT")) {
				is_field_lat = true;
			} else if (attrName.equals("ALT")) {
				is_field_alt = true;
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		if (qName.equals("SimpleData")) {
			is_simpledata = false;
		} else if (qName.equals("SchemaData")) {
			is_schemadata = false;
		} else if (qName.equals("ExtendedData")) {
			is_extendeddata = false;
		} else if (qName.equals("Placemark")) {
			if(is_placemark) {
				is_placemark = false;
				counter++;
				if(counter == 1) {
          
          // inserting data
          /*PreparedStatement prep = con
            .prepareStatement("insert into user values(?,?,?,?,?,?);");
          prep.setString(2, "John");
          prep.setString(3, "21");
          prep.setString(4, "male");
          prep.setString(5, "77");
          prep.setString(6, "185");
          prep.execute();
          
					ContentValues newLine = new ContentValues();
					//newLine.put("MUNICIPIO_ID", currMunicipio.getId());
					newLine.put("NOME", currMunicipio.getNm_municipio());
					newLine.put("ID", currMunicipio.getId());
					newLine.put("ESTADO_ID", estadoToId.get(currMunicipio.getNm_uf()));
					newLine.put("CODIGO", currMunicipio.getCd_geocodmu());
					newLine.put("LONGITUDE", currMunicipio.getLongitude());
					newLine.put("LATITUDE", currMunicipio.getLatitude());
					newLine.put("ALTITUDE", currMunicipio.getAltitude());
					db.insert("municipios", null, newLine);
                  */
				}
			}
		} else if (qName.equals("name")) {
			is_foldername = false;
		} else if (qName.equals("Folder")) {
			is_folder = false;
		}

	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

		String data = new String(ch, start, length);
		data.trim();

		if(is_folder && is_foldername) {
			if(!data.contains("CIDADE")) {
				is_folder = false;
			}
		}

		try {
			if (is_field_id) {
				currMunicipio.setId(Integer.valueOf(data));
				is_field_id = false;
			} else if (is_field_geocodmu) {
				currMunicipio.setCd_geocodmu(Integer.valueOf(data));
				is_field_geocodmu = false;
			} else if (is_field_municipio) {
				currMunicipio.setNm_municipio(data);
				is_field_municipio = false;
			} else if (is_field_uf) {
				currMunicipio.setNm_uf(data);
				is_field_uf = false;
			} else if (is_field_long) {
				currMunicipio.setLongitude(Double.valueOf(data));
				is_field_long = false;
			} else if (is_field_lat) {
				currMunicipio.setLatitude(Double.valueOf(data));
				is_field_lat = false;
			} else if (is_field_alt) {
				currMunicipio.setAltitude(Double.valueOf(data));
				is_field_alt = false;
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}

	public boolean parse() {
		boolean ok = false;
		try {
			// creates and returns new instance of SAX-implementation:
			SAXParserFactory factory = SAXParserFactory.newInstance();
			//factory.setValidating(true);

			// create SAX-parser...
			SAXParser parser = factory.newSAXParser();
			
			parser.parse(KML_NAME, this);
			
			ok = true;

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		
		return ok;
	}
}
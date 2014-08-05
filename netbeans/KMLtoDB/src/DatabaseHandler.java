
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHandler {

	public static String[] ESTADOS = new String[]{
		"RONDÔNIA", "ACRE", "AMAZONAS", "RORAIMA", "PARÁ", "AMAPÁ",
		"TOCANTINS", "MARANHÃO", "PIAUÍ", "CEARÁ", "RIO GRANDE DO NORTE",
		"PARAÍBA", "PERNAMBUCO", "ALAGOAS", "SERGIPE", "BAHIA",
		"MINAS GERAIS", "ESPÍRITO SANTO", "RIO DE JANEIRO", "SÃO PAULO",
		"PARANÁ", "SANTA CATARINA", "RIO GRANDE DO SUL", "MATO GROSSO DO SUL",
		"MATO GROSSO", "GOIÁS", "DISTRITO FEDERAL"};
	
	private HashMap<String, Integer> estadoToId;
	private Connection connection;
	private Statement statement;
	private int counter;
	private long insertionTime;
	private long parseTime;

	public void createConnection() throws SQLException {
		// create a database connection
		connection = DriverManager.getConnection("jdbc:sqlite:municipios.db");
		connection.setAutoCommit(false);
		
		statement = connection.createStatement();
		statement.setQueryTimeout(30);  // set timeout to 30 sec.

		statement.executeUpdate("delete from municipios where 1=1");
		statement.executeUpdate("update sqlite_sequence set seq=0 where name='municipios'");
		connection.commit();
		
		ResultSet rs = statement.executeQuery("select * from estados");
		estadoToId = new HashMap<>(rs.getFetchSize());
		
		System.out.println("ESTADOS");
		System.out.println("ID; NOME");
		
		while (rs.next()) {
			String nome = rs.getString("NOME");
			int id = rs.getInt("ESTADO_ID");
			System.out.println(id + "; " + nome);
			estadoToId.put(nome, id);
		}
		System.out.println();
	}

	public void closeConnection() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
	
	public void commit() throws SQLException {
		connection.commit();
	}

	public int loadKML(String filename) {
		counter = 0;
		insertionTime = 0;

		estadoToId = new HashMap<>(ESTADOS.length);
		for (int i = 0; i < ESTADOS.length; i++) {
			estadoToId.put(ESTADOS[i], i+1);
		}

		KML_Parser parser = new KML_Parser(this, filename);
		System.out.println("MUNICIPIOS");
		System.out.println("NOME; ID; ESTADO; CODIGO; "
				+ "LONGITUDE; LATITUDE; ALTITUDE");
		
		long tstart = System.currentTimeMillis();
		parser.parse();
		parseTime = System.currentTimeMillis() - tstart;
		
		return counter;
	}

	public void insertMunicipio(Municipio mu) {
		// inserting data
		PreparedStatement prep;
		long tstart = System.currentTimeMillis();
		
		try {
			/* CREATE TABLE "municipios" (
				`MUNICIPIO_ID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
				`NOME`			VARCHAR(50) NOT NULL,
				`ID`			INTEGER NOT NULL,
				`ESTADO_ID`		INTEGER NOT NULL,
				`CODIGO`		INTEGER NOT NULL,
				`LONGITUDE`		REAL NOT NULL,
				`LATITUDE`		REAL NOT NULL,
				`ALTITUDE`		REAL NOT NULL
			);*/
			prep = connection.prepareStatement(
					"INSERT INTO municipios VALUES(?,?,?,?,?,?,?,?);");
			prep.setString(2, mu.getNm_municipio());
			prep.setInt(3, mu.getId());
			prep.setInt(4, estadoToId.get(mu.getNm_uf()));
			prep.setInt(5, mu.getCd_geocodmu());
			prep.setDouble(6, mu.getLongitude());
			prep.setDouble(7, mu.getLatitude());
			prep.setDouble(8, mu.getAltitude());
			prep.execute();
			
			counter++;
			System.out.println(mu.toString());
			
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
		insertionTime += (System.currentTimeMillis() - tstart);
		
	}

	public long getInsertionTime() {
		return insertionTime;
	}

	public long getParseTime() {
		return parseTime;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		String KML_NAME = "BR_Localidades_2010_v1.kml";
		DatabaseHandler handler = new DatabaseHandler();
		int numMunicipios;
					
		try {		
			
			handler.createConnection();
			
			long tstart = System.currentTimeMillis();
			numMunicipios = handler.loadKML(KML_NAME);
			handler.commit();
			long tend = System.currentTimeMillis();
			
			
			long tparse = (handler.getParseTime() - handler.getInsertionTime());
			long tinsert = handler.getInsertionTime();
			long ttotal = tend-tstart;
			float tmed = (float)(tend-tstart)/numMunicipios;
			
			System.out.println("\nNumero de municipios adicionados: " + numMunicipios);
			System.out.println("Tempo parse: " + tparse + "ms");
			System.out.println("Tempo insercao: " + tinsert + "ms");
			System.out.println("Tempo médio de parse+insercao: " + tmed + "ms");
			System.out.println("Tempo total: " + ttotal + "ms");
			
			handler.closeConnection();
			
		} catch (SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}

	}
}
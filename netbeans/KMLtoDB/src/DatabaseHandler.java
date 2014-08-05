import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DatabaseHandler
{  
	public static String[] ESTADOS = new String[] {
		"RONDÔNIA",
		"ACRE",
		"AMAZONAS",
		"RORAIMA",
		"PARÁ",
		"AMAPÁ",
		"TOCANTINS",
		"MARANHÃO",
		"PIAUÍ",
		"CEARÁ",
		"RIO GRANDE DO NORTE",
		"PARAÍBA",
		"PERNAMBUCO",
		"ALAGOAS",
		"SERGIPE",
		"BAHIA",
		"MINAS GERAIS",
		"ESPÍRITO SANTO",
		"RIO DE JANEIRO",
		"SÃO PAULO",
		"PARANÁ",
		"SANTA CATARINA",
		"RIO GRANDE DO SUL",
		"MATO GROSSO DO SUL",
		"MATO GROSSO",
		"GOIÁS",
		"DISTRITO FEDERAL" };
    
	private HashMap<String, Integer> estadoToId;
  
  
  
  public static void main(String[] args) throws ClassNotFoundException
  {
    // load the sqlite-JDBC driver using the current class loader
    Class.forName("org.sqlite.JDBC");

    Connection connection = null;
    try
    {
      HashMap<String, Integer> estadoToId = 
				new HashMap<String, Integer>(ESTADOS.length);
      for(int i =0; i< ESTADOS.length; i++) {
        estadoToId.put(ESTADOS[i], i);
      }
      KMLCopyToDB kmlToDb = new KMLCopyToDB(connection, estadoToId);
      
      
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:./municipiosV2.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.

      statement.executeUpdate("drop table if exists person");
      statement.executeUpdate("create table person (id integer, name string)");
      statement.executeUpdate("insert into person values(1, 'leo')");
      statement.executeUpdate("insert into person values(2, 'yui')");
      
      ResultSet rs = statement.executeQuery("select * from person");
      while(rs.next())
      {
        // read the result set
        System.out.println("name = " + rs.getString("name"));
        System.out.println("id = " + rs.getInt("id"));
      }
    }
    catch(SQLException e)
    {
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
    finally
    {
      try
      {
        if(connection != null)
          connection.close();
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e);
      }
    }
  }
}
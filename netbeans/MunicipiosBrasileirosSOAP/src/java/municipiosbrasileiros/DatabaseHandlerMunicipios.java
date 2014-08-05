
package municipiosbrasileiros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author fernando
 */
public class DatabaseHandlerMunicipios {
  
    public static Connection getConnection() {
        try {
            // carregar driver: sqlite-jdbc-3.7.15-M1
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e1) {
            return null;
        }
        
        try {
            // 1. criar uma conexao no netbeans:
            //    https://forums.netbeans.org/post-95380.html#95380
            // 2. criar um connection pool no glasfish (pode ser feito pelo netbeans)
            //    http://sourceforge.net/p/sqlite-connpool/wiki/How%20To%20Setup%20a%20SQLite%20Connection%20Pool%20on%20Glassfish/
            // 3. obter a conexao
            String url = "jdbc:sqlite:/home/fernando/NetBeansProjects/MunicipiosBrasileirosREST/municipios.db";
            Connection con = DriverManager.getConnection(url);
            
            return con;
        
        } catch (SQLException sql1) {
            System.out.println("Erro: "+sql1);
            return null;
        }
        
    }
}


package municipiosbrasileiros;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author fernando
 */
public class DAOMunicipios {
  
    private Connection con;
    private Statement consulta1, consulta2;
    
    public DAOMunicipios() {
        con = DatabaseHandlerMunicipios.getConnection();
        try {
            consulta1 = con.createStatement();
            consulta2 = con.createStatement();
        } catch (SQLException ex) {
            System.out.println("ERRO EM CONSTRUTOR: "+ex);
        }
    }
      
    public Municipio find(int codigo) {
        try {
            String query = "SELECT * FROM `municipios` WHERE `CODIGO`='" + codigo + "'";
            ResultSet cursor = consulta1.executeQuery(query);
            if (cursor.next()) {
                int estado = cursor.getInt(4);
                ResultSet cursor2 = consulta2.executeQuery("SELECT `SIGLA` FROM `estados`" + "WHERE `ESTADO_ID`='" + estado + "'");
                
                Municipio municipio = new Municipio();
                municipio.setNm_municipio(cursor.getString(2));
                municipio.setId(cursor.getInt(3));
                municipio.setNm_uf(cursor2.getString(1));
                municipio.setCd_geocodmu(cursor.getInt(5));
                municipio.setLongitude(cursor.getDouble(6));
                municipio.setLatitude(cursor.getDouble(7));
                municipio.setAltitude(cursor.getDouble(8));
                return municipio;
            } else {
              return null;
            }            
            
        } catch (SQLException sql1) {
            System.out.println("ERRO EM FIND: "+sql1);
            return null;
        }
    }
}

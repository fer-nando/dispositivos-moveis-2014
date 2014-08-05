
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
public class DAOEstados {
  
    private Connection con;
    private Statement consulta1;
    
    public DAOEstados() {
        con = DatabaseHandlerMunicipios.getConnection();
        try {
            consulta1 = con.createStatement();
        } catch (SQLException ex) {
            System.out.println("ERRO EM CONSTRUTOR: "+ex);
        }
    }  
    
    public ArrayList<Municipio> find(String uf) {
        try {
            ArrayList<Municipio> municipios = new ArrayList<Municipio>();
            String query = "SELECT * FROM `municipios` WHERE "
              + "`ESTADO_ID`=(SELECT `ESTADO_ID` FROM `estados`"
              + "WHERE `SIGLA`='" + uf + "') ORDER BY `NOME` ASC";
            ResultSet cursor = consulta1.executeQuery(query);
            while (cursor.next()) {
                Municipio municipio = new Municipio();     
                municipio.setNm_municipio(cursor.getString(2));
                municipio.setId(cursor.getInt(3));
                municipio.setCd_geocodmu(cursor.getInt(5));
                municipio.setLongitude(cursor.getDouble(6));
                municipio.setLatitude(cursor.getDouble(7));
                municipio.setAltitude(cursor.getDouble(8));
                municipios.add(municipio);
            }
            return municipios;
            
        } catch (SQLException sql1) {
            System.out.println("ERRO EM FIND: "+sql1);
            return null;
        }
    }
    
    public ArrayList<Estado> listAll() {
        try {
            ArrayList<Estado> estados = new ArrayList<Estado>();
            ResultSet rs = consulta1.executeQuery("SELECT * FROM `estados`");
            while (rs.next()) {
                Estado estado = new Estado();
                estado.setId(rs.getInt(1));
                estado.setNome(rs.getString(2));
                estado.setUf(rs.getString(3));
                estados.add(estado);
            }
            return estados;
        } catch (SQLException sql1) {
            System.out.println("ERRO EM LIST: "+sql1);
            return null;
        }
        
    }
  
}

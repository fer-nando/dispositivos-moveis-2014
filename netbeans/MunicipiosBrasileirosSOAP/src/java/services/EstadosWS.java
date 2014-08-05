
package services;

import java.util.ArrayList;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import municipiosbrasileiros.DAOEstados;
import municipiosbrasileiros.Estado;
import municipiosbrasileiros.Municipio;

/**
 *
 * @author fernando
 */
@WebService(serviceName = "EstadosWS")
@Stateless()
public class EstadosWS {

  private DAOEstados estadosDAO;

  public EstadosWS() {
      estadosDAO = new DAOEstados();
  }

  /**
   * Web service operation
   */
  @WebMethod(operationName = "uf")
  public String uf(@WebParam(name = "sigla") String sigla) {
    String result = "";
    if(sigla != null) {
      ArrayList<Municipio> municipios = estadosDAO.find(sigla);
      for (Municipio municipio : municipios) {
          result += municipio.toString() + '\n';
      }
    } else {
      ArrayList<Estado> estados = estadosDAO.listAll();
      for (Estado estado : estados) {
          result += estado.toString() + '\n';
      }
    }
    return result;
  }
    
  
}

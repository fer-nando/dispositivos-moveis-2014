
package services;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import municipiosbrasileiros.DAOMunicipios;
import municipiosbrasileiros.Municipio;

/**
 *
 * @author fernando
 */
@WebService(serviceName = "MunicipiosWS")
@Stateless()
public class MunicipiosWS {
  
    private DAOMunicipios municipiosDAO;
    
    public MunicipiosWS() {
        municipiosDAO = new DAOMunicipios();
    }

  /**
   * Web service operation
   */
  @WebMethod(operationName = "municipio")
  public String operation(@WebParam(name = "codigo") int codigo) {
    String result = "";
    Municipio municipio = municipiosDAO.find(codigo);
    result += municipio.showData() + '\n';
    return result;
  }

  
  
  
}

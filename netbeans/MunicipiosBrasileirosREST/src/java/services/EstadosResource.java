
package services;

import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import municipiosbrasileiros.DAOEstados;
import municipiosbrasileiros.Estado;
import municipiosbrasileiros.Municipio;

/**
 *
 * @author fernando
 */
@Path("uf")
public class EstadosResource {
  
  @Context
    private UriInfo context;

    private DAOEstados estadosDAO;
    
    public EstadosResource() {
        estadosDAO = new DAOEstados();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getEstados() {
        String result = "";
        ArrayList<Estado> estados = estadosDAO.listAll();
        for (Estado estado : estados) {
            result += estado.toString() + '\n';
        }
        return result;
    }

    @GET
    @Path("{ufarg}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLivroPorID(@PathParam("ufarg") String uf) {
        String result = "";
        ArrayList<Municipio> municipios = estadosDAO.find(uf);
        for (Municipio municipio : municipios) {
            result += municipio.toString() + '\n';
        }
        return result;
    }
    
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
  
}


package services;

import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import municipiosbrasileiros.DAOMunicipios;
import municipiosbrasileiros.Municipio;

/**
 *
 * @author fernando
 */
@Path("municipio")
public class MunicipiosResource {
  
    @Context
    private UriInfo context;

    private DAOMunicipios municipiosDAO;
    
    public MunicipiosResource() {
        municipiosDAO = new DAOMunicipios();
    }

    @GET
    @Path("{codigoarg}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLivroPorID(@PathParam("codigoarg") int codigo) {
        String result = "";
        Municipio municipio = municipiosDAO.find(codigo);
        result += municipio.showData() + '\n';
        return result;
    }
    
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
  
}

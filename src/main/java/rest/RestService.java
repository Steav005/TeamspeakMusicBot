package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/{token}")
public class RestService {
    @GET
    @Path("/pause")
    public Response pause(@PathParam("token") String token){
        int userID = MongoLinker.getInstance().getUserIDFromToken(token);
        System.out.println(token);
        if(userID == -1) return Response.status(401).build();

        return Response.status(200).build();
    }
}

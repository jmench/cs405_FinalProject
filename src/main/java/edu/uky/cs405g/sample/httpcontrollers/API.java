package edu.uky.cs405g.sample.httpcontrollers;
//
// Sample code used with permission from Dr. Bumgardner
//
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.uky.cs405g.sample.Launcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

@Path("/api")
public class API {

    private Type mapType;
    private Gson gson;

    public API() {
        mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        gson = new Gson();
    }

    //curl http://localhost:9990/api/status
    //{"status_code":1}
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthcheck() {
        String responseString = "{\"status_code\":0}";
        try {
            //Here is where you would put your system test, 
			//but this is not required.
            //We just want to make sure your API is up and active/
            //status_code = 0 , API is offline
            //status_code = 1 , API is online
            responseString = "{\"status_code\":1}";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
				.header("Access-Control-Allow-Origin", "*").build();
    } // healthcheck()

    //curl http://localhost:9998/api/listusers
    //{"1":"@paul","2":"@chuck"}
    @GET
    @Path("/listusers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers() {
        String responseString = "{}";
        try {
            Map<String, String> teamMap = Launcher.dbEngine.getUsers();
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } // listUsers()


    //curl -d '{"foo":"silly1","bar":"silly2"}' \
	//     -H "Content-Type: application/json" \
    //     -X POST  http://localhost:9990/api/exampleJSON
	//
    //{"status_code":1, "foo":silly1, "bar":silly2}
    @POST
    @Path("/exampleJSON")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response exampleJSON(InputStream inputData) {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            Map<String, String> myMap = gson.fromJson(jsonString, mapType);
            String fooval = myMap.get("foo");
            String barval = myMap.get("bar");
            //Here is where you would put your system test,
            //but this is not required.
            //We just want to make sure your API is up and active/
            //status_code = 0 , API is offline
            //status_code = 1 , API is online
            responseString = "{\"status_code\":1, "
					+"\"foo\":\""+fooval+"\", "
					+"\"bar\":\""+barval+"\"}";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } // exampleJSON()

    //curl http://localhost:9990/api/exampleGETBDATE/2
    //{"bdate":"1968-01-26"}
    @GET
    @Path("/exampleGETBDATE/{idnum}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exampleBDATE(@PathParam("idnum") String idnum) {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.getBDATE(idnum);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } // exampleBDATE

    //curl -d '{"handle":"@cooldude42", "password":"mysecret!", "fullname":"Angus Mize", "location":"Kentucky", "xmail":"none@nowhere.com", "bdate":"1970-07-01"}'
    //     -H "Content-Type: application/json"
    //     -X POST http://localhost:9990/api/createuser
    //
    //{"status_code":4} where integer is idnum of new user
    @POST
    @Path("/createuser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createuser(@PathParam("handle") String handle, @PathParam("password") String password, @PathParam("fullname") String fullname, @PathParam("location") String location, @PathParam("xmail") String xmail, @PathParam("bdate") String bdate)  {
        String responseString = "{\"status\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.createuser(handle, password, fullname, location, xmail, bdate);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/seeuser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response seeuser(@PathParam("handle") String handle, @PathParam("password") String password)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.seeuser(handle, password);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    }

    /*
    @GET
    @Path("/suggestions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response suggestions(@PathParam("handle") String handle, @PathParam("password") String password)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.suggestions(handle, password);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } 

    @GET
    @Path("/poststory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response poststory(@PathParam("handle") String handle, @PathParam("password") String password, @PathParam("chapter") String chapter, @PathParam("url") String url)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.poststory(handle, password, chapter, url);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/reprint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reprint(@PathParam("handle") String handle, @PathParam("password") String password, @PathParam("likeit") String likeit)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.reprint(handle, password, likeit);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } 

    @GET
    @Path("/follow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response follow(@PathParam("handle") String handle, @PathParam("password") String password)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.follow(handle, password);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } 

    @GET
    @Path("/unfollow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unfollow(@PathParam("handle") String handle, @PathParam("password") String password)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.unfollow(handle, password);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/block")
    @Produces(MediaType.APPLICATION_JSON)
    public Response block(@PathParam("handle") String handle, @PathParam("password") String password)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.block(handle, password);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } 

    @GET
    @Path("/timeline")
    @Produces(MediaType.APPLICATION_JSON)
    public Response timeline(@PathParam("handle") String handle, @PathParam("password") String password, @PathParam("newest") String newest, @PathParam("oldest") String oldest)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.timeline(handle, password, newest, oldest);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } 


     */







} // API.java

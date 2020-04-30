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
    // {"status_code":"4"} where number is idnum of new user
    // {"status":"-2", "error":"SQL Constraint Exception"}
    @POST
    @Path("/createuser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createuser(InputStream inputData)  {
        // responseString is what will print out on the terminal after the api call
        // Gets modified in the DBEngine file
        String responseString = "{\"status\":0}";

        //This string reads the input from the curl call
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!,...}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");
            String fullname = myMap.get("fullname");
            String location = myMap.get("location");
            String xmail = myMap.get("xmail");
            String bdate = myMap.get("bdate");

            // Call the function in DBEngine that adds to the databas
            Map<String,String> teamMap = Launcher.dbEngine.createuser(handle, password, fullname, location, xmail, bdate);
            // Turn Map returned from DBEngine to JSON for output
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

    // curl -d '{"handle":"@cooldude42", "password":"mysecret!"}'
    // -H "Content-Type: application/json"
    // -X POST http://localhost:9990/api/seeuser/2
    //
    // {"status":"1", "handle":"@carlos", "fullname":"Carlos Mize", "location":"Kentucky", "email":"carlos@notgmail.com", "bdate":"1970-01-26","joined":"2020-04-01"}
    // {} no match found, could be blocked, user doesn't know
    @POST
    @Path("/seeuser/{idnum}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response seeuser(InputStream inputData, @PathParam("idnum") String idnum)  {
        String responseString = "{\"status_code\":0}";

        //This string reads the input from the curl call
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");

            Map<String,String> teamMap = Launcher.dbEngine.seeuser(handle, password, idnum);
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


    // curl -d '{"handle":"@cooldude42", "password":"mysecret!"}'
    // -H "Content-Type: application/json"
    // -X POST http://localhost:9990/api/suggestions
    //
    // Output status > 0 is number of suggestions returned
    // {"status":"3", "idnums":"1,2,4", "handles":"@paul,@carlos","@fake"}
    // {"status":"0", "error":"no suggestions"}
    @POST
    @Path("/suggestions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response suggestions(InputStream inputData)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");

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


    // curl -d '{"handle":"@cooldude42", "password":"mysecret!", "chapter":"I ate at Mario's!", "url":"http://imagesite.dne/marios.jpg"}'
    // -H "Content-Type: application/json"
    // -X POST http://localhost:9990/api/poststory
    //
    // {"status":"1"}
    // {"status":"0", "error":"invalid expires date"}
    @POST
    @Path("/poststory")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response poststory(InputStream inputData)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!, chapter=First Story, url=...}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");
            String chapter = myMap.get("chapter");
            String url = myMap.get("url");
            String expires = myMap.get("expires");

            Map<String,String> teamMap = Launcher.dbEngine.poststory(handle, password, chapter, url, expires);
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

    // curl -d '{"handle":"@cooldude42", "password":"mysecret!", "likeit":true}'
    // -H "Content-Type: application/json"
    // -X POST http://localhost:9990/api/reprint/45
    //
    // {"status":"1"}
    // {"status":"0", "error":"blocked"}
    // {"status":"0", "error":"story not found"}
    @POST
    @Path("/reprint/{sidnum}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reprint(InputStream inputData, @PathParam("sidnum") String sidnum)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!, likeit=true}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");
            String likeit = myMap.get("likeit");
            Map<String,String> teamMap = Launcher.dbEngine.reprint(handle, password, likeit, sidnum);
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


    // Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/follow/2 (Links to an external site.)
    // 2 = Identity.idnum
    // Output: {"status":"1"}
    // Output: {"status":"0", "error":"blocked"}
    @POST
    @Path("/follow/{idnum}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response follow(InputStream inputData, @PathParam("idnum") String idnum)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");

            Map<String,String> teamMap = Launcher.dbEngine.follow(handle, password, idnum);
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

    // Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/unfollow/2 (Links to an external site.)
    // 2 = Identity.idnum
    // Output: {"status":"1"}
    // Output: {"status":"0", "error":"not currently followed"}
    @POST
    @Path("/unfollow/{idnum}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unfollow(InputStream inputData, @PathParam("idnum") String idnum)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");

            Map<String,String> teamMap = Launcher.dbEngine.unfollow(handle, password, idnum);
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


    // Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/block/2 (Links to an external site.)
    // 2 = Identity.idnum
    // Output: {"status":"1"}
    // Output: {"status":"0", "error":"DNE"}
    @POST
    @Path("/block/{idnum}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response block(InputStream inputData, @PathParam("idnum") String idnum)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");

            Map<String,String> teamMap = Launcher.dbEngine.block(handle, password, idnum);
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


    // Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!", "newest":"2020-04-02 15:33:59", "oldest":"2020-03-29 00:00:01"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/timeline (Links to an external site.)
    //
    // This is the most complicated API. You'll need a single SQL statement to get
    // all the points for the rubric. This is a challenge to make the DBMS do all the hard
    // work! Think through what is needed to JOIN all the tables to produce this answer set.
    // Your task is to create a single SQL query based on the requester's handle, producing
    // all Story entries for all handles they follow, including any Reprint/retweets
    // (ie, where Reprint.likeit=false). Only list those that have tstamps between the
    // interval (older than "newest" and newer than "oldest" submitted). You can
    // assume newest and oldest are valid values. You will have to deal with all
    // tables, including the Block table. (if someone retweets a Story of someone that
    // has blocked you, it should not show on your timeline).
    // [EDIT 04/16] Here is the output for timeline. Enumerate the key for each story/reprint
    // Then the value/righthand side will be a JSON object itself, curly brace,
    // then (5) key/value pairs and a closing brace }. In the example code you
    // can put this in a Map<String,String>. We're not worried about using this
    // output, we just want to see it.
    // [EDIT 04/17] I left off sidnum, but it is needed.]
    // Output: {"0":"{\"type\":\"story\",\"author\":\"@cooldude44\",\"sidnum\":\"14\",\"chapter\":\"Just some set math, SQL is super fun!\",\"posted\":\"2020-04-16 15:37:48\"}","1":"{\"type\":\"reprint\",\"author\":\"@cooldude44\",\"sidnum\":\"15\",\"chapter\":\"JSON objects are fun and useful!\",\"posted\":\"2020-04-15 10:37:44\"}","status":"2"}
    // Output: {"status":"0"}
    @POST
    @Path("/timeline")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response timeline(InputStream inputData)  {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            // Here its is parsing the input into a JSon string
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            // Create a map of the jsonString in format {handle=@cooldude42, password=mysecret!,...}
            Map<String, String> myMap = gson.fromJson(jsonString, mapType);

            // Create strings to get values from the map
            String handle = myMap.get("handle");
            String password = myMap.get("password");
            String newest = myMap.get("newest");
            String oldest = myMap.get("oldest");

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

} // API.java

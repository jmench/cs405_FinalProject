package edu.uky.cs405g.sample.database;

// Used with permission from Dr. Bumgardner

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.*;


public class DBEngine {
    private DataSource ds;
    public boolean isInit = false;
    public DBEngine(String host, String database, String login, 
		String password) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            String dbConnectionString = null;
            if(database == null) {
                dbConnectionString ="jdbc:mysql://" + host + "?" 
					+"user=" + login  +"&password=" + password 
					+"&useUnicode=true&useJDBCCompliantTimezoneShift=true"
					+"&useLegacyDatetimeCode=false&serverTimezone=UTC"; 
			} else {
                dbConnectionString ="jdbc:mysql://" + host + "/" + database
				+ "?" + "user=" + login  +"&password=" + password
				+ "&useUnicode=true&useJDBCCompliantTimezoneShift=true"
				+ "&useLegacyDatetimeCode=false&serverTimezone=UTC";
            }
            ds = setupDataSource(dbConnectionString);
            isInit = true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    } // DBEngine()

    public static DataSource setupDataSource(String connectURI) {
        //
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory = null;
            connectionFactory = 
				new DriverManagerConnectionFactory(connectURI, null);
        //
        // Next we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, null);

        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory);

        // Set the factory's pool property to the owning pool
        poolableConnectionFactory.setPool(connectionPool);

        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //
        PoolingDataSource<PoolableConnection> dataSource =
                new PoolingDataSource<>(connectionPool);

        return dataSource;
    } // setupDataSource()


    // This will return the idnum of the current user
    // Returns -10 if invalid credentials
    public int isCorrectCredentials(String handle, String pass) {
        System.out.println("Checking user credentials...");
        Integer userId = -10;
        PreparedStatement stmt = null;

        try {
            Connection conn = ds.getConnection();
            String queryString = null;
            // Query the DB to retrieve the id of the possible user
            queryString = "SELECT idnum FROM Identity where handle = ? and pass = ?";
            stmt = conn.prepareStatement(queryString);

            stmt.setString(1,handle);
            stmt.setString(2, pass);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("User credentials are correct.");
                userId = rs.getInt("idnum");
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return userId;
    }

    // This will return the idnum of the story's publisher
    // Returns 0 if story does not exist
    public int doesStoryExist(String sidnum) {
        System.out.println("Checking id story exists...");
        Integer userId = 0;
        PreparedStatement stmt = null;

        try {
            Connection conn = ds.getConnection();
            String queryString = null;
            // Query the DB to retrieve the id of the possible user
            queryString = "SELECT idnum FROM Story where sidnum = ?";
            stmt = conn.prepareStatement(queryString);

            stmt.setString(1,sidnum);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("Story exists...");
                userId = rs.getInt("idnum");
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Story does not exist...");
        }
        return userId;
    }

    // This will return the 1 if the current user is blocked by publisher
    // Returns 0 if not blocked
    public int isUserBlocked(Integer publisherId, Integer currUser) {
        System.out.println("Checking if current user is blocked...");
        Integer isBlocked = 0;
        PreparedStatement stmt = null;

        try {
            Connection conn = ds.getConnection();
            String queryString = null;
            // Query the DB to retrieve the blknum of the block (if exists)
            queryString = "SELECT blknum FROM Block where idnum = ? and blocked = ?";
            stmt = conn.prepareStatement(queryString);

            stmt.setString(1,Integer.toString(publisherId));
            stmt.setString(2,Integer.toString(currUser));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("User is blocked");
                isBlocked = 1;
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            System.out.println("User is not blocked...");
        }
        return isBlocked;
    }

    public Map<String,String> getUsers() {
        Map<String,String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
            queryString = "SELECT * FROM Identity";
            stmt = conn.prepareStatement(queryString);
			// No parameters, so no binding needed.
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String userId = Integer.toString(rs.getInt("idnum"));
                String userName = rs.getString("handle");
                userIdMap.put(userId, userName);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return userIdMap;
    } // getUsers()

    public Map<String,String> getBDATE(String idnum) {
        Map<String,String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        Integer id = Integer.parseInt(idnum);
        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
// Here is a statement, but we want a prepared statement.
//            queryString = "SELECT bdate FROM Identity WHERE idnum = "+id;
//            
            queryString = "SELECT bdate FROM Identity WHERE idnum = ?";
// ? is a parameter placeholder
            stmt = conn.prepareStatement(queryString);
			stmt.setInt(1,id);
// 1 here is to denote the first parameter.
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bdate = rs.getString("bdate");
                userIdMap.put("bdate", bdate);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return userIdMap;
    } // getBDATE()
  
    public Map<String,String> createuser(String handle, String pass, String fullname, String location, String xmail, String bdate){
        Map<String,String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
            queryString = "INSERT INTO Identity (handle, pass, fullname, location, email, bdate) VALUES(?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, handle);
			stmt.setString(2, pass);
			stmt.setString(3, fullname);
			stmt.setString(4, location);
			stmt.setString(5, xmail);
			stmt.setString(6, bdate);

			Integer result = stmt.executeUpdate();

			if (result == 0) {
			    System.out.println("Failed to create user...");
                userIdMap.put("status", "-2");
                userIdMap.put("error", "SQL Constraint Exception");
			}

			try (ResultSet userId = stmt.getGeneratedKeys()) {
			    if(userId.next()) {
			        System.out.println("Creating new user with idnum: " + Integer.toString(userId.getInt(1)));
			        String idNum = String.valueOf(userId.getInt(1));
			        userIdMap.put("status", idNum);
                }
            }

			stmt.close();
			conn.close();
	    }
	    catch(Exception ex)
	    {
	    ex.printStackTrace();
	    }
	    return userIdMap;
    } // createuser()
	
    public Map<String,String> seeuser(String handle, String password, String idnum){
	Map<String,String> userIdMap = new LinkedHashMap<>();

    // See if current user even exists
    Integer userExists = isCorrectCredentials(handle, password);
    // If user does not exist, return the error
    if (userExists == -10) {
        userIdMap.put("status_code", Integer.toString(userExists));
        userIdMap.put("error", "invalid credentials");
    }
    // Else attempt to fetch specified idnum info
    else {
        System.out.println("Printing user info at specified idnum...");
        PreparedStatement stmt = null;

        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
            // Get all information of user with idnum
            queryString = "SELECT handle, fullname, location, email, bdate, joined FROM Identity WHERE idnum = ?";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, idnum);

            ResultSet rs = stmt.executeQuery();

            // Place all user info into Map to return to API
            while (rs.next()) {
                String userHandle = rs.getString("handle");
                String userFullName = rs.getString("fullname");
                String userLocation = rs.getString("location");
                String userEmail = rs.getString("email");
                String userBdate = rs.getString("bdate");
                String userJoined = rs.getString("joined");

                userIdMap.put("status", "1");
                userIdMap.put("handle", userHandle);
                userIdMap.put("fullname", userFullName);
                userIdMap.put("location", userLocation);
                userIdMap.put("email", userEmail);
                userIdMap.put("bdate", userBdate);
                userIdMap.put("joined", userJoined);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
	return userIdMap;
    } // seeuser()

    public Map<String,String> reprint(String handle, String password, String likeit, String sidnum){
        Map<String,String> userIdMap = new LinkedHashMap<>();

        // See if current user even exists
        Integer currUser = isCorrectCredentials(handle, password);
        // If user does not exist, return the error
        if (currUser == -10) {
            userIdMap.put("status_code", Integer.toString(currUser));
            userIdMap.put("error", "invalid credentials");
            return userIdMap;
        }
        // See if story exists
        Integer publisherId = doesStoryExist(sidnum);
        // If story does not exist, return the erro
        if (publisherId == 0) {
            userIdMap.put("status_code", Integer.toString(publisherId));
            userIdMap.put("error", "story not found");
            return userIdMap;
        }
        // See if current user is blocked by story publisher
        Integer isBlocked = isUserBlocked(publisherId, currUser);
        // if the user is blocked, return the error
        if (isBlocked == 1) {
            userIdMap.put("status_code", "0");
            userIdMap.put("error", "blocked");
            return userIdMap;
        }
        // Else user exists, story exists, and not blocked, so attempt to like the story
        else {
            System.out.println("Liking user story...");
            Integer likeVal = 0;
            if (likeit.equals("true")) {
                likeVal = 1;
            }
            PreparedStatement stmt = null;

            try
            {
                Connection conn = ds.getConnection();
                String queryString = null;
                // Create query to insert into Reprint
                queryString = "INSERT INTO Reprint (idnum, sidnum, likeit, newstory) VALUES(?, ?, ?, ?)";
                stmt = conn.prepareStatement(queryString);
                stmt.setString(1, Integer.toString(currUser));
                stmt.setString(2, sidnum);
                stmt.setString(3, Integer.toString(likeVal));
                stmt.setString(4, sidnum);

                Integer result = stmt.executeUpdate();

                if (result == 0) {
                    System.out.println("Failed to like story...");
                    userIdMap.put("status", "-2");
                    userIdMap.put("error", "SQL Constraint Exception");
                } else {
                    System.out.println("Successfully reprinted story!");
                    userIdMap.put("status", "1");
                }

                stmt.close();
                conn.close();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return userIdMap;
    } // reprint()

} // class DBEngine

/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Amazon {

   // stores user id for user that is currently logged in
   public static int current_user_id ;

   // stores user type for user that is currently logged in

   public static String current_user_type;

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Amazon store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Amazon(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Amazon

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public static double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Amazon.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Amazon esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Amazon object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Amazon (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");
                System.out.println("10. View All Orders for Store");
                System.out.println("11. View and Edit User and Product Info");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewPopularProducts(esql); break;
                   case 8: viewPopularCustomers(esql); break;
                   case 9: placeProductSupplyRequests(esql); break;
                   case 10:viewAllOrders(esql);break;
                   case 11: adminViewEdit(esql);break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
            System.out.printf("Welcome %s \n", name);
            query = String.format("SELECT UserID FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
            List<List<String>> user_id_result = esql.executeQueryAndReturnResult(query);
            current_user_id = Integer.parseInt(user_id_result.get(0).get(0));
            
            query = String.format("SELECT type FROM Users WHERE userID = %d", current_user_id);
            List<List<String>> user_type_result = esql.executeQueryAndReturnResult(query);
            current_user_type = (user_type_result.get(0).get(0)).trim();
            System.out.println(current_user_type);
            return name;
         }
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Amazon esql) {
      try{

         String query = String.format("SELECT latitude FROM Users WHERE userID = %d", current_user_id);
         List<List<String>> user_lat_result = esql.executeQueryAndReturnResult(query);
         double user_lat = Double.parseDouble(user_lat_result.get(0).get(0));

         query = String.format("SELECT longitude FROM Users WHERE userID = %d", current_user_id);
         List<List<String>> user_lon_result = esql.executeQueryAndReturnResult(query);
         double user_lon = Double.parseDouble(user_lon_result.get(0).get(0));

         List<Integer> valid_store_ids = new ArrayList<>();
         for(int i=1;i<=20;i++){

            query = String.format("SELECT latitude FROM Store WHERE storeID = %d", i);
            List<List<String>> store_lat_result = esql.executeQueryAndReturnResult(query);
            double store_lat = Double.parseDouble(store_lat_result.get(0).get(0));

            query = String.format("SELECT longitude FROM Store WHERE storeID = %d", i);
            List<List<String>> store_lon_result = esql.executeQueryAndReturnResult(query);
            double store_lon = Double.parseDouble(store_lon_result.get(0).get(0));

            if(calculateDistance (user_lat, user_lon, store_lat, store_lon)<= 30){
               valid_store_ids.add(i);
            }
         }
         query = String.format("SELECT storeid, latitude, longitude FROM STORE WHERE storeID = ");
         for(int id = valid_store_ids.size()-1;id>=0;id--){
            if(id==0)
               query += String.format("%s",id);
            else
               query += String.format("%s OR storeID = ",id);
         }
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("Total row(s): " + rowCount);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void viewProducts(Amazon esql) {
      try{
         Scanner input = new Scanner(System.in);
         System.out.print("\tEnter Store ID: ");
         int store_id = input.nextInt();

         String query = String.format("SELECT productName, numberOfUnits, pricePerUnit FROM Product WHERE storeID = %d", store_id);
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("Total row(s): " + rowCount);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void placeOrder(Amazon esql) {
      try {
         String query = String.format("SELECT latitude FROM Users WHERE userID = %d", current_user_id);
         List<List<String>> user_lat_result = esql.executeQueryAndReturnResult(query);
         double user_lat = Double.parseDouble(user_lat_result.get(0).get(0));

         query = String.format("SELECT longitude FROM Users WHERE userID = %d", current_user_id);
         List<List<String>> user_lon_result = esql.executeQueryAndReturnResult(query);
         double user_lon = Double.parseDouble(user_lon_result.get(0).get(0));

         List<Integer> valid_store_ids = new ArrayList<>();
         for(int i=1;i<=20;i++){

            query = String.format("SELECT latitude FROM Store WHERE storeID = %d", i);
            List<List<String>> store_lat_result = esql.executeQueryAndReturnResult(query);
            double store_lat = Double.parseDouble(store_lat_result.get(0).get(0));

            query = String.format("SELECT longitude FROM Store WHERE storeID = %d", i);
            List<List<String>> store_lon_result = esql.executeQueryAndReturnResult(query);
            double store_lon = Double.parseDouble(store_lon_result.get(0).get(0));

            if(calculateDistance (user_lat, user_lon, store_lat, store_lon)<= 30){
               valid_store_ids.add(i);
            }
         }

         query = String.format("SELECT storeid FROM STORE WHERE storeID = "); //output store names + id
         for(int id = valid_store_ids.size()-1;id>=0;id--){
            if(id==0)
               query += String.format("%s",id);
            else
               query += String.format("%s OR storeID = ",id);
         }
         int rowCount = esql.executeQueryAndPrintResult(query); 
         System.out.println ("Total row(s): " + rowCount);
      
      	 Scanner input = new Scanner(System.in);
	 System.out.print("\tEnter Store ID: ");
         int store_id = input.nextInt();
         input.nextLine();
         query = String.format("SELECT productName FROM Product WHERE storeID = %d", store_id);  //output products from chosen store
         rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("Total row(s): " + rowCount);

         System.out.print("\tEnter Product Name: ");
         String product_name = input.nextLine();

         System.out.print("\tEnter number of units to order: ");
         int num_units = input.nextInt();
         query = String.format("UPDATE Product SET numberOfUnits = numberOfUnits - %d WHERE productName = '%s'", num_units, product_name);
         esql.executeUpdate(query);
      
         query = String.format("Insert INTO Orders (customerID, storeID, productName, unitsOrdered, orderTime) VALUES (%d, %d, '%s', %d, CAST(CURRENT_TIMESTAMP AS TIMESTAMP(0)))", current_user_id, store_id, product_name, num_units);
	 esql.executeUpdate(query);
	 System.out.println("\t" + num_units + " units of " + product_name + " have been ordered."); 
      }
      catch (Exception e){
         System.err.println (e.getMessage ());
      }
   }
   
   public static void viewRecentOrders(Amazon esql) {
      try{
         String query;
         query = String.format("SELECT storeID, productName, unitsOrdered, orderTime FROM Orders WHERE customerID = %d ORDER BY orderTime DESC LIMIT 5", current_user_id);
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("Total row(s): " + rowCount);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void updateProduct(Amazon esql) {
      try{
         String temp = "manager";
         String query;
         if(current_user_type.equals(temp)){
            query = String.format("SELECT storeID FROM Store WHERE managerID = %d", current_user_id);
            List<List<String>> store_id_result = esql.executeQueryAndReturnResult(query);
            query = String.format("SELECT storeid FROM STORE WHERE storeID = "); //output stores by id
            for(int id = store_id_result.size()-1;id>=0;id--){
               if(id==0)
                  query += String.format("%s",id);
               else
                  query += String.format("%s OR storeID = ",id);
            }
            int rowCount = esql.executeQueryAndPrintResult(query); 
            System.out.println ("Total row(s): " + rowCount);

            Scanner input = new Scanner(System.in);
            System.out.print("\tEnter Store ID: ");
            int store_id = input.nextInt();
            input.nextLine();

            query = String.format("SELECT productName, numberOfUnits, pricePerUnit FROM Product WHERE storeID = %d", store_id);  //output products from chosen store
            rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("Total row(s): " + rowCount);

            System.out.print("\tEnter Product Name: ");
            String product_name = input.nextLine();

            System.out.print("\tUpdate number of units? Y/N: ");
            String updateunitsbool = input.nextLine();

            if(updateunitsbool.contains("Y")){
               System.out.print("\tEnter new number of units: ");
               int new_num_units = input.nextInt();
               input.nextLine();

               query = String.format("UPDATE Product SET numberofUnits = %d WHERE productName = '%s'", new_num_units, product_name);
               esql.executeUpdate(query);
               System.out.println("Updated " + product_name + " to " + new_num_units + " number of units.");
               
            }
            System.out.print("\tUpdate price per unit? Y/N: ");
            String updatepricebool = input.nextLine();
            if(updatepricebool.contains("Y")){
               System.out.print("\tEnter new price per unit: ");
               int new_price = input.nextInt();
               input.nextLine();

               query = String.format("UPDATE Product SET pricePerUnit = %d WHERE productName = '%s'", new_price, product_name);
               esql.executeUpdate(query);
               System.out.println("Updated " + product_name + " to $" + new_price + " per unit.");
            }
            
            if(updateunitsbool.contains("Y") || updatepricebool.contains("Y")){
               query = String.format("Insert INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES (%d, %d, '%s', CAST(CURRENT_TIMESTAMP AS TIMESTAMP(0)))", current_user_id, store_id, product_name);
               esql.executeUpdate(query);
            }

         }
         else{
            System.out.println ("Only Managers can use this function");
         }
            
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void viewRecentUpdates(Amazon esql) {
       try{
         String temp = "manager";
         if(current_user_type.equals(temp)){
            String query;
            query = String.format("SELECT updateNumber, storeID, productName, updatedOn FROM ProductUpdates WHERE managerID = %d ORDER BY updatedOn DESC LIMIT 5", current_user_id);
            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("Total row(s): " + rowCount);

         }
         else{
            System.out.println ("Only Managers can use this function");
         }

      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }

   }
   public static void viewPopularProducts(Amazon esql) {
      try{
         String temp = "manager";
         if(current_user_type.equals(temp)){
            String query;
            query = String.format("SELECT storeID FROM Store WHERE managerID = %d", current_user_id);
            int rowCount = esql.executeQueryAndPrintResult(query); 
            System.out.println ("Total row(s): " + rowCount);

            Scanner input = new Scanner(System.in);
            System.out.print("\tEnter Store ID: ");
            int store_id = input.nextInt();
            input.nextLine();

            query = String.format("SELECT productName, COUNT(*) as orderCount FROM Orders WHERE storeID = %d GROUP BY productName ORDER BY orderCount DESC LIMIT 5", store_id);
            rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("Total row(s): " + rowCount);
         }
         else{
            System.out.println ("Only Managers can use this function");
         }

      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void viewPopularCustomers(Amazon esql) {
      try{
         String temp = "manager";
         if(current_user_type.equals(temp)){

            String query;
            query = String.format("SELECT storeID FROM Store WHERE managerID = %d", current_user_id);
            int rowCount = esql.executeQueryAndPrintResult(query); 
            System.out.println ("Total row(s): " + rowCount);

            Scanner input = new Scanner(System.in);
            System.out.print("\tEnter Store ID: ");
            int store_id = input.nextInt();
            input.nextLine();

            
            query = String.format("SELECT customerID , COUNT(*) as customerCount FROM Orders WHERE storeID = %d GROUP BY customerID ORDER BY customerCount DESC LIMIT 5", store_id); //get manager's stores
            List<List<String>> customer_id_result = esql.executeQueryAndReturnResult(query);

            query = String.format("SELECT userID, name FROM Users WHERE userID = ");
            for(int id = customer_id_result.size()-1; id >= 0; id--){   
               if(id==0)
                  query += String.format("%s",customer_id_result.get(id).get(0));
               else
                  query += String.format("%s OR userID = ",customer_id_result.get(id).get(0));
            }
            rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("Total row(s): " + rowCount);
         }
         else{
            System.out.println ("Only Managers can use this function");
         }

      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void placeProductSupplyRequests(Amazon esql) {
      try{
         String temp = "manager";
         String query;
	 if(current_user_type.equals(temp)){
            query = String.format("SELECT storeID FROM Store WHERE managerID = %d", current_user_id);
            List<List<String>> store_id_result = esql.executeQueryAndReturnResult(query);
            query = String.format("SELECT storeid FROM STORE WHERE storeID = "); //output stores by id
            for(int id = store_id_result.size()-1;id>=0;id--){
               if(id==0)
                  query += String.format("%s",id);
               else
                  query += String.format("%s OR storeID = ",id);
            }
            int rowCount = esql.executeQueryAndPrintResult(query); 
            System.out.println ("Total row(s): " + rowCount);

            Scanner input = new Scanner(System.in);
            System.out.print("\tEnter Store ID: ");
            int store_id = input.nextInt();
            input.nextLine();

            query = String.format("SELECT productName FROM Product WHERE storeID = %d", store_id);  //output products from chosen store
            rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("Total row(s): " + rowCount);

            System.out.print("\tEnter Product Name: ");
            String product_name = input.nextLine();
	    
	    query = String.format("SELECT WarehouseID FROM Warehouse");
            rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println("Total row(s): " + rowCount);

            System.out.print("\tEnter Warehouse ID: ");
            int warehouse_id = input.nextInt();
            input.nextLine();
            
            System.out.print("\tEnter number of units needed: ");
            int num_units = input.nextInt();
            query = String.format("UPDATE Product SET numberOfUnits = numberOfUnits + %d WHERE productName = '%s'", num_units, product_name);
            esql.executeUpdate(query);

            query = String.format("Insert INTO ProductSupplyRequests (managerID, warehouseID, storeID, productName, unitsRequested) VALUES (%d, %d, %d, '%s', %d)", current_user_id, warehouse_id, store_id, product_name, num_units);
            esql.executeUpdate(query);
            System.out.println("\t" + num_units + " units of " + product_name + " have been requested.");
         }
         else{
            System.out.println ("Only Managers can use this function");
         }
            
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void viewAllOrders(Amazon esql) {
      try{
         String temp = "manager";
         if(current_user_type.equals(temp)){
            String query;
            query = String.format("SELECT storeID FROM Store WHERE managerID = %d", current_user_id);
            int rowCount = esql.executeQueryAndPrintResult(query); 
            System.out.println ("Total row(s): " + rowCount);

            Scanner input = new Scanner(System.in);
            System.out.print("\tEnter Store ID: ");
            int store_id = input.nextInt();
            input.nextLine();
            
            query = String.format("SELECT O.orderNumber, U.name, O.storeID, O.productName, O.orderTime FROM Orders O, Users U WHERE O.storeID = %d AND O.customerID = U.userID", store_id);
            rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("Total row(s): " + rowCount);
         }
         else{
            System.out.println ("Only Managers can use this function");
         }
            
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }

      public static void adminViewEdit (Amazon esql){
      try{
         String temp = "admin";
         String query;
         if(current_user_type.equals(temp)){
            boolean adminmenu = true;
            while(adminmenu) {
                System.out.println("ADMIN TOOLS");
                System.out.println("---------");
                System.out.println("1. View all Users");
                System.out.println("2. View all Products");
                System.out.println("3. Update User Info");
                System.out.println("4. Update Product Info");
                System.out.println(".........................");
                System.out.println("20. Return to main menu");
                switch (readChoice()){
                   case 1: adminViewUsers(esql); break;
                   case 2: adminViewProducts(esql); break;
                   case 3: adminUpdateUser(esql); break;
                   case 4: adminUpdateProduct(esql); break;

                   case 20: adminmenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
         }
         }
         else{
            System.out.println ("Only Admins can use this function");
         }
            
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }
   public static void adminViewUsers(Amazon esql) {
      try{
         String query;
         query = "SELECT * FROM USERS";
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("Total row(s): " + rowCount);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }

   public static void adminViewProducts(Amazon esql) {
      try{
         String query;
         query = "SELECT * FROM Product";
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("Total row(s): " + rowCount);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }

   public static void adminUpdateUser(Amazon esql) {
      try{
         Scanner input = new Scanner(System.in);
         System.out.print("\tEnter User ID of User you would like to update: ");
         int userID = input.nextInt();
         input.nextLine();
         String query = String.format("SELECT * FROM USERS WHERE userID = '%s'", userID);
         int userNum = esql.executeQuery(query);
         if(userNum == 0){
            System.out.println ("A User with that User ID does not exist");
            return;
         }
         System.out.print("\tEnter User name: ");
         String username = in.readLine();
         System.out.print("\tEnter User password: ");
         String password = in.readLine();
         System.out.print("\tEnter User latitude: ");
         double latitude = input.nextDouble();
         System.out.print("\tEnter User longitude: ");
         double longitude = input.nextDouble();
         input.nextLine();
         System.out.print("\tEnter User type: ");
         String type = in.readLine();
         query = String.format("UPDATE USERS SET name = '%s', password = '%s', latitude = %.6f, longitude = %.6f, type = '%s' WHERE userID = %d ", username, password, latitude, longitude, type, userID );
	      esql.executeUpdate(query);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }

   public static void adminUpdateProduct(Amazon esql) {
      try{
         Scanner input = new Scanner(System.in);
         System.out.print("\tEnter store ID of the product you would like to update: ");
         int storeID = input.nextInt();
         input.nextLine();
         System.out.print("\tEnter the name of the product you would like to update: ");
         String productName = in.readLine();
         String query = String.format("SELECT * FROM Product WHERE storeID = '%s' AND productName = '%s'", storeID,productName);
         int userNum = esql.executeQuery(query);
         if(userNum == 0){
            System.out.println ("A product with that store ID and product name does not exist");
            return;
         }
         System.out.print("\tEnter number of units: ");
         int numberOfUnits = input.nextInt();
         System.out.print("\tEnter price per unit: ");
         double pricePerUnit = input.nextDouble();
         input.nextLine();
         query = String.format("UPDATE Product SET numberOfUnits = %d, pricePerUnit = %.6f WHERE storeID = %d AND productName = '%s'", numberOfUnits, pricePerUnit, storeID, productName);
	      esql.executeUpdate(query);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }  
   }

}//end Amazon


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameDB {
	
	private static Scanner sc = new Scanner(System.in);
    private static Statement statement;
    private static int sqlCode = 0;
    private static String sqlState = "00000";
    private static Connection con;
    private static String user = "labrador_lover";
    private static Random random = new Random();
	private static int orderNumber;
    
	public static void main(String[] args){
		mainMenu();
		sc.close();
		try {
			statement.close();
			con.close();
		} catch (Exception e) {
			return;
		}
		return;
	}
	
	private static void mainMenu(){
		boolean keepGoing = true;
		while(keepGoing){
			printMainMenu();
			String input = sc.next();
			
			if(input.equals("1")){
				browseProductsMenu();
				continue;
			}
			
			if(input.equals("2")){
				warehouseMenu();
				continue;
			}
			
			if(input.equals("3")){
				accountMenu();
				continue;
			}
			
			if(input.equals("4")){
				addRating();
				continue;
			}
			
			if(input.equals("5")){
				placeOrder();
				continue;
			}
			
			if(input.equals("Q") || input.equals("q")){
				keepGoing=false;
				continue;
			}
			
			inputError();
		}
	}
	
	private static void browseProductsMenu(){
		boolean keepGoing = true;
		while(keepGoing){
			printBrowseProductMenu();
			String input = sc.next();
			
			if(input.equals("1")){
				printGameBrowse();
				continue;
			}
			
			if(input.equals("2")){
				printConsoleBrowse();
				continue;
			}
			
			if(input.equals("3")){
				printAccessoryBrowse();
				continue;
			}
			
			if(input.equals("M") || input.equals("m")){
				keepGoing=false;
				continue;
			}
			
			inputError();
		}
		
	}
	
	private static void warehouseMenu(){
		boolean keepGoing = true;
		while(keepGoing){
			printWarehouseMenu();
			String input = sc.next();
			
			if(input.equals("1")){
				findWarehouseLocation();
				continue;
			}
			
			if(input.equals("2")){
				browseWarehouseLocations();
				continue;
			}
			
			if(input.equals("M") || input.equals("m")){
				keepGoing=false;
				continue;
			}
			
			inputError();
		}
	}
	
	private static void findWarehouseLocation(){
		boolean keepGoing = true;
		boolean result = false;
		while(keepGoing){
			printWarehouseFindLocation();
			String input = sc.next();
			try {
				connect();
				String querySQL = "SELECT W.warehouseNumber,W.streetNumber,W.streetName,W.postalcode,W.city FROM Warehouse W WHERE W.warehouseNumber=" + input;
				java.sql.ResultSet rs = statement.executeQuery(querySQL);
				while (rs.next()) {
					result = true;
					String number = String.valueOf(rs.getInt(1));
					String streetNumber = String.valueOf(rs.getInt(2));
					String streetName = rs.getString(3);
					String postal = rs.getString(4);
					String city = rs.getString(5);

					printHeader();
					System.out.println("Store Number "+ number);
			    	System.out.println("");
			    	System.out.println("Location:");
			    	System.out.println(streetNumber + " " + streetName);
			    	System.out.println(city + "   " + postal);
					
					String querySQL2 = "SELECT count(*) FROM Store W WHERE warehouseNumber=" + input;
					java.sql.ResultSet rt = statement.executeQuery(querySQL2);
					while(rt.next()){
						int count = rt.getInt(1);
						
						if(count ==1){
							String querySQL3 = "SELECT openingTime,closingTime FROM Store WHERE warehouseNumber=" + input;
							java.sql.ResultSet ru = statement.executeQuery(querySQL3);
							while(ru.next()){
								String open = String.valueOf(ru.getInt(1));
								String close = String.valueOf(ru.getInt(2));
								System.out.println("Opening Hours:");
								System.out.println("Mon-Sat  " + open+":00 to "+close+":00");
							}
						}
						else{
							System.out.println("");
							System.out.println("");
						}
						
						System.out.println("");		
						System.out.println("Press M to return to the previous menu or I to view the warehouse inventory.");
						input = sc.next();
						if(input.equals("I") || input.equals("i")){
							getWarehouseInventory(number);
						}
						if(input.equals("M") || input.equals("m")){
							keepGoing = false;
						}
					}
				}
				if(!result){
					printHeader();
					System.out.println("");
					System.out.println("");
					System.out.println("Error!");
					System.out.println("No warehouse location matched your query.");
					System.out.println("");
					System.out.println("Please try again");
					System.out.println("");
					System.out.println("");
					System.out.println("");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {}
				}
				
			} catch (SQLException e){
				sqlCode = e.getErrorCode(); // Get SQLCODE
				sqlState = e.getSQLState(); // Get SQLSTATE
			}
		}
	}
	
	private static void getWarehouseInventory(String storeNumber){
		ArrayList<String> tuples = new ArrayList<String>();
		try {
			connect();
		    String querySQL = "SELECT S.UPC,G.name,S.quantity FROM Stock S,GAMEINSTANCE I,GAME G WHERE I.gameId=G.gameId AND I.UPC=S.UPC and S.warehouseNumber=" + storeNumber;
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	String UPC = String.valueOf(rs.getLong(1));
		    	String name = rs.getString(2);
		    	String quantity = String.valueOf(rs.getInt(3));
		    	tuples.add(UPC);
		    	tuples.add(name);
		    	tuples.add(quantity);
		    }
		} 
		catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE\
		}
		
		try {
		    String querySQL = "SELECT S.UPC,G.name,S.quantity FROM Stock S,ConsoleINSTANCE I,Console G WHERE I.consoleId=G.consoleId AND I.UPC=S.UPC and S.warehouseNumber=" + storeNumber;
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	String UPC = String.valueOf(rs.getLong(1));
		    	String name = rs.getString(2);
		    	String quantity = String.valueOf(rs.getInt(3));
		    	tuples.add(UPC);
		    	tuples.add(name);
		    	tuples.add(quantity);
		    }
		} 
		catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE\
		}
		
		try {
		    String querySQL = "SELECT S.UPC,G.name,S.quantity FROM Stock S,accessoryINSTANCE I,accessory G WHERE I.accessoryId=G.accessoryId AND I.UPC=S.UPC and S.warehouseNumber=" + storeNumber;
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	String UPC = String.valueOf(rs.getLong(1));
		    	String name = rs.getString(2);
		    	String quantity = String.valueOf(rs.getInt(3));
		    	tuples.add(UPC);
		    	tuples.add(name);
		    	tuples.add(quantity);
		    }
		} 
		catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE\
		}
		
		int index = 0;
		boolean keepGoing = true;
		while(keepGoing){
			printHeader();
			for(int i=0;i<7; ++i){
				if(index >= tuples.size()){
					keepGoing = false;
					System.out.println("");
				}
				else {
					System.out.printf(tuples.get(index)+ "   " + "%-30s %-30s\n",tuples.get(index+1), tuples.get(index+2));
					index += 3;
				}
			}
			System.out.println("");
			System.out.println("Press M to return to the previous menu or press any other key to continue.");
			String input = sc.next();
			if(input.equals("M") || input.equals("m")){
				keepGoing = false;
			}
		}
	}
	
	private static void browseWarehouseLocations(){
		ArrayList<String> tuples = new ArrayList<String>();
		try {
			connect();
			String querySQL = "SELECT W.warehouseNumber,W.streetNumber,W.streetName,W.postalcode,W.city FROM Warehouse W WHERE W.warehouseNumber>0";
			java.sql.ResultSet rs = statement.executeQuery(querySQL);
			while (rs.next()) {
				String number = String.valueOf(rs.getInt(1));
				String streetNumber = String.valueOf(rs.getInt(2));
				String streetName = rs.getString(3);
				String city = rs.getString(5);
		    	tuples.add(number);
		    	tuples.add(streetNumber);
		    	tuples.add(streetName);
		    	tuples.add(city);
		    }
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE		
		}
		
		System.out.println(tuples.size());
		
		int index = 0;
		boolean keepGoing = true;
		while(keepGoing){
			printHeader();
			for(int i=0;i<7; ++i){
				if(index >= tuples.size()){
					keepGoing = false;
					System.out.println("");
				}
				else {
					System.out.printf(tuples.get(index)+ "   " + "%-5s %-20s %-15s\n",tuples.get(index+1),tuples.get(index+2), tuples.get(index+3));
					index += 4;
				}
			}
			System.out.println("");
			System.out.println("Press M to return to the previous menu or press any other key to continue.");
			String input = sc.next();
			if(input.equals("M") || input.equals("m")){
				keepGoing = false;
			}
		}
	}
	
	private static void accountMenu(){
		boolean keepGoing = true;
		while(keepGoing){
			printAccountMenu();
			String input = sc.next();
			
			if(input.equals("1")){
				accountSearch();
				continue;
			}
			
			if(input.equals("2")){
				printAccountBrowse();
				continue;
			}
			
			if(input.equals("Q") || input.equals("q")){
				keepGoing=false;
				continue;
			}
			
			inputError();
		}
	}
	
	private static void accountSearch(){
		boolean keepGoing = true;
		boolean result = false;
		while(keepGoing){
			printAccountSearch();
			String input = sc.next();
			try {
				connect();
				String querySQL = "SELECT * FROM AccountHolder W WHERE username ='" + input+"'";
				java.sql.ResultSet rs = statement.executeQuery(querySQL);
				while (rs.next()) {
					result = true;
					String username = rs.getString(1);
					String name = rs.getString(2);
					String cc = String.valueOf(rs.getLong(3));
					String streetNum = String.valueOf(rs.getInt(4));
					String streetName = rs.getString(5);
					String postal = rs.getString(6);

					printHeader();
					System.out.println(username + "  -  " + name);
			    	System.out.println("");
			    	System.out.println("Billing information:");
			    	for(int i=0; i<16; ++i){
			    		if(i<4 || i>11) System.out.print(cc.charAt(i));
			    		else{
			    			System.out.print("X");
			    		}
			    	}
			    	System.out.println("");
			    	System.out.println("");
			    	System.out.println(streetNum + " " + streetName);
			    	System.out.println(postal);
			    	System.out.println("");
					System.out.println("");		
					System.out.println("Press M to return to the previous menu or O to view the order history.");
					input = sc.next();
					if(input.equals("O") || input.equals("o")){
						printOrderHistory(username);
					}
					if(input.equals("M") || input.equals("m")){
						keepGoing = false;
					}
					
				}
				if(!result){
					System.out.println("Error!");
					System.out.println("No user account matched your query.");
					System.out.println("");
					System.out.println("Please try again");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {}
				}
				
			} catch (SQLException e){
				sqlCode = e.getErrorCode(); // Get SQLCODE
				sqlState = e.getSQLState(); // Get SQLSTATE
			}
		}
	}
	
	private static void addRating(){
		String username = getUsername();
		if(username.equals("m")){
			return;
		}
		String UPC = getUPC(username);
		if(UPC.equals("m")){
			return;
		}
		String rating = getRating(username, UPC);
		
		try {
		    connect();
			String insertSQL = "INSERT INTO Rating VALUES ('" +username+"',"+UPC+","+rating+")";;
		    statement.executeUpdate ( insertSQL ) ;
		} catch (SQLException e){
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
        }
	}
	
	private static String getUsername(){
		printHeader();
		System.out.println("Add a Rating");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Please provide a valid username:");
		System.out.println("(Or press M to cancel and return to the previous menu)");
		String username = sc.next();
		if(username.equals("M") || username.equals("m")){
			return "m";
		}
		boolean result = false;
		try {
			connect();
			String querySQL = "SELECT * FROM AccountHolder W WHERE username ='" + username + "'";
			java.sql.ResultSet rs = statement.executeQuery(querySQL);
			while (rs.next()) {
				result = true;
			}
			if(!result){
				System.out.println("Error!");
				System.out.println("No user account matched your query.");
				System.out.println("");
				System.out.println("Please try again");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
				username = getUsername();
			}
			
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE
			System.out.println("Error!");
			System.out.println("No user account matched your query.");
			System.out.println("");
			System.out.println("Please try again");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException f) {}
			username = getUsername();
		}
		return username;
	}

	private static String getUPC(String username){
		printHeader();
		System.out.println("Add a Rating");
		System.out.println("");
		System.out.println("Username:");
		System.out.println(username);
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Please provide the product's 12-digit UPC code:");
		System.out.println("(Or press M to return to the previous menu)");
		String UPC = sc.next();
		if(UPC.equals("M") || UPC.equals("m")){
			return "m";
		}
		
		boolean result = false;
		try {
			connect();
			String querySQL = "SELECT * FROM productInstance WHERE UPC =" + UPC;
			java.sql.ResultSet rs = statement.executeQuery(querySQL);
			while (rs.next()) {
				result = true;
			}
			if(!result){
				System.out.println("Error!");
				System.out.println("No product matched your query.");
				System.out.println("");
				System.out.println("Please try again");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
				UPC = getUPC(username);
			}
			
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE
			System.out.println("Error!");
			System.out.println("No product matched your query.");
			System.out.println("");
			System.out.println("Please try again");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException f) {}
			UPC = getUPC(username);
		}
		
		
		return UPC;
	}
	
	private static String getRating(String username, String UPC){
		printHeader();
		System.out.println("Add a Rating");
		System.out.println("");
		System.out.println("Username:");
		System.out.println(username);
		System.out.println("");
		System.out.println("UPC:");
		System.out.println(UPC);
		System.out.println("");
		System.out.println("Please provide your rating (a number from 0 to 10):");
		System.out.println("(Or press M to return to the previous menu)");
		String rating = sc.next();
		int rate = 200;
		try{
			rate = Integer.parseInt(rating);
		}
		catch(Exception e){}
		
		if(rate < 0 || rate > 10 ){
			System.out.println("Error!");
			System.out.println("Please only enter a number between 0 and 10.");
			System.out.println("");
			System.out.println("Please try again");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			rating = getRating(username, UPC);
		}
		return rating;
	}
	
	private static void placeOrder(){
		ArrayList<String> tuples = new ArrayList<String>();
		boolean keepGoing = true;
		boolean placeOrder = false;
		while(tuples.size()==0 && keepGoing){
			printHeader();
			System.out.println("PLACE AN ORDER");
			System.out.println(user);
			System.out.println("You currently have no products in your order.");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("Press A to add an item, press C to confirm and finalize the order");
			System.out.println("Or press M to cancel and return to the previous menu.");
			String input = sc.next();
			
			if(input.equals("a") || input.equals("A")){
				addProduct(tuples);
				continue;
			}
			if(input.equals("C") || input.equals("c")){
				keepGoing=false;
				placeOrder = true;
			}
			if(input.equals("M") || input.equals("m")){
				keepGoing=false;
			}
		}
		
		int index = 0;
		while(keepGoing){			
			printHeader();
			System.out.println("YOUR ORDER");
			int startIndex = index;
			for(int i=0;i<5; ++i){
				if(index >= tuples.size()){
					System.out.println("");
				}
				else {
					System.out.printf(tuples.get(index)+ " " +tuples.get(index+1));
					index += 2;
				}
			}
			System.out.println("");
			System.out.println("Press A to add an item, press C to confirm and finalize the order,");
			System.out.println("Press N for the next page or M to cancel and return to the previous menu.");
			String input = sc.next();
			
			if(input.equals("a") || input.equals("A")){
				addProduct(tuples);
			}
			if(input.equals("C") || input.equals("c")){
				keepGoing=false;
				placeOrder = true;
			}
			if(input.equals("N") || input.equals("n")){
				if(tuples.size()-index > 0){
					continue;
				}
				else{
					index = startIndex;
					continue;
				}
			}
			if(input.equals("M") || input.equals("m")){
				keepGoing=false;
			}
		}
		
		if(placeOrder){
			orderNumber = random.nextInt(99999999) +1;
			try {
			    connect();
				String insertSQL = "INSERT INTO OrderInfo VALUES (" + orderNumber + ", 'iLikeVideoGames', 143, 'Nunavut Avenue', 'F6A1H8', '2007-02-09')";
			    statement.executeUpdate ( insertSQL ) ;
			} catch (SQLException e){
	            sqlCode = e.getErrorCode(); // Get SQLCODE
	            sqlState = e.getSQLState(); // Get SQLSTATE
	        }
			
			for(int i=0; i < tuples.size(); ++i){
				try {
					String insertSQL = "INSERT INTO Ordered VALUES (" + orderNumber + "," + tuples.get(i) + ","+ tuples.get(i+1)+")";
				    statement.executeUpdate (insertSQL);
				    ++i;
				} catch (SQLException e){
		            sqlCode = e.getErrorCode(); // Get SQLCODE
		            sqlState = e.getSQLState(); // Get SQLSTATE
		        }
			}
		}
		
	}
	
	private static void addProduct(ArrayList<String> tuples){
		boolean keepGoing = true;
		while(keepGoing){
			System.out.println("ADD AN ITEM");
			System.out.println("");
			System.out.println("1) Add product by UPC");
			System.out.println("2) Browse to find UPC");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("Choose one of the given options");
			System.out.println("Press M to cancel and return to the previous menu.");
			String input = sc.next();
			if(input.equals("1")){
				addUPC(tuples);
				keepGoing = false;
				continue;
			}
			if(input.equals("2")){
				browseProductsMenu();
				continue;
			}
			if(input.equals("Q") || input.equals("q")){
				keepGoing=false;
				continue;
			}
			
			inputError();
		}
	}
	
	private static void addUPC(ArrayList<String> tuples){
		printHeader();
		System.out.println("ADD A PRODUCT BY UPC");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Please provide the product's 12-digit UPC code:");
		System.out.println("(Or press M to return to the previous menu)");
		String UPC = sc.next();
		if(UPC.equals("M") || UPC.equals("m")){
			return;
		}
		
		boolean result = false;
		try {
			connect();
			String querySQL = "SELECT * FROM productInstance WHERE UPC =" + UPC;
			java.sql.ResultSet rs = statement.executeQuery(querySQL);
			while (rs.next()) {
				result = true;
				addQuantity(tuples,UPC);
				return;
			}
			if(!result){
				System.out.println("Error!");
				System.out.println("No product matched your query.");
				System.out.println("");
				System.out.println("Please try again");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
				return;
			}
			
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE
			System.out.println("Error!");
			System.out.println("No product matched your query.");
			System.out.println("");
			System.out.println("Please try again");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException f) {}
			return;
		}
	}
	
	private static void addQuantity(ArrayList<String> tuples, String UPC){
		printHeader();
		System.out.println("ADD A PRODUCT BY UPC");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Please provide the quantity you would like to order:");
		System.out.println("");
		try{
			int quantity = sc.nextInt();
			tuples.add(UPC);
			tuples.add(String.valueOf(quantity));
			return;
		}
		catch(Exception e){
			addQuantity(tuples,UPC);
		}
	}
	
	
 	private static void printHeader(){
		System.out.println("***********************************************************************");
		System.out.println("*                             G.A.M.E.                                *");
		System.out.println("***********************************************************************");
		System.out.println("");
	}
	
	private static void printMainMenu(){
		printHeader();
		System.out.println("MAIN MENU");
		System.out.println("");
		System.out.println("(1) Browse products");
		System.out.println("(2) List Warehouse Inventory");
		System.out.println("(3) View Account Information");
		System.out.println("(4) Add a product rating");
		System.out.println("(5) Place an order");
		System.out.println("(Q) Quit");
		System.out.println("");
		System.out.println("What would you like to do?");
	}
	
	private static void printBrowseProductMenu(){
		printHeader();
		System.out.println("PRODUCT BROWSE MENU");
		System.out.println("");
		System.out.println("What type of product would you like to browse:");
		System.out.println("(1) Games");		
		System.out.println("(2) Consoles");
		System.out.println("(3) Accessories");
		System.out.println("(M) Return to the previous menu");
		System.out.println("");
		System.out.println("");
		System.out.println("What kind of products would you like to browse?");
	}
	
	private static void printGameBrowse(){
		ArrayList<String> tuples = new ArrayList<String>();
		try {
			connect();
		    String querySQL = "SELECT I.UPC,C.name,G.name FROM GAME G,GAMEINSTANCE I,CONSOLE C WHERE I.gameId=G.gameId AND I.consoleID=C.consoleId";
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	String UPC = String.valueOf(rs.getLong(1));
		    	String console = rs.getString(2);
		    	String name = rs.getString (3);
		    	tuples.add(UPC);
		    	tuples.add(name);
		    	tuples.add(console);
		    }
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE\	
		}
		int index = 0;
		boolean keepGoing = true;
		while(keepGoing){
			printHeader();
			for(int i=0;i<7; ++i){
				if(index >= tuples.size()){
					keepGoing = false;
					System.out.println("");
				}
				else {
					System.out.printf(tuples.get(index)+ "   " + "%-30s %-30s\n",tuples.get(index+1), tuples.get(index+2));
					index += 3;
				}
			}
			System.out.println("");
			System.out.println("Press M to return to the previous menu or press any other key to continue.");
			String input = sc.next();
			if(input.equals("M") || input.equals("m")){
				keepGoing = false;
			}
		}
	}
	
	private static void printConsoleBrowse(){
		ArrayList<String> tuples = new ArrayList<String>();
		String UPC, name, colour, version;
		try {
			connect();
			String querySQL = "SELECT I.UPC,C.name,I.colour,I.version FROM CONSOLEINSTANCE I,CONSOLE C WHERE I.consoleID=C.consoleId";
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	UPC = String.valueOf(rs.getLong(1));
		    	name = rs.getString(2);
		    	colour = rs.getString(3);
		    	version = rs.getString(4);
		    	
		    	tuples.add(UPC);
		    	tuples.add(name);
		    	tuples.add(colour);
		    	tuples.add(version);
		    }
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE
		}

		int index = 0;
		boolean keepGoing = true;
		while(keepGoing){
			printHeader();
			for(int i=0;i<7; ++i){
				if(index >= tuples.size()){
					keepGoing = false;
					System.out.println("");
				}
				else {
					System.out.printf(tuples.get(index)+ "   " + "%-20s %-20s %-20s\n",tuples.get(index+1), tuples.get(index+2), tuples.get(index+3));
					index += 4;
				}
			}
			System.out.println("");
			System.out.println("Press M to return to the previous menu or press any other key to continue.");
			String input = sc.next();
			if(input.equals("M") || input.equals("m")){
				keepGoing = false;
			}
		}
	}
	
	private static void printAccessoryBrowse(){
		ArrayList<String> tuples = new ArrayList<String>();
		String UPC, name, colour;
		try {
			connect();
			String querySQL = "SELECT I.UPC,A.name,I.colour FROM ACCESSORYINSTANCE I,ACCESSORY A WHERE I.accessoryId = A.accessoryId ";
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	UPC = String.valueOf(rs.getLong(1));
		    	name = rs.getString(2);
		    	colour = rs.getString(3);
		    	
		    	tuples.add(UPC);
		    	tuples.add(name);
		    	tuples.add(colour);
		    }
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE
		}

		int index = 0;
		boolean keepGoing = true;
		while(keepGoing){
			printHeader();
			for(int i=0;i<7; ++i){
				if(index >= tuples.size()){
					keepGoing = false;
					System.out.println("");
				}
				else {
					System.out.printf(tuples.get(index)+ "   " + "%-20s %-20s\n",tuples.get(index+1), tuples.get(index+2));
					index += 3;
				}
			}
			System.out.println("");
			System.out.println("Press M to return to the previous menu or press any other key to continue.");
			String input = sc.next();
			if(input.equals("M") || input.equals("m")){
				keepGoing = false;
			}
		}
	}
	
	private static void printWarehouseMenu(){
		printHeader();
		System.out.println("WAREHOUSE INVENTORY MENU");
		System.out.println("");
		System.out.println("Options:");
		System.out.println("(1) See Warehouse Info");
		System.out.println("(2) Browse warehouse locations");
		System.out.println("(M or m) Return to the Main Menu");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("What would you like to do?");
	}
	
	private static void printWarehouseFindLocation(){
		printHeader();
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Please enter the store number to search:");
		System.out.println("(Or you can press M to return to the previous menu)");
		System.out.println("");
	}
	
	private static void printAccountMenu(){
		printHeader();
		System.out.println("ACCOUNT MENU");
		System.out.println("");
		System.out.println("(1) Account Search");
		System.out.println("(2) Browse Accounts");
		System.out.println("(M) Return to previous menu");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("What would you like to do?");
	}
	
	private static void printAccountSearch(){
		printHeader();
		System.out.println("");
		System.out.println("ACCOUNT SEARCH");
		System.out.println("");
		System.out.println("");
		System.out.println("Here you can find the account information of the users.");
		System.out.println("");
		System.out.println("");
		System.out.println("Please enter the account username to search:");
		System.out.println("(Or you can press M to return to the previous menu)");
		System.out.println("");
	}
	
	private static void printOrderHistory(String username){
		ArrayList<String> tuples = new ArrayList<String>();
		try {
			connect();
		    String querySQL = "SELECT ordernumber,shippingstreetnumber,shippingstreetname,shippingpostalcode,orderdate FROM OrderInfo WHERE username ='" + username+"'";
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	String number = String.valueOf(rs.getInt(1));
		    	String streetnumber = String.valueOf(rs.getInt(2));
		    	String streetname = rs.getString (3);
		    	String postal = rs.getString(4);
		    	String date = String.valueOf(rs.getDate(5));
		    	
		    	tuples.add(date);
		    	tuples.add(number);
		    	tuples.add(streetnumber);
		    	tuples.add(streetname);
		    	tuples.add(postal);
		    }
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE			
		}
		int index = 0;
		boolean keepGoing = true;
		while(keepGoing){
			printHeader();
			for(int i=0;i<6; ++i){
				if(index >= tuples.size()){
					keepGoing = false;
					System.out.println("");
				}
				else {
					System.out.println((i+1) +"- " + tuples.get(index)+ "   Order#" + tuples.get(index+1) + "  Shipped to " + tuples.get(index+2) + " " + tuples.get(index+3) + "," + tuples.get(index+4));
					index += 5;
				}
			}
			System.out.println("");
			System.out.println("Press M to return to the previous menu or press any other key to continue.");
			String input = sc.next();
			if(input.equals("M") || input.equals("m")){
				keepGoing = false;
			}
		}
	}
	
	private static void printAccountBrowse(){
		ArrayList<String> tuples = new ArrayList<String>();
		try {
			connect();
		    String querySQL = "SELECT username,name,billingstreetnumber,billingstreetname FROM Accountholder";
		    java.sql.ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	String username = rs.getString(1);
		    	String name = rs.getString(2);
		    	String streetNumber = String.valueOf(rs.getInt(3));
		    	String streetName = rs.getString(4);
		    	
		    	tuples.add(username);
		    	tuples.add(name);
		    	tuples.add(streetNumber);
		    	tuples.add(streetName);
		    }
		} catch (SQLException e){
			sqlCode = e.getErrorCode(); // Get SQLCODE
			sqlState = e.getSQLState(); // Get SQLSTATE			
		}
		int index = 0;
		boolean keepGoing = true;
		while(keepGoing){
			printHeader();
			for(int i=0;i<7; ++i){
				if(index >= tuples.size()){
					keepGoing = false;
					System.out.println("");
				}
				else {
					System.out.printf("%-20s %-20s" + tuples.get(index+2) + " " + tuples.get(index+3) + "\n",tuples.get(index),tuples.get(index+1));
					index += 4;
				}
			}
			System.out.println("");
			System.out.println("Press M to return to the previous menu or press any other key to continue.");
			String input = sc.next();
			if(input.equals("M") || input.equals("m")){
				keepGoing = false;
			}
		}
	}

	
	private static void inputError(){
		System.out.println("ERROR! \nPlease enter one of the given options");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
	}
	
	private static Connection connect() throws SQLException {
		try {
			DriverManager.registerDriver ( new com.ibm.db2.jcc.DB2Driver() );
		} catch (Exception cnfe) {
			System.out.println("Class not found");
		}

		String url = "jdbc:db2://db2:50000/cs421";
		con = DriverManager.getConnection (url,"cs421g42", "jeffeman42") ;
		statement = con.createStatement ( );

		return con;
	}
}














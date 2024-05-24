/*
 * @(#)Context.java  1.0.0  27/09/14
 *
 * MOONRISE
 * Webpage: http://webdiis.unizar.es/~maria/?page_id=250
 * 
 * University of Zaragoza - Distributed Information Systems Group (SID)
 * http://sid.cps.unizar.es/
 *
 * The contents of this file are subject under the terms described in the
 * MOONRISE_LICENSE file included in this distribution; you may not use this
 * file except in compliance with the License.
 *
 * Contributor(s):
 *  RODRIGUEZ-HERNANDEZ, MARIA DEL CARMEN <692383[3]unizar.es>
 *  ILARRI, SERGIO <silarri[3]unizar.es>
 */
package es.unizar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.mahout.cf.taste.common.TasteException;

import es.unizar.database.DBConnection;
import es.unizar.database.Database;
import es.unizar.gui.Configuration;
import es.unizar.util.Literals;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Access to the data from a database. File taken from MOONRISE.jar (and optimized).
 *
 * @author Maria del Carmen Rodriguez-Hernandez and Alejandro Piedrafita Barrantes
 */
public class DataAccessLayer extends DBConnection implements DataAccess {

	private static final Logger log = Logger.getLogger(Literals.DEBUG_MESSAGES);
	
	private Database dbInstance = null;

	/**
	 * Constructor.
	 *
	 * @param dbURL
	 *            Database URL.
	 */
	public DataAccessLayer(String dbURL, Database db) {
		super(dbURL);
		dbInstance = db;
		try {
			connect(dbURL);
		}
		catch (ClassNotFoundException | SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Connects to database if not connected yet.
	 * 
	 * @param dbURL
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void connect(String dbURL) throws ClassNotFoundException, SQLException {
		
		dbInstance.connect(dbURL);
		
		//System.out.println(getConnection() + " -> " + dbURL);
		
		/*
		 * PREVIOUS - Now there's only one connection
		 * First implementation and info. Now only one connection and connecting (if it isn't open yet) to that connection)
		 * 
		// If already connected, skip
		if (connection != null)
			return;
		
		// Connection to the DB
		Class.forName(SQLITE);
		connection = DriverManager.getConnection(dbURL);
		
		// Write-Ahead Logging in order to improve db r/w performance
		// @see https://www.sqlite.org/wal.html
		
		// WARNING: Sqlite library must be >= 3.7.0.
		// @see https://stackoverflow.com/questions/6653648/how-to-implement-write-ahead-logging-of-sqlite-in-java-program
		
		// PreparedStatement pr = connection.prepareStatement("PRAGMA journal_mode=WAL");
		// boolean pragmaWAL = pr.execute();
		// System.out.println(connection + ";PRAGMA journal_mode=WAL: "+pragmaWAL);
		
		// VITAL -> DB PERFORMANCE SUPER OPTIMIZED
		// @see https://dba.stackexchange.com/questions/252445/how-does-autocommit-off-affects-bulk-inserts-performance-in-mysql-using-innodb
		// @see http://www.w3big.com/es/sqlite/sqlite-java.html
		//connection.setAutoCommit(false);
		//connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		*/
	}
	
	/**
	 * Returns the database connection.
	 * 
	 * @return connection
	 */
	public Connection getConnection() {
		
		return dbInstance.getConnection();
	}
	
	/**
	 * Disconnects from database.
	 * 
	 * @throws SQLException
	 */
	public void disconnect() throws SQLException {
		
		dbInstance.disconnect();
	}

	/**
	 * Gets a list with all the item identifiers.
	 *
	 * @return A list with all the item identifiers.
	 */
	public List<Integer> getItemIDs() {
		ResultSet resultSet = null;
		List<Integer> listOfIdItems = new LinkedList<Integer>();
		
		try {
		
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT id_item FROM item");
			
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				listOfIdItems.add(resultSet.getInt("id_item"));
			}
			
			resultSet.close();
			select.close();
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return listOfIdItems;
	}

	/**
	 * Gets the number of items.
	 *
	 * @return Number of items.
	 */
	public int getNumberOfItems() {
		int numberOfItems = 0;
		try {
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT count(id_item) as ItemCount FROM item");
			ResultSet resultSet = select.executeQuery();
			numberOfItems = resultSet.getInt("ItemCount");
			
			resultSet.close();
			select.close();
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return numberOfItems;
	}

	/**
	 * Gets a list with all the user identifiers.
	 *
	 * @return A list with all the user identifiers.
	 */
	public long[] getUserIDs() {
		ResultSet resultSet = null;
		long[] userIDs = new long[getNumberOfUsers()];
		int i = 0;
		try {
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT id_user FROM user");
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				userIDs[i] = resultSet.getInt("id_user");
				i++;
			}
			
			resultSet.close();
			select.close();
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return userIDs;
	}

	/**
	 * Gets the number of users.
	 *
	 * @return Number of users.
	 */
	public int getNumberOfUsers() {
		int numberOfUsers = 0;
		try {
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT count(id_user) as UserCount FROM user");
			ResultSet resultSet = select.executeQuery();
			
			numberOfUsers = resultSet.getInt("UserCount");
			
			resultSet.close();
			select.close();
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return numberOfUsers;
	}

	/**
	 * Gets the users, items and ratings.
	 *
	 * @return ResultSet.
	 */
	@Override
	public List<String> getUserItemRating() {
		List<String> list = new LinkedList<>();
		try {
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT id_user,id_item,rating FROM user_item_context");
			ResultSet resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				long userID = resultSet.getLong(1);
				long itemID = resultSet.getLong(2);
				float rating = resultSet.getLong(3);
				list.add(userID + ";" + itemID + ";" + rating);
			}
			
			resultSet.close();
			select.close();
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * Gets the users, items and ratings.
	 *
	 * @return ResultSet.
	 */
	@Override
	public List<String> getUserItemRatingFrom(long userID) {
		List<String> list = new LinkedList<>();
		try {
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT id_user,id_item,rating FROM user_item_context WHERE id_user== ?");
			select.setLong(1, userID);
			ResultSet resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				long userId = resultSet.getLong(1);
				long itemId = resultSet.getLong(2);
				float rating = resultSet.getLong(3);
				list.add(userId + ";" + itemId + ";" + rating);
			}
			
			resultSet.close();
			select.close();
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * Gets the users, items, contexts and ratings for a specific user.
	 *
	 * @return ResultSet.
	 */
	@Override
	public List<String> getUserItemContextRatingFor(long userID) {
		ResultSet resultSet = null;
		List<String> list = new LinkedList<>();
		try {
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT id_user,id_item,id_context,rating FROM user_item_context WHERE id_user== ? ORDER BY rating DESC");
			select.setLong(1, userID);
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				long userId = resultSet.getLong(1);
				long itemId = resultSet.getLong(2);
				long context = resultSet.getLong(3);
				float rating = resultSet.getLong(4);
				list.add(userId + ";" + itemId + ";" + context + ";" + rating);
			}
			
			resultSet.close();
			select.close();

		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public List<String> getUserItemContextRatingRandomFor(long userID) {
		ResultSet resultSet = null;
		List<String> list = new LinkedList<>();
		try {
			
			List<String> notOrdered = new LinkedList<>();
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT DISTINCT id_user,id_item,id_context,rating FROM user_item_context WHERE id_user== ?");	// Order by random() in SQLite doesn't allow parameters -> Order list afterwards
																																										// ORDER BY RANDOM(" + Configuration.simulation.getSeed() + ")");
			select.setLong(1, userID);
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				long userId = resultSet.getLong(1);
				long itemId = resultSet.getLong(2);
				long context = resultSet.getLong(3);
				float rating = resultSet.getLong(4);
				notOrdered.add(userId + ";" + itemId + ";" + context + ";" + rating);
			}
			
			resultSet.close();
			select.close();
			
			// Order list by rand
			list = orderListByRand(notOrdered);

		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return list;
	}

	// SELECT id_user,id_item,id_context,rating FROM user_item_context WHERE
	// id_user==176 ORDER BY RANDOM() LIMIT 10;

	private List<String> orderListByRand(List<String> notOrdered) {
		Collections.shuffle(notOrdered, new Random(Configuration.simulation.getSeed()));
		return notOrdered;
		
	}


	/**
	 * Gets the users, items, contexts and ratings.
	 *
	 * @return ResultSet.
	 */
	@Override
	public ResultSet getUserItemContextRating() {
		ResultSet resultSet = null;
		try {
			
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT id_user,id_item,id_context,rating FROM user_item_context");
			resultSet = select.executeQuery();
			
			select.close();
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return resultSet;
	}

	/**
	 * Gets the context ID from a list of context values.
	 *
	 * @param currentContextValues
	 *            A list of current context values.
	 * @return long.
	 */
	@Override
	public long getContextIDFor(List<Integer> currentContextValues) {
		ResultSet resultSet = null;
		long contextID = 0;
		String values = String.valueOf(currentContextValues.get(0));
		for (int i = 1; i < currentContextValues.size(); i++) {
			values += "," + currentContextValues.get(i);
		}
		try {
			// Query
			PreparedStatement select = getConnection().prepareStatement("SELECT id_context FROM context_variable WHERE id_variable  IN (" + values + ") GROUP BY id_context HAVING COUNT(id_variable) = " + currentContextValues.size());
			resultSet = select.executeQuery();
			
			contextID = resultSet.getLong(1);
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return contextID;
	}

	/**
	 * Gets a HashMap with the number of items by user. The key is the user.
	 *
	 * @return A HashMap with the number of items by user.
	 */
	public Map<Long, Integer> getHashWithNumberItemsByUser() {
		Map<Long, Integer> hashWithNumberItemsByUser = new TreeMap<Long, Integer>();
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT id_user, count(id_item) AS ItemCount FROM user_item_context GROUP BY id_user");
			ResultSet resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				hashWithNumberItemsByUser.put(resultSet.getLong("id_user"), resultSet.getInt("ItemCount"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return hashWithNumberItemsByUser;
	}

	/**
	 * Gets a list with the context variable names.
	 *
	 * @return A list with the context variable names.
	 */
	public List<String> getVariableNames() {
		ResultSet resultSet = null;
		List<String> variableNames = new LinkedList<String>();
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT name FROM variable_name");
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				variableNames.add(resultSet.getString("name"));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return variableNames;
	}

	/**
	 * Gets the variable name from a variable value.
	 *
	 * @param variableValue
	 *            The variable value.
	 * @return The variable name from a variable value.
	 */
	public List<String> getVariableNameFromVariableValue(String variableValue) {
		List<String> variableNames = new LinkedList<String>();
		ResultSet resultSet = null;
		
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT DISTINCT name FROM variable WHERE variable.value= ?");
			select.setString(1, variableValue);
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				variableNames.add(resultSet.getString("name"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return variableNames;
	}

	/**
	 * Gets a list with the possible values of a context variable.
	 *
	 * @param variableName
	 *            The name of the a variable.
	 * @return A list with the possible values of a context variable.
	 */
	public List<String> getPossibleVariableValues(String variableName) {
		ResultSet resultSet = null;
		List<String> possibleVariableValues = new LinkedList<String>();
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT value FROM variable WHERE name= ?");
			select.setString(1, variableName);
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				possibleVariableValues.add(resultSet.getString("value"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return possibleVariableValues;
	}

	/**
	 * Gets a list with the names and values of context variables from userID and itemID.
	 *
	 * @param userID
	 *            The user identifier.
	 * @param itemID
	 *            The item identifier.
	 * @return A list with the names and values of context variables from userID and itemID.
	 */
	public List<String> getVariableNameAndValue(long userID, long itemID) {
		ResultSet resultSet = null;
		List<String> variableNameAndValue = new LinkedList<String>();
		
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT name, value FROM variable, context_variable,user_item_context WHERE user_item_context.id_user= ? and user_item_context.id_item= ? and user_item_context.id_context=context_variable.id_context and variable.id_variable=context_variable.id_variable");
			select.setString(1, Long.toString(userID));
			select.setString(2,  Long.toString(itemID));
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				variableNameAndValue.add(resultSet.getString("name") + "__" + resultSet.getString("value"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return variableNameAndValue;
	}

	/**
	 * Gets a list with the names and weights of context variables.
	 *
	 * @return A list with the names and weights of context variables.
	 */
	public List<String> getVariableNameAndWeight(long userID) {
		ResultSet resultSet = null;
		List<String> variableNameAndWeight = new LinkedList<String>();
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT name, weight FROM user_variable_name WHERE id_user= ?");
			select.setInt(1, (int) userID);
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				variableNameAndWeight.add(resultSet.getString("name") + "__" + resultSet.getString("weight"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return variableNameAndWeight;
	}

	/**
	 * Determine the distance between two variable values that are similar
	 *
	 * @param variableValueX
	 * @param variableValueY
	 * @return
	 */
	public double distanceSoftVariableValues(String variableValueX, String variableValueY) {
		double distance = -1;
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT A.distance FROM variable_variable A INNER JOIN variable F ON A.variable1=F.id_variable WHERE F.value= ? UNION SELECT A.distance FROM variable_variable A INNER JOIN variable F ON A.variable2=F.id_variable WHERE F.value= ?");
			select.setString(1, variableValueX);
			select.setString(2, variableValueY);
			ResultSet resultSet = select.executeQuery();
			
			distance = resultSet.getDouble(1);
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return distance;
	}

	/**
	 * Gets the user radius of action, according to the current transport way.
	 *
	 * @param profileId
	 *            The profile identifier.
	 * @param transportWayValue
	 *            The value of variable transport way.
	 * @return The user radius of action, according to the current transport way.
	 */
	public long getRadius(long userID, String transportWayValue) {
		long radius = 0;
		ResultSet resultSet = null;
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT distance FROM distance, user, ca_profile, ca_profile_attribute WHERE ca_profile.id_ca_profile=distance.id_ca_profile and distance.id_ca_profile=user.id_ca_profile and user.id_user= ? and ca_profile_attribute.transportway= ? and distance.id_ca_profile_attribute=ca_profile_attribute.id_ca_profile_attribute");
			select.setLong(1, userID);
			select.setString(2, transportWayValue);
			resultSet = select.executeQuery();
			
			radius = resultSet.getInt(1);
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return radius;
	}

	/**
	 * The item latitude.
	 *
	 * @param itemID
	 *            Item identifier.
	 * @return The item latitude.
	 */
	public long getItemLatitude(long itemID) {
		long latitude = 0;
		ResultSet resultSet = null;
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT latitude_gps FROM item WHERE id_item= ?");
			select.setInt(1, (int) itemID);
			resultSet = select.executeQuery();
			
			latitude = resultSet.getInt(1);
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return latitude;
	}

	/**
	 * The item longitude.
	 *
	 * @param itemID
	 *            Item identifier.
	 * @return The item longitude.
	 */
	public long getItemLongitude(long itemID) {
		long latitude = 0;
		ResultSet resultSet = null;
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT longitude_gps FROM item WHERE id_item= ?");
			select.setInt(1, (int) itemID);
			resultSet = select.executeQuery();
			
			latitude = resultSet.getInt(1);
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return latitude;
	}

	/**
	 * Gets the maximum rating value.
	 *
	 * @return The maximum rating value.
	 */
	public float getMaximumRating() {
		float maximumRating = 0;
		ResultSet resultSet = null;
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT MAX (rating) FROM user_item_context");
			resultSet = select.executeQuery();
			
			maximumRating = resultSet.getInt(1);
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return maximumRating;
	}

	/**
	 * Gets the number of item features.
	 *
	 * @return The number of item features.
	 */
	public int getNumberItemFeatures() {
		int numFeatures = 0;
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT Count(name) FROM feature");
			ResultSet resultSet = select.executeQuery();
			
			numFeatures = resultSet.getInt(1);
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return numFeatures;
	}

	/**
	 * Gets the feature names and values of an item.
	 *
	 * @param itemId
	 *            Item identifier.
	 * @return The features of an item.
	 * @throws TasteException
	 */
	public List<String> getNamesAndValuesOfFeaturesFromItem(long itemID) throws TasteException {
		List<String> listFeatures = new LinkedList<String>();
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT name, value FROM item_feature WHERE item_feature.id_item= ?");
			select.setInt(1, (int) itemID);
			ResultSet resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				listFeatures.add(resultSet.getString("name") + "__" + resultSet.getString("value"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return listFeatures;
	}

	/**
	 * Gets a list with all the feature names.
	 *
	 * @return List with all the feature names.
	 */
	public List<String> getItemFeatureNames() {
		List<String> listFeatures = new LinkedList<String>();
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT value FROM item_feature");
			ResultSet resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				listFeatures.add(resultSet.getString("value"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return listFeatures;
	}

	/**
	 * Gets a ResultSet with the user rating about an item.
	 *
	 * @param userId
	 *            User identifier.
	 * @param itemId
	 *            Item identifier.
	 * @param contextId
	 *            Context identifier.
	 * @return ResultSet.
	 */
	@Override
	public float getPreferenceFor(long userId, long itemId, long contextId) {
		float rating = 0;
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT rating FROM user_item_context WHERE id_user== ? AND id_item== ? AND id_context== ?");
			select.setInt(1, (int) userId);
			select.setInt(2, (int) itemId);
			select.setInt(3, (int) contextId);
			
			rating = (float) select.executeQuery().getDouble("rating");
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return rating;
	}

	public List<Long> getItemsOrderByRoom() {
		ResultSet resultSet = null;
		List<Long> list = new LinkedList<>();
		try {
			
			// Query
			PreparedStatement select = getConnection()
					.prepareStatement("SELECT id_item FROM item_feature WHERE name='Room' ORDER BY cast(value as unsigned)");
			resultSet = select.executeQuery();
			
			while (resultSet.next()) {
				long itemId = resultSet.getLong(1);
				list.add(itemId);
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return list;
	}
}

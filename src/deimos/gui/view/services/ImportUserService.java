package deimos.gui.view.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import deimos.common.DeimosConfig;
import deimos.common.ProcessFileUtils;
import deimos.common.StringUtils;
import deimos.phase2.DBOperations;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ImportUserService extends Service<Void> {

	private int userId = -1;
	private String filePathValues;
	private String filePathUserInfo;
	private String filePathPublicIP;
	private boolean truncate;

	/** Create Statements and preparedStatements on this connection. */
	private static Connection db_conn;

	public void setUserId(int id)
	{
		this.userId = id;
	}

	public void setTruncate(boolean b)
	{
		this.truncate = b;
	}

	public void setFilePathValues(String filePathValues) {
		this.filePathValues = filePathValues;
	}

	public void setFilePathPublicIP(String filePathPublicIP) {
		this.filePathPublicIP = filePathPublicIP;
	}

	public void setFilePathUserInfo(String filePathUserInfo) {
		this.filePathUserInfo = filePathUserInfo;
	}


	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call() throws FileNotFoundException {

				if(userId == -1) {
					System.err.println("userId = -1! Can't insert values, user-info or Public IP.");
				}
				else
				{
					PreparedStatement psUser = null;
					try {

						db_conn = DBOperations.getConnectionToDatabase("UserInfoPublicIPService");

						// Get them values
						if(truncate) {
							DBOperations.truncateUserTable(db_conn, "user_training_input", userId);
							DBOperations.truncateUserTable(db_conn, "user_info", userId);
						}

						List<String> valueLines = ProcessFileUtils.readFileIntoList(filePathValues);
						valueLines.remove(0); // remove header row
						System.out.println("Training CSV file loaded.");


						// load other files
						// Get the User Info
						List<String> userInfoLines = ProcessFileUtils.readFileIntoList(filePathUserInfo);
						System.out.println("User info file loaded.");
						String userData[] = userInfoLines.get(0).split(DeimosConfig.DELIM);
						for(int i = 0; i < userData.length; i++) // Handle nulls correctly - 1
							if(userData[i].equals("null"))
								userData[i] = null;

						// Get the Public IP
						List<String> publicIPLines = ProcessFileUtils.readFileIntoList(filePathPublicIP);
						System.out.println("Public IP file loaded.");
						String publicIP = publicIPLines.get(0);
						if(!StringUtils.isValidIPv4(publicIP))
						{
							System.out.format("WARNING: IP in %s, [%s] is not valid.\n", filePathPublicIP, publicIP);
							publicIP = null;
						}


						PreparedStatement psVal = db_conn.prepareStatement("INSERT INTO user_training_input VALUES (?, ?, ?)");
						psVal.setInt(1, userId);

						for(String s : valueLines)
						{
							// part 1 is the category name, part 2 is value (starting 0)
							String parts[] = StringUtils.getCSVParts(s);
							// System.out.format("%s, %s, %s\n", parts[0], parts[1], parts[2]);

							String category = parts[1];
							psVal.setString(2, category);

							double value = Double.parseDouble(parts[2]);
							psVal.setFloat(3, (float)value);

							// System.out.format("%d, %s, %f\n", userId, category, value);
							psVal.executeUpdate();
						}
						psVal.close();
						System.out.println("Imported user "+userId+" into user_training_input.");


						String fname = userData[0],
								lname = userData[1],
								genderStr = userData[2],
								yearOfBirthStr = userData[3],
								location = userData[4];


						// add the user-info and public IP to the database

						if(truncate)
							DBOperations.truncateUserTable(db_conn, "user_info", userId);

						psUser = db_conn.prepareStatement("INSERT INTO user_info"
								+ "(user_id, first_name, last_name, gender, birth_year, location, ip) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
						psUser.setInt(1, userId);

						if(fname != null)
							psUser.setString(2, fname);
						else
							psUser.setNull(2, Types.VARCHAR);

						if(lname != null)
							psUser.setString(3, lname);
						else
							psUser.setNull(3, Types.VARCHAR);

						if(genderStr != null) {
							char gender = genderStr.charAt(0);
							psUser.setString(4, String.valueOf(gender));
						}
						else {
							psUser.setNull(4, Types.CHAR);
						}						

						if(yearOfBirthStr != null) {
							int yearOfBirth = Integer.parseInt(yearOfBirthStr);
							psUser.setInt(5, yearOfBirth);
						}
						else {
							psUser.setNull(5, Types.INTEGER);
						}

						if(location != null)
							psUser.setString(6, location);
						else
							psUser.setNull(6, Types.VARCHAR);

						if(publicIP != null)
							psUser.setString(7, publicIP);
						else
							psUser.setNull(7, Types.VARCHAR);

						// RUN THAT SHIT
						psUser.executeUpdate();

						System.out.println("Imported user "+userId+" from user info and public IP files into user_info.");
					}
					catch (FileNotFoundException fnfe) {

						// e.printStackTrace();
						System.out.println("Can't continue, input file doesn't exist: "+fnfe);
						throw fnfe;
					} 
					catch (NumberFormatException nfe) {
						System.out.println("Invalid training input CSV file? "+nfe);

						throw nfe;

					} catch (ArrayIndexOutOfBoundsException aioob) {
						aioob.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					finally
					{
						try {
							if(psUser != null)
								psUser.close();

							db_conn.close();
						}
						catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}

				return null;
			}
		};
	}

}
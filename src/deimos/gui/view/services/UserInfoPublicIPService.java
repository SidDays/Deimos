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

public class UserInfoPublicIPService extends Service<Void> {

	private int userId = -1;
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
			public Void call(){

				if(userId == -1) {
					System.err.println("userId = -1! Can't insert user-info or Public IP.");
				}
				else
				{
					try {
						
						// Get the User Info
						List<String> userInfoLines = ProcessFileUtils.readFileIntoList(filePathUserInfo);
						String userData[] = userInfoLines.get(0).split(DeimosConfig.DELIM);
						
						for(int i = 0; i < userData.length; i++)
							if(userData[i].equals("null"))
								userData[i] = null;
						
						String fname = userData[0],
								lname = userData[1],
								genderStr = userData[2],
								yearOfBirthStr = userData[3],
								location = userData[4];
						
						char gender = genderStr.charAt(0);
						int yearOfBirth = Integer.parseInt(yearOfBirthStr);
						
						// Get the Public IP
						List<String> publicIPLines = ProcessFileUtils.readFileIntoList(filePathPublicIP);
						String publicIP = publicIPLines.get(0);
						if(!StringUtils.isValidIPv4(publicIP))
						{
							System.out.format("WARNING: IP in %s, [%s] is not valid.\n", filePathPublicIP, publicIP);
							publicIP = null;
						}
						
						// add the user-info and public IP to the database
						
						db_conn = DBOperations.getConnectionToDatabase("UserInfoPublicIPService");
						
						if(truncate)
							DBOperations.truncateUserTable(db_conn, "user_info", userId);
						
						PreparedStatement psUser = db_conn.prepareStatement("INSERT INTO user_info"
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
						
						psUser.setString(4, String.valueOf(gender));
						
						psUser.setInt(5, yearOfBirth);
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
						
						psUser.close();
						db_conn.close();
						
					} catch (ArrayIndexOutOfBoundsException aioob) {
						aioob.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
				}
				return null;
			}
		};
	}

}
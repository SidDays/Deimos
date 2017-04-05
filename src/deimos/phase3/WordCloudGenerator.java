package deimos.phase3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.image.AngleGenerator;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import deimos.common.DeimosConfig;
import deimos.phase2.DBOperations;

/**
 * 
 * Reference:
 * github.com/kennycason/kumo
 * stackoverflow.com/questions/18949813/how-to-convert-a-string-array-to-inputstream-in-java
 * 
 * @author Siddhesh Karekar
 */
public class WordCloudGenerator
{

	private static final String DIR_CLOUD_OUTPUT = DeimosConfig.DIR_OUTPUT;
	private static final String TYPEFACE = "Monaco";

	/** Create Statements and preparedStatements on this connection. */
	private static Connection db_conn;

	private static BufferedImage wordCloudImage = null;

	private static List<String> topicNamesRepeated;

	private static FrequencyAnalyzer frequencyAnalyzer;

	public static final int DIM_SIDE = 900;

	private static final Dimension dimension = new Dimension(DIM_SIDE, DIM_SIDE);
	private static WordCloud wordCloud;

	private static final Random rand = new Random();

	public static final Color[] COLOR_THEMES = {Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA};
	public static final Color COLOR_LIGHTYELLOW = new Color(255, 187, 0);
	public static final Color COLOR_LIGHTGREEN = new Color(136, 228, 1);
	public static final Color COLOR_LIGHTBLUE = new Color(0, 188, 255);

	public static final ColorPalette PALETTE_DARK = new LinearGradientColorPalette(Color.BLACK, getRandomColorFromTheme(), 50);
	public static final ColorPalette PALETTE_LIGHT = new ColorPalette(COLOR_LIGHTYELLOW, COLOR_LIGHTBLUE, COLOR_LIGHTGREEN);
	// public static final ColorPalette PALETTE_LIGHT = new LinearGradientColorPalette(COLOR_LIGHTYELLOW, COLOR_LIGHTBLUE, COLOR_LIGHTGREEN, 17, 17);

	public static BufferedImage getWordCloudImage() {
		return wordCloudImage;
	}

	public static Color getRandomColorFromTheme()
	{
		return COLOR_THEMES[rand.nextInt(COLOR_THEMES.length)];
	}

	private static void initialize() 
	{
		frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setMaxWordLength(100);
		frequencyAnalyzer.setMinWordLength(1);
		frequencyAnalyzer.clearNormalizers();

		// ColorPalette lightPalette = new ColorPalette(Color.GREEN, Color.PINK, Color.ORANGE, Color.WHITE, Color.CYAN, Color.YELLOW);
		// ColorPalette darkPalette = new LinearGradientColorPalette(new Color(243, 12, 19), new Color(17, 31, 200), 30);

		wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		wordCloud.setPadding(1);
		wordCloud.setAngleGenerator(new AngleGenerator(0));
		// wordCloud.setBackground(new RectangleBackground(dimension));
		wordCloud.setBackground(new CircleBackground(DIM_SIDE/2));
		// wordCloud.setBackground(new PixelBoundryBackground(new FileInputStream("")));
		wordCloud.setBackgroundColor(new Color(244, 244, 244)); // Default javaFX BG
		wordCloud.setColorPalette(PALETTE_LIGHT);
		wordCloud.setKumoFont(new KumoFont(new Font(TYPEFACE, Font.PLAIN, 32)));
		wordCloud.setFontScalar(new LinearFontScalar(16, 80));
	}
	
	private static List<String> interests;

	public static List<String> getInterests() {
		return interests;
	}

	/**
	 * Uses the input training values to construct a word
	 * cloud for the specified user_id.
	 * The word cloud is accessible by getWordCloud() and is
	 * also available as an image.
	 * 
	 * @param user_id UserTrainingInput must be complete.
	 * @param db_conn Supply your own connection.
	 * @throws SQLException
	 */
	public static void outputWordCloud(int user_id, Connection db_conn) throws SQLException
	{
		Statement stmt = db_conn.createStatement();

		// Selects the most similar topic_name for each URL
		/*String query = "SELECT URL, topic_name, visit_count "
				+ "FROM user_urls NATURAL JOIN "
				+ "(SELECT url, topic_name FROM user_ref_similarity NATURAL JOIN "
				+ "(SELECT url, MAX(similarity) \"SIMILARITY\" "
				+ "FROM user_ref_similarity WHERE user_id="+user_id+
				" GROUP BY url))";*/

		String query = "SELECT topic_name, value FROM user_training_input WHERE user_id = "+user_id;

		
		try {

			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()) {
				String topicName = rs.getString("topic_name");
				topicName = topicName.replace("Top/Shopping/","");
				float value = rs.getFloat("value")+0.01f;
				int frequency = Math.round(value * 50); // 50 limit of WordCloud?
				while(frequency > 0)
				{
					topicNamesRepeated.add(topicName);
					frequency--;
				}
			}

			rs.close();
			
			///////////
			String query2 = "SELECT topic_name, SUM(Similarity) \"sumsim\" FROM user_urls NATURAL JOIN (SELECT * FROM user_ref_similarity NATURAL JOIN (SELECT url, MAX(similarity) \"SIMILARITY\" FROM user_ref_similarity WHERE user_id="+user_id+" GROUP BY url)) GROUP BY topic_name order by \"sumsim\" DESC";

			
			interests = new ArrayList<>();
			rs = stmt.executeQuery(query2);
			int max = 5;
			while(rs.next() && max-- > 0)
			{
				interests.add(rs.getString("topic_name").replace("Top/Shopping/",""));
			}

			if(topicNamesRepeated.size() > 0) {
				System.out.println(topicNamesRepeated.size()+" topic name(s) added.");

				initialize();

				List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(topicNamesRepeated);
				wordCloud.build(wordFrequencies);

				wordCloudImage = wordCloud.getBufferedImage();

				wordCloud.writeToFile(DIR_CLOUD_OUTPUT + "/" + DeimosConfig.FILE_OUTPUT_CLOUD);
				System.out.println("Word cloud created and stored in "+(DIR_CLOUD_OUTPUT+ "/" + DeimosConfig.FILE_OUTPUT_CLOUD)+".");
			}
			else {
				System.out.println("No training data found for user "+user_id+"!");
			}
		}
		catch (SQLException e1) {

			e1.printStackTrace();
		}
		stmt.close();
	}

	/**
	 * Uses the input training values to construct a word
	 * cloud for the specified user_id.
	 * The word cloud is accessible by getWordCloud() and is
	 * also available as an image.
	 * <br><br>
	 * This version opens its own connection to the database.
	 * 
	 * @param user_id UserTrainingInput must be complete.
	 */
	public static void outputWordCloud(int user_id)
	{
		topicNamesRepeated = new ArrayList<>();
		BasicConfigurator.configure();

		try
		{
			db_conn = DBOperations.getConnectionToDatabase("WordCloudGenerator");

			outputWordCloud(user_id, db_conn);

			db_conn.close();

		} catch (SQLException e2) {

			e2.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		outputWordCloud(2);
	}

}

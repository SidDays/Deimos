package deimos.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.image.AngleGenerator;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

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
public class WordCloudTest
{

	private static final String FILE_CLOUD_OUTPUT = "wordcloud.png";
	private static final String DIR_CLOUD_OUTPUT = DeimosConfig.DIR_OUTPUT;

	private static DBOperations dbo;

	static {
		try {
			dbo = new DBOperations();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		List<String> topicNames = new ArrayList<>();
		
		// Selects the most similar topic_name for each URL
		String query = "SELECT topic_name, MAX(similarity) FROM user_ref_similarity GROUP BY url, topic_name";
		try
		{
			ResultSet rs = dbo.executeQuery(query);
			while(rs.next()) {
				String currentTopicName = rs.getString("topic_name");
				// currentTopicName = currentTopicName.replace("Top/", "");
				if(currentTopicName.contains("/"))
				{
					currentTopicName = currentTopicName.substring(currentTopicName.indexOf("/")+1);
				}
				currentTopicName = currentTopicName.replace("/", " ");
				topicNames.add(currentTopicName);
				System.out.print("["+currentTopicName + "] ");
			}
			System.out.println();
			System.out.println(topicNames.size()+" topic(s) added.");
			rs.close();

			final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
			List<WordFrequency> wordFrequencies;
			wordFrequencies = frequencyAnalyzer.load(topicNames);
			
			final Dimension dimension = new Dimension(640, 480);
			final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
			wordCloud.setPadding(0);
			// wordCloud.setAngleGenerator(new AngleGenerator(0));
			wordCloud.setBackground(new RectangleBackground(dimension));
			wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.YELLOW, Color.GREEN));
			wordCloud.setKumoFont(new KumoFont(new Font("Lucida Sans", Font.PLAIN, 8)));
			wordCloud.setFontScalar(new LinearFontScalar(10, 40));
			wordCloud.build(wordFrequencies);


			wordCloud.writeToFile(DIR_CLOUD_OUTPUT + "/" + FILE_CLOUD_OUTPUT);
			System.out.println("Word cloud created and stored in "+(DIR_CLOUD_OUTPUT+ "/" + FILE_CLOUD_OUTPUT)+".");

		} catch (SQLException e2) {

			e2.printStackTrace();
		}

	}

}

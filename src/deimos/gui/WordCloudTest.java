package deimos.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

import com.kennycason.kumo.*;
import com.kennycason.kumo.bg.*;
import com.kennycason.kumo.font.*;
import com.kennycason.kumo.font.scale.*;
import com.kennycason.kumo.nlp.*;
import com.kennycason.kumo.palette.*;

import deimos.common.DeimosConfig;
import deimos.common.StringUtils;
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
	private static final String FONT = "Monaco";

	private static DBOperations dbo;

	static {
		try {
			dbo = new DBOperations();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void outputWordCloud(int user_id)
	{
		List<String> topicNames = new ArrayList<>();
		BasicConfigurator.configure();
		
		// Selects the most similar topic_name for each URL
		String query = "SELECT URL, topic_name, visit_count "
				+ "FROM user_urls NATURAL JOIN "
				+ "(SELECT url, topic_name FROM user_ref_similarity NATURAL JOIN "
				+ "(SELECT url, MAX(similarity) \"SIMILARITY\" FROM user_ref_similarity WHERE user_id="+user_id+" GROUP BY url))";
		
		/*String query = "SELECT topic_name, MAX(similarity) FROM user_ref_similarity WHERE user_id="+user_id+
				" GROUP BY url, topic_name";*/
		try
		{
			ResultSet rs = dbo.executeQuery(query);
			while(rs.next()) {
				String currentTopicName = rs.getString("topic_name");
				int visitCount = rs.getInt("VISIT_COUNT");
				
				// String oldTopicName = currentTopicName;
				
				currentTopicName = currentTopicName.replace("Top/Shopping/", "");
				
				/*int lastIndex = -1;
				if(currentTopicName.contains("/"))
				{
					lastIndex = currentTopicName.lastIndexOf("/")+1;
					currentTopicName = currentTopicName.substring(lastIndex);
				}
				
				if(currentTopicName.length() < 2) {
					int secondLastIndex = oldTopicName.lastIndexOf("/", lastIndex-2);
					currentTopicName = oldTopicName.substring(secondLastIndex+1);
				}*/
				
				// currentTopicName = currentTopicName.replace("_", "");
				currentTopicName = StringUtils.toTitleCase(currentTopicName);
				currentTopicName = currentTopicName.replace("/", "-");
				
				for(int i = 0; i < visitCount; i++)
						topicNames.add(currentTopicName);
				// System.out.print("["+currentTopicName + "] ");
			}
			System.out.println();
			System.out.println(topicNames.size()+" topic(s) added.");
			rs.close();
			
			Map<String,Integer> topicsCounts = new HashMap<>();
			for(int i = 0; i < topicNames.size(); i++)
			{	
				String currentTopic = topicNames.get(i);
				Integer existingCount = topicsCounts.get(currentTopic);

				// If it is not in the HashMap, null will be returned.
				if(existingCount == null)
					topicsCounts.put(currentTopic, 1);
				else
					topicsCounts.put(currentTopic, existingCount + 1);
			}
			
			// print that shit
			for (Map.Entry<String, Integer> entry : topicsCounts.entrySet())
			{
			    String key = entry.getKey();
			    int value = entry.getValue();
			    
			    System.out.format("%27s - %d\n", key, value);
			}
			
			final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
			frequencyAnalyzer.setMaxWordLength(100);
			frequencyAnalyzer.setMinWordLength(1);
			
			frequencyAnalyzer.clearNormalizers();
			
			List<WordFrequency> wordFrequencies;
			wordFrequencies = frequencyAnalyzer.load(topicNames);
			
			final Dimension dimension = new Dimension(720, 720);
			final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
			wordCloud.setPadding(1);
			// wordCloud.setAngleGenerator(new AngleGenerator(90));
			wordCloud.setBackground(new RectangleBackground(dimension));
			// wordCloud.setBackground(new CircleBackground(360));
			// wordCloud.setBackground(new PixelBoundryBackground(new FileInputStream("")));
			wordCloud.setColorPalette(new ColorPalette(Color.GREEN, Color.PINK, Color.ORANGE, Color.WHITE, Color.CYAN, Color.YELLOW));
			wordCloud.setKumoFont(new KumoFont(new Font(FONT, Font.PLAIN, 4)));
			wordCloud.setFontScalar(new LinearFontScalar(4, 60));
			wordCloud.build(wordFrequencies);


			wordCloud.writeToFile(DIR_CLOUD_OUTPUT + "/" + FILE_CLOUD_OUTPUT);
			System.out.println("Word cloud created and stored in "+(DIR_CLOUD_OUTPUT+ "/" + FILE_CLOUD_OUTPUT)+".");

		} catch (SQLException e2) {

			e2.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		outputWordCloud(1);
	}

}

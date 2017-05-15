package deimos.phase2.similarity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import deimos.common.TimeUtils;
import deimos.phase2.DBOperations;


public class GradualExtraWeight {
	
	private static Connection db_conn;
	
	public static void executeGEWQuery(int user_id) {
		
		long startTime = System.currentTimeMillis();
		
		 try {
			 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			db_conn = DBOperations.getConnectionToDatabase("GradualExraWeight");
			
			String query = "DECLARE \n " +
					"    user_id_current user_urls.user_id%type;\n " +
					"    topic_current   ref_topics.topic_name%type;\n " +
					"    url_current     user_urls.url%type;\n " +
					"    sum_of_children user_ref_similarity.similarity%type DEFAULT 0.0;\n " +
					"    level_children  INTEGER;\n " +
					"    levels_total    INTEGER;\n " +
					"    extra           user_ref_similarity.similarity%type DEFAULT 0.0;\n " +
					"    similarity_old  user_ref_similarity.similarity%type DEFAULT 0.0;\n " +
					"    similarity_new  user_ref_similarity.similarity%type DEFAULT 0.0;\n " +
					"    alpha           user_ref_similarity.similarity%type DEFAULT 0.5;\n " +
					"    \n " +
					"        \n " +
					"    \n " +
					"BEGIN\n " +
					"    --Hardcode for now\n " +
					"    user_id_current := 1;\n " +
					"    levels_total := 3;\n " +
					"    \n " +
					"    -- This cross joins ALL topics and urls. Handle empty combinations!\n " +
					"    FOR c_urls_topics IN (SELECT ref_hierarchy.child_name, user_urls.url FROM ref_hierarchy, user_urls WHERE user_urls.user_id = user_id_current)\n " +
					"    LOOP\n " +
					"        topic_current := c_urls_topics.child_name;\n " +
					"        url_current := c_urls_topics.url;\n " +
					"        level_children := LENGTH(topic_current) - LENGTH(REPLACE(topic_current, '/', '')) + 1;\n " +
					"        \n " +
					"        --Hardcode for now\n " +
					"        --topic_current := 'Top/Shopping/Death_Care';\n " +
					"        --level_children := LENGTH(topic_current) - LENGTH(REPLACE(topic_current, '/', '')) + 1;\n " +
					"        --url_current := 'https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Dkitchen&field-keywords=neck+massager';\n " +
					"        \n " +
					"        dbms_output.put_line('url = ' || url_current || ', topic = ' || topic_current || ', level+1 = ' || level_children);\n " +
					"        \n " +
					"        BEGIN\n " +
					"            SELECT similarity\n " +
					"            INTO similarity_old\n " +
					"            FROM user_ref_similarity\n " +
					"            WHERE topic_name = topic_current AND url = url_current;\n " +
					"            dbms_output.put_line('Similarity = ' || similarity_old);\n " +
					"            \n " +
					"            \n " +
					"            -- Find the sum of the children\n " +
					"            SELECT NVL(SUM(similarity), 0)\n " +
					"            INTO sum_of_children\n " +
					"            FROM ref_hierarchy\n " +
					"            JOIN user_ref_similarity\n " +
					"            ON ref_hierarchy.child_name = user_ref_similarity.topic_name\n " +
					"            WHERE user_ref_similarity.url = url_current\n " +
					"            AND ref_hierarchy.topic_name = topic_current;\n " +
					"            \n " +
					"            dbms_output.put_line('Sum of its children = ' || sum_of_children);\n " +
					"            \n " +
					"            extra := sum_of_children * alpha * level_children;\n " +
					"            dbms_output.put_line('Extra = ' || extra);\n " +
					"            \n " +
					"            similarity_new := similarity_old + extra;\n " +
					"            dbms_output.put_line('Similarity new = ' || similarity_new);\n " +
					"			UPDATE user_ref_similarity\n " +
					"			SET similarity = similarity_new\n " +
					"			WHERE user_id = user_id_current\n " +
					"			AND url = url_current\n " +
					"			AND topic_name = topic_current;\n " +
					"			dbms_output.put_line('Similarity value is updated');\n " +
					"            \n " +
					"        EXCEPTION\n " +
					"            WHEN NO_DATA_FOUND THEN\n " +
					"            \n " +
					"                dbms_output.put_line('Handle this! no combination exists');\n " +
					"                dbms_output.put_line('Setting similarity_old to 0.');\n " +
					"                \n " +
					"                \n " +
					"                --In this case, insert a new row\n " +
					"                similarity_old := 0;\n " +
					"                dbms_output.put_line('Similarity = ' || similarity_old);\n " +
					"                \n " +
					"                --REPEAT CODE!\n " +
					"                \n " +
					"                -- Find the sum of the children\n " +
					"                SELECT NVL(SUM(similarity), 0)\n " +
					"                INTO sum_of_children\n " +
					"                FROM ref_hierarchy\n " +
					"                JOIN user_ref_similarity\n " +
					"                ON ref_hierarchy.child_name = user_ref_similarity.topic_name\n " +
					"                WHERE user_ref_similarity.url = url_current\n " +
					"                AND ref_hierarchy.topic_name = topic_current;\n " +
					"                \n " +
					"                dbms_output.put_line('Sum of its children = ' || sum_of_children);\n " +
					"                \n " +
					"                extra := sum_of_children * alpha * level_children;\n " +
					"                dbms_output.put_line('Extra = ' || extra);\n " +
					"                \n " +
					"                similarity_new := similarity_old + extra;\n " +
					"                dbms_output.put_line('Similarity new = ' || similarity_new);\n " +
					"				if( extra > 0 ) then\n " +
					"					INSERT INTO user_ref_similarity (user_id, url, topic_name, similarity)\n " +
					"					VALUES (user_id_current, url_current, topic_current, similarity_new);\n " +
					"					dbms_output.put_line('A new row is inserted');\n " +
					"				else\n " +
					"					dbms_output.put_line('New similarity is zero, discarding the row');\n " +
					"				end if;\n " +
					"            \n " +
					"        END;\n " +
					"        dbms_output.put_line('');\n " +
					"        \n " +
					"    END LOOP;\n " +
					"END; \n";
			CallableStatement cs = db_conn.prepareCall(query);
			System.out.println("GEW process started");
			cs.execute();
			long stopTime = System.currentTimeMillis();
			System.out.println("GEW process ended in " +TimeUtils.formatHmss(stopTime-startTime)+ ".");
			cs.close();
			db_conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//hardcoded user_id value
		executeGEWQuery(1);
	}
}

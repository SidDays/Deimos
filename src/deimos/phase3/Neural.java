package deimos.phase3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/*
 * Encog(tm) Java Examples v3.3
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-examples
 *
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import deimos.phase2.DBOperations;

/**
 * XOR: This example is essentially the "Hello World" of neural network
 * programming.  This example shows how to construct an Encog neural
 * network to predict the output from# the XOR operator.  This example
 * uses backpropagation to train the neural network.
 * 
 * This example attempts to use a minimum of Encog features to create and
 * train the neural network.  This allows you to see exactly what is going
 * on.  For a more advanced example, that uses Encog factories, refer to
 * the  example.
 * 
 */
public class Neural
{
	public static final int MAX_ITERATIONS = 10000;
	
	public static List<User> trainingUsers = new ArrayList<>();
	
	private static double[][] INPUT, IDEAL; // TODO

	/** Create Statements and preparedStatements on this connection. */
	private static Connection db_conn;

	private static BasicNetwork network;

	private static MLDataSet trainingSet;
	
	public static final double ERROR_ALLOWED_DEFAULT = 0.01;

	static {
		initializeNetwork();
	}

	public static String getDataAsString(double[] row)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < row.length; i++)
		{
			sb.append(String.format("%.2f", row[i]));
			if(i!=row.length-1)
				sb.append(", ");
		}

		return sb.toString();
	}

	private static PreparedStatement pstmtUsersWithValues = null;

	/**
	 * Returns a list of User objects that can be used to populate
	 * the spinner. These don't have Gender and/or YoB.
	 * Also populates its own list of Users that can be used to train
	 * the network.
	 * 
	 * @return
	 */
	public static List<User> separateUsers()
	{
		trainingUsers.clear();

		List<User> predictionUsers = null;

		// TODO
		try
		{
			db_conn = DBOperations.getConnectionToDatabase("Neural");

			// Select those users who have input values
			pstmtUsersWithValues = db_conn.prepareStatement(
						"SELECT * "
								+ "FROM user_info "
								+ "WHERE user_id "
								+ "IN (SELECT DISTINCT (user_id) FROM user_training_input)");
			ResultSet rs = pstmtUsersWithValues.executeQuery();
			
			PreparedStatement pstmt = db_conn.prepareStatement("SELECT * FROM user_training_input WHERE user_id = ?");

			// DO SOMETHING!
			// TODO
			predictionUsers = new ArrayList<>();
			while(rs.next())
			{
				User u = new User();
				u.setUserId(rs.getInt("USER_ID"));
				
				// Add its input values
				pstmt.setInt(1, u.getUserId());
				ResultSet rs2 = pstmt.executeQuery();
				double[] row = new double[NeuralConstants.NODES_INPUT];
				int current = 0;
				while(rs2.next())
				{
					row[current++] = rs2.getFloat("VALUE");
				}
				rs2.close();
				u.setInput_row(row);
				
				u.setfName(rs.getString("FIRST_NAME"));
				u.setlName(rs.getString("LAST_NAME"));
				u.setLocation(rs.getString("LOCATION"));
				u.setPublicIP(rs.getString("IP"));
				u.setYearOfBirth(rs.getInt("BIRTH_YEAR"));
				u.setGender(rs.getString("GENDER"));
				
				System.out.format("%s (%s) [%s] {%s} \n", u, u.getAge(), u.getGender(),
						NeuralConstants.GROUPS[NeuralConstants.getAgeGroup(u)]);
				
				// Separate them
				if(u.getGender() == null || u.getYearOfBirth() == 0)
				{
					predictionUsers.add(u);
				}
				else {
					predictionUsers.add(u); // Add anyway
					trainingUsers.add(u);
				}
			}

			rs.close();

			db_conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return predictionUsers;
	}

	/**
	 * Sets up the neural network structure.
	 * Called by default in static.
	 */
	private static void initializeNetwork()
	{
		// create a neural network, without using a factory
		network = new BasicNetwork();

		// BasicLayer(ActivationFunction activationFunction, boolean hasBias, int neuronCount)
		network.addLayer(new BasicLayer(null, true, NeuralConstants.NODES_INPUT));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 20));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 20));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, NeuralConstants.NODES_OUTPUT));

		network.getStructure().finalizeStructure();
		network.reset();
	}

	/**
	 * Trains the Neural Network using all the users
	 * who have gender and yOB defined.
	 */
	public static void train(double allowedError) // TODO
	{
		
		INPUT = new double[trainingUsers.size()][NeuralConstants.NODES_INPUT];
		IDEAL = new double[trainingUsers.size()][NeuralConstants.NODES_OUTPUT];
		for(int i = 0; i < INPUT.length; i++)
		{
			User u = trainingUsers.get(i);
			System.out.println(i+": Training for user "+u+", gender = "+u.getGender()+", age = "+u.getAge());
			int ageGroup = NeuralConstants.getAgeGroup(u);
			int gender = (u.getGender().equalsIgnoreCase("m"))?0:1;
			
			int group = NeuralConstants.getGroup(ageGroup, gender);
			double ideal[] = NeuralConstants.VALUES_IDEAL[group];
			System.out.println(Neural.getDataAsString(ideal));
			IDEAL[i] = ideal;
			
			for(int j = 0; j < INPUT[0].length; j++)
			{
				INPUT[i][j] = u.getInput_row()[j];
				
			}
		}
		
		/*double INPUT[][] = { NeuralConstants.getRandomInputDataRow(),
				NeuralConstants.getRandomInputDataRow(),
				NeuralConstants.getRandomInputDataRow(),
				NeuralConstants.getRandomInputDataRow(),
				NeuralConstants.getRandomInputDataRow(),
				NeuralConstants.getRandomInputDataRow()};
		// public static double INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };

		double IDEAL[][] = { NeuralConstants.YOUNG_MALE_IDEAL,
				NeuralConstants.YOUNG_FEMALE_IDEAL,
				NeuralConstants.MID_MALE_IDEAL,
				NeuralConstants.MID_FEMALE_IDEAL,
				NeuralConstants.OLD_MALE_IDEAL,
				NeuralConstants.OLD_FEMALE_IDEAL};*/
		// public static double IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

		// create training data
		trainingSet = new BasicMLDataSet(INPUT, IDEAL);

		// train the neural network
		// final Backpropagation train = new Backpropagation(network, trainingSet);
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

		int epoch = 1;

		do
		{
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error: " + train.getError());
			epoch++;
		}
		while(train.getError() > allowedError && epoch < MAX_ITERATIONS);
		
		train.finishTraining();

		predict();
	}
	
	public static void train()
	{
		train(ERROR_ALLOWED_DEFAULT);
	}
	
	public static double[] predict(User u)
	{
		System.out.println("Training for user "+u);
		return predict(u.getInput_row());
	}
	
	public static double[] predict(double[] row)
	{
		
		MLData data = new BasicMLData(row);

		// test the neural network
		System.out.println("Neural Network Results:");

		final MLData output = network.compute(data);

		System.out.println("actual = " + getDataAsString(output.getData()));
		/* +, ideal = " + getDataAsString(pair.getIdeal().getData()))*/
		
		return output.getData();

	}

	private static void predict()
	{
		// test the neural network
		System.out.println("Neural Network Results:");

		for(MLDataPair pair: trainingSet )
		{
			final MLData output = network.compute(pair.getInput());


			System.out.println("actual = " + getDataAsString(output.getData()) + 
					", ideal = " + getDataAsString(pair.getIdeal().getData()));
		}

	}

	public static void shutdown() {
		Encog.getInstance().shutdown();
	}

	/** Testing only - The main method.
	 * @param args No arguments are used.
	 */
	public static void main(final String args[])
	{
		
		initializeNetwork();
		separateUsers();
		train();
		predict();
		shutdown();		
	}
}
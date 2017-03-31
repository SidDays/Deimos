package deimos.phase3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeuralConstants {

	public static final int NODES_INPUT = 35;
	public static final int NODES_OUTPUT = 6;

	public static final int LIMIT_YOUNG_MID = 30;
	public static final int LIMIT_MID_OLD = 50;

	public static final String AGES[] = {
			"Young", "Middle-aged", "Old"
	};
	public static final int INT_AGES[] = {
			0, 1, 2
	};

	public static final String GROUPS[] = {
			"Young male",
			"Young female",
			"Middle-aged male",
			"Middle-aged female",
			"Old male",
			"Old female"
	};

	public static int getGroup(int age, int gender)
	{
		if(age == 0)

		{
			if(gender == 0)
			{
				return 0;
			}
			else if(gender == 1)
			{
				return 1;
			}
		}
		else if (age == 1)

		{
			if(gender == 0)
			{
				return 2;
			}
			else if(gender == 1)
			{
				return 3;
			}
		}
		else if (age == 2)

		{
			if(gender == 0)
			{
				return 4;
			}
			else if(gender == 1)
			{
				return 5;
			}
		}

		return -1;
	}

	public static final int YOUNG_MALE = 0;
	public static final double YOUNG_MALE_IDEAL[] = 	{ 1, 0, 0, 0, 0, 0 };

	public static final int YOUNG_FEMALE = 1;
	public static final double YOUNG_FEMALE_IDEAL[] = 	{ 0, 1, 0, 0, 0, 0 };

	public static final int MID_MALE = 2;
	public static final double MID_MALE_IDEAL[] = 		{ 0, 0, 1, 0, 0, 0 };

	public static final int MID_FEMALE = 3;
	public static final double MID_FEMALE_IDEAL[] = 	{ 0, 0, 0, 1, 0, 0 };

	public static final int OLD_MALE = 4;
	public static final double OLD_MALE_IDEAL[] = 		{ 0, 0, 0, 0, 1, 0 };

	public static final int OLD_FEMALE = 5;
	public static final double OLD_FEMALE_IDEAL[] = 	{ 0, 0, 0, 0, 0, 1 };

	public static final double[][] VALUES_IDEAL = {
			YOUNG_MALE_IDEAL,
			YOUNG_FEMALE_IDEAL,
			MID_MALE_IDEAL,
			MID_FEMALE_IDEAL,
			OLD_MALE_IDEAL,
			OLD_FEMALE_IDEAL
	};

	public static int getAgeGroup(int age)
	{
		if(age < LIMIT_YOUNG_MID)
		{
			return INT_AGES[0];
		}
		else if(age > LIMIT_MID_OLD)
		{
			return INT_AGES[2];
		}
		else
			return INT_AGES[1];
	}
	public static int getAgeGroup(User u)
	{
		return getAgeGroup(u.getAge());
	}

	/**
	 * Returns a List of Strings in order of which group matched the most.
	 * The bigger the value (i.e. the closer the value is to one), the better
	 * a match it is.
	 * 
	 * @param row
	 * @return
	 */
	public static List<String> getClosestGroups(double[] row)
	{
		List<String> closest = new ArrayList<>(Arrays.asList(GROUPS));

		// Bubble sort ftw
		int l = row.length;
		for(int i = 0; i < l -1; i++)
		{
			for(int j = 0; j < l -i-1; j++)
			{
				if(row[j] > row[j+1])
				{
					String swapA = closest.get(j);
					String swapB = closest.get(j+1);
					closest.set(j, swapB);
					closest.set(j+1, swapA);
				}
			}
		}
		return closest;
	}


	/**
	 * Returns random input values that may be used for fake-training or prediction.
	 * @return double[]
	 */
	public static double[] getRandomInputDataRow()
	{
		double[] inputData = new double[NODES_INPUT];

		for(int i = 0; i < NODES_INPUT; i++) {
			inputData[i] = Math.random();
			// System.out.print (inputData[i]+" ");
		}

		return inputData;
	}

}

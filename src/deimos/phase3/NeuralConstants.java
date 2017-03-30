package deimos.phase3;

public class NeuralConstants {
	
	public static final int NODES_INPUT = 33;
	public static final int NODES_OUTPUT = 6;
	
	public static double[] getRandomInputDataRow()
	{
		
		double[] inputData = new double[NODES_INPUT];
		
		for(int i = 0; i < NODES_INPUT; i++) {
			inputData[i] = Math.random();
			// System.out.print (inputData[i]+" ");
		}
		
		
		return inputData;
	}
	
	public static final String GROUPS[] = {
			"Young male",
			"Young female",
			"Middle-aged male",
			"Middle-aged female",
			"Old male",
			"Old female"
	};
	
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

}

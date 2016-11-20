package placing.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class GenerateGrid {

	public static Map generateGridNumbers() throws IOException {

		int num_longitude = 360;
		int num_latitude = 180;
		int grid_number = 1;
		int i = 0, j = 0;
		int global_long = -179;
		int global_lat = 90;
		int start_longitude = -179;
		int start_latitude = 90;
		Map<String, Integer> map = new HashMap<String, Integer>();
		PrintWriter gtWriter = new PrintWriter(new FileWriter("grid.txt", true));
		while (i < num_latitude - 1) {
			// if (grid_number == 63774){
			// System.out.println("");
			// }
			// if (i == num_latitude - 1) {
			// while (j < num_longitude - 1) {
			// double latbottom = -start_latitude;
			// double longbottom = start_longitude;
			// double lattop = start_latitude;
			// double longtop = start_longitude + 1;
			// String locations = latbottom + "\t" + longbottom + "\t" + lattop
			// + "\t" + longtop;
			// map.put(locations ,grid_number );
			// gtWriter.write(grid_number + "\t" + locations + "\n");
			// gtWriter.flush();
			// start_longitude = start_longitude + 1;
			// // start_latitude = start_latitude +1;
			// grid_number = grid_number + 1;
			// j = j + 1;
			// }
			// if (j == num_longitude - 1) {
			// double latbottom = start_latitude - 1;
			// double longbottom = start_longitude;
			// double lattop = start_latitude;
			// double longtop = -start_longitude;
			// String locations = latbottom + "\t" + longbottom + "\t" + lattop
			// + "\t" + longtop;
			// map.put(locations ,grid_number );
			// gtWriter.write(grid_number + "\t" + locations+ "\n");
			// gtWriter.flush();
			// grid_number = grid_number + 1;
			// }
			//
			// } else {
			while (j < num_longitude - 1) {
				double latbottom = start_latitude - 1;
				double longbottom = start_longitude;
				double lattop = start_latitude;
				double longtop = start_longitude + 1;
				String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
				map.put(locations, grid_number);
				gtWriter.write(grid_number + "\t" + locations + "\n");
				gtWriter.flush();
				start_longitude = start_longitude + 1;
				// start_latitude = start_latitude +1;
				grid_number = grid_number + 1;
				j = j + 1;
			}
			if (j == 359) {
				double latbottom = start_latitude - 1;
				double longbottom = start_longitude;
				double lattop = start_latitude;
				double longtop = -179.0;
				String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
				map.put(locations, grid_number);
				gtWriter.write(grid_number + "\t" + locations + "\n");
				gtWriter.flush();
				start_longitude = start_longitude + 1;
				// start_latitude = start_latitude +1;
				grid_number = grid_number + 1;
				j = j + 1;
			}

			// if (j == num_longitude - 1) {
			// double latbottom = start_latitude - 1;
			// double longbottom = start_longitude;
			// double lattop = start_latitude;
			// double longtop = -start_longitude;
			// String locations = latbottom + "\t" + longbottom + "\t" + lattop
			// + "\t" + longtop;
			// map.put(locations ,grid_number );
			// gtWriter.write(grid_number + "\t" + locations+ "\n");
			// gtWriter.flush();
			// grid_number = grid_number + 1;
			// }

			start_longitude = global_long;
			global_lat = global_lat - 1;
			start_latitude = global_lat;
			i = i + 1;
			j = 0;
			// if(i == 176){
			// System.out.println("Test");
			// }
			if (i == 179) {
				while (j < num_longitude - 1) {
					double latbottom = start_latitude - 1;
					double longbottom = start_longitude;
					double lattop = start_latitude;
					double longtop = start_longitude + 1;
					String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
					map.put(locations, grid_number);
					gtWriter.write(grid_number + "\t" + locations + "\n");
					gtWriter.flush();
					start_longitude = start_longitude + 1;
					// start_latitude = start_latitude +1;
					grid_number = grid_number + 1;
					j = j + 1;
				}
				if (j == 359) {
					double latbottom = start_latitude - 1;
					double longbottom = start_longitude;
					double lattop = start_latitude;
					double longtop = -179.0;
					String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
					map.put(locations, grid_number);
					gtWriter.write(grid_number + "\t" + locations + "\n");
					gtWriter.flush();
					start_longitude = start_longitude + 1;
					// start_latitude = start_latitude +1;
					grid_number = grid_number + 1;
					j = j + 1;
				}
			}
		}
		System.out.println("Done");
		return map;

	}

	public static Map<Integer, String> generateLocationGridMap() throws IOException {

		int num_longitude = 360;
		int num_latitude = 180;
		int grid_number = 1;
		int i = 0, j = 0;
		int global_long = -179;
		int global_lat = 90;
		int start_longitude = -179;
		int start_latitude = 90;
		Map<Integer, String> map = new HashMap<Integer, String>();
		PrintWriter gtWriter = new PrintWriter(new FileWriter("grid.txt", true));
		while (i < num_latitude - 1) {
			// if (grid_number == 63774){
			// System.out.println("");
			// }
			// if (i == num_latitude - 1) {
			// while (j < num_longitude - 1) {
			// double latbottom = -start_latitude;
			// double longbottom = start_longitude;
			// double lattop = start_latitude;
			// double longtop = start_longitude + 1;
			// String locations = latbottom + "\t" + longbottom + "\t" + lattop
			// + "\t" + longtop;
			// map.put(locations ,grid_number );
			// gtWriter.write(grid_number + "\t" + locations + "\n");
			// gtWriter.flush();
			// start_longitude = start_longitude + 1;
			// // start_latitude = start_latitude +1;
			// grid_number = grid_number + 1;
			// j = j + 1;
			// }
			// if (j == num_longitude - 1) {
			// double latbottom = start_latitude - 1;
			// double longbottom = start_longitude;
			// double lattop = start_latitude;
			// double longtop = -start_longitude;
			// String locations = latbottom + "\t" + longbottom + "\t" + lattop
			// + "\t" + longtop;
			// map.put(locations ,grid_number );
			// gtWriter.write(grid_number + "\t" + locations+ "\n");
			// gtWriter.flush();
			// grid_number = grid_number + 1;
			// }
			//
			// } else {
			while (j < num_longitude - 1) {
				double latbottom = start_latitude - 1;
				double longbottom = start_longitude;
				double lattop = start_latitude;
				double longtop = start_longitude + 1;
				String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
				map.put(grid_number, locations);
				gtWriter.write(grid_number + "\t" + locations + "\n");
				gtWriter.flush();
				start_longitude = start_longitude + 1;
				// start_latitude = start_latitude +1;
				grid_number = grid_number + 1;
				j = j + 1;
			}
			if (j == 359) {
				double latbottom = start_latitude - 1;
				double longbottom = start_longitude;
				double lattop = start_latitude;
				double longtop = -179.0;
				String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
				map.put(grid_number, locations);
				gtWriter.write(grid_number + "\t" + locations + "\n");
				gtWriter.flush();
				start_longitude = start_longitude + 1;
				// start_latitude = start_latitude +1;
				grid_number = grid_number + 1;
				j = j + 1;
			}

			// if (j == num_longitude - 1) {
			// double latbottom = start_latitude - 1;
			// double longbottom = start_longitude;
			// double lattop = start_latitude;
			// double longtop = -start_longitude;
			// String locations = latbottom + "\t" + longbottom + "\t" + lattop
			// + "\t" + longtop;
			// map.put(locations ,grid_number );
			// gtWriter.write(grid_number + "\t" + locations+ "\n");
			// gtWriter.flush();
			// grid_number = grid_number + 1;
			// }

			start_longitude = global_long;
			global_lat = global_lat - 1;
			start_latitude = global_lat;
			i = i + 1;
			j = 0;
			// if(i == 176){
			// System.out.println("Test");
			// }
			if (i == 179) {
				while (j < num_longitude - 1) {
					double latbottom = start_latitude - 1;
					double longbottom = start_longitude;
					double lattop = start_latitude;
					double longtop = start_longitude + 1;
					String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
					map.put(grid_number, locations);
					gtWriter.write(grid_number + "\t" + locations + "\n");
					gtWriter.flush();
					start_longitude = start_longitude + 1;
					// start_latitude = start_latitude +1;
					grid_number = grid_number + 1;
					j = j + 1;
				}
				if (j == 359) {
					double latbottom = start_latitude - 1;
					double longbottom = start_longitude;
					double lattop = start_latitude;
					double longtop = -179.0;
					String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
					map.put(grid_number, locations);
					gtWriter.write(grid_number + "\t" + locations + "\n");
					gtWriter.flush();
					start_longitude = start_longitude + 1;
					// start_latitude = start_latitude +1;
					grid_number = grid_number + 1;
					j = j + 1;
				}
			}
		}
		System.out.println("Done");
		return map;

	}

	public static Map generateGridsAtKDegreeDistance(double k) throws IOException {

		String text = Double.toString(Math.abs(k));
		int integerPlaces = text.indexOf('.');
		int decimalPlaces = text.length() - integerPlaces - 1;
		// System.out.println(decimalPlaces);

		double num_longitude = 360.0;
		double num_latitude = 180.0;
		int grid_number = 1;
		double i = 0.0, j = 0.0;
		double global_long = -179.0;
		double global_lat = 90.0;
		double start_longitude = -179.0;
		double start_latitude = 90.0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		PrintWriter gtWriter = new PrintWriter(new FileWriter("kGrid.txt", true));
		while (i < num_latitude - k) {

			while (j <= num_longitude - 1) {
				double latbottom = start_latitude - k;
				double longbottom = start_longitude;
				double lattop = start_latitude;
				double longtop = start_longitude + k;

				BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				latbottom = bd.doubleValue();
				bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longbottom = bd.doubleValue();
				bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				lattop = bd.doubleValue();
				bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longtop = bd.doubleValue();

				String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
				if (locations.equals("51.7	87.3	51.8	87.4")) {
					System.out.println("found");
				}
				map.put(locations, grid_number);
				gtWriter.write(grid_number + "\t" + locations + "\n");
				gtWriter.flush();
				start_longitude = start_longitude + k;
				// start_latitude = start_latitude +1;
				grid_number = grid_number + 1;
				j = j + k;
			}
			if (j > 359.0) {
				boolean flag = true;
				if (true) {
					start_longitude = -start_longitude;
				}
				while (j <= 360.0) {
					double latbottom = start_latitude - k;
					double longbottom = 0.0;
					if (flag) {
						longbottom = -start_longitude;
						flag = false;
					} else {
						longbottom = start_longitude;
					}
					double lattop = start_latitude;
					double longtop = start_longitude + k;

					BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					latbottom = bd.doubleValue();
					bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					longbottom = bd.doubleValue();
					bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					lattop = bd.doubleValue();
					bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					longtop = bd.doubleValue();

					String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
					map.put(locations, grid_number);
					gtWriter.write(grid_number + "\t" + locations + "\n");
					gtWriter.flush();
					start_longitude = start_longitude + k;
					// start_latitude = start_latitude +1;
					grid_number = grid_number + 1;
					j = j + k;
				}
			}

			start_longitude = global_long;
			global_lat = global_lat - k;
			start_latitude = global_lat;
			i = i + k;
			j = 0;

			if (i == num_latitude - k) {
				while (j < num_longitude - 1) {
					double latbottom = start_latitude - k;
					double longbottom = start_longitude;
					double lattop = start_latitude;
					double longtop = start_longitude + k;

					BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					latbottom = bd.doubleValue();
					bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					longbottom = bd.doubleValue();
					bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					lattop = bd.doubleValue();
					bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
					longtop = bd.doubleValue();
					String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
					map.put(locations, grid_number);
					gtWriter.write(grid_number + "\t" + locations + "\n");
					gtWriter.flush();
					start_longitude = start_longitude + k;
					// start_latitude = start_latitude +1;
					grid_number = grid_number + 1;
					j = j + k;
				}
				if (j > 359.0) {
					boolean flag = true;
					if (true) {
						start_longitude = -start_longitude;
					}
					while (j <= 360.0) {
						double latbottom = start_latitude - k;
						double longbottom = 0.0;
						if (flag) {
							longbottom = -start_longitude;
							flag = false;
						} else {
							longbottom = start_longitude;
						}
						double lattop = start_latitude;
						double longtop = start_longitude + k;

						BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
						latbottom = bd.doubleValue();
						bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
						longbottom = bd.doubleValue();
						bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
						lattop = bd.doubleValue();
						bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
						longtop = bd.doubleValue();
						String locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
						map.put(locations, grid_number);
						gtWriter.write(grid_number + "\t" + locations + "\n");
						gtWriter.flush();
						start_longitude = start_longitude + k;
						// start_latitude = start_latitude +1;
						grid_number = grid_number + 1;
						j = j + k;
					}
				}
			}
		}
		System.out.println("Done");
		return map;

	}

	public static BigInteger getGridNumber(String latitudeStr , String longitudeStr, double k){
		
		BigInteger gridNumber = new BigInteger("0");
		int numRow = 180;
		int numColumn = 360;
		double latitude = Double.parseDouble(latitudeStr);
		double longitude = Double.parseDouble(longitudeStr);
		numRow = (int) (numRow / k);
		numColumn = (int) (numColumn/k);
		
		String text = Double.toString(Math.abs(k));
		int integerPlaces = text.indexOf('.');
		int decimalPlaces = text.length() - integerPlaces - 1;
		double latbottom = 0.0;
		double longbottom = 0.0;
		double lattop = 0.0;
		double longtop = 0.0;
		
		double factor = 1/k;
		try {
			if (latitude >= 0 && longitude >= 0) {
				
				latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				
				longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				
				 lattop = latbottom + k;

				 longtop = longbottom + k;
				 
				 if(latitude == 90.0){
					 lattop = 90.0;
					 latbottom = lattop-k;
				 }
				 if(longitude == 180.0){
					 longtop = 180.0;
					 longbottom = longtop-k;
				 }
				
				BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				latbottom = bd.doubleValue();
				bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longbottom = bd.doubleValue();
				bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				lattop = bd.doubleValue();
				bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longtop = bd.doubleValue();
				
				
			} else if (latitude >= 0 && longitude < 0) {
				
				 latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				
				
				 longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				longitude = longbottom;
				
				longbottom = longbottom - k;
			//	double longbottom = (int) longitude - 1;
				 lattop = latbottom + k;
				 longtop = longbottom + k;
				//
				// if(latbottom == 89){
				// lattop = -89;
				// }
				if(k == 0.1){
				if (longitude == -179.9) {
					longbottom = 180;
					longtop = -179.9;
				}
				}else if(k == 0.01){
					if (longitude == -179.99) {
						longbottom = 180;
						longtop = -179.99;
					}
				}else if(k == 0.001){
					if (longitude == -179.999) {
						longbottom = 180;
						longtop = -179.999;
					}
				}
				
				if(latitude == 90.0){
					 lattop = 90.0;
					 latbottom = lattop-k;
				 }
				BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				latbottom = bd.doubleValue();
				bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longbottom = bd.doubleValue();
				bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				lattop = bd.doubleValue();
				bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longtop = bd.doubleValue();
				
				
			} else if (latitude < 0 && longitude >= 0) {
				
				//double latbottom = (int) latitude - 1;
				
				latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				latitude = latbottom;
				
				 longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				
				latbottom = latbottom - k;
				
				 lattop = latbottom + k;

				 longtop = longbottom + k;
				// if(longbottom == 179){
				// longtop = -179;
				// }
//				if(k == 0.1){
//					if (latitude == -89.9) {
//					latbottom = -90;
//					lattop = -89.9;
//				   }
//				}else if(k == 0.01){
//					if (latitude == -89.99) {
//						latbottom = -90;
//						lattop = -89.99;
//					 }
//				}else if(k == 0.001){
//					if (latitude == -89.999) {
//						latbottom = -90;
//						lattop = -89.999;
//					 }
//				}
				
				if(latitude == -90.0){
					 lattop = -90.0 + k;
					 latbottom = -90.0;
				 }
				 if(longitude == 180.0){
					 longtop = 180.0;
					 longbottom = longtop-k;
				 }

				BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				latbottom = bd.doubleValue();
				bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longbottom = bd.doubleValue();
				bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				lattop = bd.doubleValue();
				bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longtop = bd.doubleValue();
				
				
			} else {
				// }else if(latitude < 0 && longitude < 0){
				
				 latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				latitude = latbottom;
				
				 longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				longitude = longbottom;
				
				latbottom = latbottom - k;
				longbottom = longbottom -k;
				
				 lattop = latbottom + k;
				 longtop = longbottom + k;
				
				if(k == 0.1){
				if ( longitude == -179.9) {
					longbottom = 180;
					longtop = -179.9;
				}
				if ( latitude == -90.0) {
					latbottom = -90;
					lattop = -89.9;
				}
				} else if(k == 0.01){

					if ( longitude == -179.99) {
						longbottom = 180;
						longtop = -179.99;
					}
					if ( latitude == -90.0) {
						latbottom = -90.0;
						lattop = -89.99;
					}
				}else if(k == 0.001){

					if ( longitude == -179.999) {
						longbottom = 180;
						longtop = -179.999;
					}
					if ( latitude == -90.0) {
						latbottom = -90.0;
						lattop = -89.999;
					}
				}
				
				BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				latbottom = bd.doubleValue();
				bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longbottom = bd.doubleValue();
				bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				lattop = bd.doubleValue();
				bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longtop = bd.doubleValue();
				
				
			}
		}finally{
			
		}
		BigInteger result = null;
		double x = 90- lattop;
		String xtext = Double.toString(Math.abs(k));
		 integerPlaces = xtext.indexOf('.');
		 decimalPlaces = xtext.length() - integerPlaces - 1;
		 BigDecimal bd = new BigDecimal(x).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
		// System.out.println(bd.doubleValue() * (1/k));
		int numberOfRows =  (int) (bd.doubleValue() * (1/k));
		BigInteger numberOfRowBigInt = new BigInteger(String.valueOf(numberOfRows));
		BigInteger numColumnBigInt = new BigInteger(String.valueOf(numColumn));
		result = numberOfRowBigInt.multiply(numColumnBigInt);
		int numberOfCols = 0;
		if(longtop > -179 && longtop <= 180){
			//System.out.println((Math.abs(-179-longtop))/k);
			double val = Math.abs(-179-longtop);
			 bd = new BigDecimal(val).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
			numberOfCols = (int)(bd.doubleValue() * (1/ k));
			//numberOfCols = numberOfCols +1;
		}else{
			double val = Math.abs(-179-180);
			numberOfCols = (int)(val * (1/k));
		//	numberOfCols = (int) (numberOfCols + (180 + longtop )/k);
			numberOfCols = (int) (numberOfCols + (180 + longtop )/k);
			//numberOfCols = numberOfCols + 1;
		}
		gridNumber = result.add(new BigInteger(String.valueOf(numberOfCols) ));
		return gridNumber;
	}	
	
	
	
	
	public static List<Long> getNeighbourGrids(long gridNumber, double degree, int kNeighbours) {

		int factor = (int) (1 / degree);
		int totalCol = 360 * factor;
		int totalRows = 180 * factor;
		long rowNumber = (gridNumber / totalCol) + 1;
		long remainder = gridNumber % totalCol;
		long columnNum = remainder ;
		if (remainder == 0) {
			rowNumber = rowNumber - 1;
			columnNum = totalCol;
		}
		ArrayList<Long> neighList = new ArrayList();
		neighList.add(gridNumber);
		int currentLayer = 1;
//		if(factor > 100){
//			factor = 100;
//		}
		
		while (kNeighbours >= currentLayer) {
			long currAboveRow = rowNumber - currentLayer;
			long currBelowRow = rowNumber + currentLayer;

			long currLeftCol = columnNum - currentLayer;
			long currentLeft = gridNumber - currentLayer;

			long currRightCol = columnNum + currentLayer;
			long currentRight = gridNumber + currentLayer;

			if (currLeftCol <= 0) {
				currLeftCol = 1;
				currentLeft = (totalCol * (rowNumber - 1)) + 1;
			}
			if (currRightCol > totalCol) {
				currRightCol = totalCol;
				currentRight = (rowNumber * totalCol);
			}
			long startLeftAbv = 1;
			long startRightAbv = totalCol;
			if (currAboveRow >= 1) {
				startLeftAbv = currentLeft - (currentLayer * totalCol);
				startRightAbv = currentRight - (currentLayer *  totalCol);
				for (long i = startLeftAbv; i <= startRightAbv; i++) {
					neighList.add(i);
				}
			}else{
				startLeftAbv = currentLeft ;
				startRightAbv = currentRight;
			}

			long startLeftBlw = 1;
			long startRightBlw = totalCol;
			if (currBelowRow <= totalRows) {
				startLeftBlw = currentLeft + (currentLayer * totalCol);
				startRightBlw = currentRight + (currentLayer *  totalCol);
				for (long i = startLeftBlw; i <= startRightBlw; i++) {
					neighList.add(i);
				}
			}else{
				startLeftBlw = currentLeft;
				startRightBlw = currentRight;
			}

			while (startLeftAbv <= startLeftBlw) {
				if (!neighList.contains(startLeftAbv)) {
					neighList.add(startLeftAbv);
				}
				startLeftAbv = startLeftAbv + totalCol;
//				if (!neighList.contains(startLeftAbv)) {
//					neighList.add(startLeftAbv);
//				}
			}

			while (startRightAbv <= startRightBlw) {
				if (!neighList.contains(startRightAbv)) {
					neighList.add(startRightAbv);
				}
				startRightAbv = startRightAbv + totalCol;
//				if (!neighList.contains(startRightAbv)) {
//					neighList.add(startRightAbv);
//				}
			}

			// if (currAboveRow <= 0) {
			// currAboveRow = 0;
			// }
			// if (currBelowRow > totalRows) {
			// currBelowRow = totalRows+1;
			// }
			// for (int i = currAboveRow + 1; i < currBelowRow; i++) {
			// if (columnNum - currentLayer >= 0) {
			// neighList.add(grid[i][currLeftCol]);
			// }
			// if (columnNum + currentLayer <= 359) {
			// neighList.add(grid[i][currRightCol]);
			// }
			// }

			currentLayer = currentLayer + 1;
		}

		return neighList;
	}

	public static double[] getCornersOfGrid(String cellNumber, double degree){
		
		int factor = (int) (1 / degree);
		int totalCol = 360 * factor;
		int totalRows = 180 * factor;
		long rowNumber = (Long.parseLong(cellNumber) / totalCol) + 1;
		long remainder = Long.parseLong(cellNumber) % totalCol;
		long columnNum = remainder ;
		if (remainder == 0) {
			rowNumber = rowNumber - 1;
			columnNum = totalCol;
		}
		
		double upperLat = -90 + (degree * (rowNumber-1));
		double rightLong = -179 + (degree * (columnNum+1));
		double leftLong = rightLong - degree;
		double lowerLat = upperLat + degree;
		if(degree == 1.0){
			if(columnNum > 359){
				rightLong = -179;
				leftLong = 180;
			}
		}
		if(degree == 0.1){
			if(columnNum > 3590){
				if(columnNum == 3591){
					rightLong = -179.9;
					leftLong = 180;
				}else{
					rightLong = -180 + (columnNum-3590) * degree;
					leftLong = rightLong - degree;
				}
			}
		}
		if(degree == 0.01){
			if(columnNum > 35900){
				if(columnNum == 35901){
					rightLong = -179.99;
					leftLong = 180;
				}else{
					rightLong = -180 + (columnNum-35900) * degree;
					leftLong = rightLong - degree;
				}
			}
		}
		if(degree == 0.001){
			if(columnNum > 359000){
				if(columnNum == 359001){
					rightLong = -179.999;
					leftLong = 180;
				}else{
					rightLong = -180 + (columnNum-359000) * degree;
					leftLong = rightLong - degree;
				}
			}
		}
		
		double[] coordinates = new double[8];
		coordinates[0] = upperLat;
		coordinates[1] = leftLong;
		coordinates[2] = upperLat;
		coordinates[3] = rightLong;
		coordinates[4] = lowerLat;
		coordinates[5] = leftLong;
		coordinates[6] = lowerLat;
		coordinates[7] = rightLong;
		
		return coordinates;
	}

	public static void main(String[] args) throws IOException {
		// testCount();
		// generateGridNumbers();
		//generateGridsAtKDegreeDistance(0.1);
		// double d = 179.99999999998815;
		//
		// System.out.println(d);
		//System.out.println(90-89.7);
		///System.out.println(getGridNumber("89.9999","-179.9", 1.0));
		double coordinates[] =getCornersOfGrid("64800" , 1.0);
		for(double coordinate :  coordinates){
			System.out.println(coordinate);
		}
	}

}



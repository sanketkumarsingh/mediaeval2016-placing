package placing.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import placing.util.GenerateGrid;



public class ExternalTagData {
	
//	public static void main(String[] args) {
//		String test = "Black Sea";
//		test= test.replaceAll(" ", "+");
//		System.out.println(test);
//	}

	public static void main(String[] args) throws IOException {

		//Set<Integer> gridNotInTrainSet = getGridsNotInTrain(); // read
	//	System.out.println("Loaded grid-not-in-train.txt"); // grid-not-in-train.txt
	//	Map<String, Integer> locationGridMap = GenerateGrid.generateGridNumbers();
		// Set<Map.Entry<String, Integer>> entrySet =
		// locationGridMap.entrySet();
		// // Map<Integer, String> gridLocationMap = new HashMap<Integer,
		// // String>();
		// Iterator iterator = entrySet.iterator();
		// while (iterator.hasNext()) {
		// Map.Entry pair = (Map.Entry) iterator.next();
		// String location = (String) pair.getKey();
		// Integer gridNumber = (Integer) pair.getValue();
		// if (!gridNotInTrainSet.contains(gridNumber)) {
		// iterator.remove();
		// }
		// }
		// load usertag file
		Map<String, List<String>> geonameidUsertagMap = new HashMap<String, List<String>>();
		Path utfile = Paths.get("userTags.txt");
		Stream<String> utLines = Files.lines(utfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) utLines::iterator) {
			String lineArr[] = line.split("\t");
			if (!lineArr[1].equalsIgnoreCase("opengeodb") && !lineArr[1].equalsIgnoreCase("place")) {
				if (geonameidUsertagMap.containsKey(lineArr[0])) {
					List<String> userTags = geonameidUsertagMap.get(lineArr[0]);
					userTags.add(lineArr[1]);
				} else {
					List<String> userTags = new ArrayList();
					userTags.add(lineArr[1]);
					geonameidUsertagMap.put(lineArr[0], userTags);
				}
			}
		}
		System.out.println("Loaded userTag.txt");
		// read the all countries file.
		Path countryfile = Paths.get("allCountries.txt");
		PrintWriter tagWriter = null;
		try {
			tagWriter = new PrintWriter(new FileWriter("grid-externalTag-0.1.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stream<String> countryLines = Files.lines(countryfile, StandardCharsets.UTF_8);
		Set<BigInteger> gridCoveredByExt = new HashSet();
		
		int lineNo = 0;
		for (String line : (Iterable<String>) countryLines::iterator) {
			String lineStr[] = line.split("\t");
			// String latStr = lineStr[4]; // lat
			// String longStr = lineStr[5]; // long
		//	int gridNumber = getGridNumber(lineStr[4], lineStr[5], locationGridMap);
			//int gridNumber = getGridNumber(lineStr[4], lineStr[5], map);
			BigInteger gridNumber = GenerateGrid.getGridNumber(lineStr[4], lineStr[5], 0.1);
			//gridCoveredByExt.add(gridNumber);
			if (gridNumber != null && gridNumber.intValue() != 0) {
				gridCoveredByExt.add(gridNumber);
				List<String> userTags = geonameidUsertagMap.get(lineStr[0]);
				String utag = lineStr[1].toLowerCase().replaceAll(" ", "+");
				tagWriter.write(gridNumber + "\t" + utag + "\n"); // names
																		// of
																		// the
																		// place
																		// from
																		// all
																		// countries
				tagWriter.flush();
				if (userTags != null) {
					for (String tag : userTags) {
						tag = tag.toLowerCase().replaceAll(" ", "+");
						tagWriter.write(gridNumber + "\t" + tag + "\n"); // usertag
						// from
						// userTags.txt
						tagWriter.flush();
					}

				}
			}
			lineNo = lineNo + 1;
			if (lineNo % 1000000 == 0) {
				System.out.println("Processed:" + lineNo);
			}
		}
		tagWriter.close();
		System.out.println("Done..");
		PrintWriter gridWriter = null;
		try {
			gridWriter = new PrintWriter(new FileWriter("gridNo-coveredBy-externalTag.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Iterator iterator   = gridCoveredByExt.iterator();
		while(iterator.hasNext()){
			gridWriter.write(iterator.next() + "\n");
		}
		
		System.out.println("finished..");
	}

	private static Set<Integer> getGridsNotInTrain() throws IOException {
		Path gridfile = Paths.get("grid-not-in-train.txt");
		Stream<String> gridLines = Files.lines(gridfile, StandardCharsets.UTF_8);
		Set<Integer> gridsNotInTrainSet = new HashSet();
		for (String line : (Iterable<String>) gridLines::iterator) {
			gridsNotInTrainSet.add(Integer.parseInt(line));
		}
		return gridsNotInTrainSet;
	}

	private static int getGridNumber(String latitudeStr, String longitudeStr, Map map) {
		// System.out.println(latitudeStr + "\t" +longitudeStr );
		String locations = "";
		try {
			double latitude = 0.0;
			double longitude = 0.0;

			latitude = Double.parseDouble(latitudeStr);
			longitude = Double.parseDouble(longitudeStr);

			// double latbottom = -start_latitude;
			// double longbottom = start_longitude;
			// double lattop = start_latitude;
			// double longtop = start_longitude + 1;
			// String locations = latbottom + "\t" + longbottom + "\t" + lattop
			// +
			// "\t" + longtop;
			//
			//

			if (latitude >= 0 && longitude >= 0) {
				double latbottom = (int) latitude;
				if (latbottom == 90.0) {
					latbottom = 89.0;
				}
				double longbottom = (int) longitude;

				double lattop = latbottom + 1;
				double longtop = longbottom + 1;
				if(longbottom == 180.0){
					longtop = -179.0;
				}
				
				// if(longbottom == 179){
				// longtop = 180;
				// }
				//
				//
				// if(latbottom == 89){
				// lattop = -89;
				// }

				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			} else if (latitude >= 0 && longitude < 0) {
				double latbottom = (int) latitude;
				if (latbottom == 90.0) {
					latbottom = 89.0;
				}
				double longbottom = (int) longitude - 1;
				double lattop = latbottom + 1;
				double longtop = longbottom + 1;
				
				//
				// if(latbottom == 89){
				// lattop = -89;
				// }
				if ((int) longitude == -179) {
					longbottom = 180;
					longtop = -179;
				}
				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			} else if (latitude < 0 && longitude >= 0) {

				double latbottom = (int) latitude - 1;
				if (latitude == -90.0) {
					latbottom = -90.0;
				}
				double longbottom = (int) longitude;
				double lattop = latbottom + 1;

				double longtop = longbottom + 1;
				// if(longbottom == 179){
				// longtop = -179;
				// }
				if ((int) latitude == -89) {
					latbottom = -90;
					lattop = -89;
				}
				if(longbottom == 180.0){
					longtop = -179.0;
				}

				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			} else {
				// }else if(latitude < 0 && longitude < 0){
				double latbottom = (int) latitude - 1;
				if (latitude == -90.0) {
					latbottom = -90.0;
				}
				double longbottom = (int) longitude - 1;
				double lattop = latbottom + 1;
				double longtop = longbottom + 1;
				if ((int) longitude == -179) {
					longbottom = 180;
					longtop = -179;
				}
				if ((int) latitude == -89) {
					latbottom = -90;
					lattop = -89;
				}
				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			}

			if (locations.equals("")) {
				return 0;
			}

			return (int) map.get(locations);
		} catch (Exception e) {
			System.out.println("latitudeStr:" + latitudeStr + "   " + "longitudeStr:" + longitudeStr);
			System.out.println("locations:" + locations);
			e.printStackTrace();
		}
		return 0;
	}

}

package placing.train;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import placing.util.GenerateGrid;
import weka.core.Stopwords;

public class BaseModelTraining {

	public static void generateGridNumberUserTagFile() throws IOException {

		//  Map<String, Integer> map = GenerateGrid.generateGridNumbers();  // for degree 1
		Map<String, Integer> map = GenerateGrid.generateGridsAtKDegreeDistance(0.1);
		//Path file = Paths.get("yfcc100m_dataset-withLatLong.txt");
		//Path file = Paths.get("yfcc_withlatlong_usertag_gridNumber.txt");
		Path file = Paths.get("train-photo-video-mediaeval.txt");
		
		//Path file = Paths.get("smalltest.txt");
		// Path file = Paths.get("smallFile.txt");
		Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8);
		PrintWriter gtWriter = new PrintWriter(new FileWriter("gridNumber-userTag.txt", true));
		//PrintWriter userTagWriter = new PrintWriter(new FileWriter("no-usertags.txt", true));
		long no_usertags = 0;
		int k = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			String userTag = lineStr[8];
			if (userTag.equals("")) {
//				userTagWriter.write(line + "\n");
//				userTagWriter.flush();
				no_usertags = no_usertags + 1;
			} else {
				String latitudeStr = lineStr[11];
				String longitudeStr = lineStr[10];
				int gridNumber = getGridNumberAtKdegree(latitudeStr, longitudeStr, map, 0.1);
				if (gridNumber == 0) {
					continue;
				}
				gtWriter.write(gridNumber + "\t" + userTag + "\n");
				gtWriter.flush();
			}
			k = k + 1;
			if (k % 1000000 == 0) {
				System.out.println("Processed files: " + k);
				System.out.println("number of no usertags records:" + no_usertags);
			}

		}
		gtWriter.close();
		System.out.println("No. of images with no user tags:" + no_usertags);
		System.out.println("Generated gridNumber-userTag.txt..");
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
				if(latbottom == 90.0){
					latbottom = 89.0;
				}
				double longbottom = (int) longitude;

				double lattop = latbottom + 1;

				double longtop = longbottom + 1;
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
				if(latbottom == 90.0){
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
				if(latitude == -90.0){
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

				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			} else {
				// }else if(latitude < 0 && longitude < 0){
				double latbottom = (int) latitude - 1;
				if(latitude == -90.0){
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
	
	
	private static int getGridNumberAtKdegree(String latitudeStr, String longitudeStr, Map map, double k) {
		// System.out.println(latitudeStr + "\t" +longitudeStr );
		double latitude = Double.parseDouble(latitudeStr);
		double longitude = Double.parseDouble(longitudeStr);
		// double latbottom = -start_latitude;
		// double longbottom = start_longitude;
		// double lattop = start_latitude;
		// double longtop = start_longitude + 1;
		// String locations = latbottom + "\t" + longbottom + "\t" + lattop +
		// "\t" + longtop;
		//
		//
		String text = Double.toString(Math.abs(k));
		int integerPlaces = text.indexOf('.');
		int decimalPlaces = text.length() - integerPlaces - 1;
		
		double factor = 1/k;
		String locations = "";
		try {
			if (latitude >= 0 && longitude >= 0) {
				
				double latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				
				double longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				
				double lattop = latbottom + k;

				double longtop = longbottom + k;
				
				BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				latbottom = bd.doubleValue();
				bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longbottom = bd.doubleValue();
				bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				lattop = bd.doubleValue();
				bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longtop = bd.doubleValue();
				
				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			} else if (latitude >= 0 && longitude < 0) {
				
				double latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				
				
				double longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				longitude = longbottom;
				
				longbottom = longbottom - k;
			//	double longbottom = (int) longitude - 1;
				double lattop = latbottom + k;
				double longtop = longbottom + k;
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
				}
				BigDecimal bd = new BigDecimal(latbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				latbottom = bd.doubleValue();
				bd = new BigDecimal(longbottom).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longbottom = bd.doubleValue();
				bd = new BigDecimal(lattop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				lattop = bd.doubleValue();
				bd = new BigDecimal(longtop).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
				longtop = bd.doubleValue();
				
				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			} else if (latitude < 0 && longitude >= 0) {
				
				//double latbottom = (int) latitude - 1;
				
				double latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				latitude = latbottom;
				
				double longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				
				latbottom = latbottom - k;
				
				double lattop = latbottom + k;

				double longtop = longbottom + k;
				// if(longbottom == 179){
				// longtop = -179;
				// }
				if(k == 0.1){
					if (latitude == -89.9) {
					latbottom = -90;
					lattop = -89.9;
				   }
				}else if(k == 0.01){
					if (latitude == -89.99) {
						latbottom = -90;
						lattop = -89.99;
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
				
				locations = latbottom + "\t" + longbottom + "\t" + lattop + "\t" + longtop;
			} else {
				// }else if(latitude < 0 && longitude < 0){
				
				double latbottom = latitude * factor ;
				int latbottomInt = (int)latbottom;
				latbottom = latbottomInt * k;
				latitude = latbottom;
				
				double longbottom = longitude * factor;
				int longbottomInt = (int)longbottom;
				longbottom = longbottomInt * k;
				longitude = longbottom;
				
				latbottom = latbottom - k;
				longbottom = longbottom -k;
				
				double lattop = latbottom + k;
				double longtop = longbottom + k;
				
				if(k == 0.1){
				if ( longitude == -179.9) {
					longbottom = 180;
					longtop = -179.9;
				}
				if ((int) latitude == -89.9) {
					latbottom = -90;
					lattop = -89.9;
				}
				} else if(k == 0.01){

					if ( longitude == -179.99) {
						longbottom = 180;
						longtop = -179.99;
					}
					if ((int) latitude == -89.99) {
						latbottom = -90;
						lattop = -89.99;
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
	
	

	public static void generatePostingList() throws IOException {
		Path file = Paths.get("training-32million");
		// Path file = Paths.get("smallFile.txt");
		Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8);
		PrintWriter gtWriter = new PrintWriter(new FileWriter("posting-train-gridNum-userTag.txt", true));
		int first = 1;
		String prevGridNumber = "";
		Map<String, Integer> map = null;
		int lineNumber = 0;
		String gridNumber = "";
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			gridNumber = lineArr[0];
			String userTagsStr = lineArr[1];
			String userTagsArr[] = userTagsStr.split(",");
			if (first == 1) {
				first = 0;
				map = new HashMap();
				for (int i = 0; i < userTagsArr.length; i++) {
					String terms[] = userTagsArr[i].split(Pattern.quote("+"));
					for (int j = 0; j < terms.length; j++) {
						if (map.containsKey(terms[j])) {
							map.put(terms[j], ((int) map.get(terms[j])) + 1);
						} else {
							map.put(terms[j], 1);
						}
					}

				}
				prevGridNumber = lineArr[0];
			} else {

				if (prevGridNumber.equals(lineArr[0])) {

					for (int i = 0; i < userTagsArr.length; i++) {
						String terms[] = userTagsArr[i].split(Pattern.quote("+"));
						for (int j = 0; j < terms.length; j++) {
							if (map.containsKey(terms[j])) {
								map.put(terms[j], ((int) map.get(terms[j])) + 1);
							} else {
								map.put(terms[j], 1);
							}
						}

					}

				} else {
					writeMapToFile(lineArr[0], gtWriter, map);
					// System.out.println("Processed grid: " + lineArr[0] );
					map = new HashMap();

					for (int i = 0; i < userTagsArr.length; i++) {
						String terms[] = userTagsArr[i].split(Pattern.quote("+"));
						for (int j = 0; j < terms.length; j++) {
							if (map.containsKey(terms[j])) {
								map.put(terms[j], ((int) map.get(terms[j])) + 1);
							} else {
								map.put(terms[j], 1);
							}
						}

					}

					prevGridNumber = lineArr[0];

				}
			}

			lineNumber = lineNumber + 1;
			if (lineNumber % 100000 == 0) {
				System.out.println("Processed Line number:" + lineNumber);
			}
		}

		writeMapToFile(gridNumber, gtWriter, map);
		gtWriter.close();

	}

	private static void writeMapToFile(String gridNumber, PrintWriter gtWriter, Map<String, Integer> map) {

		String line = gridNumber;
		Set<Entry<String, Integer>> entrySet = map.entrySet();
		Iterator iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry pair = (Map.Entry) iterator.next();
			String key = (String) pair.getKey();
			Integer value = (Integer) pair.getValue();
			if (!key.isEmpty()) {

				line = line + "\t" + key + "===" + value;
			}
		}
		gtWriter.write(line + "\n");
		gtWriter.flush();

	}

	public static void generateTagBasedPostingList() throws IOException {
		//Path file = Paths.get("sorted-me-ext-grid-tag.txt"); "sorted-grid-usertag-train-me-0.1deg.txt"
		Path file = Paths.get("sorted-train-grid-ut-title-1.0deg.txt"); // sorted by grid
		// Path file = Paths.get("smallFile.txt");
		//Path file = Paths.get("training-120000.txt");
		Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8);
		PrintWriter gtWriter = new PrintWriter(new FileWriter("train-posting-ut-title-grid-1.0deg.txt", true));
		int first = 1;
		String prevGridNumber = "";
		Map<String, Integer> map = null;
		int lineNumber = 0;
		String gridNumber = "";
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Stopwords wnet = new Stopwords();
		for (String line : (Iterable<String>) lines::iterator) {
			lineNumber = lineNumber + 1;
			String lineArr[] = line.split("\t");
			gridNumber = lineArr[0];
			String userTagsStr = lineArr[1];
			if(userTagsStr.isEmpty()){
				continue;
			}
			
			String userTagsArr[] = userTagsStr.split(",");
			ArrayList<String> userTagsList = new ArrayList<String>();
			for(String tag: userTagsArr){
				userTagsList.add(tag);
			}
			
//			for(String tag: userTagsArr){
//				if(tag.length() <= 1){
//					continue;
//				}
//				if(tag.matches(".*\\d+.*")){
//					continue;
//				}
//				if(tag.contains(".jpg")){
//					continue;
//				}
//				
//				tag = tag.toLowerCase();
//				userTagsList.add(tag);
//			///	tag = tag.replaceAll("[^a-zA-Z+ ]", "").toLowerCase();
//				if (!tag.isEmpty() && tag.contains("+")) {
//					
//					String tagwords[] = tag.split(Pattern.quote("+"));
//					for(String tagword: tagwords){	
//					Matcher m = p.matcher(tagword);
//					boolean b = m.find();
//					if (b) {
//						continue;
//					}
////					if (tagword.contains("-")) {
////						String result1 = tagword.replaceAll("-", "");
////						if (result1.isEmpty()) {
////							continue;
////						}
////					}
////					if (tagword.contains(".")) {
////						tagword = tagword.replace(".", "");
////					}
////					if (tagword.contains(",")) {
////						tagword = tagword.replace(",", "");
////					}
////					if (tagword.contains("\n")) {
////						tagword = tagword.replace("\n", "");
////					}
//					if (wnet.isStopword(tagword)) {
//						continue;
//					}
//					if(tagword.isEmpty()){
//						continue;
//					}
//					userTagsList.add(tagword);
//					}
//				}
//			
//			}
			
			//u;
			if (first == 1) {
				first = 0;
				map = new HashMap();
				for (int i = 0; i < userTagsList.size(); i++) {
					if (map.containsKey(userTagsList.get(i))) {
						map.put(userTagsList.get(i), ((int) map.get(userTagsList.get(i))) + 1);
					} else {
						map.put(userTagsList.get(i), 1);
					}
				}
				prevGridNumber = lineArr[0];
			} else {
				if (prevGridNumber.equals(lineArr[0])) {

					for (int i = 0; i < userTagsList.size(); i++) {
						if (map.containsKey(userTagsList.get(i))) {
							map.put(userTagsList.get(i), ((int) map.get(userTagsList.get(i))) + 1);
						} else {
							map.put(userTagsList.get(i), 1);
						}
					}

				} else {
					writeMapToFile(prevGridNumber, gtWriter, map);
					// System.out.println("Processed grid: " + lineArr[0] );
					map = new HashMap();
					for (int i = 0; i < userTagsList.size(); i++) {
						if (map.containsKey(userTagsList.get(i))) {
							map.put(userTagsList.get(i), ((int) map.get(userTagsList.get(i))) + 1);
						} else {
							map.put(userTagsList.get(i), 1);
						}
					}

					prevGridNumber = lineArr[0];

				}
			}

			//lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Processed Line number:" + lineNumber);
			}
		}

		writeMapToFile(gridNumber, gtWriter, map);
		gtWriter.close();

	}
	
	
	public static void generateGridNumberCompleteLineFile() throws IOException {

		Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		Path file = Paths.get("yfcc100m_dataset-withLatLong.txt");
		//Path file = Paths.get("smalltest.txt");
		// Path file = Paths.get("smallFile.txt");
		Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8);
		PrintWriter gtWriter = new PrintWriter(new FileWriter("gridNumber-completedata.txt", true));
		//PrintWriter userTagWriter = new PrintWriter(new FileWriter("no-usertags.txt", true));
		long no_usertags = 0;
		int k = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			String userTag = lineStr[8];
			if (userTag.equals("")) {
//				userTagWriter.write(line + "\n");
//				userTagWriter.flush();
				no_usertags = no_usertags + 1;
			} else {
				String latitudeStr = lineStr[11];
				String longitudeStr = lineStr[10];
				int gridNumber = getGridNumber(latitudeStr, longitudeStr, map);
				if (gridNumber == 0) {
					continue;
				}
				gtWriter.write( line + "\t"+  gridNumber + "\n");
				gtWriter.flush();
			}
			k = k + 1;
			if (k % 1000000 == 0) {
				System.out.println("Processed files: " + k);
				System.out.println("number of no usertags records:" + no_usertags);
			}

		}
		gtWriter.close();
		System.out.println("No. of images with no user tags:" + no_usertags);
		System.out.println("Generated gridNumber-userTag.txt..");
	}

	public static void main(String[] args) throws IOException {
		// String test = "+sa";
		// String testarr[] = test.split(Pattern.quote("+"));

		//generatePostingList();
		//generateTagBasedPostingList();
		//generateGridNumberUserTagFile();
		//generateTagBasedPostingList();
		//generateGridNumberCompleteLineFile();
//		double x = 161.12;
//		int y = (int) x;
//		System.out.println(y);
		generateTagBasedPostingList();
	//	Integer a = new Integer("648000000");
	//	int a = 648000000;
//		System.out.println(a);
	}

}

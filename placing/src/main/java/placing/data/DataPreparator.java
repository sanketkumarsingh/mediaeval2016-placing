package placing.data;

import java.io.FileWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;
import placing.util.GenerateGrid;
import weka.core.Stopwords;

public class DataPreparator {

	public static void main(String[] args) throws IOException {
		// getMetaData("5619201398");
		generateTrain();
//		System.out.println("Training done");
		generateEstimationTestData();
	//	System.out.println("Estimation training done.");
		generateVerificationTestData();
	//	System.out.println("Verification data done..");
	//	generateGridUserTagTrainData();
	//	generateEstimateTestData();
		//	generateVerifTestData();
		
		//System.out.println(Double.toString(Math.abs(.1)));
		//getCleanData();
		
//	    double x= 0.0;
//		x = x * Math.log(x);
//		System.out.println(x);
//		BigInteger x = new BigInteger("64800000000");
//		System.out.println(x.toString());
		
		//generateTrainDataWithLatandLongAtFront();
//		String line = "test"+"\t" + "123" + "\t" + "yes" + "\t" + "no";
//		int index = StringUtils.ordinalIndexOf(line, "\t", 2);
//		line = line.substring(index+1);
//		System.out.println(line);
		
//		generateTrainDataSortedByLatLong();
		
//		generateTrainDataInOneLine();
		//findMaxGrid();
	//	fixMaxGridTrain();
	//	getDistance();
	}
	
	private static void getDistance(){
		
//		gridWriter.write(gridNumber + "\t" +userTag + "\t" + place + "\t" + title +"\t"+longStr +
//				"\t" +latStr+"\t" + lineStr[3] +"\t" + hash+ "\n");
		
			List<TrainInstance> trainList = new ArrayList();
			Path file = Paths.get("yfcc-me-train-grid-ut-title-1.0-13859.txt");
			Stream<String> lines = null;
			try {
				lines = Files.lines(file, StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}// TODO Auto-generated method st

			for (String line : (Iterable<String>) lines::iterator) {
				//lineNo++;
				String lineStr[] = line.split("\t");
				if(!lineStr[6].isEmpty()){
				TrainInstance obj = new TrainInstance();
				
				obj.setId(lineStr[6]);
				obj.setLatitude(lineStr[5]);
				obj.setLongitude(lineStr[4]);
				trainList.add(obj);
				}
				//obj.setTag(lineStr[1]);
			}
			System.out.println("Loaded train data..");
			
			int minIndex = Integer.MAX_VALUE;
			System.out.println("Total training instances:" + trainList.size());
			//double distance[][] = new double[trainList.size()][trainList.size()];
			double minDistance = Double.MAX_VALUE;
			for (int i = 0; i < trainList.size(); i++) {
				TrainInstance ithTrainInstance = trainList.get(i);
				double ithDistance = 0.0;
				for (int j = 0; j < trainList.size(); j++) {
					if(i!=j){
					TrainInstance jthTrainInstance = trainList.get(j);
					GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(ithTrainInstance.getLatitude()),
							Double.parseDouble(ithTrainInstance.getLongitude()),
							Double.parseDouble(jthTrainInstance.getLatitude()),
							Double.parseDouble(jthTrainInstance.getLongitude()), GeodesicMask.DISTANCE);
					ithDistance = ithDistance + g.s12;
					}
				}
				if (ithDistance < minDistance) {
					minDistance = ithDistance;
					minIndex = i;
				}
			}
//			instance.setTag(lineArr[1]);
//			instance.setLatitude(lineArr[5]);
//			instance.setLongitude(lineArr[4]);
//			instance.setId(lineArr[6]);
			System.out.println("Train Instance found:" + trainList.get(minIndex).getId());
			System.out.println(trainList.get(minIndex).getLatitude()+"  " + trainList.get(minIndex).getLongitude());
			System.out.println(trainList.get(minIndex).getTag());
	}
	
	private static void fixMaxGridTrain(){
		Path file = Paths.get("yfcc-me-train-photo-video-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}// TODO Auto-generated method stub
		int lineno = 0;
		PrintWriter gridWriter = null;
		try {
			gridWriter = new PrintWriter(new FileWriter("yfcc-me-train-grid-ut-title-1.0-13859.txt", true));
			//lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			lineno++;
			String lineStr[] = line.split("\t");
			if(lineStr[0].equals("13859")){
				gridWriter.write(line+"\n");
				gridWriter.flush();
			}
		}
		gridWriter.close();
	}
	//Max Grid:13859 Max Count:1019103 - only yfcc
	private static void findMaxGrid() {
	Path file = Paths.get("yfcc-train-grid-ut-title-1.0.txt");
	Stream<String> lines = null;
	try {
		lines = Files.lines(file, StandardCharsets.UTF_8);
	} catch (IOException e) {
		e.printStackTrace();
	}// TODO Auto-generated method stub
	int lineno = 0;
	Map<String, Long> mapGridCount = new HashMap();
	long maxVal = 0;
	String maxGrid = "";
	long  lineNo = 0;
	for (String line : (Iterable<String>) lines::iterator) {
		lineNo++;
		String lineStr[] = line.split("\t");
		if(mapGridCount.containsKey(lineStr[0])){
			long currentCount = (mapGridCount.get(lineStr[0]) +1);
			mapGridCount.put(lineStr[0], currentCount);	
			if(maxVal < currentCount){
				maxVal = currentCount;
				maxGrid = lineStr[0];
			}
		}else{
			mapGridCount.put(lineStr[0], (long) 1);
			if(maxVal < 1){
				maxVal = 1;
				maxGrid = lineStr[0];
			}
		}
		
		if(lineNo %10000000 == 0){
			System.out.println("Processed :" + lineNo);
		}
	}
	
	System.out.println("Max Grid:" + maxGrid + " Max Count:" + maxVal);
}
	
	public static void getCleanData() throws IOException {
		Path gridfile = Paths.get("estimation-test-onlyUT-photo-me-0.1.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Stopwords wnet = new Stopwords();
		PrintWriter gridWriter = null;
		try {
			gridWriter = new PrintWriter(new FileWriter("estimation-test-tags-photo-me-0.1.txt", true));
			//lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			boolean first = true;
			if (!lineArr[1].isEmpty()) {
				String ut = "";
				String userTags[] = lineArr[1].split(",");
				for (String tag : userTags) {
					if (tag.length() <= 1) {
						continue;
					}
					if (tag.matches(".*\\d+.*")) {
						continue;
					}
					if (tag.contains(".jpg")) {
						continue;
					}
					tag = tag.replaceAll("[^a-zA-Z+ ]", "").toLowerCase();
					if (!tag.isEmpty()) {

						String tagwords[] = tag.split(Pattern.quote("+"));
						for (String tagword : tagwords) {
							Matcher m = p.matcher(tagword);
							boolean b = m.find();
							if (b) {
								continue;
							}
							if (tagword.contains("-")) {
								String result1 = tagword.replaceAll("-", "");
								if (result1.isEmpty()) {
									continue;
								}
							}
							if (tagword.contains(".")) {
								tagword = tagword.replace(".", "");
							}
							if (tagword.contains(",")) {
								tagword = tagword.replace(",", "");
							}
							if (tagword.contains("\n")) {
								tagword = tagword.replace("\n", "");
							}
							if (wnet.isStopword(tagword)) {
								continue;
							}
							if (tagword.isEmpty()) {
								continue;
							}
							if(first){
								ut = tagword ;
								first = false;
							}else{
								ut = ut +"," + tagword;
							}
							
						}
					}

				}
				lineArr[1] = ut;
				String theLine=lineArr[0] + "\t" + ut;
				for(int i=2;i<lineArr.length;i++){
					theLine = theLine + "\t"+lineArr[i];
				}
				gridWriter.write(theLine + "\n");
				gridWriter.flush();
			}else{
				gridWriter.write(line + "\n");
				gridWriter.flush();
			}
		}
		gridWriter.close();
	}

	
	public static void generateVerifTestData() throws IOException{

		//Path file = Paths.get("train-photo-video-mediaeval.txt");
		//Path file = Paths.get("mediaeval2016_placing_verification_test_photo");
		Path file = Paths.get("mediaeval2016_placing_verification_test_video");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, String> fileMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			//System.out.println(lineArr[0]);
			if(lineArr.length<3){
				fileMap.put(lineArr[0], "");
			}else{
			fileMap.put(lineArr[0], lineArr[2]);
			}
		}
		
		//file = Paths.get("verification-test-photo-mediaeval.txt");
		file = Paths.get("verification-test-video-mediaeval.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter gridWriter = null;
		try {
			//gridWriter = new PrintWriter(new FileWriter("verification-test-tags-photo-me-0.1.txt", true));
			gridWriter = new PrintWriter(new FileWriter("verification-test-tags-video-me-0.1.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		int lineWithNoDescOrUt = 0;
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Stopwords wnet = new Stopwords();
		int gridInvalid = 0;
		//Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			//String gridNumberStr = lineStr[lineStr.length - 1];
//			String place  = lineStr[lineStr.length - 2];
//			if(place.equals("NO_PLACE")){
////				lineNumber = lineNumber + 1;
////				continue;
//				place = "";
//			}
			String place = fileMap.get(lineStr[1]);
			String longStr = lineStr[12];
			String latStr = lineStr[13];
			BigInteger gridNumber = new BigInteger("0");
			if (!longStr.isEmpty() && !latStr.isEmpty()) {
//				gridNumber = //getGridNumberAtKdegree(latStr, longStr, map, 0.1);
//						getGridNumber(latStr, longStr, map);
				gridNumber = GenerateGrid.getGridNumber(latStr, longStr, 0.1);
			}else{
				gridInvalid++;
			}
			
			String userTag = lineStr[10];
			String desc = lineStr[9];
			if (userTag.isEmpty() && desc.isEmpty()) {
				lineWithNoDescOrUt++;
			}
			
			//String descArr[] = desc.split(",");
			String descWords[] = desc.split(Pattern.quote("+"));
			
			for(String description: descWords){
				if(description != null && !description.isEmpty()){

					String result = java.net.URLDecoder.decode(description, "UTF-8");
					result = result.trim().toLowerCase();
					if(result.isEmpty()){
						continue;
					}
					if(result.contains("href") || result.contains("http") || result.contains("https") || result.contains(">") || result.contains("<")){
						continue;
					}
					if(result.matches(".*\\d+.*")){
						continue;
					}
					Matcher m = p.matcher(result);
					boolean b = m. find();
					if(b){
						continue;
					}
					if(result.contains("-")){
						String result1 = result.replaceAll("-", "");
						if(result1.isEmpty()){
							continue;
						}
					}
					if(result.contains(".")){
						result = result.replace(".", "");
					}
					if(result.contains(",")){
						result = result.replace(",", "");
					}
					if(result.contains("\n")){
						result = result.replace("\n", "");
					}
					if(wnet.isStopword(result)){
						continue;
					}
					if(result.isEmpty()){
						continue;
					}

					if(userTag.isEmpty()){
						userTag = result;
					}else{
						userTag = userTag + "," + result;
					}
				}
			}
			gridWriter.write(gridNumber + "\t" +userTag + "\t" + place + "\t" + lineStr[8] +"\t"+lineStr[12] +
					"\t" + lineStr[13]+ "\t" + lineStr[1] + "\t" + lineStr[2] + "\n" );
			gridWriter.flush();
			
			if(lineNumber % 1000000 == 0){
				System.out.println("Processed:" + lineNumber);
			}
			lineNumber = lineNumber + 1;
		}
		gridWriter.close();
		System.out.println("lineWithNoDescOrUt:" + lineWithNoDescOrUt + " Invalid Grid:" + gridInvalid);
		System.out.println("Done...");
	
	}
	
	
	public static void generateEstimateTestData() throws IOException{

		//Path file = Paths.get("train-photo-video-mediaeval.txt");
		Path file = Paths.get("estimation-test-video-mediaeval.txt");
		//Path file = Paths.get("estimation-test-video-mediaeval.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter gridWriter = null;
		try {
			gridWriter = new PrintWriter(new FileWriter("estimation-test-video-me-0.01.txt", true));
			//gridWriter = new PrintWriter(new FileWriter("estimation-test-tags-video-me-0.1.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		int lineWithNoDescOrUt = 0;
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Stopwords wnet = new Stopwords();
		int gridInvalid = 0;
	//	Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");

			String place = "";
			String longStr = lineStr[12];
			String latStr = lineStr[13];
			BigInteger gridNumber = new BigInteger("0");
			if (!longStr.isEmpty() && !latStr.isEmpty()) {
//				gridNumber = //getGridNumberAtKdegree(latStr, longStr, map, 0.1);
//						getGridNumber(latStr, longStr, map);
				gridNumber = GenerateGrid.getGridNumber(latStr, longStr, 0.01);
			}else{
				gridInvalid++;
			}
			
			String userTag = lineStr[10];
			//String desc = lineStr[9];
			if (userTag.isEmpty()) {
				lineWithNoDescOrUt++;
			}
			
//			String userTagForInstance = "";
//			boolean first = true;
//			if(!userTag.isEmpty()){
//				String tags[] = userTag.split(",");
//				for (String tag : tags) {
//					
//					if(tag.matches(".*\\d+.*")){
//						continue;
//					}
//					if(tag.contains(".jpg")){
//						continue;
//					}
//					if(tag.length() <= 1){
//						continue;
//					}
//					tag = tag.toLowerCase();
////					String toWrite = userId + "\t" + tag + "\t" + gridNumber + "\n";
////					gridWriter.write(toWrite);
////					gridWriter.flush();
//					if(first){
//						userTagForInstance = tag;
//						first = false;
//					}else{
//						userTagForInstance = userTagForInstance + "," + tag;
//					}
//					if (!tag.isEmpty() && tag.contains("+")) {
//						
//						String tagwords[] = tag.split(Pattern.quote("+"));
//							for (String tagword : tagwords) {
//								Matcher m = p.matcher(tagword);
//								boolean b = m.find();
//								if (b) {
//									continue;
//								}
////								if (tagword.contains("-")) {
////									String result1 = tagword.replaceAll("-", "");
////									if (result1.isEmpty()) {
////										continue;
////									}
////								}
////								if (tagword.contains(".")) {
////									tagword = tagword.replace(".", "");
////								}
////								if (tagword.contains(",")) {
////									tagword = tagword.replace(",", "");
////								}
////								if (tagword.contains("\n")) {
////									tagword = tagword.replace("\n", "");
////								}
//								if (wnet.isStopword(tagword)) {
//									continue;
//								}
//								if (tagword.isEmpty()) {
//									continue;
//								}
////								toWrite = userId + "\t" + tagword + "\t" + gridNumber + "\n";
////								gridWriter.write(toWrite);
////								gridWriter.flush();
//								userTagForInstance = userTagForInstance + "," + tagword;
//							}
//					}
//				}
		//	}
			
			gridWriter.write(gridNumber + "\t" +userTag + "\t" + place + "\t" + lineStr[8] +"\t"+lineStr[12] +
					"\t" + lineStr[13]+ "\t"+ lineStr[1]+ "\t" +lineStr[2] + "\n" );
			gridWriter.flush();
			
			if(lineNumber % 10000== 0){
				System.out.println("Processed:" + lineNumber);
			}
			lineNumber = lineNumber + 1;
		}
		gridWriter.close();
		System.out.println("lineWithNoDescOrUt:" + lineWithNoDescOrUt + " Invalid Grid:" + gridInvalid);
		System.out.println("Done...");
	
	}

	public static void generateGridUserTagTrainData() throws IOException {
		//Path file = Paths.get("train-photo-video-mediaeval.txt");
		//Path file = Paths.get("train-ut-desc-4.5m.txt");
		Path file = Paths.get("train-photo-video-mediaeval.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter gridWriter = null;
		try {
		//	gridWriter = new PrintWriter(new FileWriter("grid-usertag-desc-train-me-0.001deg-4.5m.txt", true));
			gridWriter = new PrintWriter(new FileWriter("train-grid-ut-title-1.0deg.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		int lineWithNoDescOrUt = 0;
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Stopwords wnet = new Stopwords();
		int gridInvalid = 0;
		//Map<String, Integer> map = GenerateGrid.generateGridsAtKDegreeDistance(0.01);
		//GenerateGrid.generateGridNumbers();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			//String gridNumberStr = lineStr[lineStr.length - 1];
			String place  = lineStr[lineStr.length - 2];
			if(place.equals("NO_PLACE")){
//				lineNumber = lineNumber + 1;
//				continue;
				place = "";
			}
			String userTag = lineStr[10];
			//String desc = lineStr[9];
			String hash = lineStr[2 ] ;		
			if (userTag.isEmpty() ) {
				lineWithNoDescOrUt++;
			}		
			String longStr = lineStr[12];
			String latStr = lineStr[13];
			BigInteger gridNumber = new BigInteger("0");
			if (!longStr.isEmpty() && !latStr.isEmpty()) {
				gridNumber = GenerateGrid.getGridNumber(latStr, longStr, 1.0);
						//getGridNumberAtKdegree(latStr, longStr, map, 0.01);
						//getGridNumber(latStr, longStr, map);
			}else{
				gridInvalid++;
			}
			
			String title = lineStr[8];
			if(!title.isEmpty()){
			String titleWords[] = title.split(Pattern.quote("+"));
			for(String word: titleWords){
				if(word != null && !word.isEmpty()){
					//String result = java.net.URLDecoder.decode(word, "UTF-8");
					String result = word.trim().toLowerCase();
					if(result.isEmpty()){
						continue;
					}
					if(result.contains("href") || result.contains("http") || result.contains("https") || result.contains(">") || result.contains("<")){
						continue;
					}
					if(result.matches(".*\\d+.*")){
						continue;
					}
					Matcher m = p.matcher(result);
					boolean b = m. find();
					if(b){
						continue;
					}
//					if(result.contains("-")){
//						String result1 = result.replaceAll("-", "");
//						if(result1.isEmpty()){
//							continue;
//						}
//					}
					if(result.contains(".")){
						result = result.replace(".", "");
					}
					if(result.contains(",")){
						result = result.replace(",", "");
					}
					if(result.contains("\n")){
						result = result.replace("\n", "");
					}
					if(wnet.isStopword(result)){
						continue;
					}
					if(result.isEmpty()){
						continue;
					}

					if(userTag.isEmpty()){
						userTag = result;
					}else{
						userTag = userTag + "," + result;
					}
				}
			
		}
			}
			
			
			gridWriter.write(gridNumber + "\t" +userTag + "\t" + place + "\t" + lineStr[8] +"\t"+lineStr[12] +
					"\t" + lineStr[13]+"\t" + hash+ "\n");
			gridWriter.flush();
			
			if(lineNumber % 1000000 == 0){
				System.out.println("Processed:" + lineNumber);
			}
			lineNumber = lineNumber + 1;
		}
		gridWriter.close();
		System.out.println("lineWithNoDescOrUt:" + lineWithNoDescOrUt + " gridInvalid:" + gridInvalid);
		System.out.println("Done...");
	}

 private static void generateTrainDataInOneLine() throws UnsupportedEncodingException{

		//Path file = Paths.get("train-photo-video-mediaeval.txt");
		//Path file = Paths.get("train-ut-desc-4.5m.txt");
		Path file = Paths.get("train-photo-video-mediaeval.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter gridWriter = null;
		try {
		//	gridWriter = new PrintWriter(new FileWriter("grid-usertag-desc-train-me-0.001deg-4.5m.txt", true));
			gridWriter = new PrintWriter(new FileWriter("train-photo-video-mediaeval-0.1.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		int lineWithNoDescOrUt = 0;
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Stopwords wnet = new Stopwords();
		int gridInvalid = 0;
		//Map<String, Integer> map = GenerateGrid.generateGridsAtKDegreeDistance(0.01);
		//GenerateGrid.generateGridNumbers();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			//String gridNumberStr = lineStr[lineStr.length - 1];
			String place  = lineStr[lineStr.length - 2];
			if(place.equals("NO_PLACE")){
//				lineNumber = lineNumber + 1;
//				continue;
				place = "";
			}
			String userTag = lineStr[10];
			//String desc = lineStr[9];
			String hash = lineStr[2 ] ;		
			if (userTag.isEmpty() ) {
				lineWithNoDescOrUt++;
			}		
			String longStr = lineStr[12];
			String latStr = lineStr[13];
			BigInteger gridNumber = new BigInteger("0");
			if (!longStr.isEmpty() && !latStr.isEmpty()) {
				gridNumber = GenerateGrid.getGridNumber(latStr, longStr, 0.1);
						//getGridNumberAtKdegree(latStr, longStr, map, 0.01);
						//getGridNumber(latStr, longStr, map);
			}else{
				gridInvalid++;
			}
			
			String title = lineStr[8];
			if(!title.isEmpty()){
			String titleWords[] = title.split(Pattern.quote("+"));
			for(String word: titleWords){
				if(word != null && !word.isEmpty()){
					String result = java.net.URLDecoder.decode(word, "UTF-8");
					result = word.trim().toLowerCase();
					if(result.isEmpty()){
						continue;
					}
					if(result.contains("href") || result.contains("http") || result.contains("https") || result.contains(">") || result.contains("<")){
						continue;
					}
					if(result.matches(".*\\d+.*")){
						continue;
					}
					Matcher m = p.matcher(result);
					boolean b = m. find();
					if(b){
						continue;
					}
					if(result.contains("-")){
						String result1 = result.replaceAll("-", "");
						if(result1.isEmpty()){
							continue;
						}
					}
					if(result.contains(".")){
						result = result.replace(".", "");
					}
					if(result.contains(",")){
						result = result.replace(",", "");
					}
					if(result.contains("\n")){
						result = result.replace("\n", "");
					}
					if(wnet.isStopword(result)){
						continue;
					}
					if(result.isEmpty()){
						continue;
					}

					if(userTag.isEmpty()){
						userTag = result;
					}else{
						userTag = userTag + "," + result;
					}
				}
			
		}
			}
			
			
			gridWriter.write(gridNumber + "\t" +userTag + "\t" + place + "\t" + lineStr[8] +"\t"+lineStr[12] +
					"\t" + lineStr[13]+"\t" + lineStr[1] + "\t"+  hash+ "\n");
			

			gridWriter.flush();
			
			if(lineNumber % 1000000 == 0){
				System.out.println("Processed:" + lineNumber);
			}
			lineNumber = lineNumber + 1;
		}
		gridWriter.close();
		System.out.println("lineWithNoDescOrUt:" + lineWithNoDescOrUt + " gridInvalid:" + gridInvalid);
		System.out.println("Done...");
	
 }

	private static void generateTrain() throws IOException {
		// Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		// Stream<String> lines = null;
		PrintWriter trainWriter = null;
		Stream<String> lines = null;
		Map<String, Integer> map = GenerateGrid.generateGridNumbers();
	//	Map<String, Integer> map = GenerateGrid.generateGridsAtKDegreeDistance(0.1);
		// Path file = Paths.get("yfcc_withlatlong_usertag_gridNumber.txt");
		Path file = Paths.get("mediaeval2016_placing_train_photo");

		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		Map<String, String> idLineNomap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			idLineNomap.put(lineStr[0], lineStr[4]);
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Finished loading:" + lineNumber);
			}
		}
		file = Paths.get("mediaeval2016_placing_train_video");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineNumber = 0;
		// Map<String, Integer> idLineNomap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			idLineNomap.put(lineStr[0], lineStr[4]);
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Finished loading:" + lineNumber);
			}
		}

		System.out.println("Map loaded. size:" + idLineNomap.size());
		// file = Paths.get("yfcc_withlatlong_usertag_gridNumber.txt");
		file = Paths.get("yfcc100m_dataset");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineNumber = 0;
		int numberFound = 0;
		// PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("train-photo-video-mediaeval-0.1.txt", true));
			// testWriter = new PrintWriter(new
			// FileWriter("test-photo-mediaeval.txt", true));
			//lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			if (idLineNomap.containsKey(lineStr[1])) {
				// if("5619201398".equals(lineStr[0])){
				// System.out.println("linenumber:" + lineNumber);
				// }
				// idLineNomap.put(lineStr[1], lineNumber);
				String place = idLineNomap.get(lineStr[1]);
				String toWrite = line + "\t" + place;  // 25
				String longStr = lineStr[12];
				String latStr = lineStr[13];
				int gridNumber = -1;
				if (!longStr.isEmpty() && !latStr.isEmpty()) {
					gridNumber = getGridNumberAtKdegree(latStr, longStr, map, 0.1);
						//	getGridNumber(latStr, longStr, map);
				}
				toWrite = toWrite + "\t" + gridNumber + "\n";
				trainWriter.write(toWrite);
				trainWriter.flush();
				numberFound = numberFound + 1;
			}
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Processed:" + lineNumber);
			}
		}
		trainWriter.close();
		System.out.println("Complete writing: " + numberFound);
	}

	private static void generateEstimationTestData() throws IOException {

		// Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		// Stream<String> lines = null;
		PrintWriter testPhotoWriter = null;
		PrintWriter testVideoWriter = null;
		Stream<String> lines = null;
		//Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		Map<String, Integer> map = GenerateGrid.generateGridsAtKDegreeDistance(0.1);
		// Path file = Paths.get("yfcc_withlatlong_usertag_gridNumber.txt");
		Path file = Paths.get("mediaeval2016_placing_estimation_test_photo");

		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		Map<String, Integer> idLineNoPhotoMap = new HashMap();
		Map<String, Integer> idLineNoVideoMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			idLineNoPhotoMap.put(lineStr[0], 0);
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Finished loading:" + lineNumber);
			}
		}
		file = Paths.get("mediaeval2016_placing_estimation_test_video");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineNumber = 0;
		// Map<String, Integer> idLineNomap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			idLineNoVideoMap.put(lineStr[0], 0);
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Finished loading:" + lineNumber);
			}
		}

		System.out.println("Map loaded. size:" + (idLineNoPhotoMap.size() + idLineNoVideoMap.size()));
		// file = Paths.get("yfcc_withlatlong_usertag_gridNumber.txt");
		file = Paths.get("yfcc100m_dataset");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineNumber = 0;
		int numberFound = 0;
		// PrintWriter trainWriter = null;
		try {
			testPhotoWriter = new PrintWriter(new FileWriter("estimation-test-photo-mediaeval-0.1.txt", true));
			testVideoWriter = new PrintWriter(new FileWriter("estimation-test-video-mediaeval-0.1.txt", true));
			// testWriter = new PrintWriter(new
			// FileWriter("test-photo-mediaeval.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			if (idLineNoPhotoMap.containsKey(lineStr[1])) {
				// if("5619201398".equals(lineStr[0])){
				// System.out.println("linenumber:" + lineNumber);
				// }
				// idLineNomap.put(lineStr[1], lineNumber);
				// String place = idLineNomap.get(lineStr[1]);
				String toWrite = line;
				String longStr = lineStr[12];
				String latStr = lineStr[13];
				int gridNumber = -1;
				if (!longStr.isEmpty() && !latStr.isEmpty()) {
					gridNumber = getGridNumberAtKdegree(latStr, longStr, map, 0.1);
							//getGridNumber(latStr, longStr, map);
				}
				toWrite = toWrite + "\t" + gridNumber + "\n";
				testPhotoWriter.write(toWrite);
				testPhotoWriter.flush();
				numberFound = numberFound + 1;
			}
			if (idLineNoVideoMap.containsKey(lineStr[1])) {
				// if("5619201398".equals(lineStr[0])){
				// System.out.println("linenumber:" + lineNumber);
				// }
				// idLineNomap.put(lineStr[1], lineNumber);
				// String place = idLineNomap.get(lineStr[1]);
				String toWrite = line;
				String longStr = lineStr[12];
				String latStr = lineStr[13];
				int gridNumber = -1;
				if (!longStr.isEmpty() && !latStr.isEmpty()) {
					gridNumber = getGridNumberAtKdegree(latStr, longStr, map, 0.1); 
							//getGridNumber(latStr, longStr, map);
				}
				toWrite = toWrite + "\t" + gridNumber + "\n";
				testVideoWriter.write(toWrite);
				testVideoWriter.flush();
				numberFound = numberFound + 1;
			}
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Processed:" + lineNumber);
			}
		}
		testPhotoWriter.close();
		testVideoWriter.close();
		System.out.println("Complete writing: " + numberFound);

	}

	private static void generateVerificationTestData() throws IOException {

		// Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		// Stream<String> lines = null;
		PrintWriter testPhotoWriter = null;
		PrintWriter testVideoWriter = null;
		Stream<String> lines = null;
	//	Map<String, Integer> map = GenerateGrid.generateGridNumbers();
		Map<String, Integer> map = GenerateGrid.generateGridsAtKDegreeDistance(0.1);
		// Path file = Paths.get("yfcc_withlatlong_usertag_gridNumber.txt");
		Path file = Paths.get("mediaeval2016_placing_verification_test_photo");

		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		Map<String, String> idLineNoPhotoMap = new HashMap();
		Map<String, String> idLineNoVideoMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			if(lineStr.length < 3){
				System.out.println(line);
			}else{
			idLineNoPhotoMap.put(lineStr[0],lineStr[2] );
			}
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Finished loading:" + lineNumber);
			}
		}
		file = Paths.get("mediaeval2016_placing_verification_test_video");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineNumber = 0;
		// Map<String, Integer> idLineNomap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			if(lineStr.length < 3){
				//idLineNoVideoMap.put(lineStr[0], "NO_PLACE");
				System.out.println(line);
			}else{
			idLineNoVideoMap.put(lineStr[0], lineStr[2]);
			}
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Finished loading:" + lineNumber);
			}
		}

		System.out.println("Map loaded. size:" + (idLineNoPhotoMap.size() + idLineNoVideoMap.size()));
		// file = Paths.get("yfcc_withlatlong_usertag_gridNumber.txt");
		file = Paths.get("yfcc100m_dataset");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineNumber = 0;
		int numberFound = 0;
		// PrintWriter trainWriter = null;
		try {
			testPhotoWriter = new PrintWriter(new FileWriter("verification-test-photo-mediaeval-0.1.txt", true));
			testVideoWriter = new PrintWriter(new FileWriter("verification-test-video-mediaeval-0.1.txt", true));
			// testWriter = new PrintWriter(new
			// FileWriter("test-photo-mediaeval.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			if (idLineNoPhotoMap.containsKey(lineStr[1])) {
				String place = idLineNoPhotoMap.get(lineStr[1]);
				String toWrite = line + "\t" + place;
				String longStr = lineStr[12];
				String latStr = lineStr[13];
				int gridNumber = -1;
				if (!longStr.isEmpty() && !latStr.isEmpty()) {
						gridNumber =  getGridNumberAtKdegree(latStr, longStr, map, 0.1);
					//		gridNumber = getGridNumber(latStr, longStr, map);
				}
				toWrite = toWrite + "\t" +gridNumber + "\n";
				testPhotoWriter.write(toWrite);
				testPhotoWriter.flush();
				numberFound = numberFound + 1;
			}
			if (idLineNoVideoMap.containsKey(lineStr[1])) {
				String place = idLineNoPhotoMap.get(lineStr[1]);
				String toWrite = line + "\t" + place;
				String longStr = lineStr[12];
				String latStr = lineStr[13];
				int gridNumber = -1;
				if (!longStr.isEmpty() && !latStr.isEmpty()) {
					gridNumber = getGridNumberAtKdegree(latStr, longStr, map, 0.1);
					//gridNumber = getGridNumber(latStr, longStr, map);
				}
				toWrite = toWrite + "\t" + gridNumber + "\n";
				testVideoWriter.write(toWrite);
				testVideoWriter.flush();
				numberFound = numberFound + 1;
			}
			lineNumber = lineNumber + 1;
			if (lineNumber % 1000000 == 0) {
				System.out.println("Processed:" + lineNumber);
			}
		}
		testPhotoWriter.close();
		testVideoWriter.close();
		System.out.println("Complete writing: " + numberFound);

	}

	private static String getMetaData(String identifier, Map<String, Integer> idLineNomap) throws IOException {

		int lineNumber = idLineNomap.get(identifier);
		try (Stream<String> allLines = Files.lines(Paths.get("yfcc100m_dataset"))) {
			String line = allLines.skip((lineNumber - 1)).findFirst().get();
			// System.out.println(line);
			return line;
		}

		// System.out.println("Linenumber:" + map.get("identifier"));
		// return null;
	}

	private static int getGridNumber(String latitudeStr, String longitudeStr, Map map) {
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

		String locations = "";
		try {
			if (latitude >= 0 && longitude >= 0) {
				double latbottom = (int) latitude;
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

//			if(locations.equals("51.7	87.3	51.8	87.4")){
//				System.out.println("found");
//				
//			}
			if (locations.equals("")) {
				return 0;
			}
//
//			if(!map.containsKey(locations)){
//				System.out.println("Doesn't contain the location..");
//			}
			return (int) map.get(locations);
		} catch (Exception e) {
			System.out.println("latitudeStr:" + latitudeStr + "   " + "longitudeStr:" + longitudeStr);
			System.out.println("locations:" + locations);
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void generateTrainDataWithLatandLongAtFront(){
		
		PrintWriter trainWriter = null;
		Stream<String> lines = null;
		Path file = Paths.get("train-photo-video-mediaeval.txt");

		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			trainWriter = new PrintWriter(new FileWriter("mod-train-photo-video-mediaeval.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNumber++;
			String lineStr[] = line.split("\t");
			String toWrite = lineStr[12] + "\t" + lineStr[13] + "\t" + line;
			trainWriter.write(toWrite + "\n");
			trainWriter.flush();
			if(lineNumber%1000000 ==0 ){
				System.out.println("Processed:" + lineNumber);
			}
		}
		trainWriter.close();
	}
	
	public static void generateTrainDataSortedByLatLong(){
		PrintWriter trainWriter = null;
		Stream<String> lines = null;
		Path file = Paths.get("sorted-mod-train-photo-video-mediaeval.txt");

		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			trainWriter = new PrintWriter(new FileWriter("train-photo-video-me-sorted.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNumber = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNumber++;
			int index = StringUtils.ordinalIndexOf(line, "\t", 2);
			line = line.substring(index+1);
			trainWriter.write(line + "\n");
			trainWriter.flush();
			if(lineNumber%1000000 ==0 ){
				System.out.println("Processed:" + lineNumber);
			}
		}
		trainWriter.close();
	}
	
}

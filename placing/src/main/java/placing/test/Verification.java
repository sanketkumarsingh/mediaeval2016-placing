package placing.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

public class Verification {

	public static Map<String, List<TestDetail>> checkTestIdsAndGenerateTestFile() {

		// loading verification.txt
		Path file = Paths.get("verification-me-photo-video.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, TestDetail> verifTestMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if (verifTestMap.containsKey(lineArr[0])) {
				System.out.println("Already present..");
			} else {
				TestDetail td = new TestDetail();
				if(lineArr.length < 3){
//					System.out.println(lineArr[0] + " " + lineArr[1]);
//					td.setGivenPlace("");
//					td.setTestId(lineArr[0]);
//					verifTestMap.put(lineArr[0], td); 
					continue;
				}
				td.setGivenPlace(lineArr[2]);
				td.setTestId(lineArr[0]);
				verifTestMap.put(lineArr[0], td); // testid and object.
			}
		}
		System.out.println("Loaded verification data.." +  verifTestMap.size());
		// Generate file with true locations of test instances..

		// file = Paths.get("yfcc100_places.txt");
		// try {
		// lines = Files.lines(file, StandardCharsets.UTF_8);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// PrintWriter trueValWriter = null;
		// try {
		// // gridWriter = new PrintWriter(new
		// // FileWriter("grid-usertag-desc-train-me-0.001deg-4.5m.txt",
		// // true));
		// trueValWriter = new PrintWriter(new
		// FileWriter("trueLocationOfTests.txt", true));
		// lines = Files.lines(file, StandardCharsets.UTF_8);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// for (String line : (Iterable<String>) lines::iterator) {
		// String lineArr[] = line.split("\t");
		// if(verifTestMap.containsKey(lineArr[0])){
		// trueValWriter.write(line + "\n");
		// trueValWriter.flush();
		// }
		// trueValWriter.close();
		// }

		file = Paths.get("lat_long_photo_1.0_run3_text_county.txt");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double errortotalDistance=0.0;
		int totalInstance=0;
		int totalPhotosFound=0;
		List<Double> distanceList = new ArrayList();
		Map<String, List<TestDetail>> trainTestDetailMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			double dist = Double.parseDouble(lineArr[5]);
		//	double dist = getDistance(lineArr[3],lineArr[4]);
			errortotalDistance = errortotalDistance + dist;
			distanceList.add(dist);
			totalInstance++;
			if (verifTestMap.containsKey(lineArr[0])) {
				totalPhotosFound++;
				TestDetail td = verifTestMap.get(lineArr[0]);
				if (trainTestDetailMap.containsKey(lineArr[lineArr.length - 1])) { // lineArr.length-2
																					// =
																					// index
																					// of
																					// trainid
					trainTestDetailMap.get(lineArr[lineArr.length - 1]).add(td);
				} else {
					List<TestDetail> tdList = new ArrayList();
					tdList.add(td);
					trainTestDetailMap.put(lineArr[lineArr.length - 1], tdList);
				}
				//verifTestMap.remove(lineArr[0]);
			}
		}
		
		System.out.println("Average error distance:" +(errortotalDistance/totalInstance));
		System.out.println();
		Collections.sort(distanceList);
		double medianDist = (distanceList.get(totalInstance/2) + distanceList.get((totalInstance/2) +1))/ (double) 2.0 ;
		System.out.println("Median error distance:" + medianDist);
		System.out.println("Number of instance processed:" + totalPhotosFound);
//		if (verifTestMap.size() == 0) {
//			System.out.println("Verfication and estimation have same testid's");
//		} else {
//			PrintWriter outputWriter = null;
//			try {
//				// gridWriter = new PrintWriter(new
//				// FileWriter("grid-usertag-desc-train-me-0.001deg-4.5m.txt",
//				// true));
//				outputWriter = new PrintWriter(new FileWriter("verifNotInEst.txt", true));
//				lines = Files.lines(file, StandardCharsets.UTF_8);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Iterator it = verifTestMap.entrySet().iterator();
//			while (it.hasNext()) {
//				Entry entry = (Entry) it.next();
//				outputWriter.write(entry.getKey() + "\n");
//				outputWriter.flush();
//			}
//			outputWriter.close();
//		}
		System.out.println("Returning the map..");
		return trainTestDetailMap;
	}

	private static double getDistance(String realLatitude, String realLongitude) {
		GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(realLatitude),
				Double.parseDouble(realLongitude), Double.parseDouble("51.50981"),
				Double.parseDouble("-0.123338"), GeodesicMask.DISTANCE);
		return g.s12;
	}

	public static void verifyPrediction() {
		Map<String, List<TestDetail>> trainTestDetailMap = checkTestIdsAndGenerateTestFile();
		Path file = Paths.get("sorted-train-photo-video-mediaeval-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter outputWriter = null;
		try {
			// gridWriter = new PrintWriter(new
			// FileWriter("grid-usertag-desc-train-me-0.001deg-4.5m.txt",
			// true));
			outputWriter = new PrintWriter(new FileWriter("verification-photo-run3.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if (trainTestDetailMap.containsKey(lineArr[6])) {
				List<TestDetail> testList = trainTestDetailMap.get(lineArr[6]);
				for (TestDetail instance : testList) {
					if (lineArr[2] == null || lineArr[2].isEmpty()) {
						System.out.println(
								instance.getGivenPlace() + "\t" + "Trainingid:" + lineArr[6] + ":place_empty.");
					}
					int value = verify(instance.getGivenPlace(), lineArr[2]); // lineArr2
																				// is
																				// place
					outputWriter.write(instance.getTestId() + "\t" + value + "\n");
					outputWriter.flush();
				}
				trainTestDetailMap.remove(lineArr[6]);
			}
		}
		outputWriter.close();

//		if (trainTestDetailMap.size() == 0) {
//			System.out.println("All test instances processed.");
//		} else {
//			PrintWriter writer = null;
//			try {
//				// gridWriter = new PrintWriter(new
//				// FileWriter("grid-usertag-desc-train-me-0.001deg-4.5m.txt",
//				// true));
//				writer = new PrintWriter(new FileWriter("rem_test_instances.txt", true));
//				lines = Files.lines(file, StandardCharsets.UTF_8);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Iterator it = trainTestDetailMap.entrySet().iterator();
//			while (it.hasNext()) {
//				Entry entry = (Entry) it.next();
//				List<TestDetail> testList = (List<TestDetail>) entry.getValue();
//				for(TestDetail testInstance: testList){
//					writer.write(testInstance.getTestId() + "\n");
//					writer.flush();
//				}
//			}
//			writer.close();
//		}
		
		System.out.println("Completed..");
	}

	private static int verify(String givenPlace, String predPlace) {
		String predPlaceArr[]  = predPlace.split(",");
		Map<String, String> placeTypeAndPlaceIdMap = new HashMap();
		for(int i=predPlaceArr.length-1 ; i>=0 ;i--){
			String placeType = predPlaceArr[i];
			String placeValues[] = placeType.split(":");
			placeTypeAndPlaceIdMap.put(placeValues[2], placeValues[0]);
		}
		
		String givenPlaceArr[] = givenPlace.split(",");
		for(int i=givenPlaceArr.length-1 ; i>=0 ;i--){
			String placeType = givenPlaceArr[i];
			String placeValues[] = placeType.split(":");
			if(placeTypeAndPlaceIdMap.containsKey(placeValues[2])){
				String placeId = placeTypeAndPlaceIdMap.get(placeValues[2]);
				if(!placeId.equals(placeValues[0])) { // matching id
					return 0;
				}
			}else{
				return 0;
			}
		}
		
		return 1;
	}
	
	
	public static void getLatAndLongForCountyFromTamura(){
		Path file = Paths.get("test-video-county-predict.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, String> hashIdCountyPredMap = new HashMap();
		
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split(" ");
			hashIdCountyPredMap.put(lineArr[1].split("-")[0], lineArr[0]);
		}
		
		file = Paths.get("estimation-test-video-mediaeval.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> testIdCountyPredMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if(hashIdCountyPredMap.containsKey(lineArr[2])){
				testIdCountyPredMap.put(lineArr[1], hashIdCountyPredMap.get(lineArr[2]));
			}
		}
		
		
		file = Paths.get("cell-pred-bymostpop-tamura-video-1.0.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(new FileWriter("cell-pred-bymostpop-county-video-1.0.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if(testIdCountyPredMap.containsKey(lineArr[0])){
				String countycell = testIdCountyPredMap.get(lineArr[0]);
				TrainInstance countyTrain = getTrainForCell(countycell);
				GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(countyTrain.getLatitude()),
						Double.parseDouble(countyTrain.getLongitude()), Double.parseDouble(lineArr[3]),
						Double.parseDouble(lineArr[4]), GeodesicMask.DISTANCE);
				outputWriter.write(lineArr[0] + "\t" + countyTrain.getLatitude() + "\t"
						+ countyTrain.getLongitude() + "\t" + lineArr[3] + "\t" + lineArr[4] + "\t" + g.s12
						+ "\t"+"true" + "\t" + countyTrain.getId() + "\n");
				outputWriter.flush();
			}
		}
		outputWriter.close();
		
	}
	
	

	private static TrainInstance getTrainForCell(String countycell) {
		TrainInstance instance = null;
		if(countycell.equals("2")){
			instance = new TrainInstance();
			instance.setTag("homeless,london,strand,sleepers");
			instance.setId("7969001470");
			instance.setLatitude("51.50981");
			instance.setLongitude("-0.123338");
		}else if (countycell.equals("1")){
			instance = new TrainInstance();
			instance.setTag("fort+lauderdale,ft.+lauderdale,photography,street,street+photography,shadow");
			instance.setId("6913083943");
			instance.setLatitude("26.121053");
			instance.setLongitude("-80.145113");
		}else if (countycell.equals("36")){
			instance = new TrainInstance();
			instance.setTag("");
			instance.setId("");
			instance.setLatitude("26.121053");
			instance.setLongitude("-80.145113");
		}
		
		return instance;
	}

	public static void main(String[] args) {
		verifyPrediction();
	}

}

class TestDetail {

	String testId;
	String givenPlace;

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getGivenPlace() {
		return givenPlace;
	}

	public void setGivenPlace(String givenPlace) {
		this.givenPlace = givenPlace;
	}
}

class TrainInstance {
	String tag;
	String latitude;
	String longitude;
	String id;
	//String place;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public String getPlace() {
//		return place;
//	}
//
//	public void setPlace(String place) {
//		this.place = place;
//	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}



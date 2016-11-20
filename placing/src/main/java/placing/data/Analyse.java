package placing.data;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

public class Analyse {

	public static void main(String[] args) throws IOException {
		// getTopIPrediction();
		// getFileNames();
	//	getNumberOfTagsPerGrid();
	//	getPhotoCountGridCountForUser();
	//	 getTestsPredByMostPopCell();
		//getResultForTamuraForMostPopCell();
		 //getTestsPredByMostPopCell();
		//getRun3();
		//getAverage();
		//getAccuracy();
		getTestInstancePredByMostPopCell();
	}
	
	
	private static void getTestInstancePredByMostPopCell() throws IOException{
		Map<String, String> resultMapRun4 = new HashMap();
		
		Path gridfile = Paths.get("resultWriter-photo-run1-1.0.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		
		long correct=0; int total=0; 
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			total++;
			if(lineArr[lineArr.length-1].equals("true")){
				resultMapRun4.put(String.valueOf(total), lineArr[0]);
			}
		}
		System.out.println("Count:" + resultMapRun4.size());
		
//		 gridfile = Paths.get("resultWriter-1.0-Thread-photo-run4.txt");
//		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
//		
//		int index=0;int count =0;
//		for (String line : (Iterable<String>) gridlines::iterator) {
//			String lineArr[] = line.split("\t");
//			index++;
//			if(lineArr[lineArr.length-1].equals("true")){
//				if(!resultMapRun4.containsKey(String.valueOf(index))){
//					System.out.println(index);
//				}
//				count++;
//			}
//		}
//		System.out.println("Count:" +count);
	}
	
	//		popGridTrainInstance.setLatitude("51.50632");
	//popGridTrainInstance.setLongitude("-0.12714");
	
	//Run1
	//photo: 44.95 , video:42.37
	// Run4
	// photo:  44.43   video: 42.49
	private static void getAccuracy() throws IOException{
		Path gridfile = Paths.get("resultWriter-1.0-Thread-video-run4-onlyyfcc.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		
		long correct=0; long total=0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(lineArr[1].equals(lineArr[3])){
				correct++;
			}
			total++;
		}
		double accuracy = (double)correct/(double)total;
		System.out.println("Correct:" + accuracy);
	}
	
	private static void getAverage() throws IOException{
		Path gridfile = Paths.get("yfcc-train-grid-ut-title-1.0-13859.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		
		double lat =0;
		double longitude = 0;
		long total = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			lat = lat + Double.parseDouble(lineArr[4]);
			longitude = longitude +  Double.parseDouble(lineArr[5]);
			total++;
		}
		System.out.println("Avg lat:" + (lat/(double)total));
		System.out.println("Avg long:" + (longitude/(double)total));
	}
	
	private static void getRun3() throws IOException{
		Path gridfile = Paths.get("cell-pred-bymostpop-county-photo-1.0.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		Map<String, String> resultphotoMap = new HashMap();
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			resultphotoMap.put(lineArr[0], line);
		}
		
		PrintWriter taphotogResult = null;
		try {
			taphotogResult = new PrintWriter(new FileWriter("lat_long_photo_1.0_run3_text_county.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int count =0;
		gridfile = Paths.get("lat_long_photo_1.0.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(resultphotoMap.containsKey(lineArr[0])){
				taphotogResult.write(resultphotoMap.get(lineArr[0])+"\n");
				taphotogResult.flush();
				count++;
			}else{
				taphotogResult.write(line+"\n");
				taphotogResult.flush();
			}
		}
		System.out.println("Count:" +count);
		taphotogResult.close();
	}

	private static void getResultForTamuraForMostPopCell() throws IOException {
		// TODO Auto-generated method stub
		Path gridfile = Paths.get("lat_long_photo_1.0_run2_county.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		Map<String, String> resultphotoMap = new HashMap();
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			resultphotoMap.put(lineArr[0], line);
		}
		Map<String, String> resultvideoMap = new HashMap();
		gridfile = Paths.get("lat_long_video_1.0_run2_county.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			resultvideoMap.put(lineArr[0], line);
		}
		
		PrintWriter taphotogResult = null;
		try {
			taphotogResult = new PrintWriter(new FileWriter("cell-pred-bymostpop-county-photo-1.0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter tagvideoResult = null;
		try {
			tagvideoResult = new PrintWriter(new FileWriter("cell-pred-bymostpop-county-video-1.0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter testPhotoResult = null;
		try {
			testPhotoResult = new PrintWriter(new FileWriter("cell-pred-bymostpop-text-photo-1.0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		PrintWriter testVideoResult = null;
		try {
			testVideoResult = new PrintWriter(new FileWriter("cell-pred-bymostpop-text-video-1.0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gridfile = Paths.get("cell-pred-bymostpop-photo-1.0.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(resultphotoMap.containsKey(lineArr[0])){
				taphotogResult.write(resultphotoMap.get(lineArr[0])+"\n");
				taphotogResult.flush();
				testPhotoResult.write(line+"\n");
				testPhotoResult.flush();
			}
//			} else{
//				
//			}
		}
		
		gridfile = Paths.get("cell-pred-bymostpop-video-1.0.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(resultvideoMap.containsKey(lineArr[0])){
				tagvideoResult.write(resultvideoMap.get(lineArr[0])+"\n");
				tagvideoResult.flush();
				testVideoResult.write(line+"\n");
				testVideoResult.flush();
			}
//			}else{
//				
//			}
		}
		taphotogResult.close();
		tagvideoResult.close();
		testPhotoResult.close();
		testVideoResult.close();
	}

	private static void getTestsPredByMostPopCell() throws IOException {
		// TODO Auto-generated method stub
		PrintWriter tagphotoResult = null;
		PrintWriter tagvideoResult = null;
		try {
			tagphotoResult = new PrintWriter(new FileWriter("cell-pred-bymostpop-photo-1.0.txt", true));
			tagvideoResult = new PrintWriter(new FileWriter("cell-pred-bymostpop-video-1.0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Path gridfile = Paths.get("lat_long_photo_1.0.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(lineArr[lineArr.length-2].equals("true")){
				tagphotoResult.write(line+"\n");
				tagphotoResult.flush();
			}
		}
		gridfile = Paths.get("lat_long_video_1.0.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(lineArr[lineArr.length-2].equals("true")){
				tagvideoResult.write(line+"\n");
				tagvideoResult.flush();
			}
		}
		tagphotoResult.close();
		tagvideoResult.close();
	}

	public static void getTopIPrediction() throws IOException {
		Path gridfile = Paths.get("resultWriter-gt-topi.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		int numberOfSucess[] = new int[10];
		int top1PredCount = 0;
		long lineno = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			lineno++;
			if (lineArr[1].equals(lineArr[2])) {
				for (int k = 0; k < 10; k++) {
					numberOfSucess[k] = numberOfSucess[k] + 1;
				}
			} else {
				for (int i = 4; i < lineArr.length - 1; i++) {
					if (lineArr[1].equals(lineArr[i])) {
						for (int k = i - 3; k < 10; k++) {
							numberOfSucess[k] = numberOfSucess[k] + 1;
						}
					}
				}
			}
			if(lineno == 3000){
				System.out.println("3000:" + ( (double) numberOfSucess[0] / (double) lineno));
			}
			 if(lineArr[1].equals(lineArr[2])){
			 top1PredCount++;
			 }
		}
		System.out.println("lineno:" + lineno);
		for (int i = 0; i < 10; i++) {
			System.out.println("Top[" + (i + 1) + "] accuracy:" + ((double) numberOfSucess[i] / (double) lineno));
		}

	}

	public static void getPredictionCount() throws IOException {

		// Path gridfile = Paths.get("sorted-resultWriter-gt-complete.txt");
		Path gridfile = Paths.get("resultWriter-gt.txt");

		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		Map<String, List<Obj>> realGridMap = new HashMap();

		// /1772251
		int maxPredCount = 0;
		int totalMisMatch = 0;
		int totalCount = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineStr[] = line.split("\t");
			String imageStr[] = lineStr[0].split(":");
			String predStr[] = lineStr[1].split(":");
			String realStr[] = lineStr[2].split(":");
			totalCount++;
			if (!imageStr[1].equals(String.valueOf(totalCount))) {
				System.out.println("Missing:" + totalCount);
			}
			if (!realStr[1].equals(predStr[1])) {
				totalMisMatch++;
				if (predStr[1].equals("1772251")) {
					maxPredCount++;
				}
			}

			if (realGridMap.containsKey(realStr[1])) {
				List<Obj> predList = realGridMap.get(realStr[1]);
				boolean found = false;
				for (Obj obj : predList) {
					if (obj.getPredGrid().equals(predStr[1])) {
						obj.setCount(obj.getCount() + 1);
						found = true;
						break;
					}
				}
				if (!found) {
					Obj obj = new Obj();
					obj.setCount(1);
					obj.setPredGrid(predStr[1]);
					predList.add(obj);
				}
			} else {
				List<Obj> predList = new ArrayList();
				Obj obj = new Obj();
				obj.setCount(1);
				obj.setPredGrid(predStr[1]);
				predList.add(obj);
				realGridMap.put(realStr[1], predList);
			}

		}
		System.out.println("totalCount:" + totalCount);
		System.out.println("maxPredCount:" + maxPredCount);
		System.out.println("totalMisMatch:" + totalMisMatch);
		PrintWriter tagResult = null;
		try {
			tagResult = new PrintWriter(new FileWriter("real-pred-analysis.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator it = realGridMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Entry) it.next();
			String realGrid = (String) pair.getKey();
			ArrayList<Obj> predList = (ArrayList<Obj>) pair.getValue();
			Collections.sort(predList);
			String toWrite = realGrid;
			for (Obj pred : predList) {
				toWrite = toWrite + "\t" + pred.getPredGrid() + ":" + pred.getCount();
			}
			tagResult.write(toWrite + "\n");
			tagResult.flush();
		}
		tagResult.close();

	}

	public static void getFileNames() throws IOException {
		Path gridfile = Paths.get("estimation-test-photo-mediaeval.txt");
		Set<String> set = new HashSet();
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			set.add(lineArr[2].substring(0, 3));
		}
		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("unique-test-photo-tags.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			String tag = (String) iterator.next();
			resultWriter.write(tag + ".txt.gz" + "\n");
			resultWriter.flush();
		}
		resultWriter.close();
	}

	public static void getNumberOfTagsPerGrid() throws IOException {
		// Path gridfile = Paths.get("userid-tag-title-grid-0.1.txt");
		Path gridfile = Paths.get("userid-tag-title-grid-gtThan4by10-0.1.txt");

		Set<String> set = new HashSet();
		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("grid-info-0.1-40pertags.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			int totalUniqueTagsForCell = lineArr.length - 1;
			int totalNumberofTagsForCell = 0;
			for (int i = 1; i < lineArr.length; i++) {
				String tagInfo[] = lineArr[i].split("===");
				totalNumberofTagsForCell = totalNumberofTagsForCell + Integer.parseInt(tagInfo[1]);
			}
			resultWriter.write(lineArr[0] + "\t" + " TotalUniqueTags:" + totalUniqueTagsForCell + "\t" + " TotalTags:"
					+ totalNumberofTagsForCell + "\n");
			resultWriter.flush();
		}
		resultWriter.close();
	}

	public static void getPhotoCountGridCountForUser() throws IOException {
		Path gridfile = Paths.get("sorted-userid-tag-title-grid-0.1.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		String prevUser ="";
		boolean first = true; 
		Set<String> trainSet = new HashSet();
		Set<String> gridSet = new HashSet();
		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("user-train-grid-info.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
 		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(first){
				prevUser = lineArr[0];
				trainSet.add(lineArr[3]);
				gridSet.add(lineArr[2]);
				first = false;
			}else{
				if(prevUser.equals(lineArr[0])){
					trainSet.add(lineArr[3]);
					gridSet.add(lineArr[2]);
				}else{
					resultWriter.write(lineArr[0] + "\t" + trainSet.size()+ "\t" + gridSet.size()+"\n");
					resultWriter.flush();
					trainSet = new HashSet();
					gridSet = new HashSet();
					trainSet.add(lineArr[3]);
					gridSet.add(lineArr[2]);
					prevUser = lineArr[0];
				}
			}
		}
 		resultWriter.write(prevUser + "\t" + trainSet.size()+ "\t" + gridSet.size()+"\n");
 		resultWriter.flush();
 		resultWriter.close();
	}
}

class Obj implements Comparable {
	String predGrid;
	int count;

	public String getPredGrid() {
		return predGrid;
	}

	public void setPredGrid(String predGrid) {
		this.predGrid = predGrid;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(Object o) {
		if (this.count > ((Obj) o).count) {
			return -1;
		} else if (this.count < ((Obj) o).count) {
			return 1;
		}
		return 0;
	}
}

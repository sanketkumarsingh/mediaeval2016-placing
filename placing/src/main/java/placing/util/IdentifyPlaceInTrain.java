package placing.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

public class IdentifyPlaceInTrain {

	public static void main(String[] args) {
		//findPlaceOccurAllTrain();
		//getPlaceInfo();
		//getCountyTrain();
		getCountyTrainLabels();
	}
	
	public static void getCountyTrainLabels(){
		Path file = Paths.get("train-with-county.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Integer> labelMap = new HashMap();
		int countylabel = 1;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if(!labelMap.containsKey(lineArr[3])){
				labelMap.put(lineArr[3], countylabel);
				countylabel++;
			}
		}
		System.out.println("Total distinct county:" + (countylabel-1));
		

		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(
					new FileWriter("train-with-county-properlabel.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 file = Paths.get("train-with-county.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if(labelMap.containsKey(lineArr[3])){
				outputWriter.write(lineArr[0] + "\t" + lineArr[1]  + "\t" + labelMap.get(lineArr[3]) + "\t" + lineArr[3]+"\n");
				outputWriter.flush();
			}else{
				System.out.println("Something wrong..");
			}
		}
		outputWriter.close();
		
	}
	
	public static void getCountyTrain(){
		Path file = Paths.get("train-photo-video-mediaeval.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(
					new FileWriter("train-with-county.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int countyCount=0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			String place = lineArr[lineArr.length -2];
			String placeArr[] = place.split(",");
			for(String placeItem: placeArr){
				String placeInfo[] = placeItem.split(":");
				if(placeInfo[2].equals("County")){
					countyCount++;
					outputWriter.write(lineArr[1] + "\t" + lineArr[2]  + "\t" + placeInfo[0] + "\t" + placeInfo[1]+"\n");
					outputWriter.flush();
				}
			}
		}
		outputWriter.close();
		System.out.println("Total County found:" + countyCount);
		
	}
	
	public static void getPlaceInfo(){
		Map<String, Integer> placeMap = new HashMap();
		Path file = Paths.get("train-photo-video-mediaeval.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineNo=0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNo++;
			String lineArr[] = line.split("\t");
			String place = lineArr[lineArr.length -2];
			String placeArr[] = place.split(",");
			Set<String> placeTypesForInstance = new HashSet();
			for(String placeItem: placeArr){
				String placeInfo[] = placeItem.split(":");
//				if(placeMap.containsKey(placeInfo[2])){
//					Train obj = placeMap.get(placeInfo[2]);
//					obj.getPlaceSet().add(placeInfo[1]);
//					obj.setNumberOfTrain(obj.getNumberOfTrain()+1);
//				}else{
//					Set<String> placeSet = new HashSet();
//					placeSet.add(placeInfo[1]);
//					Train obj = new Train();
//					obj.setNumberOfTrain(1);
//					obj.setPlaceSet(placeSet);
//					placeMap.put(placeInfo[2], obj);
//				}
				placeTypesForInstance.add(placeInfo[2]);
				
			}
			
			Iterator it = placeTypesForInstance.iterator();
			while(it.hasNext()){
				String placeType = (String) it.next();
				if(placeMap.containsKey(placeType)){
					placeMap.put(placeType,(placeMap.get(placeType) + 1));
				}else{
					placeMap.put(placeType,1);
				}
			}
			
			
			if(lineNo%100000 ==0){
				System.out.println("Processed:" + lineNo);
			}
		}
		System.out.println("Loaded data in map:" + placeMap.size());
		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(
					new FileWriter("place-train-info.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator it = placeMap.entrySet().iterator();
		///long totalData = 0;
		while(it.hasNext()){
			Entry entry = (Entry) it.next();
			Integer count = (Integer) entry.getValue();
			//totalData = obj.getPlaceSet().size() + totalData;
			String toWrite = (String) entry.getKey() + "\t" + count;
			//toWrite = toWrite + "\t" + obj.getNumberOfTrain();
//			Iterator itSet = obj.getPlaceSet().iterator();
//			while(itSet.hasNext()){
//				toWrite = toWrite + "\t" + itSet.next();
//			}
			outputWriter.write(toWrite + "\n");
			outputWriter.flush();
		}
		outputWriter.close();
		//System.out.println("totalData:" + totalData);
		
	}
	
	
	public static void findPlaceOccurAllTrain(){
		Path file = Paths.get("place-train.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Boolean> map = new HashMap();
		int lineNo=0;
		for (String line : (Iterable<String>) lines::iterator) {
			
			String lineArr[] = line.split("\t");
			map.put(lineArr[0], false);
			
		}
		System.out.println("Loaded places.." + map.size());
		file = Paths.get("train-photo-video-mediaeval.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int placeNotFound = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNo++;
			String lineArr[] = line.split("\t");
			String place = lineArr[lineArr.length -2];
			if(place.isEmpty()){
				placeNotFound++;
			}
			String placeArr[] = place.split(",");
			for(String placeItem: placeArr){
				String placeInfo[] = placeItem.split(":");
				if(map.containsKey(placeInfo[2])){
					map.put(placeInfo[2] ,  true);
				}
			}
			Iterator it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry entry = (Entry) it.next();
				if((boolean) entry.getValue() == false){
					it.remove();
				}
			}
			
			it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry entry = (Entry) it.next();
				map.put((String)entry.getKey(), false);
			}
			
		}
		System.out.println("Processed:" + placeNotFound);
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry) it.next();
			System.out.println((String)entry.getKey());
		}
		System.out.println("Done..");
	}
	
}

class Train{
	Set<String> placeSet ;
	long numberOfTrain;
	public Set<String> getPlaceSet() {
		return placeSet;
	}
	public void setPlaceSet(Set<String> placeSet) {
		this.placeSet = placeSet;
	}
	public long getNumberOfTrain() {
		return numberOfTrain;
	}
	public void setNumberOfTrain(long numberOfTrain) {
		this.numberOfTrain = numberOfTrain;
	}
	
}

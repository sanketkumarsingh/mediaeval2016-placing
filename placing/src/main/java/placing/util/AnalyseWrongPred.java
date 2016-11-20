package placing.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class AnalyseWrongPred {

	
	static int grid[][] = new int[180][360];
	public static void main1(String[] args) throws IOException {
		int gridNo = 1;
		for (int i = 0; i < 180; i++) {
			for (int j = 0; j < 360; j++) {
				grid[i][j] = gridNo;
				gridNo++;
			}
		}
		
		Map<Integer,String > gridMap = GenerateGrid.generateLocationGridMap();
		Path gridfile = Paths.get("sortedWrongPred.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		
		PrintWriter gtWriter = null;
		PrintWriter onecellCorrWriter = null;
		PrintWriter onecellInCorrWriter = null;
		try {
			gtWriter = new PrintWriter(new FileWriter("output-info.txt", true));
			onecellCorrWriter = new PrintWriter(new FileWriter("oneCellCorr.txt", true));
			onecellInCorrWriter = new PrintWriter(new FileWriter("oneCellInCorr.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<String> realGridWrongPred = new HashSet();
		int oneCellAcc = 0; int totalImage = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineStr[] = line.split("\t");
			realGridWrongPred.add(lineStr[0]);
			String yes = "no";
			String realLocation = gridMap.get(Integer.parseInt(lineStr[0]));
			String predLocation = gridMap.get(Integer.parseInt(lineStr[1]));
			List<Integer> neighbourList = getNeighouringGridsAtDistK(Integer.parseInt(lineStr[0]) , 1);
			if(neighbourList.contains(Integer.parseInt(lineStr[1]))){
				yes = "yes";
				oneCellAcc++;
				onecellCorrWriter.write(lineStr[0] +"\t" + lineStr[1]+ "\n");
				onecellCorrWriter.flush();
			}else{
				onecellInCorrWriter.write(lineStr[0] +"\t" + lineStr[1]+ "\n");
				onecellInCorrWriter.flush();
			}
			String output = lineStr[0] +"\t"+ realLocation+ "\t" + lineStr[1] + "\t" + predLocation + "\t" + yes + "\n";
			totalImage++;
			gtWriter.write(output);
			gtWriter.flush();
		}
		System.out.println("One cell count:" + oneCellAcc + " and total count:" + totalImage);
		System.out.println("One cell accuracy:" +  (((double)oneCellAcc) / ((double)totalImage)));
		gtWriter.close();
		onecellInCorrWriter.close();
		onecellCorrWriter.close();
	}
	
	

	private static ArrayList<Integer> getNeighouringGridsAtDistK(int gridNumber, int kNeighbours) {

		int rowNumber = gridNumber / 360;
		int remainder = gridNumber % 360;
		int columnNum = remainder - 1;
		if (remainder == 0) {
			rowNumber = rowNumber - 1;
			columnNum = 359;
		}

		ArrayList<Integer> neighList = new ArrayList();
		int currentLayer = 1;
		while (kNeighbours >= currentLayer) {
			int currAboveRow = rowNumber - currentLayer;
			int currBelowRow = rowNumber + currentLayer;
			int currLeftCol = columnNum - currentLayer;
			int currRightCol = columnNum + currentLayer;
			if (currLeftCol < 0) {
				currLeftCol = 0;
			}
			if (currRightCol > 359) {
				currRightCol = 359;
			}
			if (currAboveRow >= 0) {

				for (int i = currLeftCol; i <= currRightCol; i++) {
					neighList.add(grid[currAboveRow][i]);
				}
			}

			if (currBelowRow <= 179) {

				for (int i = currLeftCol; i <= currRightCol; i++) {
					neighList.add(grid[currBelowRow][i]);
				}
			}

			if (currAboveRow < 0) {
				currAboveRow = -1;
			}
			if (currBelowRow > 179) {
				currBelowRow = 180;
			}
			for (int i = currAboveRow + 1; i < currBelowRow; i++) {
				if (columnNum - currentLayer >= 0) {
					neighList.add(grid[i][currLeftCol]);
				}
				if (columnNum + currentLayer <= 359) {
					neighList.add(grid[i][currRightCol]);
				}
			}

			currentLayer = currentLayer + 1;
		}

		return neighList;
	}
	
	public static void main(String[] args) throws IOException {
		//getPerCellAccuracy();
		//sortGridsBasedOnAcc();
		
//		PrintWriter gridwriter = null;
//		try {
//			//gtWriter = new PrintWriter(new FileWriter("output-info.txt", true));
//			//onecellCorrWriter = new PrintWriter(new FileWriter("oneCellCorr.txt", true));
//			gridwriter = new PrintWriter(new FileWriter("Grid.txt", true));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		int k = 1;
//		String toWrite="";
//		for(int i=0;i<180;i++){
//			toWrite="";
//			for(int j=0;j<360 ;j++){
//				toWrite = toWrite +":" +  k   ;
//				if(j==359){
//					System.out.println(k);
//				}
//				k++;
//			}
//			gridwriter.write(toWrite + "\n");
//			gridwriter.flush();
//		}
//		gridwriter.close();
		
		getUniqueWrongPred();
		
	}
	
	private static void getUniqueWrongPred() throws IOException {
		
		Map<Integer, String> map = GenerateGrid.generateLocationGridMap();
		Map<String, String> gridDetailMap = new HashMap();
		Path gridfile = Paths.get("stat-grid-usertag.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			gridDetailMap.put(lineArr[0], line);
		}
		PrintWriter wrongPred = null;
		try {
			//gtWriter = new PrintWriter(new FileWriter("output-info.txt", true));
			//onecellCorrWriter = new PrintWriter(new FileWriter("oneCellCorr.txt", true));
			wrongPred = new PrintWriter(new FileWriter("wrong-pred-count.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		 gridfile = Paths.get("sorted-wrong-pred.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		int count = 0;
		String prevGrid = ""; boolean first = true;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if(first){
				first = false;
				count = 1;
				prevGrid = lineArr[0];
			}else{
				if(prevGrid.equals(lineArr[0])){
					count = count +1;
				}else{
					String detailLine = gridDetailMap.get(prevGrid);
					wrongPred.write(prevGrid + "\t" + count + "\t" + map.get(Integer.parseInt(prevGrid)) + "\t"+ detailLine+ "\n");
					wrongPred.flush();
					prevGrid = lineArr[0];
					count = 1;
				}
			}
		}
		wrongPred.write(prevGrid + "\t" + count + "\t" + map.get(Integer.parseInt(prevGrid)) + "\n");
		wrongPred.flush();
		wrongPred.close();
	}

	public static void getPerCellAccuracy() throws IOException{
		
		PrintWriter percellaccuracy = null;
		try {
			//gtWriter = new PrintWriter(new FileWriter("output-info.txt", true));
			//onecellCorrWriter = new PrintWriter(new FileWriter("oneCellCorr.txt", true));
			percellaccuracy = new PrintWriter(new FileWriter("percellaccuracy.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Map<Double, String> treeMap = new TreeMap<Integer, Integer>();
		Path gridfile = Paths.get("sorted-stat-grird-userTag.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineStr[] = line.split("\t");
			int totalTestphotoForGrid = 0;
			if(!lineStr[4].isEmpty()){
				 totalTestphotoForGrid = Integer.parseInt(lineStr[4]);
			}
			int totalCorrectPred = 0;
			if(!lineStr[7].isEmpty()){
				totalCorrectPred = Integer.parseInt(lineStr[7]);
			}
			if(totalTestphotoForGrid == 0){
				continue;
			}
			double accuracy = ((double)totalCorrectPred)/((double)totalTestphotoForGrid);
			
			percellaccuracy.write(line + "\t" + accuracy + "\n");
			percellaccuracy.flush();
		}
		
		percellaccuracy.close();
	}
	
	public static void sortGridsBasedOnAcc() throws IOException{
		Path gridfile = Paths.get("percellaccuracy.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		List<GridAccuracy> gridAccList = new ArrayList();
		PrintWriter accWriter = null;
		try {
			accWriter = new PrintWriter(new FileWriter("sorted-grid-acc.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineStr[] = line.split("\t");
			GridAccuracy obj = new GridAccuracy();
			obj.setAccuracy(Double.parseDouble(lineStr[lineStr.length-1]));
			obj.setGrid(Integer.parseInt(lineStr[0]));
			gridAccList.add(obj);
		}
		Collections.sort(gridAccList);
		for(GridAccuracy obj: gridAccList){
			accWriter.write(obj.getGrid()+ "\t" + obj.getAccuracy()+"\n");
			accWriter.flush();
		}
		accWriter.close();
	}
	
}

class GridAccuracy implements Comparable{
	double accuracy;
	int grid;
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public int getGrid() {
		return grid;
	}
	public void setGrid(int grid) {
		this.grid = grid;
	}
	@Override
	public int compareTo(Object o) {
		GridAccuracy obj = ((GridAccuracy) o);
		if(this.accuracy > obj.accuracy){
			return 1;
		} else if (this.accuracy < obj.accuracy){
			return -1;
		}else{
			return 0;
		}
	}
}

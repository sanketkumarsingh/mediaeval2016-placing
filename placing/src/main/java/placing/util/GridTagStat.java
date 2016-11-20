package placing.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import placing.entity.Cell;

public class GridTagStat {

	public static void main(String[] args) throws IOException {
		testCount();
	}
	
		public static void testCount() throws IOException{
			Path gridfile = Paths.get("training-mediaeval.txt");
			//Path gridfile = Paths.get("small-train.txt");
			
			Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
			Map<String , Cell> gridCellInfoMap = new HashMap();
			Map<String, Map> gridUserTagMapMap = new HashMap();
			Map<String, Integer> tagCountMap = null;
			String prevGrid = ""; boolean first = true; int totalNumberOfTagForGrid = 0;
			System.out.println("Starting..");
			for (String line : (Iterable<String>) gridlines::iterator) {
				String lineArr[] = line.split("\t");
				if(!gridCellInfoMap.containsKey(lineArr[0])){
					Cell cell = new Cell();
					cell.setCellNumber(lineArr[0]);
					cell.setNumberOfTrainPhoto(1);
					//gridCellInfoMap.put(lineArr[0], cell);
					
					if(first){
						first = false;
						tagCountMap = new HashMap();
						String tags[] = lineArr[1].split(",");
						for(int i=0;i<tags.length;i++){
							if(tagCountMap.containsKey(tags[i])){
								tagCountMap.put(tags[i], 1+ tagCountMap.get(tags[i]) );
							}else{
								tagCountMap.put(tags[i], 1);
							}
							
						}
						
						totalNumberOfTagForGrid = tags.length;
					}else{
						Cell prevCell = gridCellInfoMap.get(prevGrid);
						prevCell.setTotalNumberOfUserTagTrain(totalNumberOfTagForGrid);
						prevCell.setNumberOfUniqueUserTagTrain(tagCountMap.size());
						tagCountMap = new HashMap();
						
						String tags[] = lineArr[1].split(",");
						for(int i=0;i<tags.length;i++){
							if(tagCountMap.containsKey(tags[i])){
								tagCountMap.put(tags[i], 1+ tagCountMap.get(tags[i]) );
							}else{
								tagCountMap.put(tags[i], 1);
							}
							
						}
						totalNumberOfTagForGrid = tags.length;
						
						//totalNumberOfTagForGrid = 0;
						//prevCell.se
					}
					gridUserTagMapMap.put(lineArr[0], tagCountMap);
					gridCellInfoMap.put(lineArr[0], cell);
					prevGrid = lineArr[0];
				}else{
					Cell cell = gridCellInfoMap.get(lineArr[0]);
					cell.setNumberOfTrainPhoto(cell.getNumberOfTrainPhoto() + 1);
					String tags[] = lineArr[1].split(",");
					for(int i=0;i<tags.length;i++){
						if(tagCountMap.containsKey(tags[i])){
							tagCountMap.put(tags[i], 1+ tagCountMap.get(tags[i]) );
						}else{
							tagCountMap.put(tags[i], 1);
						}
						
					}
					totalNumberOfTagForGrid = totalNumberOfTagForGrid + tags.length;
				}	
			}
			
			gridUserTagMapMap.put(prevGrid, tagCountMap);
			Cell prevCell = gridCellInfoMap.get(prevGrid);
			prevCell.setTotalNumberOfUserTagTrain(totalNumberOfTagForGrid);
			prevCell.setNumberOfUniqueUserTagTrain(tagCountMap.size());
			
			System.out.println("Loaded Training data..");
			
			gridfile = Paths.get("estimation-test-photo.txt");
			//gridfile = Paths.get("small-test.txt");
			gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
			
			
			PrintWriter testWriter = null;
			try {
				testWriter = new PrintWriter(new FileWriter("test-not-in-train.txt", true));
			//	testWriter = new PrintWriter(new FileWriter("test10000-1.txt", true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (String line : (Iterable<String>) gridlines::iterator) {
				String lineStr[] = line.split("\t");
				if(gridUserTagMapMap.containsKey(lineStr[0])){
					Cell cell = gridCellInfoMap.get(lineStr[0]);
					cell.setNumberOfTestPhoto(cell.getNumberOfTestPhoto() + 1);
					String tags[]  = lineStr[1].split(",");
					cell.setTotalNumberOfUserTagTest(cell.getTotalNumberOfUserTagTest() + tags.length);
					Map<String,Integer> tagcountmap = gridUserTagMapMap.get(lineStr[0]);
					for(int k=0;k<tags.length;k++){
						if(tagcountmap.containsKey(tags[k])){
							cell.setNumberOfTestPhotoMatchingAnyTrainTag(cell.getNumberOfTestPhotoMatchingAnyTrainTag() + 1);
						    break;
						}
					}
				}else{
					testWriter.write(lineStr[0] + "\n");
					testWriter.flush();
				}
				
			}
			testWriter.close();
			System.out.println("Loaded Test data..");
			
			PrintWriter resultWriter = null;
			try {
				resultWriter = new PrintWriter(new FileWriter("stat-grid-usertag.txt", true));
			//	testWriter = new PrintWriter(new FileWriter("test10000-1.txt", true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Set<Map.Entry<String,Cell>> entrySet = gridCellInfoMap.entrySet();
			Iterator iterator = entrySet.iterator();

			while (iterator.hasNext()) {
				Map.Entry pair = (Map.Entry) iterator.next();
				String gridNumber = (String) pair.getKey();
				Cell cell = (Cell) pair.getValue();
				String toWrite = gridNumber + "\t" + cell.getNumberOfTrainPhoto() + "\t" + 
				cell.getTotalNumberOfUserTagTrain() + "\t"
				+ cell.getNumberOfUniqueUserTagTrain() + "\t" + cell.getNumberOfTestPhoto() + "\t" + 
				cell.getTotalNumberOfUserTagTest()+
			    "\t" + cell.getNumberOfTestPhotoMatchingAnyTrainTag() + "\t"+cell.getNumberOfCorrectPred() +"\t"+cell.getCellNumber();
			    resultWriter.write(toWrite + "\n");
			    resultWriter.flush();
			}
			resultWriter.close();
			System.out.println("Done..");
		}
}

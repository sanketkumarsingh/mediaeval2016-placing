package placing.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

public class ClusterTrainData implements Runnable {
	static DistanceMeasure distanceMeasure;
	private static String fileName;
    static double eps ; // 50km neighbour clusters.
	static int minPts ;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Starting for thread:" + Thread.currentThread().getName());
		MyDbScan<DataPoint> dbscan = new MyDbScan<DataPoint>(eps, minPts, distanceMeasure);
		List<DataPoint> points = getTrainingPoints();
		System.out.println("Data Loaded. Calling clustering for thread:" + Thread.currentThread().getName());
		List<Cluster<DataPoint>> clusters = dbscan.cluster(points);
		System.out.println("Number of clusters:" + clusters.size());
		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("cluster-" + Thread.currentThread().getName() +".txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int clusterNo = 1;
		for(Cluster<DataPoint> c: clusters){
			List<DataPoint> dataPointList = c.getPoints() ; 
			String toWrite = "Cluster"+clusterNo;
			for(DataPoint point: dataPointList){
				toWrite = toWrite + ","+ point.getPoint()[0] + ":" + point.getPoint()[1];
			}
			trainWriter.write(toWrite + "\n");
			trainWriter.flush();
			clusterNo++;
		}
		trainWriter.close();
		
		System.out.println("Done for thread:" + Thread.currentThread().getName());
	}
	
	public ClusterTrainData(String fileName, double eps,int minPts ){
		this.fileName = fileName;
		this.eps = eps;
		this.minPts = minPts;
	}
	
	public static void main(String[] args) throws IOException {
		 // number of training example in clusters.
	     distanceMeasure = new KarneyDist();
		for(int i=1;i<=8;i++){
			double epslocal  = 100000.0;  // 50km neighbour clusters.
		 	int minPtslocal = 5000;
		 	if(i==1 || i ==2 ){
		 		epslocal =  50000.0;
		 		minPtslocal = 5000;
		 	}
		 	if(i==4){
		 		epslocal =  30000.0;
		 		minPtslocal = 5000;
		 	}
			ClusterTrainData obj = new ClusterTrainData("train-photo-video-coord-"+ i + ".txt" , epslocal,minPtslocal );
			Thread thread = new Thread(obj, "Thread-"+i);
			thread.start();
		}
	}

	private static List<DataPoint> getTrainingPoints() {
		Path gridfile = Paths.get(fileName);
		Stream<String> gridlines = null;
		try {
			gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<DataPoint> points = new ArrayList();
		String coord = "point";
		long num = 1;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			double point[] = new double[2];
			point[0] = Double.parseDouble(lineArr[1]); // lat
			point[1] = Double.parseDouble(lineArr[0]); // long
			DataPoint dataPoint = new DataPoint();
			dataPoint.setPoint(point);
			dataPoint.setTrainingId(coord + "-"+num);
			points.add(dataPoint);
		}
		return points;
	}


}


class DataPoint implements Clusterable, Serializable{
	
	double point[];
	String trainingId;
	
	public void setPoint(double[] point) {
		this.point = point;
	}
	
	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	@Override
	public double[] getPoint() {
		// TODO Auto-generated method stub
		return point;
	}
	
}

class KarneyDist implements DistanceMeasure{

	@Override
	public double compute(double[] point1, double[] point2) {
		 // 0 indexes is latitude and 1 index is longitude.

		GeodesicData g = Geodesic.WGS84.Inverse(point1[0], point1[1], point2[0], point2[1] , GeodesicMask.DISTANCE);

//		         System.out.println(g.azi1 + " " + g.azi2 + " " + g.s12);

		return g.s12;
	}
	
}
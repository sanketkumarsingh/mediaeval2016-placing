package placing.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.AbstractDatabase;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.parser.NumberVectorLabelParser;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.rstar.RStarTreeFactory;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.strategies.bulk.SortTileRecursiveBulkSplit;
import de.lmu.ifi.dbs.elki.logging.Logging.Level;
import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;
import de.lmu.ifi.dbs.elki.persistent.AbstractPageFileFactory;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

public class IndexedDbScan implements Runnable {

    //protected static final boolean debug = false;
	private  double eps; // in meters
	private   int minPts;
	private   String fileName;
	///protected static final Logging logger = Logging.getLogger(IndexedDbScan.class);
    //protected static LoggingConfiguration config;
	IndexedDbScan(String fileName, double eps, int minpts) {
		this.fileName = fileName;
		this.eps = eps;
		this.minPts = minpts;
	}

	public static void main(String[] args) {

		 LoggingConfiguration.setLevelFor("de.lmu.ifi.dbs.elki.logging.progress",  Level.OFF.getName());
		

//		for (int i = 1; i <= 8; i++) {
//			double epslocal = 100000.0; // 50km neighbour clusters.
//			int minPtslocal = 5000;
//			if (i == 1 || i == 2) {
//				epslocal = 50000.0;
//				minPtslocal = 5000;
//			}
//			if (i == 4) {
//				epslocal = 30000.0;
//				minPtslocal = 5000;
//			}
//			// ClusterTrainData obj = new
//			// ClusterTrainData("train-photo-video-coord-"+ i + ".txt" ,
//			// epslocal,minPtslocal );
//			IndexedDbScan obj = new IndexedDbScan(fileName, epslocal, minPtslocal);
//			Thread thread = new Thread(obj, "Thread-" + i);
//			thread.start();
//		}
		
		
		 IndexedDbScan obj5 = new IndexedDbScan("train-photo-video-coord-5.txt"  , 100000.0, 4000);
		 Thread thread5 = new Thread(obj5, "Thread-5");
		 thread5.start();
		 IndexedDbScan obj6 = new IndexedDbScan("train-photo-video-coord-6.txt"  , 100000.0, 4000);
		 Thread thread6 = new Thread(obj6, "Thread-6");
		 thread6.start();
		 IndexedDbScan obj8 = new IndexedDbScan("train-photo-video-coord-8.txt"  , 100000.0, 4000);
		 Thread thread8 = new Thread(obj8, "Thread-8");
		 thread8.start();
//		 IndexedDbScan obj8 = new IndexedDbScan("small-data.txt"  , 100000.0, 5);
//		 Thread thread8 = new Thread(obj8, "Thread-8");
//		 thread8.start();
	}

	@Override
	public void run() {
		dbscan();
	}

	private  void dbscan() {
		System.out.println("Starting for Thread:" + Thread.currentThread().getName() + " with filename:" + fileName);
		ListParameterization dbscanParams = new ListParameterization();
		dbscanParams.addParameter(DBSCAN.Parameterizer.EPSILON_ID, eps);
		dbscanParams.addParameter(DBSCAN.Parameterizer.MINPTS_ID, minPts);
		dbscanParams.addParameter(DBSCAN.DISTANCE_FUNCTION_ID, placing.data.KarneyDistance.class);

		DBSCAN<DoubleVector> dbscan = ClassGenericsUtil.parameterizeOrAbort(DBSCAN.class, dbscanParams);
		// double[][] featuresMatrix = loadData();
		// System.out.println("Loaded the data..");
		// String[] labels = loadLabels();
		// System.out.println("Loaded the labels..:" + labels.length);
		// ArrayAdapterDatabaseConnection arrayAdapterDatabaseConnection = new
		// ArrayAdapterDatabaseConnection(
		// featuresMatrix, labels);

		ListParameterization dbparams = new ListParameterization();
		dbparams.addParameter(AbstractDatabase.Parameterizer.INDEX_ID, RStarTreeFactory.class);
		dbparams.addParameter(RStarTreeFactory.Parameterizer.BULK_SPLIT_ID, SortTileRecursiveBulkSplit.class);
		// dbparams.addParameter(AbstractDatabase.Parameterizer.DATABASE_CONNECTION_ID,
		// arrayAdapterDatabaseConnection);
		dbparams.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID, fileName);
		dbparams.addParameter(NumberVectorLabelParser.Parameterizer.LABEL_INDICES_ID, "2");
		dbparams.addParameter(AbstractPageFileFactory.Parameterizer.PAGE_SIZE_ID, 500);//

		Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, dbparams);
		db.initialize();
		System.out.println("Going to cluster for thread:" + Thread.currentThread().getName());
		Relation<?> rel = db.getRelation(TypeUtil.LABELLIST);

		Clustering<Model> result = dbscan.run(db);
		System.out.println("Clustering done for thread:" + Thread.currentThread().getName());

		List<? extends Cluster<?>> clusters = result.getAllClusters();
		System.out.println("for thread:" + Thread.currentThread().getName() + " Total clusters:" + clusters.size());
		// int[] sizes = new int[clusters.size()];

		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(
					new FileWriter("cluster-" + Thread.currentThread().getName() + ".txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Printing the cluster points.." + Thread.currentThread().getName());
		for (int i = 0; i < clusters.size(); ++i) {
		
			Cluster cluster = clusters.get(i);
			System.out.println(Thread.currentThread().getName() + " Cluster size:" + cluster.size()+ "  "+ String.valueOf(cluster.isNoise()));
//			System.out.println("for thread:" + Thread.currentThread().getName() + " Number of element in this cluster:"
//					+ cluster.size());
//			System.out.println(
//					"for thread:" + Thread.currentThread().getName() + " Is cluster noise:" + cluster.isNoise());
			/// if (!cluster.isNoise()) {
			String toWrite = "Cluster" + (i + 1) + " " +  cluster.size() + " "+ String.valueOf(cluster.isNoise());
			for (DBIDIter iter = cluster.getIDs().iter(); iter.valid(); iter.advance()) {
				toWrite = toWrite + " " + rel.get(iter);
				//System.out.print(rel.get(iter) + " ");
				// System.out.print(DBIDUtil.toString(iter) + " ");
			}
			resultWriter.write(toWrite + "\n");
			resultWriter.flush();
		}
		resultWriter.close();
		System.out.println("Done for thread:" + Thread.currentThread().getName());
	}

	// not called
	private static String[] loadLabels() {
		Path file = Paths.get("data-2000.txt");

		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int num_rows = 2000; // 9608333; // number of rows in the file
		// int num_col = 2; // number of dimension of each row in the file.
		// (here
		// just: longitude latitude)
		String[] labels = new String[num_rows];
		int index = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			labels[index] = lineArr[1] + ":" + lineArr[0];
			index++;
		}
		return labels;
	}
	// not called
	private static double[][] loadData() {

		// Path file = Paths.get("unique-coordinates.txt");
		Path file = Paths.get("data-2000.txt");

		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int num_rows = 2000; // 9608333; // number of rows in the file
		int num_col = 2; // number of dimension of each row in the file. (here
							// just: longitude latitude)
		double[][] data = new double[num_rows][num_col]; // number of rows in
															// file.
		int index = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			data[index][0] = Double.parseDouble(lineArr[1]); // lat
			data[index][1] = Double.parseDouble(lineArr[0]); // long
			index++;
		}

		return data;
	}

}

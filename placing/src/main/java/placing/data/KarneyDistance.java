package placing.data;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.spatial.SpatialComparable;
import de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractNumberVectorDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractSpatialDistanceFunction;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

//AbstractSpatialDistanceFunction
public class KarneyDistance extends AbstractSpatialDistanceFunction {

	@Override
	public double distance(NumberVector o1, NumberVector o2) {
		// System.out.println("Karney called..");
		GeodesicData g = Geodesic.WGS84.Inverse(o1.doubleValue(1), o1.doubleValue(0), o2.doubleValue(1),
				o2.doubleValue(0), GeodesicMask.DISTANCE);
		// System.out.println(g.azi1 + " " + g.azi2 + " " + g.s12);
		return g.s12;
	}

	@Override
	public double minDist(SpatialComparable mbr1, SpatialComparable mbr2) {
		if (mbr1 instanceof NumberVector && mbr2 instanceof NumberVector) {
			return distance((NumberVector) mbr1, (NumberVector) mbr2);
		}
		final int dim1 = mbr1.getDimensionality();
		if (dim1 != mbr2.getDimensionality()) {
			throw new IllegalArgumentException("Dimensionalities do not agree!");
		}
		double sumdiff = 0., sumsum = 0.;
		for (int d = 0; d < dim1; d++) {
			final double min1 = mbr1.getMin(d), max1 = mbr1.getMax(d);
			final double min2 = mbr2.getMin(d), max2 = mbr2.getMax(d);
			if (max1 < min2) {
				sumdiff += min2 - max1;
			} else if (min1 > max2) {
				sumdiff += min1 - max2;
			} else {
				// Minimum difference is 0
			}
			sumsum += Math.max(-min1, max1) + Math.max(-min2, max2);
		}
		return sumdiff / sumsum;

	}

}

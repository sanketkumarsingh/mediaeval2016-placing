package placing.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Test {

	public static void main(String[] args) {
		String saveTo = "/Users/sanket/Desktop/Thesis/YLI-GEO/features/image/sift/";
		try {
			String a[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
			String b[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
			String c[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
			URL url = null;
			String origStr = "https://multimedia-commons.s3-us-west-2.amazonaws.com/subsets/YLI-GEO/features/image/sift/";
			boolean first = true;
			for (int i = 3; i < a.length; i++) {
				System.out.println("Stating with i:" + i);
				for (int j = 0; j < a.length; j++) {
					for (int k = 0; k < a.length; k++) {
						if (first) {
							first = false;
						} else {
							String filename = a[i] + b[j] +c[k] + ".sift.gz";
							System.out.println(filename);
							String urlStr = origStr + filename;
							url = new URL(urlStr);
						

						URLConnection conn = url.openConnection();
						InputStream in = conn.getInputStream();
						FileOutputStream out = new FileOutputStream(saveTo +filename);
						byte[] bdata = new byte[1024];
						int count;
						while ((count = in.read(bdata)) >= 0) {
					//		System.out.println("hello");
							out.write(bdata, 0, count);
						}
						out.flush();
						out.close();
						in.close();
						}
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

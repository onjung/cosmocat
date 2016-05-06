import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.system.EncodedImage;

/** 
 * A thread class for ads.
 * 
 * @author On Jung and James Choi
 */
public class AdThread extends Thread {

	private HttpConnection adCon;
	private InputStream adStream;
	private EncodedImage img;

	private final String url = "http://127.0.0.1:80/";
	private final String ad1 = "ad1.png";
	private final String ad2 = "ad2.png";
	private final String ad3 = "ad3.png";
	private final String ad4 = "ad4.png";
	private final String ad5 = "ad5.png";
	private String ad_url;

	private final int NUM_ADS = 5;
	private final int PIC_SIZE = 1000000;
	private final int TIME_INTERVAL = 60000;

	public AdThread() {
		// Nothing goes here
	}

	public void run() {
		super.run();

		int index = 0;

		while (true) {
			if (index == 0) {
				ad_url = url + ad1;
			} else if (index == 1) {
				ad_url = url + ad2;
			} else if (index == 2) {
				ad_url = url + ad3;
			} else if (index == 3) {
				ad_url = url + ad4;
			} else {
				ad_url = url + ad5;
			}

			/* Connect to the given url and retrieve the image. */
			try {
				this.adCon = (HttpConnection) Connector.open(ad_url
						+ ";deviceside=true");

				if (adCon.getResponseCode() == HttpConnection.HTTP_OK) {

					this.adStream = adCon.openInputStream();
					byte[] picByte = new byte[PIC_SIZE];
					int len = 0;

					StringBuffer adStr = new StringBuffer();
					while ((len = adStream.read(picByte)) != -1) {
						adStr.append(new String(picByte, 0, len));
					}
					String picStr = new String(picByte);
					adStr.append(picStr);

					final byte[] rtnImgData = adStr.toString().getBytes();
					img = EncodedImage.createEncodedImage(rtnImgData, 0, -1);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (adStream != null)
				try {
					adStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			if (adCon != null)
				try {
					adCon.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			/* Put the thread to sleep for 60 seconds. */
			try {
				Thread.sleep(TIME_INTERVAL);
			} catch (Exception e) {
				e.printStackTrace();
			}

			index = (index + 1) % NUM_ADS;
		}
	}

	/** Get the ad image. */
	public EncodedImage getImg() {
		return img;
	}
}
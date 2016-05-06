import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.XYRect;

/**
 * The stack of disks.
 * 
 * @author On Jung and James Choi
 */
public class DiskStack {

	private final String FILENAME = "cat";
	private final String EXT = ".png";

	/* Array of disks. */
	private Disk[] stack;
	/* x-axis that the disk should be located. */
	private int xAxis;
	/* y-axis that the disk should be located. */
	private int yAxis;
	/* Width of the disk, which is placed on the stack. */
	private int width;
	/* Height of the disk, which is placed on the stack */
	private int height;

	/** Constructor */
	public DiskStack(int n, int x, int y, int w, int h) {
		this.stack = new Disk[n];
		this.xAxis = x;
		this.yAxis = y;
		this.width = w;
		this.height = h;
	}

	/** Generate a stack by placing disks. */
	public void generate() {
		for (int i = 0; i < this.stack.length; i++) {
			XYRect rec = new XYRect(xAxis, yAxis, width, height);
			EncodedImage img = EncodedImage.getEncodedImageResource(FILENAME
					+ (i + 1) + EXT);
			this.stack[i] = new Disk(img, rec, i + 1);
			this.xAxis += 5;
			this.yAxis -= 20;
			this.width -= 10;
		}
	}

	/** Get the array of disks. */
	public Disk getDisk(int n) {
		return this.stack[n];
	}

	/** Size of the disk stack. */
	public int length() {
		return this.stack.length;
	}
}
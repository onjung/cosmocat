import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.XYRect;

/**
 * Disk class that represents each disk.
 * 
 * @author On Jung and James Choi
 */
public class Disk {

	/*
	 * Rod that this disk is located. 0 is leftmost rod, 1 is middle rod, 2 is
	 * rightmost rod.
	 */
	private int rod;
	/* Image of this disk */
	private EncodedImage source;
	/* XYRect that this disk's image will be drawn. */
	private XYRect destin;
	/* Index of this disk. */
	private int index;
	/* True if this disk is selected. (clicked) */
	private boolean selected;

	/** Constructor */
	public Disk(EncodedImage s, XYRect d, int n) {
		this.source = s;
		this.destin = d;
		this.index = n;
		this.selected = false;
		this.rod = 0;
	}

	/** Get the image */
	public EncodedImage getSource() {
		return this.source;
	}

	/** Get the XYRect that this disk's image is drawn. */
	public XYRect getDestin() {
		return this.destin;
	}

	/** Get the rod that this disk is located. */
	public int getRod() {
		return this.rod;
	}

	/** Set the rod when the disk is relocated. */
	public void setRod(int r) {
		this.rod = r;
	}

	/** Get the index of this disk. */
	public int getIndex() {
		return this.index;
	}

	/** Check if this disk is selected. */
	public boolean isSelected() {
		return this.selected;
	}

	/** Select or Unselect is disk. */
	public void clicked() {
		if (this.selected == true) {
			this.selected = false;
		} else {
			this.selected = true;
		}
	}
}
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;

/** 
 * Main GUI class for a game field.
 * 
 * @author On Jung and James Choi
 */
class GUI extends Field {
	
	/* Width of the screen */
	private int width;
	/* Height of the screen */
	private int height;
	/* Stack for disks */
	private DiskStack stack;
	/* Array of rods */
	private XYRect[] rods;
	/* Rectangle contains advertisements */
	private XYRect ad;
	/* Thread for ad rotation */
	private AdThread adThread;
	/* Thread for time update */
	private TimeThread timeThread;
	/* x-axis of cursor */
	private int cursorX;
	/* y-axis of cursor */
	private int cursorY;
	/* Number of disks used for this game */
	private int numDisk;
	/* Counter for number of steps */
	private int numCount;
	/* See if a disk is selected to ensure only one disk can be clicked */
	private boolean selected;
	
	/* x-axis of the disk's previous location */
	private int diskX;
	/* y-axis of the disk's previous location */
	private int diskY;
	
	/* Background image */
	private Bitmap background;
	/* cCursor Image */
	private Bitmap finger;
	/* Ad image */
	private EncodedImage adImg;
	
	/** Constructor. */
	public GUI(int w, int h, int d) {
		this.width = w;
		this.height = h;
		this.numDisk = d;
		
		this.background = Bitmap.getBitmapResource("bg.png");
		this.finger = Bitmap.getBitmapResource("finger.png");
		this.ad = new XYRect(0, this.height - 60, this.width, 60);
		this.rods = new XYRect[3];
		rods[0] = new XYRect(32, 0, 140, this.height - 60);
		rods[1] = new XYRect(172, 0, 140, this.height - 60);
		rods[2] = new XYRect(312, 0, 140, this.height - 60);
		
		this.stack = new DiskStack(numDisk, 45, 250, 110, 20);
		stack.generate();
		this.adThread = new AdThread();
		adThread.start();
		this.timeThread = new TimeThread();
		timeThread.start();
		this.cursorX = this.width/2;
		this.cursorY = this.height/2;
		this.selected = false;
	}	

	protected void layout(int w, int h) {
		setExtent(this.width, this.height);
	}

	protected void paint(Graphics g) {
		g.clear();
		drawBackground(g);
		drawDiskStack(g);
		drawCursor(g);
		drawPane(g);
		drawAd(g);
	}
	
	/** Draw background */
	private void drawBackground(final Graphics g) {
		g.drawBitmap(0, 0, background.getWidth(), background.getHeight(), background, 0, 0);
	}
	
	/** Draw disks */
	private void drawDiskStack(final Graphics g) {
		for(int i = 0; i < stack.length(); i++) {
			EncodedImage s = stack.getDisk(i).getSource();
			g.drawBitmap(stack.getDisk(i).getDestin(), s.getBitmap(), 0, 0);
		}
	}

	/** Draw time and steps pane */
	private void drawPane(final Graphics g) {
		g.setColor(Color.WHITE);
		g.drawText("Time  " + timeThread.getTime(), 20, 20);
		g.drawText("Steps  " + numCount, 360, 20);
		this.invalidate();
	}
	
	/**Draw cursor */
	private void drawCursor(final Graphics g){
		g.drawBitmap(cursorX, cursorY, finger.getWidth(), finger.getHeight(), finger, 0, 0);
	}
	
	/**Draw ad */
	private void drawAd(final Graphics g){
		adImg = adThread.getImg();
		if (adImg != null) {
			g.drawBitmap(ad, adImg.getBitmap(), 0, 0);
		}
		this.invalidate();
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public void onUnfocus() {
		super.onUnfocus();
		this.invalidate();
	}
	
	/** Listener for the cursor movement */
	protected boolean navigationMovement(int dx, int dy,int status,int time) {
		
		cursorX += 10*dx;
		cursorY += 10*dy;
		
		/* Cursor stays in the viewable screen area */
		if (cursorX < 0) cursorX = 0;
		if (cursorX > this.width) cursorX = this.width - 15;
		
		if (cursorY < 0) cursorY = 0;
		if (cursorY > this.height) cursorY = this.height - 20;
		
		/* If a disk is selected, the disk moves with the cursor */
		for(int i = 0; i < stack.length(); i++) {
			if(stack.getDisk(i).isSelected()) {
				int x = cursorX - stack.getDisk(i).getSource().getWidth()/2;
				int y = cursorY - 10;
				stack.getDisk(i).getDestin().setLocation(x, y);
			}
		}
		
		this.invalidate();
		return true;
	}
	
	/** Listener for a click */
	protected boolean navigationClick(int status, int time) { 
		
		for(int i = 0; i < stack.length(); i++) {
			if(stack.getDisk(i).getDestin().contains(cursorX, cursorY)
					&& isSmall(stack.getDisk(i))
					&& !stack.getDisk(i).isSelected()
					&& !this.selected) {
				/* When the unselected first disk is clicked */
			
				stack.getDisk(i).clicked();
				
				/* Remember the disk's location in case of return */
				this.diskX = stack.getDisk(i).getDestin().x;
				this.diskY = stack.getDisk(i).getDestin().y;
				
				this.selected = true;
				break;
				
			} else if(stack.getDisk(i).isSelected() && this.selected) {
				/* When the disk that has been selected is clicked */
				for(int j = 0; j < rods.length; j++) {
			
					if(stack.getDisk(i).getDestin().intersects(rods[j])) {
						/* Check if a disk is moved */
						boolean isMoved = move(stack.getDisk(i), rods[j], j);
						
						
						if(isMoved) {
							/* Update the rod */
							stack.getDisk(i).setRod(j);
							/* Update the step */
							numCount++;
						} else {
							stack.getDisk(i).getDestin().x = diskX;
							stack.getDisk(i).getDestin().y = diskY;
						}
					
						stack.getDisk(i).clicked();
						this.selected = false;
						break;
					}

				}
			}
		}
		this.invalidate();
		
		/* Finish the game is the player wins */
		if(isWin()) {
			Dialog.alert("Congratulations! You took " + numCount + " steps.");
		}
		return true;   
	}  
	
	/** Move the disk  */
	private boolean move(Disk disk, XYRect rod, int index) {
		int diskCenter = (disk.getDestin().x + disk.getDestin().X2())/2;
		int diskBottom = disk.getDestin().Y2();
		
		int rodCenter = (rod.x + rod.X2())/2;
		int yAxisBottom = 270;
		
		for(int i = 0; i < stack.length(); i++) {
			
			if(stack.getDisk(i).getRod() == index && !disk.equals(stack.getDisk(i))) {
				if(stack.getDisk(i).getDestin().y < yAxisBottom) {
					yAxisBottom = stack.getDisk(i).getDestin().y;
					
					if(disk.getIndex() < stack.getDisk(i).getIndex()) {
						/* The disk should be smaller than others */
						return false;
					}
				}
			}
		}
		if (diskCenter != rodCenter) {
			disk.getDestin().x = rodCenter - disk.getDestin().width/2;
		}
		
		if (diskBottom != yAxisBottom){
			disk.getDestin().y = yAxisBottom - disk.getDestin().height;
		}
		return true;
	}
	
	/** Check if this disk is smaller than other disks on the same rod. */
	private boolean isSmall(Disk d){
		for (int i = 0; i < stack.length(); i++){
			if(stack.getDisk(i).getRod() == d.getRod() && d.getIndex() < stack.getDisk(i).getIndex()) {
				/* Larger index means smaller size */
				return false;
			}
		}
		return true;
	}
	
	/** Check if the player has won the game. */
	private boolean isWin(){
		if (stack.getDisk(0).getRod() == 0){
			/* Should be placed at the center rod or the rightmost rod. */
			return false;
		} else {
			for(int i = 1; i < stack.length(); i++) {
				if (stack.getDisk(i).getRod() != stack.getDisk(0).getRod()){
					/* All disks are at not at the same rod. */
					return false;
				}
			}
		}
		return true;
	}
}

/** Thread class for the play time update */
class TimeThread extends Thread {
	private final int INTERVAL = 1000;
	private int sec;
	private int min;
	private int hour;
	public TimeThread() {
		sec = 0;
		min = 0;
		hour = 0;
	}

	public void run() {
		while (true) {
			sec++;
			if (sec >= 60) {
				sec /= 60;
				min++;
			}
			if (min >= 60) {
				min /= 60;
				hour++;
			}

			try {
				Thread.sleep(INTERVAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Return the time */
	public String getTime() {
		String retVal = hour + ":" + min + ":" + sec;
		return retVal;
	}
}
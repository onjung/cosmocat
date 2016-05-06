import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Cosmo Cat stack game.
 * 
 * @author On Jung and James Choi
 *
 */
public class CosmoCat extends UiApplication {

	public static void main(String[] args) {
		CosmoCat game = new CosmoCat();
		game.enterEventDispatcher();
	}

	public CosmoCat() {
		pushScreen(new Intro());
	}
}

class Intro extends MainScreen {

	private ObjectChoiceField choiceField;
	private ButtonField okButton;
	private int selection;

	public Intro() {
		super();

		String choices[] = { "3", "4", "5", "6", "7", "8" };
		choiceField = new ObjectChoiceField("Number of Disks", choices);
		add(choiceField);

		okButton = new ButtonField("START GAME");
		add(okButton);

		/* Button listener. */
		okButton.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				/* selection 0-5 for number of disks 3-8, respectively */
				selection = choiceField.getSelectedIndex();
				/* Invoke to get into the game screen. */
				UiApplication.getUiApplication().pushScreen(
						new Screen(selection + 3));
			}
		});
	}

	public boolean onMenu(int i) {
		return false;
	}
}

class Screen extends MainScreen {

	public Screen(int selection) {
		super();
		GUI field = new GUI(Display.getWidth(), Display.getHeight(), selection);
		add(field);
		field.setFocus();
	}

	public boolean onClose() {
		Dialog.alert("Thanks for playing!");
		System.exit(0);
		return true;
	}
}

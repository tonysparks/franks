/*
 * see license.txt 
 */
package newera.ui;

import leola.frontend.listener.EventDispatcher;
import newera.gfx.Inputs;
import newera.sfx.Sounds;
import newera.ui.Label.TextAlignment;
import newera.ui.events.CheckboxEvent;
import newera.ui.events.OnCheckboxClickedListener;

/**
 * @author Tony
 *
 */
public class Checkbox extends Widget {

	private boolean isHovering;
	private boolean isChecked;
	private Label label;	
	
	/**
	 * @param eventDispatcher
	 */
	public Checkbox(boolean isChecked, EventDispatcher eventDispatcher) {
		super(eventDispatcher);
				
		getBounds().setSize(18, 18);
		
		this.isChecked = isChecked;
		this.label = new Label();
		this.label.getBounds().add(getBounds().width + 8, getBounds().height - 5);
		this.label.setTextAlignment(TextAlignment.LEFT);
		addWidget(label);
		
		addInputListener(new Inputs() {
			
			@Override
			public boolean mouseMoved(int x, int y) {						
				super.mouseMoved(x, y);
				if ( ! isDisabled() ) {
					if ( getScreenBounds().contains(x,y)) {																			
						setHovering(true);	
						return false;
					}
				}
							
				setHovering(false);
				return false;
			}
			
			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				if(getScreenBounds().contains(x, y)) {
					setChecked(! isChecked() );
					return true;
				}
				
				return false;
			}
		});
	}

	/**
	 * 
	 */
	public Checkbox(boolean isChecked) {
		this(isChecked, new EventDispatcher());
	}
	
	/* (non-Javadoc)
	 * @see seventh.ui.Widget#setTheme(seventh.client.gfx.Theme)
	 */
	@Override
	public void setTheme(Theme theme) {	
		super.setTheme(theme);
		label.setFont(theme.getSecondaryFontName());
		label.setTextSize(14);
	}
	
	public void addCheckboxClickedListener(OnCheckboxClickedListener l) {
		getEventDispatcher().addEventListener(CheckboxEvent.class, l);
	}
	
	public void removeCheckboxClickedListener(OnCheckboxClickedListener l) {
		getEventDispatcher().removeEventListener(CheckboxEvent.class, l);
	}
	
	/**
	 * @param isHovering the isHovering to set
	 */
	private void setHovering(boolean isHovering) {
				
		if(isHovering) {
			
			if(!this.isHovering) {
				Sounds.playGlobalSound(Sounds.uiHover);
			}
		}
		
		this.isHovering = isHovering;
	}
	
	/**
	 * @return the isHovering
	 */
	public boolean isHovering() {
		return isHovering;
	}
	
	/**
	 * @return the isChecked
	 */
	public boolean isChecked() {
		return isChecked;
	}
	
	/**
	 * @param isChecked the isChecked to set
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		Sounds.playGlobalSound(Sounds.uiSelect);
		getEventDispatcher().sendNow(new CheckboxEvent(this, this));
	}
	
	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}
	
	/**
	 * Sets the label text
	 * @param text
	 */
	public void setLabelText(String text) {
		this.label.setText(text);
	}
}

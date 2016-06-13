/*
 * see license.txt 
 */
package newera.ui;

import leola.frontend.listener.EventDispatcher;
import newera.gfx.Inputs;
import newera.sfx.Sounds;
import newera.ui.events.OnSliderMovedListener;
import newera.ui.events.SliderMovedEvent;

/**
 * @author Tony
 *
 */
public class Slider extends Widget {

	private static final int MAX_INDEX = 100;	
	private int index;
	private Button handle;
	private boolean isHandleHeld;
	private boolean isHovering;
	private SliderMovedEvent event;
	/**
	 * @param eventDispatcher
	 */
	public Slider(EventDispatcher eventDispatcher) {
		super(eventDispatcher);		
		this.event = new SliderMovedEvent(this, this);
		this.handle = new Button(eventDispatcher);
		this.handle.getBounds().setSize(15, 18);
		this.handle.getBounds().setLocation(0, -7);		
		moveHandle(0);
		
		addWidget(handle);
		addInputListener(new Inputs() {		
			
			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				isHandleHeld = false;
				return false;
			}
			
			@Override
			public boolean touchDragged(int x, int y, int pointer) {
				if(isHandleHeld) {
					moveHandleTo(x);					
					return true;
				}
				return false;
			}
			
			@Override
			public boolean touchDown(int x, int y, int pointer, int button) {
				if (handle.getScreenBounds().contains(x, y)) {
					isHandleHeld = true;
					return true;
				}
				else if(getScreenBounds().contains(x, y)) {
					moveHandleTo(x);
					return true;
				}
				return false;
			}
			
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
		});
	}

	/**
	 * 
	 */
	public Slider() {
		this(new EventDispatcher());
	}
	
	public void addSliderMoveListener(OnSliderMovedListener l) {
		getEventDispatcher().addEventListener(SliderMovedEvent.class, l);
	}
	
	public void removeSliderMoveListener(OnSliderMovedListener l) {
		getEventDispatcher().removeEventListener(SliderMovedEvent.class, l);
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
	
	/* (non-Javadoc)
	 * @see seventh.ui.Widget#setTheme(seventh.client.gfx.Theme)
	 */
	@Override
	public void setTheme(Theme theme) {	
		super.setTheme(theme);
		this.handle.setBackgroundColor(theme.getBackgroundColor());
	}
	
	/**
	 * @return the handle
	 */
	public Button getHandle() {
		return handle;
	}
	
	private void moveHandleTo(int x) {
		int maxX = getBounds().x + getBounds().width;// - handle.getBounds().width/2;
		int minX = getBounds().x;
		
		int newIndex = x - minX;
		if(x <= maxX && x >= minX) {
			handle.getBounds().x = newIndex;
		}
		if(newIndex >= 0 && newIndex <= 100) {
			index = newIndex;			
			getEventDispatcher().sendNow(event);
		}
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Moves the index value
	 * @param index
	 */
	public void moveHandle(int index) {
		
		if(index > MAX_INDEX) {
			index = MAX_INDEX;
		}
		else if(index < 0) {
			index = 0;
		}
		
//		int width = getBounds().width - handle.getBounds().width;
		float percentage = (float)index / (float)MAX_INDEX;
		float x = getBounds().width * percentage;		
		moveHandleTo( getBounds().x + (int)x);
	}

}

/*
**************************************************************************************
*Myriad Engine                                                                       *
*Copyright (C) 2006-2007, 5d Studios (www.5d-Studios.com)                            *
*                                                                                    *
*This library is free software; you can redistribute it and/or                       *
*modify it under the terms of the GNU Lesser General Public                          *
*License as published by the Free Software Foundation; either                        *
*version 2.1 of the License, or (at your option) any later version.                  *
*                                                                                    *
*This library is distributed in the hope that it will be useful,                     *
*but WITHOUT ANY WARRANTY; without even the implied warranty of                      *
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU                   *
*Lesser General Public License for more details.                                     *
*                                                                                    *
*You should have received a copy of the GNU Lesser General Public                    *
*License along with this library; if not, write to the Free Software                 *
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA      *
**************************************************************************************
*/
package newera.ui;


import java.util.ArrayList;
import java.util.List;

import leola.frontend.listener.EventDispatcher;
import newera.gfx.Canvas;
import newera.gfx.Cursor;
import newera.gfx.Inputs;
import newera.util.TimeStep;

/**
 * Use the {@link UserInterfaceManager} to add any {@link Widget}s and register them to the 
 * designated {@link EventDispatcher} and delegates user input to each component.
 * 
 * @author Tony
 *
 */
public class UserInterfaceManager extends Inputs {
	
	
	/**
	 * Widgets
	 */
	private List<Widget> widgets;
	private Cursor cursor;
		
	/**
	 * @param eventDispatcher
	 */
	public UserInterfaceManager() {
		this.widgets = new ArrayList<Widget>();
		this.cursor = new Cursor();
	}

	/**
	 * Brings the UI to the front of input processing.
	 * @param inputSystem
	 */
//	public void init(MultiplInputProcessor inputs) {
//		inputs.a
//	}
	
	public void hideMouse() {
		this.cursor.setVisible(false);
	}
	
	public void showMouse() {
		this.cursor.setVisible(true);
	}
	
	/**
	 * @return the cursor
	 */
	public Cursor getCursor() {
		return cursor;
	}
	
	/**
	 * @param cursor the cursor to set
	 */
	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}
	
	
	
	/**
	 * Destroys the widgets
	 */
	public void destroy() {
		Widget.globalInputListener.destroy();
	}
	
	/**
	 * @param w
	 */
	public void addWidget(Widget w) {
		this.widgets.add(w);
	}
	
	/**
	 * @param w
	 */
	public void removeWidget(Widget w) {
		this.widgets.remove(w);
	}
	
	/**
	 * @return
	 */
	public List<Widget> getWidgets() {
		return this.widgets;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#keyDown(int)
	 */
	@Override
	public boolean keyDown(int key) {
		return Widget.globalInputListener.keyDown(key);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#keyUp(int)
	 */
	@Override
	public boolean keyUp(int key) {
		return Widget.globalInputListener.keyUp(key);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#mouseMoved(int, int)
	 */
	@Override
	public boolean mouseMoved(int x, int y) {
		cursor.moveTo(x,y);
		return Widget.globalInputListener.mouseMoved(cursor.getX(), cursor.getY());
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#touchDown(int, int, int, int)
	 */
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return Widget.globalInputListener.touchDown(cursor.getX(), cursor.getY(), pointer, button);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#touchUp(int, int, int, int)
	 */
	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return Widget.globalInputListener.touchUp(cursor.getX(), cursor.getY(), pointer, button);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#keyTyped(char)
	 */
	@Override
	public boolean keyTyped(char key) {
		return Widget.globalInputListener.keyTyped(key);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#scrolled(int)
	 */
	@Override
	public boolean scrolled(int amount) {
		return Widget.globalInputListener.scrolled(amount);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Inputs#touchDragged(int, int, int)
	 */
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		cursor.moveTo(x,y);
		return Widget.globalInputListener.touchDragged(cursor.getX(), cursor.getY(), pointer);
	}
	
	/* (non-Javadoc)
	 * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
	 */
	public void render(Canvas canvas) {
		this.cursor.render(canvas);
	}

	/* (non-Javadoc)
	 * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
	 */
	public void update(TimeStep timeStep) {		
	}
}

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
package franks.ui.view;

import javax.swing.Renderer;

import franks.gfx.Camera;
import franks.ui.Button;
import franks.ui.Dialog;
import franks.ui.Widget;

/**
 * Renderer of {@link Widget}s.
 * 
 * @author Tony
 *
 */
public interface WidgetRenderer {

    /**
     * Renders a {@link Button}.
     * 
     * @param button
     * @param renderer
     * @param camera
     * @param alpha
     */
    public void renderButton(Button button, Renderer renderer, Camera camera, long alpha);
    
    
    /**
     * Renders a {@link Dialog}.
     * 
     * @param dialog
     * @param renderer
     * @param camera
     * @param alpha
     */
    public void renderDialog(Dialog dialog, Renderer renderer, Camera camera, long alpha);
}

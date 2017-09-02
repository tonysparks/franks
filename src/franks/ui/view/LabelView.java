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

import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.RenderFont;
import franks.gfx.Renderable;
import franks.math.Rectangle;
import franks.ui.Label;
import franks.util.TimeStep;

/**
 * Renders text on a label.
 * 
 * @author Tony
 *
 */
public class LabelView implements Renderable {

    /**
     * The label
     */
    private Label label;
    
    
    
    /**
     * @param label
     */
    public LabelView(Label label) {
        this.label = label;
    }

    
    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
     */
    public void render(Canvas renderer, Camera camera, float alpha) {
        String buttonTxt = this.label.getText();
        if ( buttonTxt == null || buttonTxt.equals("") || !this.label.isVisible()) {
            return;
        }            
                
//        Widget parent = this.label.getParent();        
//        if( parent != null ) {                
//            renderer.setColor(parent.getForegroundColor(), parent.getForegroundAlpha());            
//        }
//        else {
//            renderer.setColor(this.label.getForegroundColor(),this.label.getForegroundAlpha());
//             
//        }
        
        renderer.setColor(this.label.getForegroundColor(),this.label.getForegroundAlpha());
        
        Rectangle bounds = this.label.getScreenBounds(); 
        if ( this.label.ignoreCR() ) {
    
            renderer.setFont(label.getFont(), (int)label.getTextSize());
            int width = renderer.getWidth(buttonTxt);    
            switch(this.label.getTextAlignment()) {
            case LEFT:
                RenderFont.drawShadedString(renderer
//                renderer.drawString(
                          , buttonTxt
                          , bounds.x
                          , bounds.y + (bounds.height/2), null );
                break;
            case RIGHT:
                RenderFont.drawShadedString(renderer
//                renderer.drawString(
                          , buttonTxt
                          , bounds.x + bounds.width - width
                          , bounds.y + (bounds.height/2), null );
                break;
            case CENTER:
                RenderFont.drawShadedString(renderer
//                renderer.drawString(
                                  , buttonTxt
                                  , bounds.x + (bounds.width/2) - (width/2)
                                  , bounds.y + (bounds.height/2), null );
                break;
            }
//            font.setSize(textSize);
        }
        else {
            renderWithCR(renderer, buttonTxt, bounds);
        }
    }

    /**
     * Renders with Carriage returns.
     * @param renderer
     * @param buttonTxt
     * @param bounds
     */
    private void renderWithCR(Canvas renderer, String buttonTxt, Rectangle bounds) {
//        Font font = renderer.getDefaultFont();
//
//        float textSize = font.getSize();
//        font.setSize(this.label.getTextSize());
//        int height = font.getHeight('W');
//        
//        String[] strs = buttonTxt.split("\n");
//        int size = strs.length;
//        for(int i = 0; i < size; i++ ) {
//            String txt = strs[i].trim();
//            if ( txt != null ) {
//                int width = font.getWidth(txt);    
//                switch(this.label.getTextAlignment()) {
//                case LEFT:
//                    renderer.drawString(buttonTxt
//                              , bounds.x
//                              , bounds.y + (bounds.height/2) + (height * i) );
//                    break;
//                case RIGHT:
//                    renderer.drawString(buttonTxt
//                              , bounds.x + bounds.width - width
//                              , bounds.y + (bounds.height/2) + (height * i) );
//                    break;
//                case CENTER:
//                    renderer.drawString(txt
//                                      , bounds.x + (bounds.width/2) - (width/2)
//                                      , bounds.y + (bounds.height/2) + (height * i));
//                break;
//                }
//
//            }
//        }
//        font.setSize(textSize);
    }

    
    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
     */
    public void update(TimeStep timeStep) {
    }

}

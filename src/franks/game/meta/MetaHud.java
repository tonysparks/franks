/*
 * see license.txt 
 */
package franks.game.meta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import franks.game.AttackCostAnalyzer;
import franks.game.Game;
import franks.game.MovementCostAnalyzer;
import franks.game.World;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.gfx.Art;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.RenderFont;
import franks.gfx.Renderable;
import franks.map.IsometricMap;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.util.TimeStep;

/**
 * Heads Up Display
 * 
 * @author Tony
 *
 */
public class MetaHud implements Renderable {

    private Game game;
    
    private Cursor cursor;
    private World world;
    private IsometricMap map;
    
    private MovementCostAnalyzer movementAnalyzer;
    private AttackCostAnalyzer attackAnalyzer;
    
    /**
     * 
     */
    public MetaHud(Game game) {
        this.game = game;
        
        
        this.movementAnalyzer = new MovementCostAnalyzer(game);
        this.attackAnalyzer = new AttackCostAnalyzer(game);
    
        
        this.cursor = game.getCursor();
        this.world = game.getWorld();
        this.map = game.getMap();
    }

    @Override
    public void update(TimeStep timeStep) {
        this.movementAnalyzer.update(timeStep);
        this.attackAnalyzer.update(timeStep);
    }
        
    public void renderUnderEntities(Canvas canvas, Camera camera, float alpha) {    
        drawMouseHover(canvas, camera, alpha);
        
        Entity selectedEntity = game.getSelectedEntity();
        if(selectedEntity != null) {
            Vector2f tilePos = selectedEntity.getTilePos();
            Vector2f cameraPos = camera.getRenderPosition(alpha);
            
            Rectangle tileBounds = selectedEntity.getTileBounds();
            
            MapTile tile = map.getTile(0, (int)tilePos.x, (int)tilePos.y);
            if(tile!=null) {
                map.renderIsoRect(canvas, 
                                  tile.getIsoX()-cameraPos.x, 
                                  tile.getIsoY()-cameraPos.y,
                                  /*tile.getWidth(), 
                                  tile.getHeight(),*/
                                  tileBounds.getWidth(),
                                  tileBounds.getHeight(),
                                  0xab34baff);
            }        
            
            drawAttackRange(canvas, selectedEntity, cameraPos);
            //drawMovementRange(canvas, selectedEntity, cameraPos);            
        }
    }
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        int textColor = 0xff00ff00;
        canvas.setFont("Consola", 12);
        
        canvas.drawString( (int)camera.getPosition().x +"," + (int)camera.getPosition().y , canvas.getWidth() - 100, 20, textColor);
        
        Entity selectedEntity = game.getSelectedEntity();
        if(selectedEntity != null) {                        
            float sx = 20;
            float sy = 20;
            RenderFont.drawShadedString(canvas, "Selected: " + selectedEntity.getName(), sx, sy, textColor);
            renderEntityAttributes(canvas, selectedEntity, sx, sy + 15, textColor);
        }
                
        Entity hoveredOverEntity = game.getEntityOverMouse();
        if(hoveredOverEntity != null) {
            Vector2f pos = cursor.getCursorPos();
            RenderFont.drawShadedString(canvas, "" + hoveredOverEntity.getName(), pos.x + 30, pos.y, textColor);
            renderEntityAttributes(canvas, hoveredOverEntity, pos.x + 30, pos.y + 15, textColor);
        }
                
        Vector2f pos = cursor.getCenterPos();
        pos = world.screenRelativeToCamera(pos.x, pos.y);        
        MapTile tile = game.getTileOverMouse();
        
        if(Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
            canvas.drawString( "Screen: " + (int)cursor.getCenterPos().x+","+ (int)cursor.getCenterPos().y, cursor.getX()-50, cursor.getY()+40, 0xffffffff);
            canvas.drawString( "World:  " + (int)pos.x+","+ (int)pos.y, cursor.getX()-50, cursor.getY()+60, 0xffffffff);
            if(tile!=null) {
                canvas.drawString( "TilePos: " + tile.getX()+","+ tile.getY(), cursor.getX()-50, cursor.getY()+80, 0xffffffff);
                canvas.drawString( "TileIndex: " + tile.getXIndex()+","+ tile.getYIndex(), cursor.getX()-50, cursor.getY()+100, 0xffffffff);
            }
        }
        
        if(game.hasSelectedEntity()) {
        
            if(this.attackAnalyzer.isValidAttack()) {
                RenderFont.drawShadedString(canvas, "A: " + this.attackAnalyzer.getCost(), cursor.getX(), cursor.getY() + 25, 0xffffffff);
            }
            
            if(this.movementAnalyzer.isMovementAllowed()) {
                RenderFont.drawShadedString(canvas, "M: " + this.movementAnalyzer.getCost(), cursor.getX(), cursor.getY() + 25, 0xffffffff);
            }
        }
        
        RenderFont.drawShadedString(canvas, "Current Players Turn: " + game.getCurrentTurn().getActivePlayer().getName(), 10, canvas.getHeight() - 20, textColor);
    }
    
    private void renderEntityAttributes(Canvas canvas, Entity entity, float x, float y, int textColor) {
//        LeoMap attributes = entity.getAttributes();
//        if(!attributes.isEmpty()) {
//            //RenderFont.drawShadedString(canvas, "Attributes: ", x, y, textColor);
//            
//            for(int i = 0; i < attributes.bucketLength(); i++) {
//                LeoObject key = attributes.getKey(i);
//                if(key!=null) {
//                    LeoObject value = attributes.getValue(i);
//                    RenderFont.drawShadedString(canvas, key + ": " + value, x, y, textColor);
//                    y+=15;
//                }
//            }
//        }
                
        float healthPer = ((float)entity.getHealth() / (float)entity.getMaxHealth()) * 100f;
        RenderFont.drawShadedString(canvas, "Health: " + (int) healthPer + "%" , x, y, textColor);
        RenderFont.drawShadedString(canvas, "Defense: " + entity.calculateDefenseScore(), x, y+15, textColor);
        RenderFont.drawShadedString(canvas, "Attack Base Cost: " + entity.attackBaseCost(), x, y+30, textColor);
        RenderFont.drawShadedString(canvas, "Movement Base Cost: " + entity.movementBaseCost(), x, y+45, textColor);
        RenderFont.drawShadedString(canvas, "ActionPoints: " + entity.getMeter().remaining(), x, y+60, textColor);
    }
    
    private void drawMouseHover(Canvas canvas, Camera camera, float alpha) {
        Vector2f pos = cursor.getCenterPos();
        pos = world.screenRelativeToCamera(pos.x, pos.y);
        
        IsometricMap map = world.getMap();
        MapTile tile = map.getWorldTile(0, pos.x, pos.y);
        
        //this.cells.forEach(cell -> cell.render(canvas, camera, alpha));
        //this.cells.get(0).render(canvas, camera, alpha);
        
        
        
        if(tile != null) {                        
            if(attackAnalyzer.isValidAttack()) {
                cursor.setCursorImg(Art.attackCursorImg);
            }
            else {
                cursor.setCursorImg(Art.normalCursorImg);
            }
            
            int color = 0xffffffff;
            Entity selectedEntity = game.getSelectedEntity();
            if(selectedEntity != null) {
                if(movementAnalyzer.isMovementAllowed()) {
                    color = 0xff00ff00;
                }
                else {
                    color = 0xffff0000;
                }
            }
            
            Vector2f cameraPos = camera.getRenderPosition(alpha);
            map.renderIsoRect(canvas, tile.getIsoX()-cameraPos.x, tile.getIsoY()-cameraPos.y, tile.getWidth(), tile.getHeight(), color);
        }
    }
    
    
    private void drawAttackRange(Canvas canvas, Entity selectedEntity, Vector2f cameraPos) {
        
        int remainingPoints = selectedEntity.remainingActionPoints();
        int attackCost = selectedEntity.attackBaseCost();
        if(remainingPoints >= attackCost) {
            Vector2f tilePos = selectedEntity.getTilePos();
            int tileX = (int)tilePos.x;
            int tileY = (int)tilePos.y;
            
            IsometricMap map = game.getMap();
            
            for(Direction dir : Direction.values) {
                for(int i = 1; i <= selectedEntity.attackRange(); i++) {
                    int deltaX = tileX + (dir.getX() * i);
                    int deltaY = tileY + (dir.getY() * i);
                    if(!map.checkTileBounds(deltaX, deltaY)) {
                        MapTile tile = map.getTile(0, deltaX, deltaY);    
                        if(tile!=null) {
                            map.renderIsoRect(canvas, tile.getIsoX()-cameraPos.x, tile.getIsoY()-cameraPos.y, tile.getWidth(), tile.getHeight(), 0x4fFF0000);
                        }
                    }
                }
            }
        }
        
    }
    
//    private void drawMovementRange(Canvas canvas, Entity selectedEntity, Vector2f cameraPos) {
//        int remainingPoints = selectedEntity.getRemainingActionPoints() / Math.max(1, selectedEntity.movementCost());                        
//        if(remainingPoints > 0) {
//            Vector2f tilePos = selectedEntity.getTilePos();
//            int tileX = (int)tilePos.x;
//            int tileY = (int)tilePos.y;
//            
//            IsometricMap map = game.getMap();
//            
//            for(Direction dir : Direction.values) {
//                for(int i = 1; i <= remainingPoints; i++) {
//                    int deltaX = tileX + (dir.getX() * i);
//                    int deltaY = tileY + (dir.getY() * i);
//                    if(!map.checkTileBounds(deltaX, deltaY)) {
//                        MapTile tile = map.getTile(0, deltaX, deltaY);                            
//                        map.renderIsoRect(canvas, tile.getIsoX()-cameraPos.x, tile.getIsoY()-cameraPos.y, tile.getWidth(), tile.getHeight(), 0x4f3a9aFF);
//                    }
//                }
//            }
//        }
//    }
}

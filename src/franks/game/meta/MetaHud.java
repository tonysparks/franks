/*
 * see license.txt 
 */
package franks.game.meta;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.FranksGame;
import franks.game.AttackCostAnalyzer;
import franks.game.Game;
import franks.game.MovementCostAnalyzer;
import franks.game.Resources;
import franks.game.World;
import franks.game.actions.Action;
import franks.game.actions.ActionType;
import franks.game.entity.Direction;
import franks.game.entity.Entity;
import franks.game.entity.EntityList;
import franks.game.entity.EntityModel;
import franks.game.events.EntitySelectedEvent;
import franks.game.events.EntitySelectedListener;
import franks.game.events.EntityUnselectedEvent;
import franks.game.events.EntityUnselectedListener;
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
import franks.ui.Button;
import franks.ui.Panel;
import franks.ui.events.ButtonEvent;
import franks.ui.events.OnButtonClickedListener;
import franks.ui.view.ButtonView;
import franks.ui.view.ImageView;
import franks.ui.view.PanelView;
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
    
    private Panel entityPanel;
    private Panel uiPanel;
    private PanelView uiPanelView;
    
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
        
        game.registerEventListener(EntityUnselectedEvent.class, new EntityUnselectedListener() {
            
            @Override
            public void onUnselected(EntityUnselectedEvent event) {
                destroyEntityPanel();
            }
        });
        
        game.registerEventListener(EntitySelectedEvent.class, new EntitySelectedListener() {
            
            @Override
            public void onSelected(EntitySelectedEvent event) {
                createEntityPanel(event.getSelectedEntity());
            }
        });
        
        createUI();
    }
    
    private void createUI() {
        this.uiPanel = new Panel();
        this.uiPanel.setBounds(new Rectangle(FranksGame.DEFAULT_MINIMIZED_SCREEN_WIDTH, 300));
        this.uiPanel.setBackgroundColor(0xffa9a9a9);
        
        this.uiPanelView = new PanelView();
        //this.uiPanelView.addElement(element);
        
    }

    private void destroyEntityPanel() {
        if(this.entityPanel != null) {
            this.entityPanel.destroy();
        }
        
        this.uiPanelView.clear();
    }
    
    private void createEntityPanel(Entity entity) {
        destroyEntityPanel();
        
        this.entityPanel = new Panel();
        
        Vector2f uiPos = new Vector2f(10, 600);
        
        EntityModel model = entity.getModel();
        TextureRegion tex = model.getHudDisplay();
        if(tex != null) {
            Sprite sprite = new Sprite(tex);
            sprite.setPosition(uiPos.x, uiPos.y);
            this.uiPanelView.addElement(new ImageView(sprite));
        }
        
        uiPos.x += 100;
        
        List<Action> actions = entity.getAvailableActions();
        actions.sort( (a,b) -> a.getDisplayName().compareTo(b.getDisplayName()));
        
        for(Action action : actions) {
            setupButton(uiPos, action);
            uiPos.x += 120;
        }
        
        
    }
    
    private Button setupButton(Vector2f pos, Action action) {
        Button btn = new Button();
        btn.setText(action.getDisplayName());
        btn.setBounds(new Rectangle(pos, 100, 30));
        btn.setTextSize(12);
        btn.setHoverTextSize(14);
        btn.setBackgroundColor(0xffcacaca);
        btn.setBorder(true);
        btn.setBorderColor(0xff8a8a8a);
        btn.setHoverBorderColor(0xffffffff);
        btn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                game.setActionContext(action.getType());
                if(action.getType().isCreateAction()) {
                    game.dispatchCommand(game.getActionContext());
                }
            }
        });
        
        this.entityPanel.addWidget(btn);
        this.uiPanelView.addElement(new ButtonView(btn));
        
        return btn;        
    }
    
    private void setCursorIcon() {
        ActionType actionContext = game.getActionContext();
        switch(actionContext) {
            case Attack:
                cursor.setCursorImg(Art.attackCursorImg);
                break;                            
            case Die:
                // TODO
            case Move:
            default:
                if(actionContext.isBuildAction()) {
                    cursor.setCursorImg(Art.buildCursorImg);
                }
                else {
                    cursor.setCursorImg(Art.normalCursorImg);
                }
                break;        
        }
    }
    
    @Override
    public void update(TimeStep timeStep) {
        this.movementAnalyzer.update(timeStep);
        this.attackAnalyzer.update(timeStep);
        
        this.uiPanelView.update(timeStep);
        
        setCursorIcon();
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
        renderPanel(canvas);
        
        int textColor = 0xff00ff00;
        canvas.setFont("Consola", 12);
        
        canvas.drawString( (int)camera.getPosition().x +"," + (int)camera.getPosition().y , canvas.getWidth() - 100, 20, textColor);
        
        Entity selectedEntity = game.getSelectedEntity();
        if(selectedEntity != null) {                        
            float sx = 20;
            float sy = 20;
            RenderFont.drawShadedString(canvas, "Selected: " + selectedEntity.getName(), sx, sy, textColor);
            renderEntityAttributes(canvas, selectedEntity, sx, sy + 15, textColor);
            renderSelectedEntity(canvas, selectedEntity, textColor);
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
        
        this.uiPanelView.render(canvas, camera, alpha);
        
        RenderFont.drawShadedString(canvas, "Current Players Turn: " + game.getCurrentTurn().getActivePlayer().getName(), 10, canvas.getHeight() - 20, textColor);
    }
    
    private void renderEntityAttributes(Canvas canvas, Entity entity, float x, float y, int textColor) {
        float healthPer = ((float)entity.getHealth() / (float)entity.getMaxHealth()) * 100f;
        RenderFont.drawShadedString(canvas, "Health: " + (int) healthPer + "%" , x, y, textColor);
        RenderFont.drawShadedString(canvas, "Defense: " + entity.calculateDefenseScore(), x, y+15, textColor);
        RenderFont.drawShadedString(canvas, "Attack Base Cost: " + entity.attackBaseCost(), x, y+30, textColor);
        RenderFont.drawShadedString(canvas, "Movement Base Cost: " + entity.movementBaseCost(), x, y+45, textColor);
        RenderFont.drawShadedString(canvas, "ActionPoints: " + entity.getMeter().remaining(), x, y+60, textColor);
        
    }
    
    
    private void renderSelectedEntity(Canvas canvas, Entity entity, int textColor) {

        Resources resources = entity.getResources();
        if(resources != null) {
            RenderFont.drawShadedString(canvas, "Gold: " + resources.getGold(), 120, 570, textColor);
            RenderFont.drawShadedString(canvas, "Food: " + resources.getFood(), 270, 570, textColor);
            RenderFont.drawShadedString(canvas, "Material: " + resources.getMaterial(), 420, 570, textColor);
        }
        
        EntityList entities = entity.getHeldEntities();
        if(entities!=null) {
            int x = 110;
            int y = 650;
            for(Entity ent : entities) {
                TextureRegion tex = ent.getModel().getHudDisplay();
                canvas.fillRect(x, y, 32, 32, 0xff000000);
                canvas.drawRect(x, y, 32, 32, 0xffffffff);
                
                if(tex != null) {
                    canvas.drawScaledImage(tex, x+1, y+1, 30, 30, null);
                }
                x += 40;
            }
        }
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
                //cursor.setCursorImg(Art.normalCursorImg);
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
    
    private void renderPanel(Canvas canvas) {
        final int height = 220;
        canvas.fillRect(0, FranksGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT-height, FranksGame.DEFAULT_MINIMIZED_SCREEN_WIDTH, height, 0xff5F6A6A);
        canvas.drawRect(1, FranksGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT-height, FranksGame.DEFAULT_MINIMIZED_SCREEN_WIDTH-1, height-1, 0xffffffff);
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

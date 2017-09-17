/*
 * see license.txt 
 */
package franks.game.entity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import franks.game.Army;
import franks.game.Game;
import franks.game.Ids;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Renderable;
import franks.map.MapTile;
import franks.math.Rectangle;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class EntityList implements Renderable, Iterable<Entity> {

    public static final int MAX_ENTITIES = 256;
    
    
    private Comparator<Entity> renderOrder = new Comparator<Entity>() {
        
        @Override
        public int compare(Entity left, Entity right) {
            if(left==null) {
                if(right==null) {
                    return 0;
                }
            
                return 1;
            }
            else if(right==null) {
                return -1;
            }
            
            return left.getZOrder() - right.getZOrder();
        }
    };
    
    
    private Ids ids;
    private Entity[] entities;
    private Entity[] renderEntities;
    
    /**
     * 
     */
    public EntityList(Ids ids) {
        this.ids = ids;
        this.entities = new Entity[MAX_ENTITIES];
        this.renderEntities = new Entity[MAX_ENTITIES];
    }

    private int getNextId() {
        return ids.getNextId();
    }
    
    public Entity buildEntity(Game game, Army army, EntityData data) {
        return buildEntity(getNextId(), game, army, data);
    }
    
    public Entity buildEntity(int id, Game game, Army army, EntityData data) {
        Entity  ent = new Entity(id, game, army, data);
        addEntity(ent);
        return ent;
    }
    
    public void addEntity(Entity entity) {
        this.entities[entity.getId()] = entity;
    }
    
    public void removeEntity(Entity entity) {
        this.entities[entity.getId()] = null;
    }
    
    public Entity getEntity(int id) {
        if(ids.validId(id)) {            
            return this.entities[id];
        }
        
        return null;
    }
    
    /**
     * Gets an {@link Entity} by index, not by ID
     * 
     * @param index
     * @return the {@link Entity}
     */
    public Entity get(int index) {
        for(int i = 0; i < entities.length; i++) {
            Entity ent = entities[i]; 
            if(ent != null && ids.validId(i)) {
                index--;
                if(index<0) {
                    return getEntity(i);
                }
            }
        }
        return null;
    }

    public void addAll(EntityList entities) {
        for(Entity ent : entities) {
            addEntity(ent);
        }
    }

    
    public void addAll(List<Entity> entities) {
        for(Entity ent : entities) {
            addEntity(ent);
        }
    }
    
    public Entity getEntityOnTile(MapTile tile) {
        return getEntityByBounds(tile.getBounds());
    }
    
    public Entity getEntityByBounds(Rectangle bounds) {
        
        for(int i = 0; i < entities.length; i++) {
            Entity ent = entities[i]; 
            if(ent != null && ids.validId(i)) {
                if(bounds.intersects(ent.getBounds()) ||
                   bounds.contains(ent.getCenterPos())) {
                    return ent;
                }
            }
        }
        
        return null;
    }
    
    public void endTurn() {
        for(int i = 0; i < entities.length; i++) {
            Entity ent = entities[i]; 
            if(ent != null && ids.validId(i)) {
                ent.endTurn();
            }
        }
    }
    
    public void clear() {
        for(int i = 0; i < entities.length; i++) {            
            entities[i] = null;
        }
    }
    
    public int size() {
        int size = 0;
        for(int i = 0; i < entities.length; i++) {
            if(entities[i] != null && ids.validId(i)) size++;
        }
        return size;
    }
    
    public void removeDead() {
        for(int i = 0; i < entities.length; i++) {
            if(entities[i] != null) {
                if(entities[i].isDeleted()) {
                    entities[i] = null;
                    ids.reclaimId(i);
                }
            }
        }
        
    }
    
    
    /**
     * There are no more pending/executing commands
     * 
     * @return true if no more commands are being executed
     */
    public boolean commandsCompleted() {        
        for(int i = 0; i < entities.length; i++) {
            Entity ent = entities[i]; 
            if(ent != null && ids.validId(i)) {
                if(!ent.isCommandQueueEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public Iterator<Entity> iterator() {    
        return new Iterator<Entity>() {
            int index = 0;
            
            @Override
            public boolean hasNext() {
                for(int i = index; i < entities.length; i++) {
                    Entity ent = entities[i];
                    if(ent != null && ids.validId(i)) {
                        return true;
                    }
                }
                
                return false;
            }
            
            @Override
            public Entity next() {
                for(; index < entities.length;) {
                    Entity ent = entities[index];
                    index++;
                    if(ent != null && ids.validId(index-1)) {                        
                        return ent;
                    }
                }
                                
                throw new NoSuchElementException();
            }
        };
    }
    
    @Override
    public void update(TimeStep timeStep) {
        
        for(int i = 0; i < entities.length; i++) {
            Entity ent = entities[i]; 
            if(ent != null) {
                if(ids.validId(i)) {
                    ent.update(timeStep);
                }
                
                if(ent.isDeleted()) {
                    ent.getTeam().removeMember(ent);
                    
                    ids.reclaimId(i);
                    entities[i] = null;
                    
                }
            }
        }
        
        for(int i = 0; i < entities.length; i++) {
            renderEntities[i] = entities[i];
        }
        Arrays.sort(renderEntities, renderOrder);
    }
    
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        for(int i = 0; i < renderEntities.length; i++) {
            Entity ent = renderEntities[i]; 
            if(ent != null) {
                ent.render(canvas, camera, alpha);
            }
        }                
    }
}

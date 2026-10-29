package inf112.skeleton.view.sprites;

import java.awt.image.BufferedImage;

import inf112.skeleton.model.entities.NPC;

/**
 * Handles sprite rendering for NPCs.
 * This is the view implementation of sprite handling.
 */
public class NPCSprite extends EntitySprite {
    private final NPC npc;
    
    public NPCSprite(NPC npc) {
        super(npc, npc.getSkin());
        this.npc = npc;
    }
    
    /**
     * Gets the current sprite.
     * @return the current sprite
     */
    @Override
    public BufferedImage getCurrentSprite() {
        String direction = npc.getDirection();        
        if ("waiting_at_counter".equals(direction)) {
            // When NPC is waiting at the counter, always use down1 sprite to stand still
            return getDownSprite(1);
        }
        
        return super.getCurrentSprite();
    }

    /**
     * Checks if the NPC is visible.
     * @return true if the NPC is visible, false otherwise
     */
    @Override
    public boolean isVisible() {
        return npc.isVisible();
    }
} 
package com.saqfish.spdnet.ui;

import com.saqfish.spdnet.actors.Char;
import com.saqfish.spdnet.net.actor.Player;
import com.saqfish.spdnet.scenes.GameScene;
import com.saqfish.spdnet.scenes.PixelScene;
import com.saqfish.spdnet.sprites.CharSprite;
import com.saqfish.spdnet.utils.GLog;

public class PlayerHealthIndicator extends HealthBar {

    private static final int HEIGHT = 1;
    private RenderedTextBlock PlayerTitle;
    private Char target;

    public PlayerHealthIndicator( Char c ){
        target = c;
        GameScene.add(this);
    }



    @Override
    protected void createChildren() {
        super.createChildren();
        height = HEIGHT;

        String nick;
        if(target() instanceof Player){
            nick = ((Player) target()).nick();
            PlayerTitle = PixelScene.renderTextBlock(nick, 7);
            GLog.n(nick);
            add(PlayerTitle);
        }else {
            PlayerTitle = null;
        }
    }

    @Override
    public void update() {
        super.update();
            CharSprite sprite = target.sprite;
            width = sprite.width()*(4/6f);
            x = sprite.x + sprite.width()/6f;
            y = sprite.y - 2;
            level( target );
            visible = true;
            if(PlayerTitle!=null){
                PlayerTitle.setPos(sprite.x-10,sprite.y-10);
            }
    }

    public void target( Char ch ) {
        if (ch != null && ch.isAlive()) {
            target = ch;
        } else {
            target = null;
        }
    }

    public Char target() {
        return target;
    }
}


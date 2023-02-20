package com.saqfish.spdnet.ui;

import com.saqfish.spdnet.actors.Char;
import com.saqfish.spdnet.scenes.GameScene;
import com.saqfish.spdnet.sprites.CharSprite;

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

        //TODO 如何遍历玩家名字？
//        Player player=Player.getPlayer2();
//        PlayerTitle = PixelScene.renderTextBlock(player.nick, 7);
//        add(PlayerTitle);
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
//            PlayerTitle.setPos(sprite.x-10,sprite.y-10);
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


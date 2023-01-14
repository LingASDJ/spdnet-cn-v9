/*
 * Pixel Dungeon
 * Copyright (C) 2021 saqfish
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.saqfish.spdnet.net.windows;

import static com.saqfish.spdnet.ShatteredPixelDungeon.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saqfish.spdnet.actors.hero.HeroClass;
import com.saqfish.spdnet.messages.Messages;
import com.saqfish.spdnet.net.events.Receive;
import com.saqfish.spdnet.scenes.PixelScene;
import com.saqfish.spdnet.ui.Button;
import com.saqfish.spdnet.ui.RenderedTextBlock;
import com.saqfish.spdnet.ui.ScrollPane;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class WndNetRanking extends NetWindow {

    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 144;
    private static final int HEIGHT	= 120;

    private static final int VGAP = 5;

    private static final int SMALL_HGAP = 2;

    public WndNetRanking(JSONObject recordsData) {
        super(PixelScene.landscape() ? WIDTH_L : WIDTH_P, HEIGHT);

        float y = 2;

        RenderedTextBlock winsLbl = PixelScene.renderTextBlock(Messages.get(WndNetRanking.class,"wins"), 9);
        add(winsLbl);
        winsLbl.setPos(width-winsLbl.width()-VGAP, y);

        RenderedTextBlock nickLbl = PixelScene.renderTextBlock(Messages.get(WndNetRanking.class,"players"), 9);
        add(nickLbl);
        nickLbl.setPos(VGAP, y);

        ColorBlock sep = new ColorBlock(1, 1, 0xFF000000);
        sep.size(width-VGAP*2, 1);
        sep.x = VGAP;
        sep.y = nickLbl.bottom()+SMALL_HGAP;
        add(sep);

        y+=sep.y+SMALL_HGAP;

        ScrollPane list = new ScrollPane( new Component() );
        add( list );

        Component content = list.content();
        content.clear();

        list.scrollTo( 0, 0 );

        float ypos = 0;

        ArrayList<Receive.Record> records = new ArrayList<>();


        Iterator iteratorObj = recordsData.keys();
        while (iteratorObj.hasNext())
        {

            String nick = (String)iteratorObj.next();
            try {
                Receive.Record player = new Receive.Record();
                player.nick = nick;

                JSONObject playerData = recordsData.getJSONObject(nick);

                int wins = playerData.getInt("wins");
                player.wins = wins;

                JSONObject itemsData = playerData.getJSONObject("items");

                Receive.NetItems items = net().mapper().readValue(itemsData.toString(), Receive.NetItems.class);
                player.items = items;

                int playerClass = playerData.getInt("playerClass");
                player.playerClass = playerClass;

                int depth = playerData.getInt("depth");
                player.depth = depth;

                records.add(player);
            } catch (JSONException | JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(records, new Comparator<Receive.Record>() {
            @Override
            public int compare(Receive.Record record, Receive.Record t1) {
                if (record.wins > t1.wins)
                    return -1;
                else if (record.wins < t1.wins)
                    return 1;
                return 0;
            }
        });

        for(Receive.Record player: records){
            float xpos = VGAP;
            PlayerRank playerRank = new PlayerRank(player){
                @Override
                protected void onClick() {
                   //
                }
            };

            float kpos = 110;
            PlayerRank playerRankX = new PlayerRank(player){
                @Override
                protected void onClick() {
                    if(player.depth != null)
                        runWindow(new WndInfoPlayer(player));
                }
            };

            //TODO 必须套一个更好的盒子 这是临时解决方案
            playerRank.setRect( xpos, ypos, width, 12 );
            playerRankX.setRect( kpos, ypos, width, 12 );
            content.add( playerRankX );

            ypos=playerRankX.bottom()+2;
        }

        content.setRect(0,y, width, ypos );
        list.setRect( 0, y, width,height-20);
    }

    public static class PlayerRank extends Button {
        private RenderedTextBlock wins;
        private RenderedTextBlock label;

        private boolean enabled;


        public PlayerRank(Receive.Record player){
            this.enabled = player.depth != null;

            wins = PixelScene.renderTextBlock(enabled ? String.valueOf(player.wins): "-", 8);
            add(wins);

            label = PixelScene.renderTextBlock(player.nick, 8);
            add(label);
        }

        @Override
        protected void layout() {
            super.layout();
            wins.setPos(width-wins.width()-VGAP, y);
            label.setPos(VGAP, y);
            wins.alpha( enabled ? 1.0f : 0.3f );
            label.alpha( enabled ? 1.0f : 0.3f );
        }

    }

    public static HeroClass playerClassToHeroClass(int playerClass){
        switch (playerClass){
            case 0: default:
                return HeroClass.WARRIOR;
            case 1:
                return HeroClass.MAGE;
            case 2:
                return HeroClass.ROGUE;
            case 3:
                return HeroClass.HUNTRESS;
        }
    }

    protected boolean enabled( int index ){
        return true;
    }

    protected void onSelect( int index ) {}
}

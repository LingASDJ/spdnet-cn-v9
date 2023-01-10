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

import com.saqfish.spdnet.messages.Messages;
import com.saqfish.spdnet.net.Settings;
import com.saqfish.spdnet.net.ui.BlueButton;
import com.saqfish.spdnet.net.ui.NetIcons;
import com.saqfish.spdnet.scenes.PixelScene;
import com.saqfish.spdnet.ui.RenderedTextBlock;
import com.saqfish.spdnet.windows.IconTitle;

public class WndServerInfo extends NetWindow {
    private static final int WIDTH_P	    = 122;
    private static final int WIDTH_L	    = 223;
    private static final int BTN_HEIGHT	    = 18;

    private static final float GAP          = 2;

    IconTitle title;
    RenderedTextBlock host;
    RenderedTextBlock status;
    BlueButton keyBtn;
    BlueButton connectBtn;

    WndServerInfo self = this;

    int zoom = PixelScene.defaultZoom;

    public WndServerInfo() {
        super();

        int height, y = 0;

        int maxWidth = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        title = new IconTitle(NetIcons.get(NetIcons.GLOBE), Messages.get(this,"server_connection"));
        title.setRect(0, 0, maxWidth, 20);
        add(title);

        float bottom = y;
        bottom = title.bottom();

        host = PixelScene.renderTextBlock(Messages.get(this,"host") + Settings.uri(), 9);
        host.maxWidth(maxWidth);
        host.setPos(0, bottom + GAP);
        add(host);

        bottom = host.bottom() + GAP;

        status = new RenderedTextBlock(net().connected() ? Messages.get(this,"connected") : Messages.get(this,"disconnected"), 9*zoom){
            @Override
            public synchronized void update() {
                super.update();
                text(net().connected() ? Messages.get(WndServerInfo.class, "connected") : Messages.get(WndServerInfo.class, "disconnected"));
                hardlight(net().connected() ? 0x00FF00 : 0xFF0000);
            }
        };

        status.zoom(1/(float)zoom);
        status.setRect(0, bottom + GAP, maxWidth, 20);
        add(status);

        bottom = status.bottom() + (GAP*3);

        keyBtn = new BlueButton(Messages.get(this,"set_key")) {
            @Override
            protected void onClick() {
                NetWindow.showKeyInput();
            }
        };
        add(keyBtn);
        keyBtn.setSize(maxWidth/2, BTN_HEIGHT);
        keyBtn.setPos(0, bottom);

        float finalBottom = bottom;
        connectBtn = new BlueButton(Messages.get(WndServerInfo.class, "connect")) {
            @Override
            public synchronized void update() {
                super.update();
                text.text(net().connected() ? Messages.get(WndServerInfo.class, "disconnect") : Messages.get(WndServerInfo.class, "connect"));
                connectBtn.setRect(keyBtn.right(), finalBottom, maxWidth/2 , BTN_HEIGHT);
            }

            @Override
            protected void onClick() {
                super.onClick();
                net().toggle(self);
            }
        };
        add(connectBtn);
        connectBtn.setSize(maxWidth/2, BTN_HEIGHT);
        connectBtn.setPos(keyBtn.right(), bottom);

        height = (int) (connectBtn.bottom() + GAP/2);

        resize(maxWidth, height);
    }
}
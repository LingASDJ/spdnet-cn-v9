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

package com.saqfish.spdnet.net.ui;

import static com.saqfish.spdnet.scenes.PixelScene.landscape;

import com.saqfish.spdnet.Chrome;
import com.saqfish.spdnet.ShatteredPixelDungeon;
import com.saqfish.spdnet.net.windows.NetWindow;
import com.saqfish.spdnet.ui.StyledButton;

public class NetBtn extends StyledButton {
    public static final int HEIGHT = 24;
    public static final int MIN_WIDTH = 30;

    private ShatteredPixelDungeon instance = ((ShatteredPixelDungeon) ShatteredPixelDungeon.instance);

    public NetBtn() {
        super(landscape()?Chrome.Type.WINDOW : Chrome.Type.GREY_BUTTON_TR, "");
        icon(NetIcons.get(NetIcons.GLOBE));
        icon.brightness(landscape()?0.4f:0.6f);
    }

    @Override
    protected void onClick() {
        super.onClick();
        NetWindow.showServerInfo();
    }

    @Override
    protected boolean onLongClick() {
        NetWindow.showKeyInput();
        return true;
    }
}

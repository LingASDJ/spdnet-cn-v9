package com.saqfish.spdnet.ui;

import com.saqfish.spdnet.ShatteredPixelDungeon;
import com.saqfish.spdnet.Statistics;
import com.saqfish.spdnet.messages.Messages;
import com.saqfish.spdnet.net.windows.NetWindow;
import com.saqfish.spdnet.windows.WndKeyBindings;

public class ReloadButton extends IconButton {

    public ReloadButton() {
        super(Icons.CHANGES.get());

        width = 20;
        height = 20;
    }

    @Override
    protected void onClick() {
        if (!Statistics.reload && !ShatteredPixelDungeon.net().connected()) {
            NetWindow.error("你尚未连接互联网，无法从远程服务器获取最新资源");
        } else if(!Statistics.reload) {
            ShatteredPixelDungeon.net().loader().downloadAllAssets();
            Statistics.reload = true;
        } else {
            NetWindow.error("素材仅能在每次完全关闭游戏后才能再次重载");
        }

    }

    @Override
    protected String hoverText() {
        return Messages.titleCase(Messages.get(WndKeyBindings.class, "reload"));
    }
}

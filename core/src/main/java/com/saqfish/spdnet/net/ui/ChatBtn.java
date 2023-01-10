package com.saqfish.spdnet.net.ui;

import static com.saqfish.spdnet.ShatteredPixelDungeon.net;

import com.saqfish.spdnet.net.windows.NetWindow;
import com.saqfish.spdnet.ui.Tag;
import com.watabou.noosa.Image;

public class ChatBtn extends Tag {

    public static final int COLOR	= 0xFF4C4C;

    private Image icon;

    public ChatBtn() {
        super( 0xFF4C4C );
        setSize( icon.width()+6, icon.height()+6 );
        flip(true);
        visible = true;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        icon = NetIcons.get(NetIcons.CHAT);
        icon.scale.set(0.72f);
        add( icon );
    }

    @Override
    protected void layout() {
        super.layout();
        icon.x = (right()-icon.width()-2)/2;
        icon.y = y+3;
    }

    @Override
    public void update() {
        super.update();
        //连接中有新消息变成黄色，反之绿色
        if(net().connected()){
            bg.hardlight(net().reciever().newMessage() ? 0xffff44: 0x44ff44);
        } else {
            //断开连接默认是红色
            bg.hardlight(0x845252);
        }
    }

    @Override
    protected void onClick() {
        if (net().connected()) {
            NetWindow.showChat();
        }else{
            //TODO 多语言
            NetWindow.error("未连接", "你必须连接后才能与其他玩家畅聊");
        }
    }
}


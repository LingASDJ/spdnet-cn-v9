package com.saqfish.spdnet.net.ui;

import static com.saqfish.spdnet.ShatteredPixelDungeon.net;

import com.saqfish.spdnet.net.windows.NetWindow;
import com.saqfish.spdnet.ui.LeftTag;
import com.watabou.noosa.Image;

public class ChatBtn extends LeftTag {

    public static final int COLOR	= 0xFF4C4C;

    private final int CHAT = 1;

    private Image icon;

    public ChatBtn() {
        super( 0xFF4C4C );
        setSize( icon.width()+6, icon.height()+6 );
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
        icon.x = 0;
        icon.y = y;
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
        setIcon(CHAT);
    }

    @Override
    protected void onClick() {
        if (net().connected()) {
            NetWindow.showChat();
        }else{
            NetWindow.error("Not connected", "You must connect before viewing players");
        }
    }

    private void setIcon(int type){
        if (type == CHAT) {
            icon.copy(NetIcons.get(NetIcons.CHAT));
            icon.scale.set(0.62f);
        }
    }
}


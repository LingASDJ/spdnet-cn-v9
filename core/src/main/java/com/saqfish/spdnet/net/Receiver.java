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

package com.saqfish.spdnet.net;

import static com.saqfish.spdnet.Dungeon.seed;
import static com.saqfish.spdnet.ShatteredPixelDungeon.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saqfish.spdnet.Dungeon;
import com.saqfish.spdnet.GamesInProgress;
import com.saqfish.spdnet.items.Item;
import com.saqfish.spdnet.messages.Messages;
import com.saqfish.spdnet.net.actor.Player;
import com.saqfish.spdnet.net.events.Events;
import com.saqfish.spdnet.net.events.Receive;
import com.saqfish.spdnet.net.windows.NetWindow;
import com.saqfish.spdnet.scenes.GameScene;
import com.saqfish.spdnet.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Reflection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class Receiver {
        private ObjectMapper mapper;
        private Net net;

        //不匹配的种子应该停止玩家的工作
        private boolean eligible;
        private boolean newMessage;
        private ArrayList<ChatMessage> messages;

        public void set( int slot ) {

                try {
                        Bundle bundle = FileUtils.bundleFromFile(GamesInProgress.gameFile(slot));
                        seed = bundle.getLong("seed");
                        eligible = net().connected() &&
                                net().seed() == seed &&
                                seed != 0;
                } catch (IOException e) {
                        // e.printStackTrace();
                }
        }

        public Receiver(Net net, ObjectMapper mapper) {
                this.net = net;
                this.mapper = mapper;
        }

        // Start all receiver events
        public void startAll() {
                Emitter.Listener onAction = args -> {
                        int type = (int) args[0];
                        String data = (String) args[1];
                        handleAction(type, data);
                };
                Emitter.Listener onTransfer = args -> {
                        String data = (String) args[0];
                        handleTransfer(data);
                };
                Emitter.Listener onChat = args -> {
                        String id = (String) args[0];
                        String nick = (String) args[1];
                        String message = (String) args[2];
                        handleChat(id, nick, message);
                };
                Emitter.Listener onInit = args -> {
                        String data = (String) args[0];
                        handleInit(data);
                };
                Emitter.Listener onLeave= args -> {
                        String nick = (String) args[0];
                        String id = (String) args[1];
                        handleLeaveJoin(true, nick);
                };
                Emitter.Listener onJoin = args -> {
                        String nick = (String) args[0];
                        String id = (String) args[1];
                        handleLeaveJoin(false, nick);
                };
                Emitter.Listener onKick = args -> {
                        String kickMsg = (String) args[0];
                        handleKick(kickMsg);
                };
                net.socket().once(Events.INIT, onInit);
                net.socket().on(Events.ACTION, onAction);
                net.socket().on(Events.TRANSFER, onTransfer);
                net.socket().on(Events.CHAT, onChat);
                net.socket().on(Events.LEAVE, onLeave);
                net.socket().on(Events.JOIN, onJoin);
                net.socket().on(Events.KICK, onKick);
                messages = new ArrayList<>();
        }

        // Stop all receiver events
        public void cancelAll() {
                net.socket().off(Events.ACTION);
                net.socket().off(Events.TRANSFER);
                net.socket().off(Events.CHAT);
                net.socket().off(Events.INIT);
                net.socket().off(Events.LEAVE);
                net.socket().off(Events.JOIN);
                net.socket().off(Events.KICK);
                messages = null;

                net.loader().clear();
        }

        // Handlers

        // Handle init
        public void handleInit(String json) {
                try {
                        Receive.Init init = mapper.readValue(json, Receive.Init.class);
                        NetWindow.init(init.motd, init.seed);
                        net.seed(init.seed);
                        DeviceCompat.log("ASSET", Long.toString(init.assetVersion));
                        if(Settings.asset_version() != init.assetVersion){
                                net.loader().downloadAllAssets();
                                Settings.asset_version(init.assetVersion);
                        }
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
        }

        // Leave/Join
        public void handleLeaveJoin(boolean isLeaving,  String nick) {
                GLog.p(nick + Messages.get(Receiver.class, "has") + (isLeaving? Messages.get(Receiver.class, "leave"):
                        Messages.get(Receiver.class, "join")));
        }

        // Action handler
        public void handleAction(int type, String json) {
                Player player;
                try {
                        switch (type) {
                                case Receive.MOVE:
                                        //种子不匹配就滚出地牢的移动逻辑，避免越界
                                        if(eligible) {
                                                Receive.Leave l = mapper.readValue(json, Receive.Leave.class);
                                                player = Player.getPlayer(l.id);
                                                if (player != null) player.leave();
                                        } else {
                                                Receive.Move m = mapper.readValue(json, Receive.Move.class);
                                                player = Player.getPlayer(m.id);
                                                if (player != null && player.sprite != null) {
                                                        if (player.sprite.parent == null) {
                                                                player.sprite.destroy();
                                                                GameScene.addSprite(player);
                                                        }
                                                        //TODO 存在未知待定问题
                                                        player.move(m.pos);
                                                        player.sprite.move(player.pos, m.pos);
                                                }
                                        }
                                        break;
                                case Receive.JOIN:
                                        Receive.Join join = mapper.readValue(json, Receive.Join.class);
                                        Player.addPlayer(join.id, join.nick, join.playerClass, join.pos, join.depth, join.items);
                                        break;
                                case Receive.JOIN_LIST:
                                        Receive.JoinList jl = mapper.readValue(json, Receive.JoinList.class);
                                        for (int i = 0; i < jl.players.length; i++) {
                                                Receive.Join j = jl.players[i];
                                                Player.addPlayer(j.id, j.nick, j.playerClass, j.pos, j.depth, j.items);
                                        }
                                        break;
                                case Receive.LEAVE:
                                        Receive.Leave l = mapper.readValue(json, Receive.Leave.class);
                                        player = Player.getPlayer(l.id);
                                        if (player != null) player.leave();
                                        break;
                                case Receive.GLOG:
                                        Receive.Glog g = mapper.readValue(json, Receive.Glog.class);
                                        GLog.n(g.msg);
                                        GLog.newLine();
                                        break;
                                default:
                                        DeviceCompat.log("Unknown Action", json);
                        }
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
        }

        // Item sharing handler Fixed 2023-1-9
        public void handleTransfer(String json) {
                Receive.Transfer i = null;
                try {

                        i = (Receive.Transfer) this.mapper.readValue(json, Receive.Transfer.class);
                        Item item = (Item) Reflection.newInstance(Reflection.forName(addPkgName(i.className)));
                        item.cursed = i.cursed;
                        item.level(i.level);
                        if (i.identified) {
                                item.identify();
                        }
                        item.doPickUp(Dungeon.hero);
                        GameScene.pickUp(item, Dungeon.hero.pos);
                        GLog.p(Messages.get(Receiver.class, "reved") + item.name());
                }catch (Exception e){
                        System.out.println("==========>"+e.getMessage());
                }
        }

        // Chat handler
        public static class ChatMessage {
                public String id;
                public String nick;
                public String message;

                public ChatMessage (String id, String nick, String message){
                        this.id = id;
                        this.nick = nick;
                        this.message = message;
                }
        }

        public void handleChat(String id,String nick,String message){
                        messages.add(new ChatMessage(id, nick, message));
                        GLog.c(nick + ": " + message);
                        newMessage = true;
        }

        public void handleKick(String kickMsg){
                //TODO 探出提示消息：你因为$kickMsg被踢出游戏
                // 返回主界面
                // 并断开连接
                net.disconnect();
        }

        public void readMessages(){
                newMessage = false;
        }

        public ArrayList<ChatMessage> messages(){
                newMessage = false;
                return messages;
        }

        public List<ChatMessage> messages(int n){
                newMessage = false;
                if(messages != null && messages.size() > n)
                        messages = new ArrayList(messages.subList(messages.size() - n, messages.size()));
                return messages;
        }

        public ChatMessage lastMessage(){
                newMessage = false;
                return messages.get(messages.size()-1);
        }

        public boolean newMessage(){
                return newMessage;
        }


        // Static helpers
        private String addPkgName(String c){
                if (c.contains(Game.pkgName)) {
                        return c;
                }else if (c.contains("items.")){
                        return Game.pkgName+".spdnet."+c;
                }
                return Game.pkgName+".spdnet.items."+c;
        }
}

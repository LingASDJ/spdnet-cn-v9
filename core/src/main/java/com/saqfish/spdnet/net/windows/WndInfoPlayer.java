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

import static com.saqfish.spdnet.Dungeon.hero;

import com.saqfish.spdnet.Dungeon;
import com.saqfish.spdnet.ShatteredPixelDungeon;
import com.saqfish.spdnet.actors.hero.Hero;
import com.saqfish.spdnet.actors.mobs.ErrorRat;
import com.saqfish.spdnet.items.Item;
import com.saqfish.spdnet.items.KindOfWeapon;
import com.saqfish.spdnet.items.KindofMisc;
import com.saqfish.spdnet.items.armor.Armor;
import com.saqfish.spdnet.items.artifacts.Artifact;
import com.saqfish.spdnet.items.error.ErrorArmorItem;
import com.saqfish.spdnet.items.error.ErrorArtifactItem;
import com.saqfish.spdnet.items.error.ErrorMiscItem;
import com.saqfish.spdnet.items.error.ErrorRingItem;
import com.saqfish.spdnet.items.error.ErrorWeaponItem;
import com.saqfish.spdnet.items.rings.Ring;
import com.saqfish.spdnet.net.actor.Player;
import com.saqfish.spdnet.net.events.Receive;
import com.saqfish.spdnet.net.ui.BlueButton;
import com.saqfish.spdnet.scenes.GameScene;
import com.saqfish.spdnet.scenes.PixelScene;
import com.saqfish.spdnet.scenes.TitleScene;
import com.saqfish.spdnet.sprites.HeroSprite;
import com.saqfish.spdnet.sprites.ItemSpriteSheet;
import com.saqfish.spdnet.ui.ItemSlot;
import com.saqfish.spdnet.ui.RenderedTextBlock;
import com.saqfish.spdnet.windows.WndBag;
import com.saqfish.spdnet.windows.WndInfoItem;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

public class WndInfoPlayer extends NetWindow {

	private final static int ITEM_HEIGHT = 23;
	private final static int ITEM_WIDTH = 23;
	private final static int VGAP = 3;
	private final static int HGAP = 4;

	private Image image;
	private RenderedTextBlock name;
	private RenderedTextBlock depth;
	private ColorBlock sep;
	private ItemsList items;

	KindOfWeapon weapon;
	Armor armor;
	Artifact artifact;
	KindofMisc misc;
	Ring ring;

	public WndInfoPlayer( Receive.Player player ) {
		layout(player.nick, player.playerClass, player.depth, player.items);
	}

	public WndInfoPlayer( Player player ) {
		layout(player.nick(), player.playerClass(), player.depth(), player.items());
	}

    public WndInfoPlayer(Receive.Record player) {
		layout(player.nick, player.playerClass, player.depth, player.items);
    }

    private void setUp(Receive.NetItems netItems){
		if(!(Game.scene() instanceof GameScene)) {
			// These are artificial values for now
			if(hero == null){
				hero = new ErrorRat();
			} else {
				hero = new Hero();
				hero.STR = 100;
				hero.HP = 0;
			}


		}

		Ring.initGems();

		//FIXED 为空我们应该干什么？返回一个默认值，而不是让用户崩溃。批注：2023-1-16 JDSALing
		if(weapon==null) weapon = new ErrorWeaponItem();
		weapon = netItems == null ? new ErrorWeaponItem() : (KindOfWeapon) instance(netItems.weapon);

		if(armor==null) armor = new ErrorArmorItem();
		armor = netItems == null ? new ErrorArmorItem() : (Armor) instance(netItems.armor);

		if(artifact==null) artifact = new ErrorArtifactItem();
		artifact = netItems == null ? new ErrorArtifactItem() : (Artifact) instance(netItems.artifact);

		if(misc==null) misc = new ErrorArtifactItem();
		misc = netItems == null ? new ErrorArtifactItem() : (KindofMisc) instance(netItems.misc);

		if(ring==null) ring = new ErrorRingItem();
		ring = netItems == null ? new ErrorRingItem() : (Ring) instance(netItems.ring);
	}

	private Item instance(Receive.NetItem ni){
	    Item a = null;
		try{

			String className = addPkgName(ni.className);
			System.out.println(className);
			Class<?> k = Reflection.forNameUnhandled(className);
			a = (Item)Reflection.newInstance(k);
			a.level(ni.level);
			a.levelKnown = true;
			a.cursed = false;

			if(a instanceof Ring){
				((Ring) a).setKnown();
			}
		} catch (Exception e) {
			//
		}
		return a;
	}




	private String addPkgName(String c){
		if (c.contains(Game.pkgName)) {
			return c;
		}else if (c.contains("items.")){
			return Game.pkgName+".spdnet."+c;
		}
		return Game.pkgName+".spdnet.items."+c;
	}
	private void layout(String nick, int playerClass, int pdepth, Receive.NetItems netItems) {
		setUp(netItems);

		int x = 0;
		int y = 0;

		image = HeroSprite.avatar(WndPlayerList.playerClassToHeroClass(playerClass), Dungeon.hero.tier());
		add( image );
		image.x = 0;
		image.y = 0;

		name = PixelScene.renderTextBlock( nick, 9 );
		add( name );
		name.setPos(x + image.width + VGAP,
				image.height() > name.height() ? y +(image.height() - name.height()) / 2 : y);

		depth = PixelScene.renderTextBlock( String.valueOf(pdepth), 9 );
		add( depth );

		sep = new ColorBlock(1, 1, 0xFF000000);
		add(sep);
		sep.size(width, 1);
		sep.y = image.height() + HGAP;

		items = new ItemsList();
		add( items );
		items.setPos(0, sep.y+(2*HGAP));

		resize((int)items.width(), (int)items.bottom());
	}

	@Override
	public synchronized void update() {
		super.update();
		sep.size(width, 1);
		depth.setPos(width - depth.width() - (VGAP+2), HGAP);
	}

	private class ItemsList extends Component {
		ItemSlot weaponSlot;
		ItemSlot armorSlot;
		ItemSlot artifactSlot;
		ItemSlot miscSlot;
		ItemSlot ringSlot;

		//不能返回空！批注：2023-1-14 JDSALing
		public ItemsList() {
			super();

			weaponSlot = itemSlot(weapon == null ? new ErrorWeaponItem() : weapon, ItemSpriteSheet.WEAPON_HOLDER);
			armorSlot = itemSlot(armor == null ? new ErrorArmorItem() : armor, ItemSpriteSheet.ARMOR_HOLDER);
			artifactSlot = itemSlot(artifact == null ? new ErrorArtifactItem() : artifact,
					ItemSpriteSheet.ARTIFACT_HOLDER);
			miscSlot = itemSlot(misc == null ? new ErrorMiscItem() : misc, ItemSpriteSheet.SOMETHING);
			ringSlot = itemSlot(ring == null ? new ErrorRingItem() : ring, ItemSpriteSheet.RING_HOLDER);
		}




		@Override
		protected void layout() {
			super.layout();

			weaponSlot.setRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);
			add(weaponSlot);
			x += ITEM_WIDTH;

			armorSlot.setRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);
			add(armorSlot);
			x += ITEM_WIDTH;

			artifactSlot.setRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);
			add(artifactSlot);
			x += ITEM_WIDTH;

			miscSlot.setRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);
			add(miscSlot);
			x += ITEM_WIDTH;

			ringSlot.setRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);
			add(ringSlot);
			x += ITEM_WIDTH;

			width = x;
			height = ITEM_HEIGHT;

			BlueButton playBtn = new BlueButton("关闭"){
				@Override
				protected void onClick() {
					super.onClick();
					ShatteredPixelDungeon.switchNoFade(TitleScene.class);
				}
			};

			if (Game.scene().getClass() != GameScene.class) {
				playBtn.visible = true;
				playBtn.active = true;
			} else {
				playBtn.visible = false;
				playBtn.active = false;
			}
			playBtn.setRect(x - 50, y - 26, 18, 15);
			add(playBtn);
		}

		private ItemSlot itemSlot (Item item, int placeHolder){
			ItemSlot slot;
			if(item != null) {
				slot = new ItemSlot(item) {
					@Override
					protected void onClick() {
						super.onClick();
						runWindow(new WndInfoItem(item));
					}
				};
			} else{
				WndBag.Placeholder p = new WndBag.Placeholder(placeHolder);
				slot = new ItemSlot(p);
			}
			return slot;
		}
	}
}

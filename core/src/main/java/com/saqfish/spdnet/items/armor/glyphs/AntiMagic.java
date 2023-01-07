/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.saqfish.spdnet.items.armor.glyphs;

import com.saqfish.spdnet.actors.Char;
import com.saqfish.spdnet.actors.buffs.Charm;
import com.saqfish.spdnet.actors.buffs.Degrade;
import com.saqfish.spdnet.actors.buffs.Hex;
import com.saqfish.spdnet.actors.buffs.MagicalSleep;
import com.saqfish.spdnet.actors.buffs.Vulnerable;
import com.saqfish.spdnet.actors.buffs.Weakness;
import com.saqfish.spdnet.actors.hero.abilities.mage.WarpBeacon;
import com.saqfish.spdnet.actors.mobs.DM100;
import com.saqfish.spdnet.actors.mobs.Eye;
import com.saqfish.spdnet.actors.mobs.Shaman;
import com.saqfish.spdnet.actors.mobs.Warlock;
import com.saqfish.spdnet.actors.mobs.YogFist;
import com.saqfish.spdnet.items.armor.Armor;
import com.saqfish.spdnet.items.bombs.Bomb;
import com.saqfish.spdnet.items.rings.RingOfArcana;
import com.saqfish.spdnet.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.saqfish.spdnet.items.wands.CursedWand;
import com.saqfish.spdnet.items.wands.WandOfBlastWave;
import com.saqfish.spdnet.items.wands.WandOfDisintegration;
import com.saqfish.spdnet.items.wands.WandOfFireblast;
import com.saqfish.spdnet.items.wands.WandOfFrost;
import com.saqfish.spdnet.items.wands.WandOfLightning;
import com.saqfish.spdnet.items.wands.WandOfLivingEarth;
import com.saqfish.spdnet.items.wands.WandOfMagicMissile;
import com.saqfish.spdnet.items.wands.WandOfPrismaticLight;
import com.saqfish.spdnet.items.wands.WandOfTransfusion;
import com.saqfish.spdnet.items.wands.WandOfWarding;
import com.saqfish.spdnet.items.weapon.missiles.ForceCube;
import com.saqfish.spdnet.levels.traps.DisintegrationTrap;
import com.saqfish.spdnet.levels.traps.GrimTrap;
import com.saqfish.spdnet.sprites.ItemSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class AntiMagic extends Armor.Glyph {

	private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing( 0x88EEFF );
	
	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( MagicalSleep.class );
		RESISTS.add( Charm.class );
		RESISTS.add( Weakness.class );
		RESISTS.add( Vulnerable.class );
		RESISTS.add( Hex.class );
		RESISTS.add( Degrade.class );
		
		RESISTS.add( DisintegrationTrap.class );
		RESISTS.add( GrimTrap.class );

		RESISTS.add( Bomb.MagicalBomb.class );
		RESISTS.add( ScrollOfPsionicBlast.class );

		RESISTS.add( CursedWand.class );
		RESISTS.add( WandOfBlastWave.class );
		RESISTS.add( WandOfDisintegration.class );
		RESISTS.add( WandOfFireblast.class );
		RESISTS.add( WandOfFrost.class );
		RESISTS.add( WandOfLightning.class );
		RESISTS.add( WandOfLivingEarth.class );
		RESISTS.add( WandOfMagicMissile.class );
		RESISTS.add( WandOfPrismaticLight.class );
		RESISTS.add( WandOfTransfusion.class );
		RESISTS.add( WandOfWarding.Ward.class );

		RESISTS.add( WarpBeacon.class );
		
		RESISTS.add( DM100.LightningBolt.class );
		RESISTS.add( Shaman.EarthenBolt.class );
		RESISTS.add( Warlock.DarkBolt.class );
		RESISTS.add( Eye.DeathGaze.class );
		RESISTS.add( YogFist.BrightFist.LightBeam.class );
		RESISTS.add( YogFist.DarkFist.DarkBolt.class );
	}
	
	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, see:
		// Hero.damage
		// GhostHero.damage
		// Shadowclone.damage
		// ArmoredStatue.damage
		// PrismaticImage.damage
		return damage;
	}
	
	public static int drRoll( Char ch, int level ){
		return Random.NormalIntRange(
				Math.round(level * RingOfArcana.enchantPowerMultiplier(ch)),
				Math.round((3 + (level*1.5f)) * RingOfArcana.enchantPowerMultiplier(ch)));
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return TEAL;
	}

}
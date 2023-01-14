package com.saqfish.spdnet.items.error;

import com.saqfish.spdnet.items.KindOfWeapon;
import com.saqfish.spdnet.sprites.ItemSpriteSheet;

public class ErrorWeaponItem extends KindOfWeapon {

    {
        image = ItemSpriteSheet.HALLS_PAGE;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public int energyVal() {
        return 0;
    }

    @Override
    public int min(int lvl) {
        return 0;
    }

    @Override
    public int max(int lvl) {
        return 0;
    }
}


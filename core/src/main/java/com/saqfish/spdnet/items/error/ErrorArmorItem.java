package com.saqfish.spdnet.items.error;

import com.saqfish.spdnet.items.armor.Armor;
import com.saqfish.spdnet.sprites.ItemSpriteSheet;

public class ErrorArmorItem extends Armor {

    {
        image = ItemSpriteSheet.PRISON_PAGE;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public ErrorArmorItem() {
        super(1);
    }
}


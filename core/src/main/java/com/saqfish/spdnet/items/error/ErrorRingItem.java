package com.saqfish.spdnet.items.error;

import com.saqfish.spdnet.items.rings.Ring;
import com.saqfish.spdnet.sprites.ItemSpriteSheet;

public class ErrorRingItem extends Ring {

    {
        image = ItemSpriteSheet.SEWER_PAGE;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isKnown() {
        return true;
    }
}

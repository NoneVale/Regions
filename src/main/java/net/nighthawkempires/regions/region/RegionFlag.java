package net.nighthawkempires.regions.region;

public enum RegionFlag {

    BREAK,
    BUILD,
    CROP_TRAMPLE,
    DAMAGE,
    ENDERPEARL,
    ENTER_IN_COMBAT,
    FIRE,
    FIRE_SPREAD,
    HUNGER_DEGEN,
    INTERACT_ANVIL,
    INTERACT_BUTTON,
    INTERACT_CAULDRON,
    INTERACT_CHEST,
    INTERACT_DOOR,
    INTERACT_ENDER_CHEST,
    INTERACT_LEVER,
    LECTERN_TAKE_BOOK,
    MARKET,
    //MOB_BREAK,
    //MOB_BUILD,
    MOB_DAMAGE,
    MOB_SPAWN,
    POTION_SPLASH,
    PROJECTILES,
    PVP,
    RACE_ABILITIES,
    TNT;

    public enum Result {
        ALLOW,
        DENY,
        IGNORE
    }
}

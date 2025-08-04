package net.vinh.compass.util;

@FunctionalInterface
public interface CompassDamageCalculationType {
    void applyWithCustomLogic(DamageContext ctx);
}

package io.github.jefflegendpower.knockbackoptimizer;

import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;

import java.util.Map;

public class KBUtils {

    private final KnockbackModule knockbackModule = KnockbackModule.INSTANCE;

    public void setKBSettings(
            double horizontal,
            double vertical,
            double inheritance_strength_horizontal,
            double ground_horizontal,
            double ground_vertical,
            double sprint_horizontal,
            double vertical_limit,
            double sprint_vertical,
            double attacker_slowdown
            ) {
        KnockbackProfile knockbackProfile = KnockbackModule.getDefault();
        knockbackProfile.limitesNoVerticalY.value = vertical_limit;
        knockbackProfile.horizontal.value = horizontal;
        knockbackProfile.vertical.value = vertical;
        knockbackProfile.friccaoHorizontal.value = inheritance_strength_horizontal;
//        knockbackProfile.friccaoVertical.value = inheritance_strength_vertical;
        knockbackProfile.chaoHorizontal.value = ground_horizontal;
        knockbackProfile.chaoVertical.value = ground_vertical;
        knockbackProfile.corridaHorizontal.value = sprint_horizontal;
        knockbackProfile.corridaVertical.value = sprint_vertical;
//            knockbackProfile.arcoHorizontal.value = bow_horizontal;
//            knockbackProfile.arcoVertical.value = bow_vertical;
//            knockbackProfile.canaDePescaHorizontal.value = rod_horizontal;
//            knockbackProfile.canaDePescaVertical.value = rod_vertical;
        knockbackProfile.abrandamento.value = attacker_slowdown;
//            knockbackProfile.velocidadeDeQuedaDaPocao.value = potion_fall_speed;
//            knockbackProfile.multiplicadorDeImpulsoDaPocao.value = potion_multiplier;
//            knockbackProfile.deslocamentoDaPocao.value = potion_offset;

        Map<String, KnockbackProfile> newProfiles = knockbackModule.profiles;
        newProfiles.put("default", knockbackProfile);
        knockbackModule.profiles = newProfiles;
    }
}

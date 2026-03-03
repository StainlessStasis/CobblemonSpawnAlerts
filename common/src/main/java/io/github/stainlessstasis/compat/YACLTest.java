package io.github.stainlessstasis.compat;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YACLTest {
    public static Screen createScreen(Screen parent) {
        System.out.println("CREATING SCREEN");
        return new YACLTest().createNewScreen(parent);
    }

    public YACLTest() {
        System.out.println("YACLTest CONSTRUCTOR");
    }

    private boolean myBooleanOption = false;

    public Screen createNewScreen(Screen parent) {
        System.out.println("CREATING NEW SCREEN");
        this.myBooleanOption = true;

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Used for narration. Could be used to render a title in the future."))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Name of the category"))
                        .tooltip(Component.literal("This text will appear as a tooltip when you hover or focus the button with Tab. There is no need to add \n to wrap as YACL will do it for you."))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Name of the group"))
                                .description(OptionDescription.of(Component.literal("This text will appear when you hover over the name or focus on the collapse button with Tab.")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Boolean Option"))
                                        .description(OptionDescription.of(Component.literal("This text will appear as a tooltip when you hover over the option.")))
                                        .binding(true, () -> this.myBooleanOption, newVal -> this.myBooleanOption = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .build()
            .generateScreen(parent);
    }
}

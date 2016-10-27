package me.noahp78.jemu;

import me.noahp78.jemu.emu.Z80;

/**
 * Created by noahp on 26/okt/2016 for JA-EMU
 */
public class GameboyEmulator {
    public static void main(String[] args) {
        Gameboy.init();
        Gameboy.run();
    }
}

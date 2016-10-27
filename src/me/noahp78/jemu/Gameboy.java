package me.noahp78.jemu;

import me.noahp78.jemu.emu.GPU;
import me.noahp78.jemu.emu.Memory;
import me.noahp78.jemu.emu.Z80;

import java.io.File;
import java.nio.file.Files;

/**
 * Class that represents a "gameboy" itself
 * Created by noahp on 26/okt/2016 for JA-EMU
 */
public class Gameboy {
    public static Z80 cpu;
    public static Memory memory;
    public static GPU gpu;
    public static boolean HALT_IF_UNKNOWN_OPCODE = false;
    public static void init(){
        cpu = new Z80();
        memory = new Memory();
        gpu = new GPU();
        //DEV CODE READ tetris.gb
        //this.memory.rom=
        try{

            memory.rom = (Files.readAllBytes(new File("mario.gb").toPath()));
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("READ ROM");




    }
    public static void run(){
        while(!cpu.stopped){
            cpu.exec();
        }
    }

}

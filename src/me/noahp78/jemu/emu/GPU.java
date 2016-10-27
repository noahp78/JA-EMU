package me.noahp78.jemu.emu;

import me.noahp78.jemu.Gameboy;

/**
 * Created by noahp on 27/okt/2016 for JA-EMU
 */
public class GPU {
    public int[] vram = new int[16000];
    public int[] oam = new int[16000];
    public int[] screen = new int [160*144*4];

    public int mode = 0;
    public int mode_clock = 0;
    public int line = 0;
    public int[][][] tileset= new int[384][8][8];

    public void reset(){
        for (int i = 0; i <screen.length; i++) {
            this.screen[i] = 255;
        }
        tileset = new int[384][8][8];
        for (int i = 0; i < 384; i++) {
            tileset[i] = new int[8][8];
            for (int j = 0; j < 8; j++) {
                this.tileset[i][j] = new int[]{0,0,0,0,0,0,0,0};
            }
        }
    }

    public void updatetile(int addr, int val){
        addr&=0x1FFE;
        int tile = (addr>>4) & 511;
        int y = (addr>>1)&7;
        int sx;
        for (int x = 0; x < 8; x++) {
            sx = 1 << (7-x);
            this.tileset[tile][y][x] =
                    (((this.vram[addr]&sx)==0) ? 1:0) +
                            (((this.vram[addr+1]&sx)==0) ? 2:0);

        }

    }
    public void step(){
        this.mode_clock = Gameboy.cpu.t;
        switch(this.mode){
            case 2:
                if(this.mode_clock >= 80){
                    this.mode_clock = 0;
                    this.mode=3;
                }
                break;
            case 3:
                if(this.mode_clock >=172){
                    this.mode_clock=0;
                    this.mode=0;
                    this.renderscan();
                }
                break;
            case 0:
                if(this.mode_clock>=204){
                    this.mode_clock=0;
                    this.line++;
                    if(this.line==143){
                        this.mode=1;
                        //TODO DRAW SCREEN
                    }else{
                        this.mode=2;
                    }
                }
                break;
            case 1:
                if(this.mode_clock>=456){
                    this.mode_clock=0;
                    this.line++;
                    if(this.line>153){
                        this.mode=2;
                        this.line=0;
                    }
                }
                break;
        }
    }
    public void renderscan(){

    }
}

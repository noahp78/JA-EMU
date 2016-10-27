package me.noahp78.jemu.emu;

import me.noahp78.jemu.Gameboy;

import java.lang.reflect.Field;

/**
 * Z80 CPU emulator
 * Created by noahp on 26/okt/2016 for JA-EMU
 */
public class Z80 {
    //REGISTERS (8 BITS MAX 255)
    public int a, b, c, d, e, h, l, f;
    public int bc = -32770;
    public int de = -32770;
    public int hl = -32770;
    public int nn = 0;
    public int n = 0;
    //16 Bit Register
    public int sp, pc;
    //CUSTOM / JEMU
    public boolean stopped = false;
    //CLOCK
    int m, t;

    public Z80() {
        f = 0x0;
        //Maybe also point PC to start of rom?
        this.pc = 0;
        this.sp = 0x0100;
    }

    public void exec() {
        //READ OPCODE FROM MEMORY
        int opcode = Gameboy.memory.rb(pc);
        nn = Gameboy.memory.rw(pc + 1);
        n = Gameboy.memory.rb(pc + 1);
        //We store these so we can check if any math is done on them and port it back
        int pre_bc = bc;
        int pre_de = de;
        int pre_hl = hl;
        run(opcode);
        pc++;
        if (bc != pre_bc) {
            //There was a operation on BC
            //Port BC back to B and C
            b = bc & 0xFF;
            c = bc >> 8;
        } else if (de != pre_de) {
            //There was a operation on DE
            d = de & 0xFF;
            e = de >> 8;
        } else if (hl != pre_hl) {
            //There was a operation on HL
            h = hl & 0xFF;
            l = hl >> 8;
        }
        if (pc > 1000) {
            this.stopped = true;
        }
        pc &= 0xFFFF;
    }

    public void run(int opcode) {
        System.out.print(Integer.toHexString(opcode) + " ");
        switch (opcode) {
            case 0x00:
            case 0x49:
            case 0xD3:
            case 0xDB:
            case 0xE3:
            case 0xE4:
            case 0xEB:
            case 0xEC:
            case 0xED:
            case 0xF4:
            case 0xFC:
            case 0xFD:
                this.NOP();
                return;
            case 0x87:
                this.ADD("a", "a");
                return;
            case 0x80:
                this.ADD("a", "b");
                return;
            case 0x81:
                this.ADD("a", "c");
                return;
            case 0x82:
                this.ADD("a", "d");
                return;
            case 0x83:
                this.ADD("a", "e");
                return;
            case 0x84:
                this.ADD("a", "b");
                return;
            case 0x85:
                this.ADD("a", "b");
                return;
            case 0x86:
                this.ADD("a", "hl");
                return;
            case 0xC6:
                this.ADD("a", "#");
                return;
            case 0xBF:
                this.CP("a", "a");
                return;
            case 0xB8:
                this.CP("a", "b");
                return;
            case 0xB9:
                this.CP("a", "c");
                return;
            case 0xBA:
                this.CP("a", "d");
                return;
            case 0xBB:
                this.CP("a", "e");
                return;
            case 0xBC:
                this.CP("a", "h");
                return;
            case 0xBD:
                this.CP("a", "l");
                return;
            case 0x06:
                this.LD("b", "n");
                return;
            case 0x0E:
                this.LD("c", "n");
                return;
            case 0x16:
                this.LD("d", "n");
                return;
            case 0x1E:
                this.LD("e", "n");
                return;
            case 0x26:
                this.LD("h", "n");
                return;
            case 0x2E:
                this.LD("l", "n");
                return;

            case 1000:
                this.STOP();
                return;
        }
        System.out.println("0x" + Integer.toHexString(opcode) + " at PC 0x" + Integer.toHexString(this.pc) + "(" + opcode + "/" + this.pc + ")");
        if (Gameboy.HALT_IF_UNKNOWN_OPCODE) {
            this.stopped = true;
        }
    }

    /**
     * Add Instruction
     *
     * @param index  letter of the index
     * @param index2 letter of the index
     */
    public void ADD(String index, String index2) {
        System.out.println("ADD " + index + "," + index2);
        Field i1 =null;
        if(index2.equals("#")) {
            Field i2;
            try {
                i1 = this.getClass().getDeclaredField(index.toLowerCase());
                i2 = this.getClass().getDeclaredField(index2.toLowerCase());
            } catch (Exception e) {
                System.out.println("Invalid Execution Code ADD " + index + "," + index2);
                e.printStackTrace();
                return;
            }
            if (i1.getType() == int.class && i2.getType() == int.class) {
                try {
                    i2.setInt(this, i1.getInt(this) + i2.getInt(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid INDEX (ADD " + index + "," + index2 + ")");
            }
        }else{
            try{
                i1 = this.getClass().getDeclaredField(index.toLowerCase());
                i1.setInt(this,i1.getInt(this) + n);
            pc++;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        try {
            if ((i1.getInt(this) & 255) == 0) this.f |= 0x80;
            if (i1.getInt(this)  > 255) this.f |= 0x10;       // Check for carry
            this.a &= 255;
            this.m = 1;
            this.t = 4;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * No-Operation NOP
     */
    private void NOP() {
        System.out.println("NOP");
        this.m = 1;
        this.t = 4;
    }
    private void incPC(){
        pc++;
        pc &= 0xFFFF;
    }
    private void LD(String nn, String n){
        System.out.println("LD " + n + "," + nn);
        Field i1;
        Field i2;
        try {
            i1 = this.getClass().getDeclaredField(nn.toLowerCase());
            i2 = this.getClass().getDeclaredField(n.toLowerCase());
        } catch (Exception e) {
            System.out.println("Invalid Execution Code CP " + a + "," + b);
            e.printStackTrace();
            return;
        }
        if (i1.getType() == int.class) {
            try {
                i1.setInt(this, i2.getInt(this));
                incPC();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void CP(String a, String b) {
        System.out.println("CP " + a + "," + b);
        Field i1;
        Field i2;
        try {
            i1 = this.getClass().getDeclaredField(a.toLowerCase());
            i2 = this.getClass().getDeclaredField(b.toLowerCase());
        } catch (Exception e) {
            System.out.println("Invalid Execution Code CP " + a + "," + b);
            e.printStackTrace();
            return;
        }
        if (i1.getType() == int.class && i2.getType() == int.class) {
            try {
                int i = i1.getInt(this);    //TEMP COPY OF A
                i -= i2.getInt(this);        //SUBTRACT B
                this.f |= 0x40;             //WE SUBTRACT
                if ((i & 255) == 0) this.f |= 0x80; //ZERO
                if (i < 0) this.f |= 0x10;
                this.m = 1;
                this.t = 4;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid INDEX (CP" + a + "," + b + ")");
        }
        if ((this.a & 255) == 0) this.f |= 0x80;
        if (this.a > 255) this.f |= 0x10;       // Check for carry
    }

    private void STOP() {
        this.stopped = true;
    }
}

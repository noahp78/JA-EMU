package me.noahp78.jemu.emu;

import me.noahp78.jemu.Gameboy;

import java.lang.reflect.Field;

/**
 * Z80 CPU emulator
 * Created by noahp on 26/okt/2016 for JA-EMU
 */
public class Z80 {
    private static final int Z_FLAG = 0x80;
    private static final int N_FLAG = 0x40;
    private static final int H_FLAG = 0x20;
    private static final int C_FLAG = 0x10;
    //REGISTERS (8 BITS MAX 255)
    public int a, b, c, d, e, h, l, f;
    public int af = -32770;
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
    int unknown = 0;

    public Z80() {
        f = 0x0;
        //Maybe also point PC to start of rom?
        this.pc = 0;
        this.sp = 0;
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
        int pre_af = af;
        run(opcode);
        pc++;
        if (bc != pre_bc) {
            //There was a operation on BC
            //Port BC back to B and C
            b = bc & 0xFF;
            c = bc >> 8;
            //System.out.println("Operation on BC("+bc+"), splitting to B:" + b + "/" + c);

        } else if (de != pre_de) {
            //There was a operation on DE
            d = de & 0xFF;
            e = de >> 8;
            //System.out.println("Operation on DE ("+de+"), splitting to D:" + d + "/" + e);

        } else if (hl != pre_hl) {
            //There was a operation on HL
            h = hl & 0xFF;
            l = hl >> 8;
            //System.out.println("Operation on HL ("+hl+"), splitting to H:" + h + "/" + l);

        }else if(af !=pre_af){
            //There was a operation on HL
            a = af & 0xFF;
            f = af >> 8;
           // System.out.println("Operation on af ("+af+"), splitting to H:" + a + "/" + f);

        }
        if (pc > Gameboy.memory.bios.length) {
            System.out.println("BIOS ENDED with SIZE " + Gameboy.memory.bios.length + " or 0x" + Integer.toHexString(Gameboy.memory.bios.length) + " We know " + (256 - unknown) + "/256 opcodes");
            this.stopped = true;
        }
        pc &= 0xFFFF;
    }
    private int res(int n, int val) {

        val &= ~(0x1 << n);

        return val & 0xFF;
    }
    public void run(int opcode) {
        //System.out.print(Integer.toHexString(pc) + " ");
        switch (opcode) {
            case 0x00:
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
            case 0x7F:
                this.LD("a", "a");
                return;
            case 0x78:
                this.LD("a", "b");
                return;
            case 0x79:
                this.LD("a", "c");
                return;
            case 0x7A:
                this.LD("a", "d");
                return;
            case 0x7B:
                this.LD("a", "e");
                return;
            case 0x7C:
                this.LD("a", "h");
                return;
            case 0x7D:
                this.LD("a", "l");
                return;
            case 0x0A:
                this.LD("a","bc");
                return;
            case 0x1A:
                this.LD("a", "de");
                return;
            case 0xFA:
                this.LD("a", "nn");
                return;
            case 0x3E:
                this.LD("a", "n");
                return;
            case 0x7E:
                this.LD("a", "hl");
                return;
            case 0x40:
                this.LD("b", "b");
                return;
            case 0x41:
                this.LD("b", "c");
                return;
            case 0x42:
                this.LD("b", "d");
                return;
            case 0x43:
                this.LD("b", "e");
                return;
            case 0x44:
                this.LD("b", "h");
                return;
            case 0x45:
                this.LD("b", "l");
                return;
            case 0x46:
                this.LD("b", "hl");
                return;
            case 0x48:
                this.LD("c", "b");
                return;
            case 0x49:
                this.LD("c", "c");
                return;
            case 0x4A:
                this.LD("c", "d");
                return;
            case 0x4B:
                this.LD("c", "e");
                return;
            case 0x4C:
                this.LD("c", "h");
                return;
            case 0x4D:
                this.LD("c", "l");
                return;
            case 0x4E:
                this.LD("c", "hl");
                return;
            case 0x50:
                this.LD("d", "b");
                return;
            case 0x51:
                this.LD("d", "c");
                return;
            case 0x52:
                this.LD("d", "d");
                return;
            case 0x53:
                this.LD("d", "e");
                return;
            case 0x54:
                this.LD("d", "h");
                return;
            case 0x55:
                this.LD("d", "l");
                return;
            case 0x56:
                this.LD("d", "hl");
                return;
            case 0x58:
                this.LD("e", "b");
                return;
            case 0x59:
                this.LD("e", "c");
                return;
            case 0x5A:
                this.LD("e", "d");
                return;
            case 0x5B:
                this.LD("e", "e");
                return;
            case 0x5C:
                this.LD("e", "h");
                return;
            case 0x5D:
                this.LD("e", "l");
                return;
            case 0x5E:
                this.LD("e", "hl");
                return;
            case 0x60:
                this.LD("h", "b");
                return;
            case 0x61:
                this.LD("h", "c");
                return;
            case 0x62:
                this.LD("h", "d");
                return;
            case 0x63:
                this.LD("h", "e");
                return;
            case 0x64:
                this.LD("h", "h");
                return;
            case 0x65:
                this.LD("h", "l");
                return;
            case 0x66:
                this.LD("h", "hl");
                return;
            case 0x68:
                this.LD("l", "b");
                return;
            case 0x69:
                this.LD("l", "c");
                return;
            case 0x6A:
                this.LD("l", "d");
                return;
            case 0x6B:
                this.LD("l", "e");
                return;
            case 0x6C:
                this.LD("l", "h");
                return;
            case 0x6D:
                this.LD("l", "l");
                return;
            case 0x6E:
                this.LD("l", "hl");
                return;
            case 0x70:
                this.LD("hl", "b");
                return;
            case 0x71:
                this.LD("hl", "c");
                return;
            case 0x72:
                this.LD("hl", "d");
                return;
            case 0x73:
                this.LD("hl", "e");
                return;
            case 0x74:
                this.LD("hl", "h");
                return;
            case 0x75:
                this.LD("hl", "l");
                return;
            case 0x36:
                this.LD("hl", "n");
                return;
            case 0x47:
                this.LD("b","a");
                return;
            case 0x4F:
                this.LD("c","a");
                return;
            case 0x57:
                this.LD("d","a");
                return;
            case 0x5F:
                this.LD("e","a");
                return;
            case 0x67:
                this.LD("h","a");
                return;
            case 0x02:
                this.LD("bc","a");
                return;
            case 0x12:
                this.LD("de","a");
                return;
            case 0x77:
                this.LD("hl","a");
                return;
            case 0xEA:
                this.LD("nn","a");
                return;
            case 0xF2:
                this.LDAC();
                return;
            case 0xE2:
                this.LDCA();
                return;
            case 0x3A:
                this.LDD("a", "hl");
                return;
            case 0x32:
                this.LDD("hl", "a");
                return;
            case 0x2A:
                this.LDI("a", "hl");
                return;
            case 0x22:
                this.LDI("hl", "a");
                return;
            case 0xF0:
                this.LDHAN();
                return;
            case 0xE0:
                this.LDHNA();
                return;
            case 0x01:
                this.LD("bc","nn");
                return;
            case 0x11:
                this.LD("de","nn");
                return;
            case 0x21:
                this.LD("hl","nn");
                return;
            case 0x31:
                this.LD("sp","nn");
                return;
            case 0xF9:
                this.LD("sp","hl");
                return;
            case 0xF8:
                this.LDHL("sp","n");
                return;
            case 0x08:
                this.LD("nn","sp");
                return;
            case 0xF5:
                this.PUSH("af");
                return;
            case 0xC5:
                this.PUSH("bc");
                return;
            case 0xD5:
                this.PUSH("de");
                return;
            case 0xE5:
                this.PUSH("hl");
                return;
            case 0xF1:
                this.POP("af");
                return;
            case 0xC1:
                this.POP("bc");
                return;
            case 0xD1:
                this.POP("de");
                return;
            case 0xE1:
                this.POP("hl");
                return;
            case 0x8F:
                this.adc("a", "a");
                return;
            case 0x88:
                this.adc("a", "b");
                return;
            case 0x89:
                this.adc("a", "c");
                return;
            case 0x8A:
                this.adc("a", "d");
                return;
            case 0x8B:
                this.adc("a", "e");
                return;
            case 0x8C:
                this.adc("a", "h");
                return;
            case 0x8D:
                this.adc("a", "l");
                return;
            case 0x8E:
                this.adc("a", "hl");
                return;
            case 0xCE:
                this.adc("a", "n");
                return;
            case 0x97:
                this.SUB("a","a");
                return;
            case 0x90:
                this.SUB("a","b");
                return;
            case 0x91:
                this.SUB("a","c");
                return;
            case 0x92:
                this.SUB("a","d");
                return;
            case 0x93:
                this.SUB("a","e");
                return;
            case 0x94:
                this.SUB("a","h");
                return;
            case 0x95:
                this.SUB("a","l");
                return;
            case 0x96:
                this.SUB("a","hl");
                return;
            case 0xD6:
                this.SUB("a","n");
                return;

            // ^ Everything upto page 81 doc ^ //
            case 1000:
                this.STOP();
                return;
        }
        //System.out.println("0x" + Integer.toHexString(opcode) + " at PC 0x" + Integer.toHexString(this.pc) + "(" + opcode + "/" + this.pc + ")");
        log("- UNKNOWN OPCODE -");
        unknown++;
        if (Gameboy.HALT_IF_UNKNOWN_OPCODE) {
            this.stopped = true;
        }
    }

    public void log(String text) {
        String base = "SP: " + sp + " | PC: " + Integer.toHexString(pc);
        String mem = "";
        if (text.contains(",nn") || text.contains("nn,")) {
            mem = Integer.toHexString(Gameboy.memory.rb(pc)) + " " + Integer.toHexString(Gameboy.memory.rb(pc + 1)) + " " + Integer.toHexString(Gameboy.memory.rb(pc + 2));
        } else if (text.contains(",n") || text.contains("n,")) {
            mem = Integer.toHexString(Gameboy.memory.rb(pc)) + " " + Integer.toHexString(Gameboy.memory.rb(pc + 1));
        } else {
            mem = Integer.toHexString(Gameboy.memory.rb(pc));
        }
        text = text.replace(",nn", "," + Integer.toHexString(nn));
        text = text.replace(",n", "," + Integer.toHexString(n));
        text = text.replace("nn,", Integer.toHexString(nn) + ",");
        text = text.replace("n,", Integer.toHexString(n) + ",");
        int whitespace = 64;
        int spaceforinfo = 22;
        spaceforinfo = spaceforinfo - base.length();
        for (int i = 0; i < spaceforinfo; i++) {
            base = base + " ";
        }
        int length = base.length() + text.length();
        whitespace = whitespace - length - mem.length();
        for (int i = 0; i <= whitespace; i++) {
            mem = " " + mem;
        }
        System.out.println(base + " " + text + " " + mem);
    }

    /**
     * Add Instruction
     *
     * @param index  letter of the index
     * @param index2 letter of the index
     */
    public void ADD(String index, String index2) {
        log("ADD " + index + "," + index2);
        Field i1 = null;
        if (index2.equals("#")) {
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
        } else {
            try {
                i1 = this.getClass().getDeclaredField(index.toLowerCase());
                i1.setInt(this, i1.getInt(this) + n);
                incPC();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if ((i1.getInt(this) & 255) == 0) this.f |= 0x80;
            if (i1.getInt(this) > 255) this.f |= 0x10;       // Check for carry
            this.a &= 255;
            this.m = 1;
            this.t = 4;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * No-Operation NOP
     */
    private void NOP() {
        log("NOP");
        this.m = 1;
        this.t = 4;
    }

    private void incPC() {
        pc++;
        pc &= 0xFFFF;
    }

    /**
     * Put value N into NN
     * @param nn
     * @param n
     */
    private void LD(String nn, String n) {
        log("LD " + nn + "," + n);
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
                if (n.equals("n") || nn.equals("n")) {
                    incPC();
                } else if (n.equals("nn") || nn.equals("nn")) {
                    incPC();
                    incPC();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void LDAC(){
        log("LD A,C");
        a= 0xff00 + c;
    }
    private void LDCA(){
        log("LD C,A");
        Gameboy.memory.wb(0xFF00+c,Gameboy.memory.rom[pc+1]);
    }
    private void LDHNA(){
        log("LDH n,A");
        Gameboy.memory.wb(0xFF00+n, (byte) a);
        incPC();
    }
    private void LDHAN(){
        log("LDH A,n");
        a = Gameboy.memory.rb(0xFF00+n);
        incPC();
    }



    private void CP(String a, String b) {
        log("CP " + a + "," + b);
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
        if (a.equals("n") || b.equals("n")) {
            incPC();
        } else if (a.equals("nn") || b.equals("nn")) {
            incPC();
            incPC();
        }

    }
    private void LDD(String a, String b){
        log("LDD " + a + "," + b);
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
                i1.setInt(this, Gameboy.memory.rb(i2.getInt(this)));
                i2.setInt(this,i2.getInt(this)-1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (a.equals("n") || b.equals("n")) {
            incPC();
        } else if (a.equals("nn") || b.equals("nn")) {
            incPC();
            incPC();
        }
    }
    private void LDI(String a, String b){
        log("LDD " + a + "," + b);
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
                i1.setInt(this, Gameboy.memory.rb(i2.getInt(this)));
                i2.setInt(this,i2.getInt(this)+1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (a.equals("n") || b.equals("n")) {
            incPC();
        } else if (a.equals("nn") || b.equals("nn")) {
            incPC();
            incPC();
        }
    }
    private void LDHL(String a, String b){
        log("LDHL " + a + "," + b);
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
                hl = i2.getInt(this) + i1.getInt(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (a.equals("n") || b.equals("n")) {
            incPC();
        } else if (a.equals("nn") || b.equals("nn")) {
            incPC();
            incPC();
        }
    }
    private void PUSH(String a1){
        log("PUSH " + a1);
        Field i1;
        try {
            i1 = this.getClass().getDeclaredField(a1.toLowerCase());

        } catch (Exception e) {
            System.out.println("Invalid Execution Code PUSH " + a1);
            e.printStackTrace();
            return;
        }
        if(i1.getType() == int.class){
            try {
                sp--;
                sp &= 0xFFFF;
                int word = i1.getInt(this);
                Gameboy.memory.wb(sp, (byte) ((word>>8)&0xFF));
                sp--;
                sp &= 0xFFFF;
                Gameboy.memory.wb(sp, (byte) ((word&0xFF)));

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private void POP(String a1){
        log("POP " + a1);
        Field i1;
        try {
            i1 = this.getClass().getDeclaredField(a1.toLowerCase());

        } catch (Exception e) {
            System.out.println("Invalid Execution Code POP " + a1);
            e.printStackTrace();
            return;
        }
        if(i1.getType() == int.class){
            try {
                sp++;
                sp &= 0xFFFF;
                int sp2 = sp+1;
                sp2&=0xFFFF;
                int word = (((Gameboy.memory.rb(sp2)&0xFF)<<8)| Gameboy.memory.rb(sp) & 0xff);

                sp++;
                sp &= 0xFFFF;
                i1.setInt(this,word);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private void adc(String target, String target2) {
        log("ADC " +target + "," + target2);
        Field i1;
        Field i2;
        try {

            i1 = this.getClass().getDeclaredField(target.toLowerCase());
            i2 = this.getClass().getDeclaredField(target2.toLowerCase());
        } catch (Exception e) {
            System.out.println("Invalid Execution Code ADD " + target + ", " + target2);
            e.printStackTrace();
            return;
        }
        if(i1.getType() == int.class) {
            try {
                int val = i1.getInt(this);
                int atemp = i2.getInt(this);
                int carry = ((atemp & C_FLAG) >> 4);
                int temp = f + val + carry;

                int F = 0;
                if ((((f & 0x0F) + (val & 0x0F) + carry) & 0x10) == 0x10)
                    F |= H_FLAG;
                if ((temp & 0x100) == 0x100)
                    F |= C_FLAG;
                if ((temp & 0xFF) == 0)
                    F |= Z_FLAG;
                a = (F);

                f = (temp);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if (target.equals("n") || target2.equals("n")) {
            incPC();
        } else if (target.equals("nn") || target2.equals("nn")) {
            incPC();
            incPC();
        }
    }

    /**
     * Subtract N from A
     * @param target A
     * @param target2 N
     */
    private void SUB(String target, String target2){
        log("SUB " +target + "," + target2);
        Field i1;
        Field i2;
        try {

            i1 = this.getClass().getDeclaredField(target.toLowerCase());
            i2 = this.getClass().getDeclaredField(target2.toLowerCase());
        } catch (Exception e) {
            System.out.println("Invalid Execution Code SUB " + target + ", " + target2);
            e.printStackTrace();
            return;
        }
        if(i1.getType() == int.class) {
            try {
                int val = i1.getInt(this);
                int atemp = i2.getInt(this);
                int temp = atemp - val;

                int F = N_FLAG;
                F |= (H_FLAG & ((atemp ^ val ^ (temp & 0xFF)) << 1));
                if (temp < 0)
                    F |= C_FLAG;
                if ((temp & 0xFF) == 0)
                    F |= Z_FLAG;
                f=F;

                i1.setInt(this,temp);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if (target.equals("n") || target2.equals("n")) {
            incPC();
        } else if (target.equals("nn") || target2.equals("nn")) {
            incPC();
            incPC();
        }
    }

    private void STOP() {
        this.stopped = true;
    }
}

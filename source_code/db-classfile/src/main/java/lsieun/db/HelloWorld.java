package lsieun.db;

import io.javalin.Javalin;
import lsieun.db.cst.OpcodeConst;

public class HelloWorld {
    public static void main(String[] args) {
        String format = "case OpcodeConst.%s: obj = new %s(); break;";
        for(int i=0; i<256; i++) {

            String opcode_name = OpcodeConst.getOpcodeName(i);
            if (OpcodeConst.ILLEGAL_OPCODE.equals(opcode_name)) continue;
            String name = opcode_name.toUpperCase();
            String line = String.format(format, name, name);
            System.out.println(line);
        }
    }
}

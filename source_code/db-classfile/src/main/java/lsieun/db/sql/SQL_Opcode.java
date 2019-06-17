package lsieun.db.sql;

import lsieun.db.cst.OpcodeConst;

public class SQL_Opcode {
    public static void main(String[] args) {
        int max_length = 0;
        String format = "INSERT INTO `biz_opcode` (`id`, `name`) VALUES (%d, '%s');";
        for(int i=0; i<256; i++) {
            String opcode_name = OpcodeConst.getOpcodeName(i);
            if(OpcodeConst.ILLEGAL_OPCODE.equals(opcode_name)) continue;

            if(opcode_name.length() > max_length) {
                max_length = opcode_name.length();
            }
            String line = String.format(format, i, opcode_name);
            System.out.println(line);
        }

        System.out.println("max_length = " + max_length);
    }
}

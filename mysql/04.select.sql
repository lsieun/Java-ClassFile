
DROP TABLE ``;

TRUNCATE TABLE `biz_opcode`;

SHOW CREATE TABLE `dumb_table`;

SELECT * from `dumb_table`;

SELECT * from `dict_unit`;

SELECT * from `dict_structure`;

SELECT * from `biz_opcode`;

SELECT * from `biz_instruction`;

SELECT * from `biz_instruction_code`;

SELECT * from `biz_instruction_code` ORDER BY `id`;

SELECT * from `biz_instruction_code` WHERE `instruction` = 'TypedInstruction';

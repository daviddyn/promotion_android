package com.davidsoft.utils;

public final class TextUtils {

    /**
     * 全角符号转半角符号
     */
    public static char toHalfCode(char src) {
        switch (src) {
            case '　':
                return ' ';
            case '﹨':
                return '\\';
            case '‘':
            case '’':
                return '\'';
            case '“':
            case '”':
            case '「':
            case '『':
            case '」':
            case '』':
                return '\"';
            case '﹒':
            case '。':
                return '.';
            case '﹦':
                return '=';
            case '﹟':
                return '#';
            case '﹠':
                return '&';
            case '﹡':
                return '*';
            case '﹢':
                return '+';
            case '﹣':
                return '-';
            case '﹐':
                return ',';
            case '﹔':
                return ';';
            case '﹕':
                return ':';
            case '﹖':
                return '?';
            case '﹗':
                return '!';
            case '﹪':
                return '%';
            case '﹩':
                return '$';
            case '﹫':
                return '@';
            case '〈':
            case '《':
            case '﹤':
                return '<';
            case '〉':
            case '》':
            case '﹥':
                return '>';
            case '【':
            case '〔':
            case '〖':
            case '﹝':
                return '[';
            case '】':
            case '〕':
            case '〗':
            case '﹞':
                return ']';
            case '﹛':
                return '{';
            case '﹜':
                return '}';
            case '﹙':
                return '(';
            case '﹚':
                return ')';
            default:
                if (src >= 65281 && src <= 65374) {
                    return (char) (src - 65248);
                }
                break;
        }
        return src;
    }

    /**
     * 字符分类
     */
    public enum CharacterType {
        CONTROL,        //控制字符(如\0等)
        SPLITTER,       //空白分隔符(如空格、\t、\n、\r等)
        SYMBOL,         //常见的符号(如逗号、句号、各种括号引号等)
        NUMBER,         //数字0-9
        UPPER_CASE,     //大写字母A-Z
        LOWER_CASE,     //小写字母a-z
        CHINESE,        //中文汉字
        SPECIAL_SYMBOL  //除上述之外的字符(统称为“特殊符号”)
    }

    /**
     * 获得字符的类型。
     *
     * @param c 字符
     * @return 字符类型。为枚举{@link CharacterType}中的值之一。
     */
    @SuppressWarnings("ConstantConditions")
    public static CharacterType getCharacterType(char c) {
        if (c <= 8 || 14 <= c && c <= 31 || c == 127) {
            return CharacterType.CONTROL;
        }
        else if (9 <= c && c <= 13 || c == 32) {
            return CharacterType.SPLITTER;
        }
        else if (33 <= c && c <= 47 || 58 <= c && c <= 64 || 91 <= c && c <= 96 || 123 <= c && c <= 126) {
            return CharacterType.SYMBOL;
        }
        else if (48 <= c && c <= 57) {
            return CharacterType.NUMBER;
        }
        else if (65 <= c && c <= 90) {
            return CharacterType.UPPER_CASE;
        }
        else if (97 <= c && c <= 122) {
            return CharacterType.LOWER_CASE;
        }
        else if (13312 <= c && c <= 40917) {
            return CharacterType.CHINESE;
        }
        else {
            return CharacterType.SPECIAL_SYMBOL;
        }
    }
}

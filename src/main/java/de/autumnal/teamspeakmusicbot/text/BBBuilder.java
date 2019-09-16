package de.autumnal.teamspeakmusicbot.text;

import java.awt.*;

public class BBBuilder {
    public static String breakLine(){
        return "\n\r";
    }

    public static String bold(String text){
        return format(text, "b");
    }

    public static String kursiv(String text){
        return format(text, "i");
    }

    public static String underline(String text){
        return format(text, "u");
    }

    public static String leftBound(String text){
        return format(text, "left");
    }

    public static String centered(String text){
        return format(text, "center");
    }

    public static String rightBound(String text){
        return format(text, "right");
    }

    public static String list(String[] items, ListType type){
        StringBuilder sb = new StringBuilder();

        switch (type){
            case Bulletpoint:
                sb.append("[list]");
                break;
            case Number:
                sb.append("[list=1]");
                break;
            case BigLetter:
                sb.append("[list=A]");
                break;
            case SmallLetter:
                sb.append("[list=a]");
                break;
        }

        for (String i: items)
            sb.append("[*]").append(i.replace("[*]", ""));

        sb.append("[/list]");
        return sb.toString();
    }

    public static String color(String text, BBColor color){
        StringBuilder sb = new StringBuilder();
        sb.append("[color=").append(color.toString()).append("]");
        sb.append(text);
        sb.append("[/color]");
        return sb.toString();
    }

    public static String size(String text, int size){
        if(size == 0) return text;
        StringBuilder sb = new StringBuilder();
        sb.append("[size=");
        if(size > 0) sb.append("+");
        else sb.append("-");
        sb.append(Math.abs(size)).append("]");

        sb.append(text);
        sb.append("[/size]");
        return sb.toString();
    }

    public static String font(String text, Font font){
        StringBuilder sb = new StringBuilder();
        sb.append("[font=").append(font.toString()).append("]");
        sb.append(text);
        sb.append("[/font]");
        return sb.toString();
    }

    public static String hyperlink(String text, String hyperlink){
        StringBuilder sb = new StringBuilder();
        sb.append("[url=").append(hyperlink).append("]");
        sb.append(text);
        sb.append("[/url]");
        return sb.toString();
    }

    public static String image(String link){
        return format(link, "image");
    }

    public static String highlight(String text){
        return format(text, "highlight");
    }

    private static String format(String text, String bb){
        String open = "[" + bb + "]";
        String close = "[/" + bb + "]";

        StringBuilder sb = new StringBuilder();
        sb.append(open);
        sb.append(text.replace(open, "")
                .replace(close, ""));
        sb.append(close);
        return sb.toString();
    }
}

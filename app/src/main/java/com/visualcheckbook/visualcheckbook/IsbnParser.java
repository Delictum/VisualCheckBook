package com.visualcheckbook.visualcheckbook;

public final class IsbnParser {

    public static String ParserISBN(String text) {
        if (isISBN(text)) {
            text = text.replace("-", "");
            String isbn = "";
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == ';') {
                    if (isbn.length() > 8 && isbn.length() < 15 && android.text.TextUtils.isDigitsOnly(isbn)) {
                        return isbn;
                    }
                    isbn = "";
                    continue;
                }
                isbn += text.charAt(i);
            }
        }
        return null;
    }

    public static boolean isISBN(String text) {
        return text.contains("ISBN");
    }
}

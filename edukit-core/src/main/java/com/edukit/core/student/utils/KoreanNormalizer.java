package com.edukit.core.student.utils;

public class KoreanNormalizer {
    private static final int JUNGSUNG_COUNT = 21; // 중성 개수
    private static final int JONGSUNG_COUNT = 28; // 종성 개수
    private static final int HANGUL_START = 0xAC00; // 한글 시작 유니코드
    private static final int HANGUL_END = 0xD7A3; // 한글 끝 유니코드
    
    // 한글 초성
    private static char[] chosung = {
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };
    
    // 한글 중성
    private static char[] jungsung = {
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    };
    
    // 한글 종성
    private static char[] jongsung = {
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    // 한글을 음소로 분해 (ex. 홍길동 -> ㅎㅗㅇㄱㅣㄹㄷㅗㅇ)
    public static String toNormalized(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (isKorean(c)) {
                int unicode = c - HANGUL_START;
                int jong = unicode % JONGSUNG_COUNT;
                int jung = (unicode / JONGSUNG_COUNT) % JUNGSUNG_COUNT;
                int cho = unicode / JONGSUNG_COUNT / JUNGSUNG_COUNT;
                
                result.append(chosung[cho]);
                if (jung > 0) {
                    result.append(jungsung[jung]);
                }
                if (jong > 0) {
                    result.append(jongsung[jong]);
                }
            } else if (Character.isAlphabetic(c)) {
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static boolean isKorean(char c) {
        return c >= HANGUL_START && c <= HANGUL_END;
    }
}

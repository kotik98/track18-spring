package ru.track.cypher;

import java.util.*;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по кторому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        cypher = new LinkedHashMap<>();

        Iterator<Entry<Character, Integer>> domainHistIterator = domainHist.entrySet().iterator();
        Iterator<Entry<Character, Integer>> encryptedDomainHistIterator = encryptedDomainHist.entrySet().iterator();

        while (domainHistIterator.hasNext() && encryptedDomainHistIterator.hasNext()) {
            cypher.put(encryptedDomainHistIterator.next().getKey(), domainHistIterator.next().getKey());
        }
    }

    public Map<Character, Character> getCypher() {
        return cypher;
    }

    /**
     * Применяет построенный шифр для расшифровки текста
     *
     * @param encoded зашифрованный текст
     * @return расшифровка
     */
    @NotNull
    public String decode(@NotNull String encoded) {
        StringBuilder decoded = new StringBuilder(encoded.length());
        for (Character c:
             encoded.toCharArray()) {
            if (Character.isAlphabetic(c)){
                decoded.append(cypher.get(Character.toLowerCase(c)));
            } else {
                decoded.append(c);
            }
        }
        return decoded.toString();
    }

    /**
     * Считывает входной текст посимвольно, буквы сохраняет в мапу.
     * Большие буквы приводит к маленьким
     *
     *
     * @param text - входной текст
     * @return - мапа с частотой вхождения каждой буквы (Ключ - буква в нижнем регистре)
     * Мапа отсортирована по частоте. При итерировании на первой позиции наиболее частая буква
     */
    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> hist = new LinkedHashMap<>();
        for (Character curr = 'a'; curr < '{'; curr++) {
            Integer f = 0;
            for (int i = 0; i < text.length(); i++) {
                if (Character.toLowerCase(text.charAt(i)) == curr) {
                    f++;
                }
            }
            hist.put(curr, f);
        }

        List<Map.Entry<Character, Integer>> entryList = new ArrayList<>(hist.entrySet());
        Collections.sort(entryList, (a, b) -> { return b.getValue() - a.getValue(); });
        hist.clear();
        for (Map.Entry<Character, Integer> e:
             entryList) {
            hist.put(e.getKey(), e.getValue());
        }
        return hist;
    }

}

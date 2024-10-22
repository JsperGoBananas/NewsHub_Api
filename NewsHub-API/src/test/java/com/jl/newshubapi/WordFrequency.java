import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class WordFrequency {
    public static void main(String[] args) throws IOException {

        Set<String> stops = new HashSet<>(Arrays.asList(
            new String(Files.readAllBytes(Paths.get("./stop_words.txt"))).split(","))
        );

        stops.addAll(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));

        String inputText = new String(Files.readAllBytes(Paths.get(args[0])));
        
        List<String> words = Arrays.stream(inputText.split("[^a-zA-Z]+"))
                                   .map(String::toLowerCase)
                                   .filter(word -> !word.isEmpty() && !stops.contains(word))
                                   .collect(Collectors.toList());


        Map<String, Long> wordCount = words.stream()
                                           .collect(Collectors.groupingBy(w -> w, Collectors.counting()));


        wordCount.entrySet().stream()
                 .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                 .limit(25)
                 .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }
}

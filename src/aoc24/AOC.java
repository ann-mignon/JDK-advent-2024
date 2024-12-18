package aoc24;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static aoc24.Util.putln;

public class AOC {
    private static final String aocURL = "https://adventofcode.com/2024/day/%d/input";

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                Matcher dpmc = Pattern.compile("(\\d{1,2})([a-z]+)?").matcher(args[0]);

                if (dpmc.find()) {
                    Losning losning = losningById(args[0]);

                    int dag = Integer.parseInt(dpmc.group(1));

                    String input = args.length == 1 ?
                            hamtaInputWeb(dag) :
                            hamtaInputFil(dag, args[1]);

                    losning.setInput(input);

                    svar(args[0], losning.svar(), args.length > 1 ? args[1] : null);

                } else {
                    throw new RuntimeException("Ange lucka som 13 / 13b .. ( mönster: \\d{1,2}[a-z]+ )");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static Losning losningById(String dagId) throws ReflectiveOperationException {
        Class<? extends Losning> losklass =
                Class.forName("aoc24.luckor.lucka_%s".formatted(dagId))
                     .asSubclass(Losning.class);

        return losklass.getConstructor().newInstance();
    }

    static void svar(String dag, String svar, String filnamn) {
        if (filnamn == null) {
            putln("AoC '24, lucka %s » %s".formatted(dag, svar));
        } else {
            putln("Svar AoC '24, lucka %s » %s   (med test-input: /res/%1$s/%s)"
                    .formatted(dag, svar, filnamn));
        }
    }

    static String hamtaInputWeb(int dag) throws IOException, InterruptedException {
        try {
            HttpRequest hr = HttpRequest.newBuilder().uri(URI.create(aocURL.formatted(dag)))
                    .setHeader("Cookie", "session=%s".formatted(getProperty("pepparkaka")))
                    .method("GET", HttpRequest.BodyPublishers.noBody()).build();

            return HttpClient.newHttpClient().send(hr, HttpResponse.BodyHandlers.ofString()).body();
        } catch ( Exception e ) {
            throw new RuntimeException("Det gick inte att hämta input automatiskt från domänen.\n" +
                    "Har du ställt in pepparkaksparametern? ( i /src/aoc24/res/aoc.properties )");
        }
    }

    public static String hamtaInputFil(int dag, String sv) {
        try (InputStream is = AOC.class.getClassLoader()
                                       .getResourceAsStream("%d/%s".formatted(dag, sv))) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new Exception();
            }
        } catch (Exception x) {
            throw new RuntimeException(String.format("Infilen saknas (%s) %n", sv));
        }
    }

    public static String getProperty(String key) {
        try (InputStream propFile = AOC.class.getClassLoader().getResourceAsStream("aoc.properties")) {
            Properties prop = new Properties();
            prop.load(propFile);
            return prop.getProperty(key);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

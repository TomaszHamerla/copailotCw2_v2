import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CryptoCurrencyAnalyzer {

    public static void main(String[] args) {
        // Ustawienie czasu początkowego uruchomienia zadania
        long initialDelay = 0;

        // Okres uruchamiania zadania (24 godziny)
        long period = 24 * 60 * 60;

        // Utworzenie i skonfigurowanie scheduler'a
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> analyzeCryptoCurrency(), initialDelay, period, TimeUnit.SECONDS);
    }

    private static void analyzeCryptoCurrency() {
        try {
            // Pobieranie danych ze strony internetowej
            String url = "https://finance.yahoo.com/quote/BTC-USD/history/?guccounter=1";
            Document document = Jsoup.connect(url).get();

            // Analiza HTML, pobieranie danych kursu
            Elements closingPrices = document.select("table[data-test='historical-prices'] tr td:nth-child(5)");
            List<Double> prices = new ArrayList<>();
            closingPrices.forEach(element -> prices.add(Double.parseDouble(element.text().replace(",", ""))));
            Collections.reverse(prices); // Odwrócenie kolejności, aby mieć najnowsze dane na początku

            // Obliczanie 8-sesyjnej SMA
            double sma = calculateSMA(prices, 8);
            System.out.println("8-sesyjna SMA: " + sma);

            // Obliczanie 8-sesyjnej EMA
            double ema = calculateEMA(prices, 8);
            System.out.println("8-sesyjna EMA: " + ema);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double calculateSMA(List<Double> prices, int period) {
        int size = prices.size();
        double sum = 0;

        for (int i = 0; i < period; i++) {
            sum += prices.get(i);
        }

        return sum / period;
    }

    private static double calculateEMA(List<Double> prices, int period) {
        int size = prices.size();

        // Ustal wartość początkową EMA jako pierwsza cena
        double ema = prices.get(0);

        // Współczynnik wygładzania
        double multiplier = 2.0 / (period + 1);

        for (int i = 1; i < size; i++) {
            ema = (prices.get(i) - ema) * multiplier + ema;
        }

        return ema;
    }
}

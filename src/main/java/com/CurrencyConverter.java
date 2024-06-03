import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.currency.exchange.utils.HttpClientUtil;
import com.ApiKey;

import java.util.Scanner;
import java.util.regex.Pattern;

public class CurrencyConverter {
    private static final String API_KEY = ApiKey.KEY;

    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    private static final String AVAILABLE_CURRENCIES = """
            USD --- US Dollar
            MXN --- Mexican Peso
            ARS --- Argentine Peso
            BRL --- Brazilian Real
            COP --- Colombian Peso
            EUR --- Euro
        """;

    private static final Pattern CURRENCY_PATTERN = Pattern.compile("^[A-Z]{3}$");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueConversion = true;

        while (continueConversion) {
            System.out.println(" ฅ^•ﻌ•^ฅ Welcome to Currency Converter! (=ↀωↀ=) ");
            System.out.println("************************************");
            System.out.println("Type the code of your currency:");
            System.out.println(AVAILABLE_CURRENCIES);

            String sourceCurrency = getValidCurrencyCode(scanner);

            System.out.println("Enter the amount:");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Type the code of the target currency:");
            System.out.println(AVAILABLE_CURRENCIES);

            String targetCurrency = getValidCurrencyCode(scanner);

            try {
                double convertedAmount = convertCurrency(sourceCurrency, targetCurrency, amount);
                System.out.printf("Converted amount: %.2f %s%n", convertedAmount, targetCurrency);
            } catch (Exception e) {
                System.err.println("Error fetching exchange rate: " + e.getMessage());
            }

            System.out.println("Type 9 if you want another conversion or type 0 if you want to exit:");
            int userResponse = scanner.nextInt();
            scanner.nextLine();
            if (userResponse != 9) {
                continueConversion = false;
            }
        }

        System.out.println("Thank you for using the Currency Converter! ฅ^•ﻌ•^ฅ");
        scanner.close();
    }

    private static String getValidCurrencyCode(Scanner scanner) {
        String currencyCode;
        while (true) {
            currencyCode = scanner.nextLine().trim().toUpperCase();
            if (CURRENCY_PATTERN.matcher(currencyCode).matches()) {
                break;
            } else {
                System.out.println("Invalid currency code. Please enter a 3-letter currency code:");
            }
        }
        return currencyCode;
    }

    private static double convertCurrency(String sourceCurrency, String targetCurrency, double amount) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String url = BASE_URL + sourceCurrency;

        try {
            String response = httpClientUtil.sendGet(url);
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonObject conversionRates = jsonResponse.getAsJsonObject("conversion_rates");
            double exchangeRate = conversionRates.get(targetCurrency).getAsDouble();
            return amount * exchangeRate;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert currency: " + e.getMessage(), e);
        }
    }
}

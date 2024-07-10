package org.example;

import com.google.gson.Gson;
import org.example.model.Currency;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static String API_URL = "https://v6.exchangerate-api.com/v6/";
    static String API_KEY = System.getenv("EXCHANGE_API_KEY");

    public static void main(String[] args) throws IOException, InterruptedException {
        Currency currency = get_codes();

        List<String[]> supportedCodes = currency.getSupported_codes();
        Scanner scanner = new Scanner(System.in);
        int currentPage = 0;
        int pageSize = 8;
        String fromCurrency = null;
        String toCurrency = null;

        while (true) {
            int totalPages = (int) Math.ceil((double) supportedCodes.size() / pageSize);
            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, supportedCodes.size());

            if (fromCurrency == null){
                System.out.println("******************************************************** \n   BIENVENIDO AL SISTEMA DE CONVERSION DE MONEDAS\n********************************************************" );
                System.out.println("Seleccione la moneda a convertir...");
            } else {
                System.out.println("Seleccione la moneda hacia la cual convertir...");
            }

            for (int i = start; i < end; i++) {
                System.out.println((i - start + 1) + ". " + supportedCodes.get(i)[0] + " - " + supportedCodes.get(i)[1]);
            }
            System.out.println("9. Página siguiente");
            System.out.println("0. Cerrar programa");

            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("Cerrando el programa...");
                break;
            } else if (choice == 9) {
                currentPage = (currentPage + 1) % totalPages;
            } else if (choice >= 1 && choice <=8) {
                int selectedIndex = start + (choice - 1);
                    if (selectedIndex < supportedCodes.size()) {
                        if (fromCurrency == null) {
                            fromCurrency = supportedCodes.get(selectedIndex)[0];
                            System.out.println("**************************************************** \nSeleccionó la moneda: " + fromCurrency + "\n ****************************************************" );
                        } else {
                            toCurrency = supportedCodes.get(selectedIndex)[0];
                            System.out.println("**************************************************** \nSeleccionó la moneda: " + toCurrency + "\n ****************************************************");
                            System.out.println("<==============================================================>");
                            System.out.print("Ingrese el monto a convertir: ");
                            double amount = scanner.nextDouble();

                            double conversionRate = getExchangeRate(fromCurrency, toCurrency);
                            double convertedAmount = amount * conversionRate;

                            DecimalFormat df = new DecimalFormat("0.00");
                            String newConvertedAmount = df.format(convertedAmount);

                            System.out.println("El monto convertido es: $" + newConvertedAmount + "[" + toCurrency + "]");
                            System.out.println("<===============================================================>");

                            fromCurrency = null;
                            toCurrency = null;
                    }
                }
            }
        }
    }

    public static double getExchangeRate(String currency1, String currency2) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/" + currency1 + "/" + currency2))
                .build();
        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        Currency currency = gson.fromJson(response.body(), Currency.class);

        return currency.getConversion_rate();
    }

    public static Currency get_codes() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + API_KEY + "/codes"))
                .build();
        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), Currency.class);
    }
}
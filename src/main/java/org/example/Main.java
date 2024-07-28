package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static List<Ticket> tickets;

    public static void main(String[] args) {
        System.out.println(readAllJSONFIle());
        System.out.println(getTicketsWithCities());
        System.out.println(countTickets());
        System.out.println(countCities());
        System.out.println(getMinDurationTimeAndCompany());
        System.out.println(findAveragePrice());
        System.out.println(findMedian());
        System.out.println(findTheDifference(findAveragePrice(), findMedian()));

    }


    //чтение всего файла
    private static List<Ticket> readAllJSONFIle() {
        try (InputStream is = Main.class.getResourceAsStream("/tickets.json")) {
            Map<String, List<Ticket>> map = mapper.readValue(is, new TypeReference<Map<String, List<Ticket>>>() {
            });
            return map.get("tickets");
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return null;
    }
    public static int countTickets() {
        List<Ticket> tickets1 = readAllJSONFIle();
        return tickets1.size();
    }
    public static int countCities(){
        List<Ticket> tickets1 = getTicketsWithCities();
        return tickets1.size();
    }
    //отбор рейсов Владивосток-Тель-Авив
    private static List<Ticket>getTicketsWithCities() {
        tickets = readAllJSONFIle();
        List<Ticket> cities = new ArrayList<>();
        for (Ticket temp : tickets) {
            if(temp.getOriginName().equals("Владивосток") && temp.getDestinationName().equals("Тель-Авив")){
                cities.add(temp);
            }
        }
        return cities;
    }

    //нахождение минимального времени перелета
    private static Map<String, Duration> getMinDurationTimeAndCompany() {
        tickets = getTicketsWithCities();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        Map<String, Duration> minFlightTimes = new HashMap<>();

        for (Ticket ticket : tickets) {
            LocalTime departure = LocalTime.parse(ticket.getDepartureTime(), formatter);
            LocalTime arrival = LocalTime.parse(ticket.getArrivalTime(), formatter);
            Duration duration = Duration.between(departure, arrival);

            if (!minFlightTimes.containsKey(ticket.getCarrier()) || duration.compareTo(minFlightTimes.get(ticket.getCarrier())) < 0) {
                minFlightTimes.put(ticket.getCarrier(), duration);
            }
        }

        return minFlightTimes;
    }

//    //среднее арифметическое
    private static double findAveragePrice() {
        List<Ticket> tickets = getTicketsWithCities();
        double sum = 0;
        int count = 0;
        sum = tickets.stream().mapToDouble(Ticket::getPrice).sum();
        count = tickets.size();

        return sum / count;
    }
//
//    //медиана
    private static double findMedian() {
        List<Ticket> tickets = getTicketsWithCities();
        List<Ticket> sortedTickets = tickets.stream()
                .sorted((t1,t2)->Double.compare(t1.getPrice(),t2.getPrice())).toList();
            if (sortedTickets.size() % 2 != 0) {
                return sortedTickets.get(sortedTickets.size() / 2).getPrice();
            } else {
                return (sortedTickets.get(sortedTickets.size() / 2 - 1).getPrice() + sortedTickets.get(sortedTickets.size() / 2).getPrice()) / 2.0;
            }
    }

//    //разница между медианой и средним значением
    private static double findTheDifference(double a, double b) {
        return a - b;
    }
}
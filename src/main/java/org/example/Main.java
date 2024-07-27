package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static List<Ticket> tickets;

    public static void main(String[] args) {
        System.out.println(readAllJSONFIle());
        System.out.println(getMinDurationTimeAndCompany());
        System.out.println(findTheDifference(findAveragePrice(), findMedian()));

    }


    //чтение всего файла
    private static List<Ticket> readAllJSONFIle() {
        try (InputStream is = Main.class.getResourceAsStream("/tickets.json")) {
            tickets = mapper.readValue(is, new TypeReference<>() {
            });
            return tickets;
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return null;
    }

    //нахождение минимального времени перелета
    private static Map<String, Duration> getMinDurationTimeAndCompany() {
        List<Ticket> tickets = readAllJSONFIle();
        Map<String, Duration> minFlightTimes = new HashMap<>();
        String s = null;
        LocalDateTime departure;
        LocalDateTime arrival;
        Duration duration;
        for (int i = 0; i < Objects.requireNonNull(tickets).size(); i++) {
            Ticket ticket = tickets.get(i);
            departure = ticket.getDepartureTime();
            arrival = ticket.getArrivalTime();
            duration = Duration.between(departure, arrival);

            if (i < tickets.size() - 1) {
                Ticket tempTicket = tickets.get(i + 1);
                if (ticket.getCompany().equals(tempTicket.getCompany())) {
                    LocalDateTime departureTemp = tempTicket.getDepartureTime();
                    LocalDateTime arrivalTemp = tempTicket.getArrivalTime();
                    Duration durationTemp = Duration.between(departureTemp, arrivalTemp);
                    if (durationTemp.getSeconds() > duration.getSeconds()) {
                        minFlightTimes.put(ticket.getCompany(), duration);
                    } else {
                        minFlightTimes.put(ticket.getCompany(), durationTemp);
                    }
                }
            }
        }
        return minFlightTimes;
    }

    //среднее арифметическое
    private static double findAveragePrice() {
        List<Ticket> tickets = readAllJSONFIle();
        double sum = 0;
        int count = 0;
        sum = Objects.requireNonNull(tickets).stream().mapToDouble(Ticket::getPrice).sum();
        count = tickets.size();

        return sum / count;
    }

    //медиана
    private static double findMedian() {
        List<Ticket> tickets = readAllJSONFIle();
        List<Ticket> sortedTickets = Objects.requireNonNull(tickets).stream()
                .sorted((t1, t2) -> Integer.compare(t1.getPrice(), t2.getPrice())).toList();
        double sum = 0.0;
        for (int i = 0; i < sortedTickets.size(); i++) {
            if (sortedTickets.size() % 2 != 0) {
                Ticket ticket = sortedTickets.get(sortedTickets.size() / 2);
                sum = (double) ticket.getPrice();
            } else if (sortedTickets.size() % 2 == 0) {
                Ticket firstMiddle = sortedTickets.get(sortedTickets.size() / 2 - 1);
                Ticket secondMiddle = sortedTickets.get(sortedTickets.size() / 2);
                sum = ((double) firstMiddle.getPrice() + (double) secondMiddle.getPrice()) / 2;
            }
        }
        return sum;
    }

    //разница между медианой и средним значением
    private static double findTheDifference(double a, double b) {
        return a - b;
    }
}